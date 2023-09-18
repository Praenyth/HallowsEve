package lol.praenyth.mods;

import lol.praenyth.mods.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HallowsEve implements ModInitializer {

	public static final String MOD_ID = "hallowseve";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		// register items
		ModItems.init();

		LOGGER.info("Done initializing! Welcome to HallowsEve!");

	}
}