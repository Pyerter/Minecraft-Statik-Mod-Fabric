package pyerter.statik.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;
import pyerter.statik.screen.handlers.FoodPreppingStationScreenHandler;
import pyerter.statik.util.Util;

import java.util.List;

public class FoodPreppingStationScreen extends HandledScreen<FoodPreppingStationScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(Statik.MOD_ID, "textures/gui/food_prepping_station_gui.png");

    private boolean holdingMouse = false;

    public FoodPreppingStationScreen(FoodPreppingStationScreenHandler handler, PlayerInventory inventory, Text title) {
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

        // draw top title tab
        drawTexture(matrices, x + 24, y - 20, 0, 179, 128,  20);

        if (handler.toolSlotOccupied(0)) {
            drawTexture(matrices, x + 141, y + 53, 176, 48, 16, 16);
        }
        if (handler.toolSlotOccupied(1)) {
            drawTexture(matrices, x + 8, y + 11, 176, 48, 16, 16);
        }
        if (handler.toolSlotOccupied(2)) {
            drawTexture(matrices, x + 8, y + 32, 176, 48, 16, 16);
        }
        if (handler.toolSlotOccupied(3)) {
            drawTexture(matrices, x + 8, y + 53, 176, 48, 16, 16);
        }

        if (mouseInBowl(x, y, mouseX, mouseY)) {
            // Draw hammer symbol depending on mouse state
            if (holdingMouse) {
                // Hammer symbol
                drawTexture(matrices, x + 137, y + 4, 176, 0, 24, 24);

                // Draw tooltip for hammer time!
                List<Text> chargeText = List.of(MutableText.of(new LiteralTextContent("OH YEAH!")).formatted(Formatting.WHITE, Formatting.ITALIC));
                renderTooltip(matrices, chargeText, mouseX + 20, mouseY);
            } else {
                // Hammer symbol
                drawTexture(matrices, x + 137, y + 4, 176, 24, 24, 24);

                // Draw tooltip for hammer time!
                List<Text> chargeText = List.of(MutableText.of(new LiteralTextContent("Got Milk?")).formatted(Formatting.GRAY, Formatting.ITALIC));
                renderTooltip(matrices, chargeText, mouseX + 20, mouseY);
            }
        }

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        boolean bowlClicked = mouseInBowl(x, y, mouseX, mouseY);

        if (bowlClicked) {
            Statik.logInfo("Clicking prep!");
            Boolean successStatus = handler.tryCraft();
            if (hasShiftDown())
                this.client.interactionManager.clickButton(((FoodPreppingStationScreenHandler)this.handler).syncId, 1);
            else
                this.client.interactionManager.clickButton(((FoodPreppingStationScreenHandler)this.handler).syncId, 0);
            holdingMouse = true;
            return successStatus;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean mouseInBowl(int x, int y, double mouseX, double mouseY) {
        return Util.pointInRectangleByCorners(mouseX, mouseY, x + 138, y + 5, x + 159, y + 26);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        holdingMouse = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
