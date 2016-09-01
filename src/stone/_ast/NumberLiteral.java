package stone._ast;

import stone.Environment;
import stone.Token;
import stone.ast.ASTLeaf;
import stone.llvmbackend.Constant;
import stone.llvmbackend.LLVMIRBuilder;
import stone.llvmbackend.Value;

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

	@Override
	public Object eval(Environment env) {
		return value();
	}

	@Override
	public Value compileLLVMIR(LLVMIRBuilder builder) {
		return new Constant(value());
	}
}