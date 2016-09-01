package stone.llvmbackend;

public class Constant implements Value {
	private final String value;

	public Constant(Integer value) {
		this.value = value.toString();
	}

	@Override
	public String toString() {
		return value;
	}
}