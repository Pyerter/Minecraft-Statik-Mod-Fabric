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
import pyerter.statik.screen.handlers.CaptureChamberScreenHandler;

import java.util.List;

public class CaptureChamberScreen extends HandledScreen<CaptureChamberScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(Statik.MOD_ID, "textures/gui/capture_chamber_gui.png");

    public CaptureChamberScreen(CaptureChamberScreenHandler handler, PlayerInventory inventory, Text title) {
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

        if (handler.hasCharge()) {
            int scaledChargeProgress = handler.getScaledChargeProgress();
            drawTexture(matrices, x + 144, y + 10 + 60 - scaledChargeProgress, 176, 60 - scaledChargeProgress, 20, scaledChargeProgress);
        }

        // draw top title tab
        drawTexture(matrices, x + 24, y - 20, 0, 179, 128,  20);

        if (mouseX >= x + 144 && mouseX <= x + 144 + 20 && mouseY >= y + 10 && mouseY <= y + 10 + 60) {
            List<Text> chargeText = List.of(MutableText.of(new LiteralTextContent("Charge : " + handler.getCharge())).formatted(Formatting.AQUA));
            renderTooltip(matrices, chargeText, mouseX, mouseY);
        }

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}