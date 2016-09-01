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
import stone._types.Callable;
import stone.ast.ASTList;
import stone.ast.ASTree;
import stone.llvmbackend.Constant;
import stone.llvmbackend.GlobalVariable;
import stone.llvmbackend.Int32Type;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.Register;
import stone.llvmbackend.Type;
import stone.llvmbackend.Value;

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

	private static class Fun extends ASTList {

		private String name;
		private ASTree parameters;
		private ASTree body;

		protected ASTree parameters() {
			return parameters;
		}

		protected ASTree body() {
			return body;
		}

		public Fun(Name name, ASTree paramater, ASTree body) {
			super(name, paramater, body);

			this.name = name.name();

			this.parameters = paramater;
			this.body = body;
		}

		public Fun(ASTree paramater, ASTree body) {
			super(paramater, body);
			this.name = null;

			this.parameters = paramater;
			this.body = body;
		}

		@Override
		public Object eval(Environment env) {
			Object funcObject = new Function(env, this);
			if (name != null) {
				env.put(name, funcObject);
			}
			return funcObject;
		}

		public Object call(Environment env, Object[] args) {
			if (parameters().numChildren() != args.length) {
				throw new IllegalArgumentException("bad number of arguments");
			}
			Environment nestedEnv = new NestedEnvironment(env);

			for (int i = 0; i < parameters().numChildren(); i++) {

				String name = ((Name) parameters().child(i)).name();
				nestedEnv.put(name, args[i]);
			}
			return body().eval(nestedEnv);

		}

		@Override
		public String toString() {

			if (name != null) {

				return String.format("(def %s %s %s)", name, parameters(),
						body());
			} else {
				return String.format("(fun %s %s)", parameters(), body());
			}
		}

		public int numOfParameters() {
			return parameters().numChildren();
		}

		@Override
		public Value compileLLVMIR(LLVMIRBuilder builder) {

			Int32Type i32 = new Int32Type();

			Type[] argTypes = new Type[numOfParameters()];

			for (int i = 0; i < argTypes.length; i++) {
				argTypes[i] = i32;
			}

			Register[] args = builder.defineFunction(i32, name, argTypes);

			for (int i = 0; i < parameters().numChildren(); i++) {

				String name = ((Name) parameters().child(i)).name();
				((Name) parameters().child(i)).compileLLVMIRAssign(builder,
						args[i]);

			}

			builder.ret(body().compileLLVMIR(builder));

			builder.endFunction();

			return new Constant(0);
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

	private static class Function implements Callable {
		private Environment env;
		private Fun defStatement;

		public Function(Environment env, Fun def) {
			this.env = env;
			defStatement = def;
		}

		public int numOfParameters() {
			return defStatement.numOfParameters();
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
			if (!(funcObject instanceof Callable)) {
				throw new StoneException(""
						+ funcObject.getClass().getCanonicalName()
						+ "is not callable", this);
			}
			Callable func = (Callable) funcObject;

			Object[] args = new Object[args().numChildren()];

			for (int i = 0; i < args.length; i++) {
				args[i] = args().child(i).eval(env);
			}

			try {
				return func.call(args);
			} catch (Exception e) {
				throw new StoneException(e, this);
			}

		}

		@Override
		public Value compileLLVMIR(LLVMIRBuilder builder) {
			Name funcName = (Name) child(0);
			Value funcSymbol = new GlobalVariable(funcName.name());

			Value[] args = new Value[args().numChildren()];

			for (int i = 0; i < args.length; i++) {
				args[i] = args().child(i).compileLLVMIR(builder);
			}

			return builder.call(funcSymbol, args);

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

		@Override
		public Value compileLLVMIR(LLVMIRBuilder builder) {
			throw new RuntimeException(
					"this node cannot be comliped! never call me!");
		}

	}

}
