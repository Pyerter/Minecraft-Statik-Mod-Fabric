package pyerter.statik.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;
import pyerter.statik.screen.handlers.KitchenStoveStationScreenHandler;

import java.util.List;

public class KitchenStoveStationScreen extends HandledScreen<KitchenStoveStationScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(Statik.MOD_ID, "textures/gui/kitchen_stove_station_gui.png");

    public KitchenStoveStationScreen(KitchenStoveStationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = -10;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // arrow pointing right corners:
        // 176, 14 -> 201, 23

        // arrow pointing left corners:
        // 176, 24 -> 201, 33

        // draw bottom left arrow
        // 40, 53 -> 65, 62
        if (handler.isCrafting(0)) {
            Integer scaledProgress = handler.getScaledProgress(0);
            drawTexture(matrices, x + 40 + 26 - scaledProgress, y + 53, 202 - scaledProgress, 24, scaledProgress, 10);
        }
        // draw top left arrow
        // 40, 11 -> 65, 20
        if (handler.isCrafting(2)) {
            Integer scaledProgress = handler.getScaledProgress(2);
            drawTexture(matrices, x + 40 + 26 - scaledProgress, y + 11, 202 - scaledProgress, 24, scaledProgress, 10);
        }

        // draw bottom right arrow
        // 110, 53 -> 135, 62
        if (handler.isCrafting(1)) {
            Integer scaledProgress = handler.getScaledProgress(1);
            drawTexture(matrices, x + 110, y + 53, 176, 14, scaledProgress, 10);
        }
        // draw top right arrow
        // 110, 11 -> 135, 20
        if (handler.isCrafting(3)) {
            Integer scaledProgress = handler.getScaledProgress(3);
            drawTexture(matrices, x + 110, y + 11, 176, 14, scaledProgress, 10);
        }

        if (handler.hasFuel()) {
            int scaledFuelProgress = handler.getScaledFuelProgress();
            drawTexture(matrices, x + 98, y + 30 + 14 - scaledFuelProgress, 176, 14 - scaledFuelProgress, 14, scaledFuelProgress);
        }
        if (handler.hasFuel()) {
            int scaledFuelProgress = handler.getScaledFuelProgress();
            drawTexture(matrices, x + 64, y + 30 + 14 - scaledFuelProgress, 176, 14 - scaledFuelProgress, 14, scaledFuelProgress);
        }

        // draw top title tab
        drawTexture(matrices, x + 24, y - 20, 0, 168, 128,  20);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
