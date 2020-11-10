package net.fabricmc.example.mixin;

import net.fabricmc.example.PCMAudioStream;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.sound.sampled.AudioInputStream;
import java.io.InputStream;

@Mixin(SoundLoader.class)
public class SoundLoaderMixin {
    @Inject(method="method_19745", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/resource/Resource;getInputStream()Ljava/io/InputStream;"), locals= LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    public void loadStreamedInject(Identifier identifier, boolean arg1, CallbackInfoReturnable<AudioStream> cir, Resource resource, InputStream inputStream) {
        if (identifier.getNamespace().equals("remote")) {
            cir.setReturnValue(new PCMAudioStream((AudioInputStream) inputStream));
        }
    }
}
