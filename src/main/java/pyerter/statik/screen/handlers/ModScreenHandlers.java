package pyerter.statik.screen.handlers;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;

public class ModScreenHandlers {
    public static ScreenHandlerType<CaptureChamberScreenHandler> CAPTURE_CHAMBER_SCREEN_HANDLER;

    public static ScreenHandlerType<PautschItemScreenHandler> PAUTSCH_ITEM_SCREEN_HANDLER;

    public static ScreenHandlerType<EngineeringStationScreenHandler> ENGINEERING_STATION_SCREEN_HANDLER;

    public static ScreenHandlerType<FoodPreppingStationScreenHandler> FOOD_PREPPING_STATION_SCREEN_HANDLER;

    public static ScreenHandlerType<KitchenStoveStationScreenHandler> KITCHEN_STOVE_STATION_SCREEN_HANDLER;

    public static ScreenHandlerType<PlayerScreenHandler> PLAYER_SCREEN_HANDLER;

    public static ScreenHandlerType<TridiScreenHandler> TRIDI_SCREEN_HANDLER;

    public static void registerAllScreenHandlers() {
        CAPTURE_CHAMBER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Statik.MOD_ID, "capture_chamber"), CaptureChamberScreenHandler::new);
        //ACCESSORIES_INVENTORY_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(statik.MOD_ID, "accessories_inventory"),
        //        (id, inv) -> new AccessoryInventoryScreenHandler(id, inv, ((IAccessoriesInventory)inv.player).getAccessoriesInventory()));
        ENGINEERING_STATION_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Statik.MOD_ID, "engineering_station"), EngineeringStationScreenHandler::new);
        FOOD_PREPPING_STATION_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Statik.MOD_ID, "food_prepping_station"), FoodPreppingStationScreenHandler::new);
        KITCHEN_STOVE_STATION_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Statik.MOD_ID, "kitchen_stove_station"), KitchenStoveStationScreenHandler::new);
        TRIDI_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Statik.MOD_ID, "tridi"), TridiScreenHandler::new);
        PLAYER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("inventory"), (id, inv) -> new PlayerScreenHandler(inv, false, inv.player));
    }
}
