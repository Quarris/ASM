package quarris.asmmod.blocks.tiles.base;

import quarris.asmmod.asm.exceptions.MemoryAccessException;

public interface IMemoryHolder {

	long readData(int address) throws MemoryAccessException;
	void writeData(int address, long data) throws MemoryAccessException;
	int getMemorySize();
}
