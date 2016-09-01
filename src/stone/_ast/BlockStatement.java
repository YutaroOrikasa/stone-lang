package stone._ast;

import java.util.ArrayList;

import stone.Environment;
import stone._types.Nil;
import stone.ast.ASTList;
import stone.ast.ASTree;
import stone.llvmbackend.Constant;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.Value;

public class BlockStatement extends ASTList {

	public BlockStatement(ArrayList<ASTree> list) {
		super(list);
	}

	@Override
	public Object eval(Environment env) {
		Object result = Nil.STONE_NIL;

		// XXX
		// why can i access super field ?
		for (ASTree child : this.children) {
			result = child.eval(env);
		}

		return result;

	}

	@Override
	public Value compileLLVMIR(LLVMIRBuilder builder) {

		Value lastEval = new Constant(0);
		for (ASTree child : this) {
			lastEval = child.compileLLVMIR(builder);
		}

		return lastEval;
	}
}
