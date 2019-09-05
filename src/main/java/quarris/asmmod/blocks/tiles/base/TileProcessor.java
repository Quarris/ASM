package quarris.asmmod.blocks.tiles.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import quarris.asmmod.ASMMod;
import quarris.asmmod.asm.Processor;
import quarris.asmmod.asm.Program;
import quarris.asmmod.asm.exceptions.ProcessorException;

public abstract class TileProcessor extends ASMTileEntity implements ITickable {

	public Processor processor;

	public TileProcessor() {
		processor = new Processor();
		loadProgram(ASMMod.defaultProgram);
		processor.setVerbose(true);
	}

	@Override
	public void update() {
		if (!world.isRemote && world.getTotalWorldTime() % 10 == 0) {
			if (processor.isRunning()) {
				try {
					processor.fetch();
					processor.execute();
				}
				catch (ProcessorException e) {
					System.out.println("Caught exception at instruction "+processor.getProgramCounter()+":  "+e.getMessage());
					processor.halt();
				}
			}
		}
	}

	public void loadProgram(Program program) {
		processor.loadProgram(program);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		processor = new Processor();
		processor.readFromNBT(compound.getCompoundTag("Processor"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("Processor", processor.writeToNBT());
		return super.writeToNBT(compound);
	}
}
