package stone._parsers;

import stone.BasicEnvironment;
import stone.Environment;

class NestedEnvironment implements Environment {

	Environment innerEnv;
	BasicEnvironment outerEnv = new BasicEnvironment();

	public NestedEnvironment(Environment innerEnv) {
		this.innerEnv = innerEnv;
	}

	@Override
	public void put(String name, Object value) {
		outerEnv.put(name, value);

	}

	@Override
	public Object get(String name) {
		Object value = outerEnv.get(name);

		if (value == null) {
			value = innerEnv.get(name);
		}

		return value;
	}

}