package runners;

import stone.CodeFactory;
import stone.Lexer;
import stone.ParseException;
import stone.Parser;
import stone.Token;

public class ExprParserRunner {
	public static void main(String[] args) throws ParseException {
		Lexer lexer = new Lexer(CodeFactory.makeReaderFromString("a\n-2"));
		Parser parser = new Parser(lexer);
		while (lexer.lookAhead1() != Token.EOF) {
			System.out.println(parser.expression());
		}

		System.out.println("end");
		System.exit(0);
	}
}
