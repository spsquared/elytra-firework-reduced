package app.web.spsquared.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import app.web.spsquared.ElytraFireworkReduced;
import app.web.spsquared.network.GameRuleMirror;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {

    @Shadow
    private LivingEntity attachedToEntity;
    @Shadow
    private int lifetime;

    private boolean isPlayerWithMod(LivingEntity entity) {
        return entity != null && (!(entity instanceof Player) || ElytraFireworkReduced.playersWithMod.containsKey(entity.getUUID()));
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    private void reduceAttachedFireworkLife(final Level level, final ItemStack sourceItemStack, final LivingEntity stuckTo, CallbackInfo callbackInfo) {
        if (ElytraFireworkReduced.enabled && isPlayerWithMod(stuckTo))
            this.lifetime = (int) Mth.ceil(lifetime * GameRuleMirror.get().fireworkTimeMultiplier());
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add"))
    private Vec3 reduceAttachedFireworkPower(Vec3 instance, double dx, double dy, double dz, Operation<Vec3> original) {
        // this is run on the server and client, so we have to be careful what the checks are
        // the client adds itself to the list in the case that this isn't an integrated server
        // and the server otherwise keeps track of what players need this to avoid rubberbanding
        if (ElytraFireworkReduced.enabled && this.attachedToEntity != null && isPlayerWithMod(this.attachedToEntity)) {
            Vec3 lookAngle = this.attachedToEntity.getLookAngle();
            Vec3 movement = this.attachedToEntity.getDeltaMovement();
            // vanilla is weird so i replaced all of it
            double power = GameRuleMirror.get().fireworkPower();
            double speed = GameRuleMirror.get().fireworkSpeed() / 20; // convert to blocks per tick
            // min(power, speed) prevents huge boosts from standstill before power reduction happens
            // acceleration decreases linearly until max speed is reached, then becomes 0
            return movement.add(lookAngle.scale(Math.min(power, speed) * Math.max(0, 1 - movement.length() / speed)));
        }
        // this is cursed
        return original.call(instance, dx, dy, dz);
    }
}
