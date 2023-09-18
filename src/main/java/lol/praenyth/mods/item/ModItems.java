package lol.praenyth.mods.item;

import lol.praenyth.mods.HallowsEve;
import lol.praenyth.mods.item.items.SoulItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item SOUL = Registry.register(
            Registries.ITEM,
            new Identifier(HallowsEve.MOD_ID, "soul"),
            new SoulItem(new FabricItemSettings())
    );

    public static void init() {
        // This method is called by the mod initializer
    }

}
