package stone._ast;

import java.util.Arrays;
import java.util.List;

import stone.ast.ASTList;
import stone.ast.ASTree;

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

}