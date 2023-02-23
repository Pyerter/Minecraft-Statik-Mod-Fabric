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
import net.minecraft.util.Pair;
import pyerter.statik.Statik;
import pyerter.statik.screen.handlers.EngineeringStationScreenHandler;
import pyerter.statik.util.Util;

import java.util.List;

public class EngineeringStationScreen extends HandledScreen<EngineeringStationScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(Statik.MOD_ID, "textures/gui/engineering_station_gui.png");

    private boolean holdingMouse = false;

    public EngineeringStationScreen(EngineeringStationScreenHandler handler, PlayerInventory inventory, Text title) {
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

        if (mouseInHammer(x, y, mouseX, mouseY)) {
            // Draw hammer symbol depending on mouse state
            if (holdingMouse) {
                // Hammer symbol
                drawTexture(matrices, x + 136, y + 14, 176, 131, 199 - 176 + 1, 181 - 131 + 1);

                // Draw grear
                if (handler.getHammerStage() == 0 && handler.getSuccess())
                    drawGear(matrices, x, y, 3);
                else if (handler.getHammerStage() == 1)
                    drawGear(matrices, x, y, 1);
                else if (handler.getHammerStage() == 2)
                    drawGear(matrices, x, y, 3);

                // Draw tooltip for hammer time!
                List<Text> chargeText = List.of(MutableText.of(new LiteralTextContent("HAMMER TIME!!")).formatted(Formatting.WHITE, Formatting.ITALIC));
                renderTooltip(matrices, chargeText, mouseX + 20, mouseY);
            } else {
                // Hammer symbol
                drawTexture(matrices, x + 136, y + 14, 176, 80, 199 - 176 + 1, 130 - 80 + 1);

                // Draw grear
                if (handler.getHammerStage() == 0)
                    drawGear(matrices, x, y, 0);
                else if (handler.getHammerStage() == 1)
                    drawGear(matrices, x, y, 2);

                // Draw tooltip for hammer time!
                List<Text> chargeText = List.of(MutableText.of(new LiteralTextContent("Hammer Time?")).formatted(Formatting.GRAY, Formatting.ITALIC));
                renderTooltip(matrices, chargeText, mouseX + 20, mouseY);
            }
        } else {
            // Draw grear
            if (!holdingMouse) {
                if (handler.getHammerStage() == 0)
                    drawGear(matrices, x, y,0);
                else if (handler.getHammerStage() == 1)
                    drawGear(matrices, x, y, 2);
            }
        }

    }

    public void drawGear(MatrixStack matrices, int x, int y, int gearStage) {
        gearStage = gearStage % 4;
        int gearWidth = 22;
        int gearHeight = 20;
        int targetX = 75 + x;
        int targetY = 30 + y;
        int gearX = 176;
        int gearY = 0;
        switch (gearStage) {
            case 0: default: break;
            case 1: drawTexture(matrices, targetX, targetY, gearX, gearY, gearWidth, gearHeight); break;
            case 2: drawTexture(matrices, targetX, targetY, gearX, gearY + gearHeight, gearWidth, gearHeight); break;
            case 3: drawTexture(matrices, targetX, targetY, gearX, gearY + gearHeight * 2, gearWidth, gearHeight); break;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        boolean hammerClicked = mouseInHammer(x, y, mouseX, mouseY);

        if (hammerClicked) {
            Statik.logInfo("Clicking hammer!");
            Pair<Boolean, Boolean> successStatus = handler.tryHammer();
            if (hasShiftDown())
                this.client.interactionManager.clickButton(((EngineeringStationScreenHandler)this.handler).syncId, 1);
            else
                this.client.interactionManager.clickButton(((EngineeringStationScreenHandler)this.handler).syncId, 0);
            holdingMouse = true;
            return successStatus.getLeft();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean mouseInHammer(int x, int y, double mouseX, double mouseY) {
        return Util.pointInRectangleByCorners(mouseX, mouseY, x + 136, y + 14, x + 159, y + 64);
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
