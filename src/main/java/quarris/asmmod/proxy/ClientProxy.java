package quarris.asmmod.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import quarris.asmmod.blocks.tiles.TileTextHologram;
import quarris.asmmod.client.tesr.TESRTextHologram;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		ClientRegistry.bindTileEntitySpecialRenderer(TileTextHologram.class, new TESRTextHologram());
	}

	@Override
	public void registerModel(Item item, int meta, ModelResourceLocation location) {
		ModelLoader.setCustomModelResourceLocation(item, meta, location);
	}
}
