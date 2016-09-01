package stone._ast;

import stone.Environment;
import stone.Token;
import stone.ast.ASTLeaf;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.Value;

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

	@Override
	public String toString() {
		return "\"" + value() + "\"";
	}

	@Override
	public Value compileLLVMIR(LLVMIRBuilder builder) {
		throw new UnsupportedOperationException(
				"string literal in stone llvm backend");
	}

}
