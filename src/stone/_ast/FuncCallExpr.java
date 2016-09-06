package stone._ast;

import stone.Environment;
import stone.StoneException;
import stone._types.Callable;
import stone.ast.ASTList;
import stone.ast.ASTree;
import stone.llvmbackend.GlobalVariable;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.Value;

public class FuncCallExpr extends ASTList {

	public FuncCallExpr(ASTree expr, ASTList args) {
		super(expr, args);
	}

	private ASTree args() {
		return child(1);
	}

	@Override
	public Object eval(Environment env) {
		Object funcObject = child(0).eval(env);
		if (!(funcObject instanceof Callable)) {
			throw new StoneException(""
					+ funcObject.getClass().getCanonicalName()
					+ "is not callable", this);
		}
		Callable func = (Callable) funcObject;

		Object[] args = new Object[args().numChildren()];

		for (int i = 0; i < args.length; i++) {
			args[i] = args().child(i).eval(env);
		}

		try {
			return func.call(args);
		} catch (Exception e) {
			throw new StoneException(e, this);
		}

	}

	@Override
	public Value compileLLVMIR(LLVMIRBuilder builder) {
		Name funcName = (Name) child(0);
		Value funcSymbol = new GlobalVariable(funcName.name());

		Value[] args = new Value[args().numChildren()];

		for (int i = 0; i < args.length; i++) {
			args[i] = args().child(i).compileLLVMIR(builder);
		}

		return builder.call(funcSymbol, args);

	}
}