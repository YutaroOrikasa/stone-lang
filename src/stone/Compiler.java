package stone;

import java.io.Reader;

import stone.Parser.NullStatement;
import stone.ast.ASTree;
import stone.llvmbackend.LLVMIRBuilder;

public class Compiler {
	public static void main(String[] args) throws ParseException {

		try {
			String code = compile(CodeFactory.getReaderFromDialog());
			System.out.println(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static String compile(Reader r) throws ParseException {
		Lexer lexer = new Lexer(r);
		Parser parser = new Parser(lexer);
		BasicEnvironment env = new BasicEnvironment();
		BuiltinFunctions.appendBuitlins(env);

		LLVMIRBuilder builder = new LLVMIRBuilder();
		Object eval = null;
		while (lexer.lookAhead1() != Token.EOF) {
			ASTree t = parser.program();
			if (!(t instanceof NullStatement)) {
				System.out.println(t);
				t.compileLLVMIR(builder);
			}

		}

		return builder.toString();
	}

}
