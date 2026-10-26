package com.example.addon;

import com.example.addon.modules.LaneWater;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Lane Water");
    // Kept so the template's example HUD class still compiles if you leave it in.
    public static final HudGroup HUD_GROUP = new HudGroup("Lane Water");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Lane Water addon");
        Modules.get().add(new LaneWater());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }
}
