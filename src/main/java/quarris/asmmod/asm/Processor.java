package quarris.asmmod.asm;

import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;
import quarris.asmmod.ASMMod;
import quarris.asmmod.asm.exceptions.CompilerException;
import quarris.asmmod.asm.exceptions.ExecuteException;
import quarris.asmmod.asm.exceptions.FetchException;
import quarris.asmmod.asm.operands.IOperand;
import quarris.asmmod.asm.operands.Immediate;
import quarris.asmmod.asm.operands.Register;

import java.util.ArrayList;
import java.util.List;

public class Processor {

	private List<StringBuilder> rawCode = new ArrayList<>();
	private Program program;
	private boolean running;
	private boolean halted;
	private Register[] registerBank;
	private int programCounter;
	private int linkRegister;
	private int stackPointer; // Not Implemented

	public boolean verbose;

	// The current instruction to be executed by the processor.
	private Instruction instruction;

	// The flags which the conditions are checked against.
	// The flags are stored in bits of the byte.
	// Negative - Zero - oVerflow - Carry
	private boolean[] flags;

	// Constructor
	public Processor() {
		loadProgram(ASMMod.defaultProgram);
	}

	// Loads the program into the instruction memory of the processor.
	public void loadProgram(Program program) {
		running = false;
		halted = false;
		programCounter = 0;
		stackPointer = 0;
		linkRegister = 0;
		registerBank = new Register[8];
		for (int i = 0; i < 8; i++) {
			registerBank[i] = new Register();
		}
		instruction = null;
		flags = new boolean[4];

		this.program = program;
	}

	// Fetches the instruction and increments the program counter.
	// If the program counter reaches the end of the program it indicated a halted program.
	public void fetch() throws FetchException {
		if (!running) {
			System.err.println("Attempted to fetch an instruction after program was halted");
		}
		if (verbose) {
			System.out.println("Fetching instruction at " + programCounter);
		}
		try {
			instruction = program.get(programCounter);
		} catch (IndexOutOfBoundsException e) {
			throw new FetchException("Could not fetch instruction at " + programCounter);
		}
		programCounter++;
		if (verbose) {
			System.out.println("Instruction fetched: " + instruction);
		}
	}

	// Acts as the ALU of the processor.
	// Executes the instruction if the condition is met.
	public void execute() throws ExecuteException {
		if (verbose) {
			System.out.println(
					"Begin Execution on " + instruction
							+ "\nwith flags"
							+ String.format("\nNegative: %b, Zero: %b, oVerflow: %b, Carry: %b", flags[0], flags[1], flags[2], flags[3])
			);

		}
		if (instruction.condition.matches(flags)) {
			if (verbose) {
				System.out.println("Condition matched");
			}

			// Create IOperands to be used by the instruction using the provided string array from the instruction.
			List<IOperand> operands = new ArrayList<>();
			for (String op : instruction.operands) {
				if (op == null || op.isEmpty()) {
					continue;
				}
				boolean startReg = op.startsWith("R");
				boolean startHash = op.startsWith("#");
				if (startReg || startHash) {
					String val = op.substring(1);
					long num = Long.parseLong(val);
					operands.add(startReg ? registerBank[(int) num] : new Immediate(num));
				}
				else {
					operands.add(new Immediate(program.getLabelIndex(op)));
				}
			}

			// Debug before the operation
			if (verbose) {
				StringBuilder builder = new StringBuilder();
				builder.append("\nBefore\n______\n");
				for (int i = 0; i < operands.size(); i++) {
					builder.append(instruction.operands[i])
							.append(" = ")
							.append(operands.get(i).getData()).append('\n');
				}
				builder.append('\n');
				System.out.println(builder.toString());
			}
			// Execute the instruction on the given operands.
			instruction.operation.operate(this, instruction.setFlags, operands.toArray(new IOperand[0]));

			// Debug after operation
			if (verbose) {
				StringBuilder builder = new StringBuilder();
				builder.append("\nAfter\n_____\n");
				for (int i = 0; i < operands.size(); i++) {
					builder.append(instruction.operands[i])
							.append(" = ")
							.append(operands.get(i).getData()).append('\n');
				}
				builder.append('\n');
				System.out.println(builder.toString());
			}
		}
		else if (verbose) {
			System.out.println("Condition not matched, skipping execution");
		}
		// Indicates the program should be halted and next fetch should not be called.
		// The programmer should check if the program is halted
		if (programCounter >= program.size()) {
			running = false;
			halted = true;
			if (verbose) {
				System.out.println("Program counter now exceeds the instruction count, program should not be fetching anymore");
			}
		}
	}

	public boolean isHalted() {
		return halted;
	}

	public boolean isRunning() {
		return running;
	}

	public void setFlags(boolean[] flags) {
		this.flags = flags;
	}

	public int getProgramCounter() {
		return programCounter;
	}

	public int getLinkRegister() {
		return linkRegister;
	}

	public void setProgramCounter(int programCounter) {
			this.programCounter = programCounter;
	}

	public void setLinkRegister(int linkRegister) {
		if (linkRegister < program.size()) {
			this.linkRegister = linkRegister;
		}
	}

	// Not Implemented
	private void setStackPointer(int stackPointer) {
		this.stackPointer = stackPointer;
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		NBTTagCompound programTag = program.toNBT();
		compound.setTag("Program", programTag);
		compound.setBoolean("Running", running);
		compound.setBoolean("Halted", halted);

		NBTTagList registerTag = new NBTTagList();
		for (Register reg : registerBank) {
			registerTag.appendTag(new NBTTagLong(reg.getData()));
		}
		compound.setTag("RegisterBank", registerTag);

		compound.setInteger("PC", programCounter);
		compound.setInteger("LR", linkRegister);
		compound.setInteger("SP", stackPointer);

		compound.setBoolean("Verbose", verbose);

		NBTTagList flagsTag = new NBTTagList();
		for (boolean flag : flags) {
			flagsTag.appendTag(new NBTTagByte(flag ? (byte) 1 : (byte) 0));
		}
		compound.setTag("Flags", flagsTag);
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound programTag = compound.getCompoundTag("Program");
		program = Program.fromNBT(programTag);

		running = compound.getBoolean("Running");
		halted = compound.getBoolean("Halted");
		registerBank = new Register[8];
		NBTTagList registerTag = compound.getTagList("RegisterBank", Constants.NBT.TAG_LONG);
		for (int i = 0; i < registerTag.tagCount(); i++) {
			long data = ((NBTTagLong) registerTag.get(i)).getLong();
			Register reg = new Register();
			reg.setData(data);
			registerBank[i] = reg;
		}

		programCounter = compound.getInteger("PC");
		linkRegister = compound.getInteger("LR");
		stackPointer = compound.getInteger("SP");

		verbose = compound.getBoolean("Verbose");

		flags = new boolean[4];
		NBTTagList flagsTag = compound.getTagList("Flags", Constants.NBT.TAG_BYTE);
		for (int i = 0; i < flagsTag.tagCount(); i++) {
			flags[i] = ((NBTTagByte) flagsTag.get(i)).getByte() == 1;
		}
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setHalted(boolean halted) {
		this.halted = halted;
	}

	public void halt() {
		setRunning(false);
		setHalted(true);
	}
}
