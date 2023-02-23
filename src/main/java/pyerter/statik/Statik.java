package pyerter.statik;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pyerter.statik.block.ModBlocks;
import pyerter.statik.block.entity.ModBlockEntities;
import pyerter.statik.item.ModItemGroup;
import pyerter.statik.item.ModItems;
import pyerter.statik.loottables.ModLootTableModifiers;
import pyerter.statik.particle.ModParticles;
import pyerter.statik.recipe.ModRecipes;
import pyerter.statik.screen.ModScreens;
import pyerter.statik.screen.handlers.ModScreenHandlers;
import pyerter.statik.world.gen.ModWorldGen;

public class Statik implements ModInitializer {
	public static final String MOD_ID = "statik";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("statik");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Statik > Beginning initialization");

		// Register item groups
		ModItemGroup.registerItemGroups();

		// Reigster items
		ModItems.registerModItems();

		// Register blocks
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerAllBlockEntities();

		// Register particles
		ModParticles.registerParticles();

		// Register data
		ModRecipes.registerRecipes();
		ModLootTableModifiers.modifyLootTables();

		// Register custom screen handlers
		ModScreenHandlers.registerAllScreenHandlers();

		// Initialize world gen
		ModWorldGen.initializeWorldGen();
	}

	public static void logInfo(String message) {
		LOGGER.info(MOD_ID + " --> " + message);
	}

	public static void logDebug(String message) {
		LOGGER.debug(MOD_ID + " --> " + message);
	}
}