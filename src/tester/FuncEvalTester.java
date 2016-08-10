package tester;

import stone.CodeFactory;
import stone.Interpreter;
import stone.ParseException;

public class FuncEvalTester {
	public static void main(String[] args) throws ParseException {
		if(! Interpreter.run(CodeFactory
				.makeReaderFromString("def add (a,b) {a+b};add(1,2);"
						+ "def bind(f, arg1) { fun (arg2) { f(arg1, arg2)} };"
						+ "bind(add, 100)(1)==101")).equals(true)){
			throw new RuntimeException("test failed!");
		}
	}
}
