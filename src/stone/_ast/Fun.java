package stone._ast;

import stone.Environment;
import stone.NestedEnvironment;
import stone._types.Callable;
import stone.ast.ASTList;
import stone.ast.ASTree;

public class Fun extends ASTList {

	private String name;
	private ASTree parameters;
	private ASTree body;

	protected ASTree parameters() {
		return parameters;
	}

	protected ASTree body() {
		return body;
	}

	public Fun(Name name, ASTree paramater, ASTree body) {
		super(name, paramater, body);

		this.name = name.name();

		this.parameters = paramater;
		this.body = body;
	}

	public Fun(ASTree paramater, ASTree body) {
		super(paramater, body);
		this.name = null;

		this.parameters = paramater;
		this.body = body;
	}

	@Override
	public Object eval(Environment env) {
		Object funcObject = new Function(env, this);
		if (name != null) {
			env.put(name, funcObject);
		}
		return funcObject;
	}

	public Object call(Environment env, Object[] args) {
		if (parameters().numChildren() != args.length) {
			throw new IllegalArgumentException("bad number of arguments");
		}
		Environment nestedEnv = new NestedEnvironment(env);

		for (int i = 0; i < parameters().numChildren(); i++) {

			String name = ((Name) parameters().child(i)).name();
			nestedEnv.put(name, args[i]);
		}
		return body().eval(nestedEnv);

	}

	@Override
	public String toString() {

		if (name != null) {

			return String.format("(def %s %s %s)", name, parameters(), body());
		} else {
			return String.format("(fun %s %s)", parameters(), body());
		}
	}

	public int numOfParameters() {
		return parameters().numChildren();
	}
}

class Function implements Callable {
	private Environment env;
	private Fun defStatement;

	public Function(Environment env, Fun def) {
		this.env = env;
		defStatement = def;
	}

	public int numOfParameters() {
		return defStatement.numOfParameters();
	}

	public Object call(Object[] args) {
		return defStatement.call(env, args);

	}
}
