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

public class WhileStatement extends ASTList {

	public WhileStatement(List<ASTree> list) {
		super(list);
	}

	public WhileStatement(ASTree condition, ASTree bodyBlock) {
		this(Arrays.asList(condition, bodyBlock));
	}

	public ASTree condition() {
		return child(0);
	}

	public ASTree body() {
		return child(1);
	}

	public String toString() {
		return "(while " + condition() + " " + body() + ")";
	}

	@Override
	public Object eval(Environment env) {
		Object result = Nil.STONE_NIL;
		while (Utility.convertToStoneBooleanValue(condition().eval(env))) {
			result = body().eval(env);
		}

		return result;
	}

	@Override
	public Value compileLLVMIR(LLVMIRBuilder builder) {

		Label whileLabel = builder.genTmpLabel();
		Label whileBodyLabel = builder.genTmpLabel();
		Label endWhileLabel = builder.genTmpLabel();

		builder.assignLocalVariable(".whileStatementLastEvaluated",
				new Constant(0));

		builder.branch(whileLabel);
		builder.enter(whileLabel);
		// cond
		builder.branch(condition().compileLLVMIR(builder), whileBodyLabel,
				endWhileLabel);

		// body
		builder.enter(whileBodyLabel);
		builder.assignLocalVariable(".whileStatementLastEvaluated", body()
				.compileLLVMIR(builder));

		builder.branch(whileLabel);

		// end while
		builder.enter(endWhileLabel);

		try {
			return builder.get(".whileStatementLastEvaluated");
		} catch (NoSuchLocalVariableException e) {
			throw new RuntimeException("fatal", e);
		}
	}
}