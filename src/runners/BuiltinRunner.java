package runners;

import stone.CodeFactory;
import stone.Interpreter;
import stone.ParseException;

public class BuiltinRunner {
	public static void main(String[] args) throws ParseException {
		Interpreter.run(CodeFactory.makeReaderFromString("a=currentTime();"
				+ "println(a);" + "b=currentTime();" + "println(b);"
				+ "println(b-a);"));
	}

}
