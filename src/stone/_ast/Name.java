package stone._ast;

import stone.Environment;
import stone.StoneException;
import stone.Token;
import stone.ast.ASTLeaf;
import stone.ast.ASTree;

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

	@Override
	public Object eval(Environment env) {

		Object result = env.get(name());

		if (result == null) {
			throw new StoneNameException(name(), this);
		}

		return result;

	}

	public void computeAssign(Environment env, Object value) {
		env.put(name(), value);
	}

	protected static class StoneNameException extends StoneException {

		public StoneNameException(String name, ASTree t) {
			super("name " + name + " is not defined", t);
		}

	}
}