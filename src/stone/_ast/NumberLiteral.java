package stone._ast;

import stone.Token;
import stone.ast.ASTLeaf;

/*
 * 数値リテラルを保持する
 */
public class NumberLiteral extends ASTLeaf {
	public NumberLiteral(Token t) {
		super(t);
	}

	public int value() {
		return token().getNumber();
	}
}