package stone;

public interface Environment {

	public Object put(String name, Object value);
	public Object get(String name);

}
