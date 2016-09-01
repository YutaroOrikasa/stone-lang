package stone.llvmbackend;


public class Register implements Value {
	final String name;

	public Register(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "%" + name;
	}
}