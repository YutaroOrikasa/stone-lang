package stone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import stone._types.Callable;

public class NativeLoader {

	/**
	 * 
	 * @param env
	 * @param name
	 * @param klazz
	 * @param namiveMethodName
	 * @param parameterTypes
	 * @return env
	 */
	public static Environment load(Environment env, String name,
			Class<?> klazz, String namiveMethodName, Class<?>... parameterTypes) {
		Method m;
		try {
			m = klazz.getMethod(namiveMethodName, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new StoneException("failed to load native method "
					+ namiveMethodName + " because of:\n" + e);
		}

		env.put(name, new NativeFunction(m));

		return env;

	}

	private static class NativeFunction implements Callable {

		private Method method;

		public NativeFunction(Method m) {
			method = m;
		}

		@Override
		public int numOfParameters() {
			return method.getParameterTypes().length;
		}

		@Override
		public Object call(Object[] args) {
			try {
				return method.invoke(null, args);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new StoneException("failed to call native method: " + e);
			}
		}

	}

}
