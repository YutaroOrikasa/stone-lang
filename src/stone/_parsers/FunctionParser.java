package stone._parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import stone.BasicEnvironment;
import stone.Environment;
import stone.Lexer;
import stone.ParseException;
import stone.Parser;
import stone.StoneException;
import stone.Token;
import stone._ast.Name;
import stone.ast.ASTList;
import stone.ast.ASTree;

public class FunctionParser extends Parser {
	Lexer lexer;

	public FunctionParser(Lexer l) {
		super(l);
		lexer = l;

	}

	/*
	 * def ::= 'def' Identifier paramList block
	 */
	public ASTree defStatement() throws ParseException {
		eat("def");
		return new DefStatement(eatName(), paramList(), block());

	}

	/*
	 * closure ::= "fun" paramList block
	 */
	public ASTree closure() throws ParseException {
		Token t = eat("fun");

		return new DefStatement(new Name(new Token(t.getLineNumber())),
				paramList(), block());

	}

	private static class DefStatement extends ASTList {

		private String name;
		private ASTree paramaters;
		private ASTree body;

		public DefStatement(Name name, ASTree paramater, ASTree body) {
			super(name, paramater, body);
			this.name = name.name();
			this.paramaters = paramater;
			this.body = body;
		}

		@Override
		public Object eval(Environment env) {
			Object funcObject = new Function(env, this);
			env.put(name, funcObject);
			return funcObject;
		}

		public Object call(Environment env, Object[] args) {
			if (paramaters.numChildren() != args.length) {
				throw new StoneException("bad number of arguments", this);
			}
			Environment nestedEnv = new NestedEnvironment(env);

			for (int i = 0; i < paramaters.numChildren(); i++) {

				String name = ((Name) paramaters.child(i)).name();
				nestedEnv.put(name, args[i]);
			}
			return body.eval(nestedEnv);

		}

		@Override
		public String toString() {

			return String.format("(def %s %s %s)", name, paramaters, body);
		}
	}

	private static class NestedEnvironment implements Environment {

		Environment innerEnv;
		BasicEnvironment outerEnv = new BasicEnvironment();

		public NestedEnvironment(Environment innerEnv) {
			this.innerEnv = innerEnv;
		}

		@Override
		public void put(String name, Object value) {
			outerEnv.put(name, value);

		}

		@Override
		public Object get(String name) {
			Object value = outerEnv.get(name);

			if (value == null) {
				value = innerEnv.get(name);
			}

			return value;
		}

	}

	private static class Function {
		private Environment env;
		private DefStatement defStatement;

		public Function(Environment env, DefStatement def) {
			this.env = env;
			defStatement = def;
		}

		public Object call(Object[] args) {
			return defStatement.call(env, args);

		}
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

	/**
	 * @return ( primary0 ( '(' args? ')' )* )
	 */
	public ASTree funcCallChain(ASTree primary0) throws ParseException {
		ASTree expr = primary0;
		for (;;) {
			if (isToken("(")) {
				eat("(");
				if (isToken(")")) {
					expr = new FuncCallExpr(expr, new FakeASTList());
				} else {
					ASTList args = args();
					expr = new FuncCallExpr(expr, args);
				}
				eat(")");

			} else {
				return expr;

			}
		}
	}

	static private class FuncCallExpr extends ASTList {

		public FuncCallExpr(ASTree expr, ASTList args) {
			super(expr, args);
		}

		private ASTree args() {
			return child(1);
		}

		@Override
		public Object eval(Environment env) {
			Object funcObject = child(0).eval(env);
			if (!(funcObject instanceof Function)) {
				throw new StoneException(""
						+ funcObject.getClass().getCanonicalName()
						+ "is not callable", this);
			}
			Object[] args = new Object[args().numChildren()];

			for (int i = 0; i < args.length; i++) {
				args[i] = args().child(i).eval(env);
			}

			return ((Function) funcObject).call(args);
		}
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
