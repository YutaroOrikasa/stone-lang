package stone;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import stone._ast.BinaryExpr;
import stone._ast.BlockStatement;
import stone._ast.IfStatement;
import stone._ast.Name;
import stone._ast.NumberLiteral;
import stone._ast.StringLiteral;
import stone._ast.UnaryExpr;
import stone._ast.WhileStatement;
import stone._parsers.ClassParser;
import stone._parsers.DotParser;
import stone._parsers.FunctionParser;
import stone.ast.ASTLeaf;
import stone.ast.ASTree;

public class Parser {
	private static HashMap<String, Integer> operatorPriorityTable = new HashMap<>();
	static {

		ArrayList<List<String>> operatorGroup = new ArrayList<>();

		operatorGroup.add(Arrays.asList("="));
		operatorGroup.add(Arrays.asList("==", ">", "<"));
		operatorGroup.add(Arrays.asList("+", "-"));
		operatorGroup.add(Arrays.asList("*", "/", "%"));

		int priority = 1;
		for (List<String> list : operatorGroup) {
			for (String op : list) {
				operatorPriorityTable.put(op, priority);
			}
			priority++;

		}

	}

	/*
	 * 操車場アルゴリズムで使う。 スタックの底にある $ を意味する。
	 */
	private static ASTree dollar = new Operator(null);

	Lexer lexer;

	public Parser(Lexer l) {
		lexer = l;
	}

	public ASTree program() throws ParseException {
		if (isToken(";") || isToken(Token.EOL)) {

			return new NullStatement(eat());
		}

		ASTree s = statement();

		if (isToken(";") || isToken(Token.EOL)) {
			eat();
		}
		return s;

	}

	public static class NullStatement extends ASTLeaf {

		public NullStatement(Token t) {
			super(t);
		}

		@Override
		public Object eval(Environment env) {
			return null;
		}

	}

	public ASTree statement() throws ParseException {

		if (isToken("if")) {
			return ifStatement();
		} else if (isToken("while")) {
			return whileStatement();
		} else if (isToken("def")) {
			return new FunctionParser(lexer).defStatement();
		} else if (isToken("class")) {
			return new ClassParser(lexer).classStatement();
		} else {
			return simple();
		}

	}

	public ASTree ifStatement() throws ParseException {
		// eat "if"
		lexer.read();

		ASTree cond = expression();
		ASTree ifBlock = block();
		if (isToken("else")) {
			// eat "else"
			lexer.read();
			ASTree elseBlock = block();

			return new IfStatement(cond, ifBlock, elseBlock);
		} else {
			return new IfStatement(cond, ifBlock);
		}

	}

	public ASTree whileStatement() throws ParseException {
		// eat "while"
		lexer.read();
		return new WhileStatement(expression(), block());
	}

	public ASTree block() throws ParseException {

		// { S? (; S?)* }
		// ; S?の繰り返し

		ArrayList<ASTree> statements = new ArrayList<>();

		eat("{");
		for (;;) {
			if (isToken("}")) {
				eat();
				break;
			}
			if (isToken(";") || isToken(Token.EOL)) {
				eat();
				continue;
			}
			statements.add(statement());
		}

		return new BlockStatement(statements);

	}

	public ASTree simple() throws ParseException {
		return expression();
	}

	public ASTree expression() throws ParseException {
		/*
		 * 操車場アルゴリズム
		 * 
		 * output -------------input
		 * 
		 * _______\______/
		 * 
		 * _________stack
		 * 
		 * maybeFactorから1こ取って stackに入っているものと比べて 優先順位が高い方をendに入れる
		 */

		Deque<ASTree> input = rawExpressionForShuntingYard();
		ArrayDeque<ASTree> output = new ArrayDeque<>();
		ArrayDeque<ASTree> stack = new ArrayDeque<>();
		stack.add(dollar);

		for (;;) {
			ASTree inputTop = input.peekFirst();
			ASTree stackTop = stack.peekFirst();

			if (inputTop == dollar && stackTop == dollar) {

				// BinaryExprを生成する
				return makeASTreeFormPostfixNotation(output);
			}

			if (priority(inputTop) > priority(stackTop)) {
				input.pop();
				stack.push(inputTop);
			} else {
				stack.pop();
				output.addLast(stackTop);
			}
		}

	}

	protected Name identifier() throws ParseException {

		Token name = eat();

		if (!name.isIdentifer()) {
			throw new ParseException(
					String.format(
							Locale.US,
							"Idenfier must be come after '.' but  '%s' came at line %d",
							name.getText(), name.getLineNumber()));
		}
		return new Name(name);
	}

