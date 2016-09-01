package stone._ast;

import java.util.List;

import stone.Environment;
import stone.StoneException;
import stone.ast.ASTLeaf;
import stone.ast.ASTList;
import stone.ast.ASTree;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.Value;

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

	@Override
	public Value compileLLVMIR(LLVMIRBuilder builder) {
		String op = ((ASTLeaf) operator()).token().getText();
		if (op.equals("=")) {
			return compileLLVMIRAssign(builder, left(), right());
		}

		Value left = left().compileLLVMIR(builder);
		Value right = right().compileLLVMIR(builder);

		return compileLLVMIRComputeNumber(builder, op, left, right);

		// if (left instanceof Integer && right instanceof Integer) {
		// return computeNumber(op, (int) left, (int) right);
		// } else {
		// throw new StoneException(String.format("cannot compute %s %s %s",
		// left, op, right), this);
		// }
	}

	private Value compileLLVMIRComputeNumber(LLVMIRBuilder builder, String op,
			Value left, Value right) {

		switch (op) {
		case "+":
			return builder.add(left, right);

		case "-":
			return builder.sub(left, right);

		case "*":
			return builder.mul(left, right);

		case "/":
			return builder.sdiv(left, right);

		case ">":
			return builder.cmp("sgt", left, right);

		case "<":
			return builder.cmp("slt", left, right);

		case ">=":
			return builder.cmp("sge", left, right);

		case "<=":
			return builder.cmp("sle", left, right);

		case "==":
			return builder.cmp("eq", left, right);

		case "!=":
			return builder.cmp("ne", left, right);

		default:
			throw new StoneException("uncomputable binary operator " + op, this);
		}

	}

	private Value compileLLVMIRAssign(LLVMIRBuilder builder, ASTree lhs,
			ASTree right) {

		if (!(lhs instanceof Name)) {
			throw new StoneException(
					"left hand side of assign operator must be L-Value but "
							+ lhs, this);
		}

		return ((Name) lhs).compileLLVMIRAssign(builder,
				right.compileLLVMIR(builder));
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

		if (!(lhs instanceof Name)) {
			throw new StoneException(
					"left hand side of assign operator must be L-Value but "
							+ lhs, this);
		}
		env.put(((Name) lhs).name(), rhs.eval(env));
		return lhs.eval(env);
	}
}