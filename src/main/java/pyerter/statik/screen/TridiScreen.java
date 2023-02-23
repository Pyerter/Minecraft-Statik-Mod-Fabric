package pyerter.statik.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;
import pyerter.statik.screen.handlers.TridiScreenHandler;

import java.util.List;

public class TridiScreen extends HandledScreen<TridiScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(Statik.MOD_ID, "textures/gui/tridi_gui.png");

    public TridiScreen(TridiScreenHandler handler, PlayerInventory inventory, Text title) {
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

        // 25, 48 is bottom left corner of bottom arrow set
        // 85, 26 is top left corner of right arrow set
        // bottom arrow: x = [1, 100], y = [169, 177]
        // top arrow: x = [125, 176], y = [169, 178]
        if (handler.isCrafting()) {
            List<Integer> scaledProgress = handler.getScaledProgress();
            // draw bottom arrow texture
            drawTexture(matrices, x + 24, y + 48 - scaledProgress.get(0), 0, 177 - scaledProgress.get(0), 100, scaledProgress.get(0));

            if (scaledProgress.get(1) > 0) {
                drawTexture(matrices, x + 84, y + 25, 124, 168, scaledProgress.get(1), 10);
            }
        }

        if (handler.hasFuel()) {
            int scaledFuelProgress = handler.getScaledFuelProgress();
            drawTexture(matrices, x + 68, y + 7 + 14 - scaledFuelProgress, 176, 14 - scaledFuelProgress, 14, scaledFuelProgress);
        }

        // draw top title tab
        drawTexture(matrices, x + 24, y - 20, 0, 179, 128,  20);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
