package stone;

import java.io.Reader;

import stone.ast.ASTree;

public class Interpreter {
	public static void main(String[] args) throws ParseException {
		
		try {
			run(CodeFactory.getReaderFromDialog());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static void run(Reader r) throws ParseException {
		Lexer lexer = new Lexer(r);
		Parser parser = new Parser(lexer);
		BasicEnvironment env = new BasicEnvironment();
		BuiltinFunctions.appendBuitlins(env);
		while (lexer.lookAhead1() != Token.EOF) {
			ASTree t = parser.program();
			System.out.println(t);
			System.out.println("eval: " + t.eval(env));

		}
	}

}
