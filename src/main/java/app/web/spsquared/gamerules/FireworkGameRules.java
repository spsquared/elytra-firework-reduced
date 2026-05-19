package app.web.spsquared.gamerules;

import org.jspecify.annotations.NonNull;
import app.web.spsquared.Version;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class FireworkGameRules {
    public static final @NonNull GameRule<Double> FIREWORK_POWER = GameRuleBuilder.forDouble(0.25).minValue(0.0).category(GameRuleCategory.PLAYER).buildAndRegister(Identifier.fromNamespaceAndPath(Version.NAMESPACE, "firework_power"));
    public static final @NonNull GameRule<Double> FIREWORK_SPEED = GameRuleBuilder.forDouble(1.5).minValue(0.0).category(GameRuleCategory.PLAYER).buildAndRegister(Identifier.fromNamespaceAndPath(Version.NAMESPACE, "firework_speed"));
    public static final @NonNull GameRule<Double> FIREWORK_TIME = GameRuleBuilder.forDouble(0.5).minValue(0.0).category(GameRuleCategory.PLAYER).buildAndRegister(Identifier.fromNamespaceAndPath(Version.NAMESPACE, "firework_time"));

    public static void init() {}
}
