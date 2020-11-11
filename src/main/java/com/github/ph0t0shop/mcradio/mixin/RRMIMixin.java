package com.github.ph0t0shop.mcradio.mixin;

import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ReloadableResourceManagerImpl.class)
public interface RRMIMixin {
    @Accessor(value="namespaceManagers")
    Map<String, NamespaceResourceManager> getNamespaceManagers();
}
