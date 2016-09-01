package stone._ast;

import java.util.Arrays;
import java.util.List;

import stone.Environment;
import stone._types.Nil;
import stone._types.Utility;
import stone.ast.ASTList;
import stone.ast.ASTree;
import stone.llvmbackend.Constant;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.LLVMIRBuilder.NoSuchLocalVariableException;
import stone.llvmbackend.Label;
import stone.llvmbackend.Value;

public class IfStatement extends ASTList {
	public IfStatement(List<ASTree> list) {
		super(list);
	}

	public IfStatement(ASTree condition, ASTree ifBlock, ASTree elseBlock) {
		this(Arrays.asList(condition, ifBlock, elseBlock));
	}

	public IfStatement(ASTree condition, ASTree ifBlock) {
		this(Arrays.asList(condition, ifBlock));
	}

	public ASTree condition() {
		return child(0);
	}

	public ASTree thenBlock() {
		return child(1);
	}

	public ASTree elseBlock() {
		if (numChildren() >= 3) {
			return child(2);
		} else {
			return null;
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(if " + condition() + " " + thenBlock());

		if (elseBlock() != null) {
			builder.append(" else " + elseBlock());
		}
		return builder.append(")").toString();
	}

	@Override
	public Object eval(Environment env) {

		Object condObject = condition().eval(env);

		boolean cond = Utility.convertToStoneBooleanValue(condObject);

		if (cond) {
			return thenBlock().eval(env);

		} else {
			if (elseBlock() != null) {
				return elseBlock().eval(env);
			} else {
				return Nil.STONE_NIL;
			}
		}
	}

	@Override
	public Value compileLLVMIR(LLVMIRBuilder builder) {

		Value cond = condition().compileLLVMIR(builder);

		Label thenLabel = builder.genTmpLabel();
		Label elseLable = builder.genTmpLabel();
		Label endifLabel = builder.genTmpLabel();

		builder.branch(cond, thenLabel, elseLable);

		builder.enter(thenLabel);
		builder.assignLocalVariable(".ifStatementLastEvaluated", thenBlock()
				.compileLLVMIR(builder));
		builder.branch(endifLabel);

		builder.enter(elseLable);
		if (elseBlock() != null) {
			builder.assignLocalVariable(".ifStatementLastEvaluated",
					elseBlock().compileLLVMIR(builder));
		} else {
			builder.assignLocalVariable(".ifStatementLastEvaluated",
					new Constant(0));
		}
		builder.branch(endifLabel);

		builder.enter(endifLabel);

		try {
			return builder.get(".ifStatementLastEvaluated");
		} catch (NoSuchLocalVariableException e) {
			throw new RuntimeException("fatal", e);
		}

	}
}