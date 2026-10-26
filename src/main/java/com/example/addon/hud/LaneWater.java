package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * LaneWater (Minecraft 1.21.11, Mojang mappings)
 *
 * Fills a straight 1-wide lane with water SOURCE blocks using one bucket.
 * An empty block between two water sources (with a solid block below) becomes a source
 * by itself, so we place water on every 2nd block and scoop a self-refilling gap block
 * to get the bucket full again.
 *
 * Before enabling:
 *  - Put water yourself on lane block 1 and lane block 3.
 *  - Have a bucket (empty or full) in your HOTBAR.
 *  - Look at the FLOOR block under lane block 1.
 *  - Set the lane direction in the module settings.
 */
public class LaneWater extends Module {
    public enum LaneDirection {
        North(0, -1), South(0, 1), East(1, 0), West(-1, 0);

        public final int dx, dz;

        LaneDirection(int dx, int dz) {
            this.dx = dx;
            this.dz = dz;
        }
    }

    private enum State { PLACE, WAIT_PLACE, SCOOP, WAIT_SCOOP, FINISH }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> length = sgGeneral.add(new IntSetting.Builder()
        .name("length")
        .description("Number of blocks in the lane.")
        .defaultValue(60)
        .min(3)
        .sliderRange(3, 128)
        .build());

