package lol.praenyth.mods.hallowseve.mixin;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lol.praenyth.mods.hallowseve.item.ModItems;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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

import java.util.Map;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class SoulEffectsMixin {

    @Shadow private float syncedHealth;
    @Unique
    private int soulCheckTicksLeft = 0; // ticks left on the soul check timer
    @Unique private Multimap<EntityAttribute, EntityAttributeModifier> currentModifier = MultimapBuilder.hashKeys().arrayListValues().build();
    @Unique private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Inject(at = @At("TAIL"), method = "playerTick")
    private void hallowseve$tick(CallbackInfo ci) {

        ++this.soulCheckTicksLeft;

        if (this.soulCheckTicksLeft >= 20) {

            AttributeContainer playerAttributes = player.getAttributes();

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

            }

            // check for bare minimum souls
            if (soulCount >= 5 && soulCount < 100) {

                for (StatusEffect effect : getEffects(soulCount).keySet()) {
                    player.addStatusEffect(
                            new StatusEffectInstance(
                                    effect, 60, getEffects(soulCount).get(effect), false, false, true
                            )
                    );
                }

            }


            if (this.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH) <= 20) {

                if (soulCount >= 20 && soulCount < 75) {

                    playerAttributes.addTemporaryModifiers(generateHealthModifier(8));

                } else if (soulCount >= 75) {

                    playerAttributes.addTemporaryModifiers(generateHealthModifier(16));

                }

            }

            if (soulCount < 20) {

                playerAttributes.removeModifiers(this.currentModifier);
                this.currentModifier = MultimapBuilder.hashKeys().arrayListValues().build();

            }

        }

    }

    @Unique
    private Map<StatusEffect, Integer> getEffects(int soulCount) {

        int soulEstimate = soulCount / 5;

        return switch (soulEstimate) {
            case 1 -> Map.of(StatusEffects.SPEED, 0);
            case 2 -> Map.of(StatusEffects.HASTE, 0, StatusEffects.SPEED, 0);
            case 3, 4 -> Map.of(StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 0, StatusEffects.SPEED, 0);
            case 5 -> Map.of(StatusEffects.RESISTANCE, 0, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 0, StatusEffects.SPEED, 0);
            case 6 -> Map.of(StatusEffects.RESISTANCE, 0, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 0, StatusEffects.SPEED, 1);
            case 7 -> Map.of(StatusEffects.RESISTANCE, 0, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
            case 8 -> Map.of(StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 0, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
            case 9 -> Map.of(StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 0, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
            case 10 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 0, StatusEffects.STRENGTH, 0, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
            case 11 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 0, StatusEffects.STRENGTH, 1, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
            case 12 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 2, StatusEffects.STRENGTH, 1, StatusEffects.HASTE, 2, StatusEffects.SPEED, 1);
            case 13 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 2, StatusEffects.STRENGTH, 1, StatusEffects.HASTE, 2, StatusEffects.SPEED, 3);
            case 14, 15 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 2, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 2, StatusEffects.SPEED, 3);
            case 16 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 2, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3);
            case 17 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 2, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3, StatusEffects.WATER_BREATHING, 0);
            case 18 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 2, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3,StatusEffects.WATER_BREATHING, 0, StatusEffects.FIRE_RESISTANCE, 0);
            case 19 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 4, StatusEffects.STRENGTH, 2, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3,StatusEffects.WATER_BREATHING, 0, StatusEffects.FIRE_RESISTANCE, 0);
            case 20 -> Map.of(StatusEffects.SATURATION, 0, StatusEffects.REGENERATION, 1, StatusEffects.DOLPHINS_GRACE, 1, StatusEffects.RESISTANCE, 4, StatusEffects.STRENGTH, 5, StatusEffects.HASTE, 3, StatusEffects.SPEED, 3,StatusEffects.WATER_BREATHING, 0, StatusEffects.FIRE_RESISTANCE, 0);
            default -> Map.of();
        };
    }

    @Unique
    private Multimap<EntityAttribute, EntityAttributeModifier> generateHealthModifier(double healthModifier) {

        Multimap<EntityAttribute, EntityAttributeModifier> playerAttributeModifiers = MultimapBuilder.hashKeys().arrayListValues().build();

        playerAttributeModifiers.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(
                "Health Boost", healthModifier, EntityAttributeModifier.Operation.ADDITION
        ));

        this.currentModifier = playerAttributeModifiers;

        return playerAttributeModifiers;

    }

}
