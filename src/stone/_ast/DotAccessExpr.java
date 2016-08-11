package stone._ast;

import stone.Environment;
import stone.StoneException;
import stone._parsers.StoneObject;
import stone._parsers.StoneObject.AccessException;
import stone.ast.ASTList;
import stone.ast.ASTree;

public class DotAccessExpr extends ASTList {

	public DotAccessExpr(ASTree primary, Name name) {
		super(primary, name);
	}

	@Override
	public Object eval(Environment env) {
		try {
			return evalLeft(env).getField(right());
		} catch (AccessException e) {
			throw noSuchFieldException(right());
		}
	}

	private StoneException noSuchFieldException(String name) {
		return new StoneException("field '" + name + "' is not found", this);
	}

	public Object computeAssign(Environment env, ASTree rhs) {

		Object value = rhs.eval(env);
		return evalLeft(env).setField(right(), value);

	}

	private StoneObject evalLeft(Environment env) {
		Object left = left().eval(env);
		if (!(left instanceof StoneObject)) {
			throw new StoneException(
					"left hand side of '.' is not StoneObject", this);
		}
		return (StoneObject) left;
	}

	private ASTree left() {
		return child(0);
	}

	private String right() {
		return ((Name) child(1)).name();
	}

	@Override
	public String toString() {
		return "(" + left() + "." + right() + ")";
	}

}