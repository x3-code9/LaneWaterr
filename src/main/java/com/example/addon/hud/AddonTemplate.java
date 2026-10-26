19s
Run ./gradlew build
Starting a Gradle Daemon (subsequent builds will be faster)
Calculating task graph as no cached configuration is available for tasks: build

> Configure project :
Fabric Loom: 1.14.10

Cannot remap onEntityCollision because it does not exists in any of the targets [net/minecraft/class_2560]
Cannot remap method_9548 because it does not exists in any of the targets [net/minecraft/class_2560]
Cannot remap onEntityCollision because it does not exists in any of the targets [net/minecraft/class_3830]
Cannot remap method_9548 because it does not exists in any of the targets [net/minecraft/class_3830]

> Task :remapSourcesJar SKIPPED
> Task :processTestResources NO-SOURCE
> Task :validateAccessWidener NO-SOURCE
> Task :processIncludeJars
> Task :processResources
Error: /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/hud/LaneWater (1).java:38: error: class LaneWater is public, should be declared in a file named LaneWater.java

public class LaneWater extends Module {
       ^
Error: /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/hud/LaneWater.java:38: error: duplicate class: com.example.addon.modules.LaneWater
public class LaneWater extends Module {
       ^
Error: /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/hud/AddonTemplate (1).java:11: error: class AddonTemplate is public, should be declared in a file named AddonTemplate.java
public class AddonTemplate extends MeteorAddon {
       ^
Error: /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/AddonTemplate.java:16: error: duplicate class: com.example.addon.AddonTemplate
public class AddonTemplate extends MeteorAddon {
       ^
> Task :compileJava
Error: /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/commands/CommandExample.java:11: error: CommandExample is not abstract and does not override abstract method build(LiteralArgumentBuilder<SharedSuggestionProvider>) in Command
public class CommandExample extends Command {
       ^
Error: /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/commands/CommandExample.java:20: error: name clash: build(LiteralArgumentBuilder<ClientSuggestionProvider>) in CommandExample and build(LiteralArgumentBuilder<SharedSuggestionProvider>) in Command have the same erasure, yet neither overrides the other
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
                ^
Error: /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/commands/CommandExample.java:19: error: method does not override or implement a method from a supertype
    @Override
    ^
Error: /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/commands/CommandExample.java:26: error: no suitable method found for then(LiteralArgumentBuilder<SharedSuggestionProvider>)

        builder.then(literal("name").then(argument("nameArgument", StringArgumentType.word()).executes(context -> {
> Task :compileJava FAILED
               ^
    method ArgumentBuilder.then(ArgumentBuilder<ClientSuggestionProvider,?>) is not applicable
      (argument mismatch; LiteralArgumentBuilder<SharedSuggestionProvider> cannot be converted to ArgumentBuilder<ClientSuggestionProvider,?>)
    method ArgumentBuilder.then(CommandNode<ClientSuggestionProvider>) is not applicable
      (argument mismatch; LiteralArgumentBuilder<SharedSuggestionProvider> cannot be converted to CommandNode<ClientSuggestionProvider>)
8 errors
gradle/actions: Writing build results to /home/runner/work/_temp/.gradle-actions/build-results/__run-1793049650854.json


[Incubating] Problems report is available at: file:///home/runner/work/LaneWaterr/LaneWaterr/build/reports/problems/problems-report.html

Deprecated Gradle features were used in this build, making it incompatible with Gradle 10.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/9.8.1/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.
3 actionable tasks: 3 executed
Configuration cache entry stored.
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileJava' (registered by plugin class 'org.gradle.api.plugins.JavaBasePlugin').
> Compilation failed; see the compiler output below.
Error:   /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/hud/LaneWater (1).java:38: error: class LaneWater is public, should be declared in a file named LaneWater.java
  public class LaneWater extends Module {
         ^
Error:   /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/hud/AddonTemplate (1).java:11: error: class AddonTemplate is public, should be declared in a file named AddonTemplate.java
  public class AddonTemplate extends MeteorAddon {
         ^
Error:   /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/hud/LaneWater.java:38: error: duplicate class: com.example.addon.modules.LaneWater
  public class LaneWater extends Module {
         ^
Error:   /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/AddonTemplate.java:16: error: duplicate class: com.example.addon.AddonTemplate
  public class AddonTemplate extends MeteorAddon {
         ^
Error:   /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/commands/CommandExample.java:11: error: CommandExample is not abstract and does not override abstract method build(LiteralArgumentBuilder<SharedSuggestionProvider>) in Command
  public class CommandExample extends Command {
         ^
Error:   /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/commands/CommandExample.java:20: error: name clash: build(LiteralArgumentBuilder<ClientSuggestionProvider>) in CommandExample and build(LiteralArgumentBuilder<SharedSuggestionProvider>) in Command have the same erasure, yet neither overrides the other
      public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
                  ^
Error:   /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/commands/CommandExample.java:19: error: method does not override or implement a method from a supertype
      @Override
      ^
Error:   /home/runner/work/LaneWaterr/LaneWaterr/src/main/java/com/example/addon/commands/CommandExample.java:26: error: no suitable method found for then(LiteralArgumentBuilder<SharedSuggestionProvider>)
          builder.then(literal("name").then(argument("nameArgument", StringArgumentType.word()).executes(context -> {
                 ^
      method ArgumentBuilder.then(ArgumentBuilder<ClientSuggestionProvider,?>) is not applicable
        (argument mismatch; LiteralArgumentBuilder<SharedSuggestionProvider> cannot be converted to ArgumentBuilder<ClientSuggestionProvider,?>)
      method ArgumentBuilder.then(CommandNode<ClientSuggestionProvider>) is not applicable
        (argument mismatch; LiteralArgumentBuilder<SharedSuggestionProvider> cannot be converted to CommandNode<ClientSuggestionProvider>)
  8 errors

* Try:
> Check your code and dependencies to fix the compilation error(s)
> Run with --scan to get full insights from a Build Scan (powered by Develocity).

BUILD FAILED in 18s
Error: Process completed with exit code 1.
