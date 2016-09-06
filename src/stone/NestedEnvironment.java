package stone;

public class NestedEnvironment implements Environment {

	Environment outerEnv;
	Environment innerEnv;

	public NestedEnvironment(Environment outerEnv) {
		this(outerEnv, new BasicEnvironment());

	}

	public NestedEnvironment(Environment outerEnv, Environment innerEnv) {
		this.outerEnv = outerEnv;
		this.innerEnv = innerEnv;
	}

	@Override
	public void put(String name, Object value) {
		innerEnv.put(name, value);

	}

	@Override
	public Object get(String name) {
		Object value = innerEnv.get(name);

		if (value == null) {
			value = outerEnv.get(name);
		}

		return value;
	}

}