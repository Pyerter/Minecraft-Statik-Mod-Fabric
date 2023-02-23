package pyerter.statik.screen;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import pyerter.statik.screen.handlers.ModScreenHandlers;

public class ModScreens {

    public static void registerScreens() {
        ScreenRegistry.register(ModScreenHandlers.CAPTURE_CHAMBER_SCREEN_HANDLER, CaptureChamberScreen::new);
        ScreenRegistry.register(ModScreenHandlers.ENGINEERING_STATION_SCREEN_HANDLER, EngineeringStationScreen::new);
        ScreenRegistry.register(ModScreenHandlers.FOOD_PREPPING_STATION_SCREEN_HANDLER, FoodPreppingStationScreen::new);
        ScreenRegistry.register(ModScreenHandlers.KITCHEN_STOVE_STATION_SCREEN_HANDLER, KitchenStoveStationScreen::new);
        ScreenRegistry.register(ModScreenHandlers.TRIDI_SCREEN_HANDLER, TridiScreen::new);
    }

    /*public static void registerAccessoryTabScreens() {
        AccessoryTabAssistant.tryRegisterScreens(ModScreenHandlers.ACCESSORIES_INVENTORY_SCREEN_HANDLER,
                new Pair<>((player) -> new AccessoryInventoryScreen(player),
                        (inventory, onServer, owner) -> new AccessoryInventoryScreenHandler(inventory, onServer, owner)),
                Text.of("Accessory Inventory"));
    }*/

}
