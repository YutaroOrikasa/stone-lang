package stone._ast;

import java.util.List;

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
}