package runners;

import stone.BasicEnvironment;
import stone.Lexer;
import stone.ParseException;
import stone.Parser;
import stone.Token; 
import stone.ast.ASTree;

public class FuncEvalRunner {
	public static void main(String[] args) throws ParseException {
		Lexer lexer = new Lexer(CodeFactory.makeReaderFromString("def add (a,b) {a+b};add(1,2);"+
	"def bind(f, arg1) { fun (arg2) { f(arg1, arg2)} };bind(add, 100)(1);bind(add, 100)(1)==101"));
		Parser parser = new Parser(lexer);
		BasicEnvironment env = new BasicEnvironment();
		while (lexer.lookAhead1() != Token.EOF) {
			ASTree t = parser.program();
			System.out.println(t);
			System.out.println("eval: " + t.eval(env));

		}

		System.out.println("end");
		System.exit(0);
	}
}
