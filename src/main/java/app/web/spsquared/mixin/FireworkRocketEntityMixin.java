package app.web.spsquared.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
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

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {

    @Shadow
    private LivingEntity attachedToEntity;
    @Shadow
    private int lifetime;

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    private void reduceAttachedFireworkLife(final Level level, final ItemStack sourceItemStack, final LivingEntity stuckTo, CallbackInfo callbackInfo) {
        this.lifetime = (int) Mth.ceil(lifetime * 0.5);
    }

    // @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement"))
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add"))
    private Vec3 reduceAttachedFireworkPower(Vec3 instance, double dx, double dy, double dz, Operation<Vec3> original) {
        if (this.attachedToEntity != null) {
            Vec3 lookAngle = this.attachedToEntity.getLookAngle();
            Vec3 movement = this.attachedToEntity.getDeltaMovement();
            // power is target speed (drag but it can pull you faster too) and powerAdd is force
            double power = 1.5; // vanilla 1.5
            double powerAdd = 0.02; // vanilla 0.1
            double acceleration = 0.05; // vanilla 0.5
            return movement.add(
                lookAngle.x * powerAdd + (lookAngle.x * power - movement.x) * acceleration,
                lookAngle.y * powerAdd + (lookAngle.y * power - movement.y) * acceleration,
                lookAngle.z * powerAdd + (lookAngle.z * power - movement.z) * acceleration
            );
        }
        // this is cursed
        return original.call(instance, dx, dy, dz);
    }
}