    private final Setting<LaneDirection> direction = sgGeneral.add(new EnumSetting.Builder<LaneDirection>()
        .name("direction")
        .description("Direction the lane runs, starting from the block you look at.")
        .defaultValue(LaneDirection.South)
        .build());

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Ticks to wait between actions.")
        .defaultValue(2)
        .min(0)
        .sliderRange(0, 10)
        .build());

    private final Setting<Boolean> autoWalk = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-walk")
        .description("Walk along the lane while filling it.")
        .defaultValue(true)
        .build());

    private final Setting<Double> walkDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("walk-distance")
        .description("Keep walking until you are this many blocks behind the next placement.")
        .defaultValue(2.0)
        .min(0.5)
        .sliderRange(0.5, 3.5)
        .visible(autoWalk::get)
        .build());

    private final Setting<Double> reach = sgGeneral.add(new DoubleSetting.Builder()
        .name("reach")
        .description("Maximum distance to a target block.")
        .defaultValue(4.0)
        .min(2)
        .sliderRange(2, 4.5)
        .build());

    private final Setting<Boolean> refillAtEnd = sgGeneral.add(new BoolSetting.Builder()
        .name("refill-at-end")
        .description("Finish with a full bucket so you can start the next lane.")
        .defaultValue(true)
        .build());

    private final Setting<Integer> timeout = sgGeneral.add(new IntSetting.Builder()
        .name("timeout")
        .description("Ticks before giving up if something gets stuck.")
        .defaultValue(100)
        .min(20)
        .sliderRange(20, 400)
        .build());

    private BlockPos start;      // floor block under lane block 0
    private LaneDirection dir;
    private int len;
    private int last;            // all blocks 0..last are sources
    private int target;          // index currently being placed
    private State state;
    private int delayTimer, stateTimer;
    private boolean refilled;

    public LaneWater() {
        super(AddonTemplate.CATEGORY, "lane-water", "Fills a 1-wide lane with water sources using a single bucket.");
    }

    // ------------------------------------------------------------ lifecycle

    @Override
    public void onActivate() {
        if (mc.player == null || mc.level == null) {
            toggle();
            return;
        }

        if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            fail("Look at the floor block under the FIRST water block of the lane, then enable the module.");
            return;
        }

        start = hit.getBlockPos().immutable();
        dir = direction.get();
        len = length.get();
        refilled = false;

        if (!isSource(0) || !isSource(2)) {
            fail("Place water on the 1st and 3rd block of the lane first (and check the direction setting).");
            return;
        }

        if (firstMissing() == -1) {
            info("The lane is already full of water.");
            toggle();
            return;
        }

        last = 2;
        while (last + 2 < len && isSource(last + 1) && isSource(last + 2)) last += 2;

        boolean hasWater = hotbarHas(Items.WATER_BUCKET);
        boolean hasEmpty = hotbarHas(Items.BUCKET);
        if (!hasWater && !hasEmpty) {
            fail("Put a bucket in your hotbar.");
            return;
        }

        setState(hasWater ? State.PLACE : State.SCOOP);
        delayTimer = 0;
    }

    @Override
    public void onDeactivate() {
        if (mc.options != null) mc.options.keyUp.setDown(false);
    }

    // ------------------------------------------------------------ main loop

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.level == null || start == null) return;

        if (state == State.FINISH) mc.options.keyUp.setDown(false);
        else if (walk()) return;

        if (delayTimer > 0) {
            delayTimer--;
            return;
        }

        if (++stateTimer > timeout.get()) {
            int missing = firstMissing();
            fail("Stuck (state " + state + ", missing block " + (missing + 1)
                + "). Make sure the floor is solid and the lane is walled in.");
            return;
        }

        switch (state) {
            case PLACE -> {
                if (!select(Items.WATER_BUCKET)) {
                    fail("No water bucket in the hotbar.");
                    return;
                }
                target = Math.min(last + 2, len - 1);
                if (useAt(floorTop(target))) setState(State.WAIT_PLACE);
            }

            case WAIT_PLACE -> {
                if (isSource(target) && isSource(target - 1)) {
                    last = target;
                    setState(last >= len - 1 ? State.FINISH : State.SCOOP);
                } else if (stateTimer > 10 && !isSource(target)
                    && mc.player.getMainHandItem().is(Items.WATER_BUCKET)) {
                    setState(State.PLACE); // placement failed, retry
                }
            }

            case SCOOP -> {
                int gap = last - 1;
                if (!isSource(gap)) return; // wait for the gap to refill itself
                if (!select(Items.BUCKET)) {
                    fail("No empty bucket in the hotbar.");
                    return;
                }
                if (useAt(center(gap))) setState(State.WAIT_SCOOP);
            }

            case WAIT_SCOOP -> {
                if (mc.player.getMainHandItem().is(Items.WATER_BUCKET)) {
                    setState(State.PLACE);
                } else if (stateTimer > 10 && isSource(last - 1)) {
                    setState(State.SCOOP); // scoop failed, retry
                }
            }

            case FINISH -> {
                if (firstMissing() != -1) return; // wait for the last gaps to fill

                if (refillAtEnd.get() && !refilled) {
                    refilled = true;
                    stateTimer = 0;
                    if (select(Items.BUCKET)) useAt(center(len - 2));
                    return;
                }

                info("Lane finished.");
                toggle();
            }
        }
    }

    // ------------------------------------------------------------ helpers

    private boolean walk() {
        if (!autoWalk.get()) return false;

        int t = Math.min(last + 2, len - 1);
        BlockPos p = lanePos(t);
        double playerAlong = mc.player.getX() * dir.dx + mc.player.getZ() * dir.dz;
        double targetAlong = (p.getX() + 0.5) * dir.dx + (p.getZ() + 0.5) * dir.dz;

        if (playerAlong < targetAlong - walkDistance.get()) {
            mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dir.dz, dir.dx)) - 90));
            mc.player.setXRot(0);
            mc.options.keyUp.setDown(true);
            return true;
        }

        mc.options.keyUp.setDown(false);
        return false;
    }

    private BlockPos floorPos(int i) {
        return start.offset(dir.dx * i, 0, dir.dz * i);
    }

    private BlockPos lanePos(int i) {
        return start.offset(dir.dx * i, 1, dir.dz * i);
    }

    private Vec3 floorTop(int i) {
        BlockPos f = floorPos(i);
        return new Vec3(f.getX() + 0.5, f.getY() + 1.0, f.getZ() + 0.5);
    }

    private Vec3 center(int i) {
        BlockPos p = lanePos(i);
        return new Vec3(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
    }

    private boolean isSource(int i) {
        FluidState fs = mc.level.getFluidState(lanePos(i));
        return fs.getType() == Fluids.WATER && fs.isSource();
    }

    private int firstMissing() {
        for (int i = 0; i < len; i++) if (!isSource(i)) return i;
        return -1;
    }

    private boolean hotbarHas(Item item) {
        for (int i = 0; i < 9; i++) if (mc.player.getInventory().getItem(i).is(item)) return true;
        return false;
    }

    private boolean select(Item item) {
        if (mc.player.getMainHandItem().is(item)) return true;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).is(item)) {
                InvUtils.swap(i, false);
                return true;
            }
        }
        return false;
    }

    private boolean useAt(Vec3 aim) {
        if (mc.player.getEyePosition().distanceTo(aim) > reach.get()) {
            fail("Target is out of reach. Stand closer to the lane or enable auto-walk.");
            return false;
        }
        lookAt(aim);
        mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
        mc.player.swing(InteractionHand.MAIN_HAND);
        return true;
    }

    private void lookAt(Vec3 p) {
        Vec3 eye = mc.player.getEyePosition();
        double dx = p.x - eye.x;
        double dy = p.y - eye.y;
        double dz = p.z - eye.z;
        double flat = Math.sqrt(dx * dx + dz * dz);
        mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90));
        mc.player.setXRot((float) -Math.toDegrees(Math.atan2(dy, flat)));
    }

    private void setState(State s) {
        state = s;
        stateTimer = 0;
        delayTimer = delay.get();
    }

    private void fail(String message) {
        error(message);
        toggle();
    }
}
