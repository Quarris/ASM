package quarris.asmmod;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import quarris.asmmod.blocks.BlockTerminal;
import quarris.asmmod.blocks.BlockTextHologram;

public class ASMContent {

	public static Block BLOCK_TERMINAL = new BlockTerminal(Material.IRON, MapColor.IRON, "block_terminal").register();
	public static Block BLOCK_TEXT_HOLOGRAM = new BlockTextHologram(Material.IRON, MapColor.IRON, "block_text_hologram").register();

}
