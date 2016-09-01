package stone.ast;

import java.util.Iterator;

import stone.Environment;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.Value;

public abstract class ASTree implements Iterable<ASTree> {

	public abstract ASTree child(int i);

	public abstract int numChildren();

	public abstract Iterator<ASTree> children();

	public abstract String location();

	@Override
	public Iterator<ASTree> iterator() {
		return children();
	}

	public abstract Object eval(Environment env);

	public abstract Value compileLLVMIR(LLVMIRBuilder builder);

}
