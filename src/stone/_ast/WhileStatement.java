package stone._ast;

import java.util.Arrays;
import java.util.List;

import stone.Environment;
import stone.ast.ASTList;
import stone.ast.ASTree;
import stone._types.Nil;
import stone._types.Utility;

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

}