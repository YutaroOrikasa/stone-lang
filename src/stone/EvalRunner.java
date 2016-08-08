package stone;

import stone.ast.ASTree;

public class EvalRunner {
	public static void main(String[] args) throws ParseException {
		Lexer lexer = new Lexer(CodeFactory.getReaderFromDialog());
		Parser parser = new Parser(lexer);
		BasicEnvironment env = new BasicEnvironment();
		while (lexer.lookAhead1() != Token.EOF) {
			ASTree t = parser.program();
			System.out.println(t);
			System.out.println("eval: " + t.eval(env));
			;
		}

		System.out.println("end");
	}
}
