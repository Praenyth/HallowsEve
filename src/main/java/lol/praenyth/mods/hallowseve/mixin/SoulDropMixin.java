package lol.praenyth.mods.hallowseve.mixin;

import lol.praenyth.mods.hallowseve.item.ModItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class SoulDropMixin {
	@Shadow public abstract ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership);

	@Shadow public abstract ServerWorld getServerWorld();

	@Inject(at = @At("HEAD"), method = "onDeath")
	private void init(DamageSource damageSource, CallbackInfo ci) {
		this.dropItem(new ItemStack(ModItems.SOUL), true, true);
	}
}