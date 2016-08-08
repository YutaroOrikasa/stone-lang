package stone;

import java.util.HashMap;

public class BasicEnvironment implements Environment {
	HashMap<String, Object> table = new HashMap<>();

	public Object get(String name) {
		return table.get(name);
	}

	public void put(String name, Object value) {
		table.put(name, value);
	}

}
