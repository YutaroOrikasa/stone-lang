package stone;

import javax.swing.JOptionPane;

public class BuiltinFunctions {

	public static Environment appendBuitlins(Environment env) {
		append(env, "println", Object.class);
		append(env, "currentTime");
		append(env, "read");
		append(env, "len", String.class);
		append(env, "toInt", Object.class);
		return env;

	}

	private static Environment append(Environment env, String name,
			Class<?>... parameterTypes) {
		Class<?> klazz = BuiltinFunctions.class;
		return NativeLoader.load(env, name, klazz, name, parameterTypes);
	}

	public static void println(Object o) {
		System.out.println(o);
	}

	public static int currentTime() {
		return (int)(System.currentTimeMillis());
	}
	
	public static String read() {
		return JOptionPane.showInputDialog(null);
	}
	
	public static int len(String s) {
		return s.length();
	}
	
	public static int toInt(Object value) {
		if(value instanceof String){
			return Integer.parseInt((String)value);
		}else if (value instanceof Integer) {
			return (int)value;
		}else {
			throw new NumberFormatException(value.toString());
		}
		
	}
}
