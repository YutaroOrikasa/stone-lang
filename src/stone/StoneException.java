package stone;

import stone.ast.ASTree;

public class StoneException extends RuntimeException {

	public StoneException(String msg) {
		super(msg);
	}

	public StoneException(String msg, ASTree t) {
		super(msg + " " + t.location());
	}

	public StoneException(Throwable cause, ASTree t) {
		super(t.location(), cause);
	}

	public StoneException(Throwable cause) {
		super(cause);
	}

}
