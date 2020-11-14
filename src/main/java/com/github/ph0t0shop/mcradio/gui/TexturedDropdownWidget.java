package com.github.ph0t0shop.mcradio.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class TexturedDropdownWidget extends ButtonWidget {
    private ArrayList<GuiTexture> options;
    public static final Identifier RADIO_SOURCES_TEXTURE = new Identifier("mcradio", "textures/gui/radio_sources.png");
    private int selectedIndex;
    private final int textureWidth;
    private final int textureHeight;
    private boolean expanded = false;

    public TexturedDropdownWidget(int x, int y, int width, int height, ArrayList<GuiTexture> textures, ButtonWidget.PressAction pressAction) {
        this(x, y, width, height, textures, 256, 256, pressAction);
    }

    public TexturedDropdownWidget(int x, int y, int width, int height, ArrayList<GuiTexture> textures, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction) {
        this(x, y, width, height, textures, textureWidth, textureHeight, pressAction, LiteralText.EMPTY);
    }

    public TexturedDropdownWidget(int x, int y, int width, int height, ArrayList<GuiTexture> textures, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction, Text text) {
        super(x, y, width, height, text, pressAction);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.options = textures;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.width) && (mouseY < (double)(this.y + this.height) || (expanded && mouseY < this.y + this.height + 20*(this.options.size())));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress.onPress(this);
        int clickedIndex = selectedIndex((int)mouseX, (int)mouseY);
        if (clickedIndex > 0) {
            this.expanded = false;
            this.selectedIndex = clickedIndex - 1;
        } else {
            this.expanded = !this.expanded;
        }
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        minecraftClient.getTextureManager().bindTexture(RADIO_SOURCES_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int vOffset = this.isHovered() ? 20 : 0;
        int rightRenderWidth = Math.max(13, this.width / 2);
        int leftRenderWidth = this.width - rightRenderWidth;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, this.x, this.y, 0, vOffset, leftRenderWidth, this.height);
        this.drawTexture(matrices, this.x + leftRenderWidth, this.y, 200 - rightRenderWidth, vOffset, rightRenderWidth, this.height);
        this.renderBg(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 0xFFFFFF : 0xA0A0A0;
        drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);


        ////////////////////////////////////////
        GuiTexture selectedTexture = this.options.get(selectedIndex);
        minecraftClient.getTextureManager().bindTexture(selectedTexture.id);

        int arrowRenderWidth = Math.min(20, this.width);

        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, this.x + (this.width - arrowRenderWidth) / 2 - this.textureWidth / 2, this.y, selectedTexture.u, selectedTexture.v, this.textureWidth, this.textureHeight);

        if (this.expanded) {
            fill(matrices, this.x, this.y + this.height, this.x + this.width, this.y + this.height + this.textureHeight * this.options.size(), 0x80000000);
            for (int i = 0; i < this.options.size(); i++) {
                GuiTexture texture = this.options.get(i);
                minecraftClient.getTextureManager().bindTexture(texture.id);
                this.drawTexture(matrices, this.x + (this.width - arrowRenderWidth) / 2 - this.textureWidth / 2, this.y + (i+1) * this.textureHeight, texture.u, texture.v, this.textureWidth, this.textureHeight);
            }
            int hoveredIndex = selectedIndex(mouseX, mouseY);
            if (hoveredIndex > -1) {
                drawHorizontalLine(matrices, this.x, this.x + this.width, this.y + 20 * hoveredIndex, 0xFFFFFFFF);
                drawHorizontalLine(matrices, this.x, this.x + this.width, this.y + 20 + 20 * hoveredIndex, 0xFFFFFFFF);
                drawVerticalLine(matrices, this.x, this.y + 20 * hoveredIndex, this.y + this.height + 20 * hoveredIndex, 0xFFFFFFFF);
                drawVerticalLine(matrices, this.x + this.width, this.y + 20 * hoveredIndex, this.y + this.height + 20 * hoveredIndex, 0xFFFFFFFF);
            }
        }
    }

    private int selectedIndex(int mouseX, int mouseY) {
        int hoveredIndex = (mouseY - this.y) / 20;
        if (hoveredIndex > this.options.size() || hoveredIndex < 1 || mouseX < this.x || mouseX > this.x + this.width) {
            hoveredIndex = -1;
        }
        return hoveredIndex;
    }

    public String getPrefix () {
        return this.options.get(this.selectedIndex).sourcePrefix;
    }

    public void selectOption(String url) {
        int longestMatch = 0;
        int longestMatchIndex = 2;

        for (int i = 0; i < this.options.size(); i++) {
            String prefix = options.get(i).sourcePrefix;
            if (url.startsWith(prefix)) {
                int length = prefix.length();
                if (length >= longestMatch) {
                    longestMatch = length;
                    longestMatchIndex = i;
                }
            }
        }
        this.selectedIndex = longestMatchIndex;
    }

    public static class GuiTexture {
        private Identifier id;
        private int u;
        private int v;
        private String sourcePrefix;

        public GuiTexture(Identifier id, int u, int v, String sourcePrefix) {
            this.id = id;
            this.u = u;
            this.v = v;
            this.sourcePrefix = sourcePrefix;
        }
    }
}
