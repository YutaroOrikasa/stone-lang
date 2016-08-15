package stone;

import java.io.Reader;

import stone.Parser.NullStatement;
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

	public static Object run(Reader r) throws ParseException {
		Lexer lexer = new Lexer(r);
		Parser parser = new Parser(lexer);
		BasicEnvironment env = new BasicEnvironment();
		BuiltinFunctions.appendBuitlins(env);
		Object eval = null;
		while (lexer.lookAhead1() != Token.EOF) {
			ASTree t = parser.program();
			if (!(t instanceof NullStatement)) {
				System.out.println(t);
				eval = t.eval(env);
				System.out.println("eval: " + eval);
			}

		}
		return eval;
	}

}
