package stone._ast;

import java.util.List;

import stone.ast.ASTList;
import stone.ast.ASTree;

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
}