package stone._parsers;

import stone.Lexer;
import stone.ParseException;
import stone.Parser;
import stone._ast.DotAccessExpr;
import stone.ast.ASTree;

public class DotParser extends Parser {

	public DotParser(Lexer l) {
		super(l);
	}

	public void defClassStatement() throws ParseException {
		eat("class");

	}

	public ASTree dotAccessExpression(ASTree primary) throws ParseException {
		/*
		 * dotAccessExpr ::= primary '.' Identifier
		 */

		eat(".");

		return new DotAccessExpr(primary, identifier());

	}

}