	/*
	 * 後置記法のリストからASTを作る
	 */
	private static ASTree makeASTreeFormPostfixNotation(ArrayDeque<ASTree> input) {

		/*
		 * スタックを用意する。
		 * 
		 * inputから1こ取り出す。
		 * 
		 * 演算子ならスタックから2こ取り出してBinaryExprを作ってスタックに入れる。
		 * 
		 * そうでないならinputから取り出した物をスタックに入れる。
		 */

		ArrayDeque<ASTree> stack = new ArrayDeque<>();

		while (!input.isEmpty()) {
			ASTree t = input.pop();

			if (isASTreeOperator(t)) {
				// inputには left right の順で積んである
				// stackは逆の順で積んであるので注意
				ASTree right = stack.pop();
				ASTree left = stack.pop();

				stack.push(new BinaryExpr(left, t, right));
			} else {
				stack.push(t);
			}

		}

		return stack.pop();

	}

	/*
	 * factor (OP factor)* をパースする 優先順位や結合を考慮していない 後にパースされた要素が後ろ側に入り、 末尾にはnullが入る
	 */
	private Deque<ASTree> rawExpressionForShuntingYard() throws ParseException {
		Deque<ASTree> stack = new ArrayDeque<ASTree>();

		stack.addLast(factor());

		while (isOperator()) {
			stack.addLast(new Operator(eat()));
			stack.addLast(factor());
		}

		stack.addLast(dollar);

		return stack;
	}

	private boolean isOperator() throws ParseException {
		return isTokenOperator(lexer.lookAhead1());
	}

	private static boolean isTokenOperator(Token t) {
		return operatorPriorityTable.containsKey(t.getText());
	}

	private int priority(ASTree tree) {
		/*
		 * dollarの優先順位は0。 式の優先順位が一番高い。
		 */

		if (tree == dollar) {
			return 0;
		} else if (isASTreeOperator(tree)) {
			// 演算子の場合
			String op = ((ASTLeaf) tree).token().getText();
			return operatorPriorityTable.get(op);
		} else {
			// 式として扱える場合
			return 100;
		}
	}

	private static boolean isASTreeOperator(ASTree tree) {
		if (!(tree instanceof ASTLeaf)) {
			return false;
		}

		return isTokenOperator(((ASTLeaf) tree).token());
	}

	public ASTree factor() throws ParseException {
		if (isToken("-")) {
			return new UnaryExpr(new Operator(lexer.read()), primary());
		} else {
			return primary();
		}
	}

	/*
	 * primary ::= closure | primary0 ( postfix )*
	 */
	public ASTree primary() throws ParseException {
		if (isToken("fun")) {
			return new FunctionParser(lexer).closure();
		}
		ASTree p0 = primary0();
		return foldlPostfixes(p0);
	}

	/**
	 * left folding postfixes parser
	 * 
	 * @param primary
	 * @return (((primary postfix1) postfix2 ... ) postfixN)
	 * @throws ParseException
	 */
	private ASTree foldlPostfixes(ASTree primary) throws ParseException {
		for (;;) {
			if (isToken("(")) {
				primary = new FunctionParser(lexer).callExpression(primary);
			} else if (isToken(".")) {
				primary = new DotParser(lexer).dotAccessExpression(primary);
			} else {
				break;
			}
		}
		return primary;
	}

	/*
	 * ( '(' expr ')' | number | string | identifier )
	 */
	private ASTree primary0() throws ParseException {
		if (isToken("(")) {
			// eat "("
			lexer.read();

			ASTree expr = expression();

			if (!isToken(")")) {
				Token t = lexer.read();
				throw new ParseException(String.format(Locale.US,
						"must come ')' but '%s' came at line %d", t.getText(),
						t.getLineNumber()));
			}
			// eat ")"
			lexer.read();

			return expr;

		} else {
			Token t = lexer.read();

			if (t.isIdentifer()) {
				return new Name(t);
			} else if (t.isNumber()) {
				return new NumberLiteral(t);
			} else if (t.isString()) {
				return new StringLiteral(t);
			} else {

				throw new ParseException(String.format(Locale.US,
						"eligal token '%s' at line %d", t.getText(),
						t.getLineNumber()));
			}

		}
	}

	private static class Operator extends ASTLeaf {

		public Operator(Token t) {
			super(t);
		}

		@Override
		public Object eval(Environment env) {
			throw new RuntimeException(
					"this node cannot be evaluated! never call eval()!");
		}

	}

	protected Token eat(String s) throws ParseException {
		if (!isToken(s)) {
			Token t = lexer.read();
			throw new ParseException(String.format(Locale.US,
					"must come '%s' but '%s' came at line %d", s, t.getText(),
					t.getLineNumber()));
		}
		return eat();
	}

	protected Token eat() throws ParseException {
		return lexer.read();
	}

	protected boolean isToken(String s) throws ParseException {
		return lexer.lookAhead1().getText().equals(s);

	}
}
