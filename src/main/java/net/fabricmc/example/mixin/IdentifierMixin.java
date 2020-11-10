package net.fabricmc.example.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Identifier.class)
public abstract class IdentifierMixin {
    @Shadow
    private static boolean isPathValid(String path) {
        return false;
    }

    @Redirect(method="<init>([Ljava/lang/String;)V", at=@At(value="INVOKE", target="Lnet/minecraft/util/Identifier;isPathValid(Ljava/lang/String;)Z"))
    private boolean isValidRedirect(String path, String[] id) {
        if (id[0].equals("remote")) return true;
        else return (isPathValid(path));
    }
}
