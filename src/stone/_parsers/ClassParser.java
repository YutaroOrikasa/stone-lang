package stone._parsers;

import stone.BasicEnvironment;
import stone.Environment;
import stone.Lexer;
import stone.ParseException;
import stone.Parser;
import stone.StoneException;
import stone._ast.Name;
import stone._types.Callable;
import stone.ast.ASTList;
import stone.ast.ASTree;

public class ClassParser extends Parser {

	public ClassParser(Lexer l) {
		super(l);
	}

	public ClassStatement classStatement() throws ParseException {
		eat("class");

		Name className = identifier();

		if (isToken("extends")) {
			eat("extends");
			return new ClassStatement(className, identifier(), block());

		}

		return new ClassStatement(className, block());
	}

	private static class ClassStatement extends ASTList {

		private Name className;
		private Name superClassName = null;
		private ASTree classBody;

		public ClassStatement(Name name, Name superName, ASTree body) {
			this(name, body);
			superClassName = superName;

		}

		public ClassStatement(Name name, ASTree body) {
			super(name, body);
			className = name;
			classBody = body;

		}

		@Override
		public Object eval(Environment env) {
			StoneClass cls;
			if (superClassName != null) {
				Object superClassObject = superClassName.eval(env);
				if (!(superClassObject instanceof StoneClass)) {
					throw new StoneException(superClassName
							+ " is not class name", this);
				}

				cls = new StoneClass(env, this, (StoneClass) superClassObject);
			} else {
				cls = new StoneClass(env, this);

			}
			className.computeAssign(env, cls);
			return cls;
		}

		@Override
		public String toString() {
			if (superClassName != null) {
				return "(class " + className + " extends " + superClassName
						+ " " + classBody + ")";
			} else {
				return "(class " + className + " " + classBody + ")";
			}
		}

		public Environment initializeFields(Environment env,
				StoneClass superClass, Environment outer) {
			if (superClass != null) {
				superClass.initializeFields(env);
			}

			NestedEnvironment nested = new NestedEnvironment(outer, env);
			classBody.eval(nested);
			return env;
		}

		private String name() {
			return className.name();
		}

	}

	private static class StoneClass extends StoneObject {

		private Environment env;
		private ClassStatement classDef;
		private StoneClass superClass = null;

		public StoneClass(Environment env, ClassStatement def) {
			this.env = env;
			classDef = def;
			setField("new", new Constructor());
		}

		public StoneClass(Environment env, ClassStatement def,
				StoneClass superClass) {
			this(env, def);
			this.superClass = superClass;
		}

		private StoneObject constructNewStoneObject() {
			BasicEnvironment env = new BasicEnvironment();
			StoneObject obj = new StoneObject(env);
			env.put("this", obj);
			return new StoneObject(initializeFields(env));

		}

		private Environment initializeFields(Environment env) {

			return classDef.initializeFields(env, superClass, this.env);
		}

		private class Constructor implements Callable {

			@Override
			public int numOfParameters() {
				return 0;
			}

			@Override
			public Object call(Object[] args) {
				return constructNewStoneObject();
			}

		}
	}
}
