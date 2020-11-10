package net.fabricmc.example.mixin;

import net.fabricmc.example.PositionedRemoteSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method="sendChatMessage", at=@At("HEAD"), locals= LocalCapture.CAPTURE_FAILEXCEPTION)
    public void chatMixin(String message, CallbackInfo ci) {
        MinecraftClient.getInstance().getSoundManager().play(new PositionedRemoteSoundInstance(new Identifier("remote", "https://www.youtube.com/watch?v=k0dMSDwZj2g"), SoundCategory.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, 10.0D, 0.0D, 10.0D, false));
    }
}
