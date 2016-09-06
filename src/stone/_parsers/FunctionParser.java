package stone._parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import stone.Environment;
import stone.Lexer;
import stone.ParseException;
import stone.Parser;
import stone.Token;
import stone._ast.Fun;
import stone._ast.FuncCallExpr;
import stone._ast.Name;
import stone.ast.ASTList;
import stone.ast.ASTree;

public class FunctionParser extends Parser {
	Lexer lexer;

	public FunctionParser(Lexer l) {
		super(l);
		lexer = l;

	}

	/**
	 * 
	 * @param primary
	 * @return (primary '(' args? ')' )
	 * @throws ParseException
	 */
	public ASTree callExpression(ASTree primary) throws ParseException {
		eat("(");
		if (isToken(")")) {
			primary = new FuncCallExpr(primary, new FakeASTList());
		} else {
			ASTList args = args();
			primary = new FuncCallExpr(primary, args);
		}
		eat(")");

		return primary;
	}

	/*
	 * def ::= 'def' Identifier paramList block
	 */
	public ASTree defStatement() throws ParseException {
		eat("def");
		return new Fun(eatName(), paramList(), block());

	}

	/*
	 * closure ::= "fun" paramList block
	 */
	public ASTree closure() throws ParseException {
		Token t = eat("fun");

		return new Fun(paramList(), block());

	}

	private void invalidToken(Token t) throws ParseException {
		throw new ParseException(String.format(Locale.US,
				"invalid identifier %s at line %d", t.getText(),
				t.getLineNumber()));
	}

	/*
	 * paramList ::= '(' params* ')'
	 */
	private ASTree paramList() throws ParseException {
		eat("(");

		if (isToken(")")) {
			eat(")");
			return new FakeASTList();
		} else {
			ASTree params = params();
			eat(")");
			return params;
		}
	}

	/*
	 * params ::= Identifier (',' Identifier)*
	 */
	private ASTree params() throws ParseException {
		ArrayList<ASTree> list = new ArrayList<>();

		list.add(eatName());

		for (;;) {
			if (!isToken(",")) {
				break;
			}
			eat(",");
			list.add(eatName());
		}

		return new FakeASTList(list);
	}

	private Name eatName() throws ParseException {
		Token t = eat();
		if (!t.isIdentifer()) {
			invalidToken(t);
		}
		return new Name(t);
	}

	/*
	 * expr (',' expr)*
	 */
	private ASTList args() throws ParseException {
		List<ASTree> args = new ArrayList<>();
		args.add(expression());
		while (!isToken(")")) {
			eat(",");
			args.add(expression());

		}
		return new FakeASTList(args);
	}

	static private class FakeASTList extends ASTList {

		public FakeASTList(List<ASTree> args) {
			super(args);
		}

		public FakeASTList() {
		}

		@Override
		public Object eval(Environment env) {
			throw new RuntimeException(
					"this node cannot be evaluated! never call eval()!");
		}

	}

}
