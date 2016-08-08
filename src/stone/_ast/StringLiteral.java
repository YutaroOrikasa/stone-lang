package stone._ast;

import stone.Environment;
import stone.Token;
import stone.ast.ASTLeaf;

public class StringLiteral extends ASTLeaf {
	public StringLiteral(Token t) {
		super(t);
	}
	


	private String value() {
		return token().getText();
	}

	@Override
	public Object eval(Environment env) {
		return value();
	}

}
