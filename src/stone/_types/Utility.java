package stone._types;

public class Utility {

	public static boolean convertToStoneBooleanValue(Object condObject) {
		// 数値の0であるならば偽
		// またはnilなら偽
		// それ以外なら真
		return !((condObject instanceof Integer && (int) condObject == 0) || condObject == Nil.STONE_NIL);
	}

}
