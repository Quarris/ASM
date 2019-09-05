package quarris.asmmod.blocks.tiles.base;

import net.minecraft.util.ITickable;
import quarris.asmmod.asm.exceptions.MemoryAccessException;

public abstract class TileMemory extends ASMTileEntity implements ITickable, IMemoryHolder {

	protected long[] memory;

	public TileMemory() {
		memory = new long[getMemorySize()];
	}

	@Override
	public void update() {
		if (world.getTotalWorldTime() % tickRate() == 0) {
			tick();
		}
	}

	/**
	 * The amount of ticks to pass before an update.
	 * Range: > 0
	 */
	public int tickRate() {
		return 5;
	}

	public abstract void tick();

	protected long getData(int address) throws MemoryAccessException {
		if (address < 0 || address >= getMemorySize()) {
			throw new MemoryAccessException("Tried to read invalid memory for " + this + " at " + getPos());
		}
		return memory[address];
	}

	protected void setData(int address, long data) throws MemoryAccessException {
		if (address < 0 || address >= getMemorySize()) {
			throw new MemoryAccessException("Tried to write to invalid memory for " + this + " at " + getPos());
		}
		memory[address] = data;
	}

	public void onMemoryWrite(int address, long old) {

	}

	public void onMemoryRead(int address) {

	}

	public long readData(int address) throws MemoryAccessException {
		if (address < 0 || address >= getMemorySize()) {
			throw new MemoryAccessException("Tried to read invalid memory for " + this + " at " + getPos());
		}
		long ret = memory[address];
		onMemoryRead(address);
		return ret;
	}

	public void writeData(int address, long data) throws MemoryAccessException {
		if (address < 0 || address >= getMemorySize()) {
			throw new MemoryAccessException("Tried to write to invalid memory for " + this + " at " + getPos());
		}
		long old = memory[address];
		memory[address] = data;
		onMemoryWrite(address, old);
	}

	public abstract int getMemorySize();
}
