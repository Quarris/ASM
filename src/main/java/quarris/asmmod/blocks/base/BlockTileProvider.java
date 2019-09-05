package quarris.asmmod.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quarris.asmmod.blocks.tiles.base.ASMTileEntity;
import quarris.asmmod.blocks.tiles.base.TileProcessor;

import java.util.Random;

public abstract class BlockTileProvider extends ASMBlock implements ITileEntityProvider {

	public BlockTileProvider(Material material, MapColor color, String name) {
		super(material, color, name);
		TileEntity.register(getRegistryName().toString(), getTileClass());
	}

	protected abstract Class<? extends ASMTileEntity> getTileClass();


	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		updateRedstoneState(world, pos);
	}

	protected void updateRedstoneState(World world, BlockPos pos) {
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileProcessor) {
				if (world.isBlockPowered(pos)) {
					world.scheduleUpdate(pos, this, this.tickRate(world));
				}
			}
		}
	}
}
