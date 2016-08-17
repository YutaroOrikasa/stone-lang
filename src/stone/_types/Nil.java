package stone._types;

/*
 * シングルトン
 */
public class Nil {
	public static final Nil STONE_NIL = new Nil();

	private Nil() {
	}

	@Override
	public String toString() {
		return "nil";
	}
}
