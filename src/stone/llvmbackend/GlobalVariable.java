package stone.llvmbackend;

public class GlobalVariable implements Value {
	final String name;

	public GlobalVariable(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "@" + name;
	}
}
