package stone.llvmbackend;


public class Label {

	private final Register reg;

	Label(Register r) {
		reg = r;
	}

	public String getLabelName() {
		return reg.name;
	}

	@Override
	public String toString() {
		return reg.toString();
	}

}