package quarris.asmmod.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import quarris.asmmod.blocks.base.BlockTileProvider;
import quarris.asmmod.blocks.tiles.TileTextHologram;
import quarris.asmmod.blocks.tiles.base.ASMTileEntity;

import javax.annotation.Nullable;

public class BlockTextHologram extends BlockTileProvider {

	public static final PropertyDirection DIRECTION = PropertyDirection.create("direction");

	// The AABBs for the hologram base. D-U-N-S-W-E
	public static final AxisAlignedBB[] HOLOGRAM_BOUNDS = new AxisAlignedBB[] {
			new AxisAlignedBB(0, 0, 0, 1.0, 0.25, 1.0),
			new AxisAlignedBB(0, 0.75, 0, 1.0, 1.0, 1.0),
			new AxisAlignedBB(0, 0, 0, 1.0, 1.0, 0.25),
			new AxisAlignedBB(0, 0, 0.75, 1.0, 1.0, 1.0),
			new AxisAlignedBB(0, 0, 0, 0.25, 1.0, 1.0),
			new AxisAlignedBB(0.75, 0, 0, 1.0, 1.0, 1.0)
	};

	public BlockTextHologram(Material material, MapColor color, String name) {
		super(material, color, name);
		setLightLevel(0.3f);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DIRECTION);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return HOLOGRAM_BOUNDS[state.getValue(DIRECTION).getIndex()];
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileTextHologram tile = (TileTextHologram) worldIn.getTileEntity(pos);
		if (tile != null) {
			tile.facing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
			tile.orientation = placer.getHorizontalFacing().getOpposite();
			System.out.println(tile.orientation);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getStateFromMeta(facing.getOpposite().getIndex());
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta < 6) {
			return getDefaultState().withProperty(DIRECTION, EnumFacing.VALUES[meta]);
		}
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(DIRECTION).getIndex();
	}

	@Override
	protected Class<? extends ASMTileEntity> getTileClass() {
		return TileTextHologram.class;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTextHologram();
	}
}
