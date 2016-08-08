package stone._ast;

import java.util.Arrays;
import java.util.List;

import stone.ast.ASTList;
import stone.ast.ASTree;

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
}