package stone._ast;

import java.util.List;

import stone.Environment;
import stone.StoneException;
import stone.ast.ASTLeaf;
import stone.ast.ASTList;
import stone.ast.ASTree;
import stone.llvmbackend.Constant;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.Value;

public class UnaryExpr extends ASTList {

	public UnaryExpr(List<ASTree> list) {
		super(list);
	}

	public UnaryExpr(ASTree... trees) {
		super(trees);
	}

	public ASTree operator() {
		return child(0);
	}

	public ASTree operand() {
		return child(1);
	}

	@Override
	public Object eval(Environment env) {
		String op = ((ASTLeaf) operator()).token().getText();

		Object operand = operand().eval(env);

		if (operand instanceof Integer) {
			int value = (int) operand;
			switch (op) {
			case "+":
				return +value;

			case "-":
				return -value;

			default:
				throw new StoneException("uncomputable unary operator " + op,
						this);

			}
		} else {
			throw new StoneException(String.format("cannot compute %s %s %s",
					op, operand), this);
		}
	}

	@Override
	public Value compileLLVMIR(LLVMIRBuilder builder) {
		String op = ((ASTLeaf) operator()).token().getText();

		Value value = operand().compileLLVMIR(builder);

		switch (op) {
		case "+":
			return value;

		case "-":
			return builder.mul(new Constant(-1), value);

		default:
			throw new StoneException("uncomputable unary operator " + op, this);

		}

	}

}