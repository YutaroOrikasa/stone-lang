package stone._ast;

import stone.Token;
import stone.ast.ASTLeaf;

/*
 * 変数名等の名前を保持する
 */
public class Name extends ASTLeaf {
	public Name(Token t) {
		super(t);
	}

	public String name() {
		return token().getText();
	}
}