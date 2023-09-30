package lol.praenyth.mods.hallowseve.mixin;

import lol.praenyth.mods.hallowseve.HallowsEve;
import lol.praenyth.mods.hallowseve.item.ModItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ServerPlayerEntity.class)
public abstract class SoulDropMixin {

	@Unique private int soulCheckTicksLeft = 0; // ticks left on the soul check timer
	@Unique private int soulDropTicksLeft = 0; // ticks left on the soul drop timer
	@Unique private static List<String> playersWithoutSouls = new ArrayList<>();
	@Unique private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
	@Shadow public abstract ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership);

	@Inject(at = @At("HEAD"), method = "onDeath")
	private void hallowseve$onDeath(DamageSource damageSource, CallbackInfo ci) {

		if (playersWithoutSouls.contains(player.getUuidAsString())) {
			player.dropItem(new ItemStack(ModItems.SOUL), true, true);
			playersWithoutSouls.remove(player.getUuidAsString());
		}

	}

	@Inject(at = @At("TAIL"), method = "playerTick")
	private void hallowseve$tick(CallbackInfo ci) {

		if (!playersWithoutSouls.contains(player.getUuidAsString())) {
			++this.soulDropTicksLeft; // adding this, so it actually increments, despite not being in the death method

			if (this.soulDropTicksLeft >= 100) {
				playersWithoutSouls.add(player.getUuidAsString());
				this.soulDropTicksLeft = 0;
			}

		}

		++this.soulCheckTicksLeft;

		if (this.soulCheckTicksLeft >= 10) {

			// check for souls in inventory
			int soulCount = 0;
			this.soulCheckTicksLeft = 0;

			for (ItemStack item : player.getInventory().main) {
				if (item.getItem() == ModItems.SOUL) {
					soulCount += item.getCount();
				}
			}

			// check for max souls
			if (soulCount >= 100) {

				for (StatusEffect effect : getEffects(100).keySet()) {
					player.addStatusEffect(
							new StatusEffectInstance(
									effect, 60, getEffects(100).get(effect), false, false, true
							)
					);
				}
				return;

			}

			// check for bare minimum souls
			if (soulCount >= 5) {

				for (StatusEffect effect : getEffects(soulCount).keySet()) {
					player.addStatusEffect(
							new StatusEffectInstance(
									effect, 60, getEffects(soulCount).get(effect), false, false, true
							)
					);
				}

			}

		}

	}

	@Unique
	private Map<StatusEffect, Integer> getEffects(int soulCount) {

		int soulEstimate = soulCount / 5;

		return switch (soulEstimate) {
			case 1 -> Map.of(StatusEffects.SPEED, 0);
			case 2 -> Map.of(StatusEffects.HASTE, 0, StatusEffects.SPEED, 0);
			case 3 -> Map.of(StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 0, StatusEffects.SPEED, 0);
			case 4 -> Map.of(StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 0, StatusEffects.SPEED, 0);
			case 5 -> Map.of(StatusEffects.RESISTANCE, 0, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 0, StatusEffects.SPEED, 0);
			case 6 -> Map.of(StatusEffects.RESISTANCE, 0, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 0, StatusEffects.SPEED, 1);
			case 7 -> Map.of(StatusEffects.RESISTANCE, 0, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
			case 8 -> Map.of(StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 0, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
			case 9 -> Map.of(StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 0, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
			case 10 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 0, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
			case 11 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 0, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 1, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
			case 12 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 2, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 1, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
			case 13 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 2, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 1, StatusEffects.HASTE, 2, StatusEffects.SPEED, 3);
			case 14 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 2, StatusEffects.HEALTH_BOOST, 1, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 2, StatusEffects.SPEED, 3);
			case 15 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 2, StatusEffects.HEALTH_BOOST, 3, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 2, StatusEffects.SPEED, 3);
			case 16 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 2, StatusEffects.HEALTH_BOOST, 3, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3);
			case 17 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 2, StatusEffects.HEALTH_BOOST, 3, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3, StatusEffects.WATER_BREATHING, 0);
			case 18 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 2, StatusEffects.HEALTH_BOOST, 3, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3,StatusEffects.WATER_BREATHING, 0, StatusEffects.FIRE_RESISTANCE, 0);
			case 19 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 4, StatusEffects.HEALTH_BOOST, 3, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3,StatusEffects.WATER_BREATHING, 0, StatusEffects.FIRE_RESISTANCE, 0);
			case 20 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.JUMP_BOOST, 1, StatusEffects.RESISTANCE, 4, StatusEffects.HEALTH_BOOST, 3, StatusEffects.STRENGTH, 5, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3,StatusEffects.WATER_BREATHING, 0, StatusEffects.FIRE_RESISTANCE, 0);
			default -> Map.of();
		};
	}

}