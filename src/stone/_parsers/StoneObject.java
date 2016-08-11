package stone._parsers;

import stone.BasicEnvironment;
import stone.Environment;

public class StoneObject {

	private Environment env;

	public StoneObject() {
		env = new BasicEnvironment();
	}

	public StoneObject(Environment env) {
		this.env = env;
	}

	public Object getField(String name) throws AccessException {
		Object value = env.get(name);
		if (value == null) {
			throw new AccessException();
		}
		return value;
	}

	public Object setField(String name, Object value) {
		env.put(name, value);
		return value;
	}

	public static class AccessException extends Exception {

	}

}
