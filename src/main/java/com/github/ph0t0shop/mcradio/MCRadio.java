package com.github.ph0t0shop.mcradio;

import com.github.ph0t0shop.mcradio.audio.RadioResourceManager;
import com.github.ph0t0shop.mcradio.block.RadioBlock;
import com.github.ph0t0shop.mcradio.block.RadioBlockEntity;
import net.fabricmc.api.ModInitializer;
import com.github.ph0t0shop.mcradio.mixin.RRMIMixin;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class MCRadio implements ModInitializer {

	private static final String MOD_ID = "mcradio";

	public static final Identifier UPDATE_RADIO_ADDRESS_PACKET_ID = new Identifier(MOD_ID, "update_radio");

	public static final Block RADIO_BLOCK = new RadioBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	public static BlockEntityType<RadioBlockEntity> RADIO_BLOCK_ENTITY;


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "radio_block"), RADIO_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "radio_block"), new BlockItem(RADIO_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
		RADIO_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mcradio:radio", BlockEntityType.Builder.create(RadioBlockEntity::new, RADIO_BLOCK).build(null));

		ServerSidePacketRegistry.INSTANCE.register(UPDATE_RADIO_ADDRESS_PACKET_ID, (packetContext, packetByteBuf) -> {
			BlockPos radioPos = packetByteBuf.readBlockPos();
			String newRadioUrl = packetByteBuf.readString();
			packetContext.getTaskQueue().execute(() -> {
				BlockEntity be = packetContext.getPlayer().world.getBlockEntity(radioPos);
				if (!(be instanceof RadioBlockEntity)) {
					return;
				}
				RadioBlockEntity rbe = (RadioBlockEntity) be;
				rbe.setUrl(newRadioUrl, true);
			});
		});

		// new RadioSoundInstance(new Identifier("mcradio_remote", "https://www.youtube.com/watch?v=k0dMSDwZj2g"), SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);
	}

	public static void onResourceLoad() {
		((RRMIMixin)MinecraftClient.getInstance().getResourceManager()).getNamespaceManagers().put("mcradio_remote", RadioResourceManager.getInstance());
	}
}
