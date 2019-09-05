package quarris.asmmod.asm.operands;

public class Immediate implements IOperand {

	private long data;

	public Immediate(long data) {
		this.data = data;
	}

	@Override
	public void setData(long data) {
		// Cannot change the data of an immediate value
	}

	@Override
	public long getData() {
		return data;
	}
}
