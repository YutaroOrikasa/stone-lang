package stone._ast;

import java.util.List;

import stone.Environment;
import stone.StoneException;
import stone.ast.ASTLeaf;
import stone.ast.ASTList;
import stone.ast.ASTree;

/*
 * a + b のような二項演算式を保持する
 */
public class BinaryExpr extends ASTList {

	public BinaryExpr(List<ASTree> list) {
		super(list);
	}

	public BinaryExpr(ASTree... trees) {
		super(trees);
	}

	public ASTree left() {
		return child(0);
	}

	public ASTree operator() {
		return child(1);
	}

	public ASTree right() {
		return child(2);
	}

	public Object eval(Environment env) {
		String op = ((ASTLeaf) operator()).token().getText();
		if (op.equals("=")) {
			return computeAssign(env, left(), right());
		}

		Object left = left().eval(env);
		Object right = right().eval(env);

		if (left instanceof Integer && right instanceof Integer) {
			return computeNumber(op, (int) left, (int) right);
		} else {
			throw new StoneException(String.format("cannot compute %s %s %s",
					left, op, right), this);
		}
	}

	private Object computeNumber(String op, int left, int right) {

		switch (op) {
		case "+":
			return left + right;

		case "-":
			return left - right;

		case "*":
			return left * right;

		case "/":
			return left / right;

		case ">":
			return left > right;

		case "<":
			return left < right;

		case ">=":
			return left >= right;

		case "<=":
			return left <= right;

		case "==":
			return left == right;

		default:
			throw new StoneException("uncomputable binary operator " + op, this);
		}

	}

	private Object computeAssign(Environment env, ASTree lhs, ASTree rhs) {
		
		if(!(lhs instanceof Name)){
			throw new StoneException("left hand side of assign operator must be L-Value but " + lhs, this);
		}
		env.put(((Name)lhs).name(), rhs.eval(env));
		return lhs.eval(env);
	}
}