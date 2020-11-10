package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.mixin.RRMIMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ExampleMod implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");

		// new PositionedRemoteSoundInstance(new Identifier("remote", "https://www.youtube.com/watch?v=k0dMSDwZj2g"), SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);
	}

	public static void onResourceLoad() {
		((RRMIMixin)MinecraftClient.getInstance().getResourceManager()).getNamespaceManagers().put("remote", new RemoteResourceManager());
	}
}
