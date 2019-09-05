package quarris.asmmod.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quarris.asmmod.ASMMod;
import quarris.asmmod.GuiHandler;
import quarris.asmmod.blocks.base.BlockProcessor;
import quarris.asmmod.blocks.tiles.TileTerminal;
import quarris.asmmod.blocks.tiles.base.ASMTileEntity;
import quarris.asmmod.blocks.tiles.base.TileProcessor;

import javax.annotation.Nullable;

public class BlockTerminal extends BlockProcessor {

	public BlockTerminal(Material material, MapColor color, String name) {
		super(material, color, name);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			TileProcessor processor = ((TileProcessor)worldIn.getTileEntity(pos));
			processor.processor.setVerbose(!processor.processor.verbose);
			return true;
		}
		playerIn.openGui(ASMMod.MODID, GuiHandler.GuiID.TERMINAL.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTerminal();
	}

	@Override
	protected Class<? extends ASMTileEntity> getTileClass() {
		return TileTerminal.class;
	}
}
