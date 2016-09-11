package sample;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import stone.Interpreter;
import stone.ParseException;

public class Main {

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		try (BufferedReader r = new BufferedReader(
				new FileReader("./tmp.stone"))) {
			Interpreter.run(r);
		}

	}
}
