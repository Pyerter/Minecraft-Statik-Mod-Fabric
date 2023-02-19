package pyerter.statik.screen.handlers;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;

public class ModScreenHandlers {
    public static ScreenHandlerType<CaptureChamberScreenHandler> CAPTURE_CHAMBER_SCREEN_HANDLER;

    public static ScreenHandlerType<PlayerScreenHandler> PLAYER_SCREEN_HANDLER;

    public static void registerAllScreenHandlers() {
        CAPTURE_CHAMBER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Statik.MOD_ID, "capture_chamber"), CaptureChamberScreenHandler::new);
        //ACCESSORIES_INVENTORY_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(statik.MOD_ID, "accessories_inventory"),
        //        (id, inv) -> new AccessoryInventoryScreenHandler(id, inv, ((IAccessoriesInventory)inv.player).getAccessoriesInventory()));
        PLAYER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("inventory"), (id, inv) -> new PlayerScreenHandler(inv, false, inv.player));
    }
}
