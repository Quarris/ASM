package quarris.asmmod.blocks.base;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quarris.asmmod.blocks.tiles.base.ASMTileEntity;
import quarris.asmmod.blocks.tiles.base.TileProcessor;

import java.util.Random;

public abstract class BlockProcessor extends BlockTileProvider {

	public BlockProcessor(Material material, MapColor color, String name) {
		super(material, color, name);
	}

	protected abstract Class<? extends ASMTileEntity> getTileClass();

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!world.isRemote) {
			TileProcessor tile = (TileProcessor) world.getTileEntity(pos);
			if (tile != null) {
				tile.processor.setRunning(!tile.processor.isRunning());
			}
		}
	}

	@Override
	public int tickRate(World worldIn) {
		return 1;
	}
}
