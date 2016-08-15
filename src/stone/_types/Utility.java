package stone._types;

public class Utility {

	public static boolean convertToStoneBooleanValue(Object condObject) {
		// Integerの0であるならば偽
		// またはBooleanのfalseならば偽
		// またはnilなら偽
		// それ以外なら真
		return !((condObject instanceof Integer && (int) condObject == 0)
				|| (condObject instanceof Boolean && (boolean) condObject == false) || condObject == Nil.STONE_NIL);
	}

}
