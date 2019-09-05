package quarris.asmmod.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import quarris.asmmod.ASMMod;
import quarris.asmmod.blocks.tiles.base.ASMTileEntity;
import quarris.asmmod.blocks.tiles.base.TileProcessor;
import quarris.asmmod.utils.ModUtils;

import java.util.Random;

public class ASMBlock extends Block {
	
	public ASMBlock(Material blockMaterialIn, MapColor blockMapColorIn, String registryName) {
		super(blockMaterialIn, blockMapColorIn);
		setRegistryName(ModUtils.createRes(registryName));
		setUnlocalizedName(getRegistryName().toString());
	}

	public Block register() {
		ForgeRegistries.BLOCKS.register(this);

		ItemBlock ib = new ItemBlock(this);
		ib.setRegistryName(this.getRegistryName());
		ib.setUnlocalizedName(this.getUnlocalizedName());
		ForgeRegistries.ITEMS.register(ib);

		ASMMod.proxy.registerModel(ib, 0, new ModelResourceLocation(ib.getRegistryName(), "inventory"));
		ASMMod.proxy.registerModel(new ItemStack(this).getItem(), 0, new ModelResourceLocation(getRegistryName(), "inventory"));

		return this;
	}
}
