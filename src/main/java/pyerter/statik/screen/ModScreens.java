package pyerter.statik.screen;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import pyerter.statik.screen.handlers.ModScreenHandlers;

public class ModScreens {

    public static void registerScreens() {
        ScreenRegistry.register(ModScreenHandlers.CAPTURE_CHAMBER_SCREEN_HANDLER, CaptureChamberScreen::new);
    }

    /*public static void registerAccessoryTabScreens() {
        AccessoryTabAssistant.tryRegisterScreens(ModScreenHandlers.ACCESSORIES_INVENTORY_SCREEN_HANDLER,
                new Pair<>((player) -> new AccessoryInventoryScreen(player),
                        (inventory, onServer, owner) -> new AccessoryInventoryScreenHandler(inventory, onServer, owner)),
                Text.of("Accessory Inventory"));
    }*/

}
