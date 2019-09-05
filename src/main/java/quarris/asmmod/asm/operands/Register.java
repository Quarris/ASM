package quarris.asmmod.asm.operands;

public class Register implements IOperand {

	private long data;

	@Override
	public long getData() {
		return data;
	}

	@Override
	public void setData(long data) {
		this.data = data;
	}
}
