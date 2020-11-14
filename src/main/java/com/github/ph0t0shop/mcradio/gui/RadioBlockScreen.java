package com.github.ph0t0shop.mcradio.gui;

import com.github.ph0t0shop.mcradio.MCRadio;
import com.github.ph0t0shop.mcradio.block.RadioBlockEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class RadioBlockScreen extends Screen {
    private static final Text titleText = new TranslatableText("mcradio:radio_gui_title");

    private TextFieldWidget urlTextField;
    private ButtonWidget doneButton;
    private ButtonWidget cancelButton;
    private TexturedDropdownWidget sourceDropdown;
    private static ArrayList<TexturedDropdownWidget.GuiTexture> dropdownTextures = new ArrayList<>();
    private RadioBlockEntity blockEntity;
    private boolean firstOpen = true;

    static {
        dropdownTextures.add(new TexturedDropdownWidget.GuiTexture(TexturedDropdownWidget.RADIO_SOURCES_TEXTURE, 0, 40, "ytsearch:"));
        dropdownTextures.add(new TexturedDropdownWidget.GuiTexture(TexturedDropdownWidget.RADIO_SOURCES_TEXTURE, 0, 60, "scsearch:"));
        dropdownTextures.add(new TexturedDropdownWidget.GuiTexture(TexturedDropdownWidget.RADIO_SOURCES_TEXTURE, 0, 80, ""));
    }

    public RadioBlockScreen(RadioBlockEntity blockEntity) {
        super(NarratorManager.EMPTY);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.urlTextField = this.addChild(new TextFieldWidget(this.textRenderer, this.width / 8 + 50,  40, this.width * 3 / 4 - 50, 20, LiteralText.EMPTY));
        this.urlTextField.setMaxLength(Integer.MAX_VALUE);

        int buttonWidth = Math.min(200, this.width / 2 - 30);

        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - buttonWidth - 10, this.height - 40, buttonWidth, 20, ScreenTexts.DONE, (button -> {
            this.confirmAndClose();
        })));
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 10, this.height - 40, buttonWidth, 20, ScreenTexts.CANCEL, (button -> {
            this.onClose();
        })));
        this.sourceDropdown = this.addButton(new TexturedDropdownWidget(this.width / 8, 40, 50, 20, dropdownTextures, 20, 20, (button -> {
        })));

        this.sourceDropdown.selectOption(blockEntity.getUrl());
        this.urlTextField.setText(blockEntity.getUrl().substring(this.sourceDropdown.getPrefix().length()));

        if (firstOpen) {
            firstOpen = false;
            this.setFocused(this.urlTextField);
            this.urlTextField.setSelected(true);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, titleText, this.width / 2, 20, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        urlTextField.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.urlTextField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 257 || keyCode == 335) { // enter key
            this.confirmAndClose();
            return true;
        } else {
            return false;
        }
    }

    private void confirmAndClose() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeBlockPos(this.blockEntity.getPos());
        packet.writeString(this.sourceDropdown.getPrefix() + this.urlTextField.getText()); // TODO: prefix
        ClientSidePacketRegistry.INSTANCE.sendToServer(MCRadio.UPDATE_RADIO_ADDRESS_PACKET_ID, packet);
        this.onClose();
    }

    @Override
    public void tick() {
        this.urlTextField.tick();
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        return this.getFocused() != null && this.getFocused().charTyped(chr, keyCode);
    }
}
