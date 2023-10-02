package lol.praenyth.mods.hallowseve.mixin;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lol.praenyth.mods.hallowseve.HallowsEve;
import lol.praenyth.mods.hallowseve.item.ModItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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

			if (this.soulDropTicksLeft >= 30000) {
				playersWithoutSouls.add(player.getUuidAsString());
				this.soulDropTicksLeft = 0;
			}

		}

	}

}