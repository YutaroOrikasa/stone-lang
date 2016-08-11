package testers;

import stone.CodeFactory;
import stone.Interpreter;
import stone.ParseException;

public class ClassEvalTester implements Tester {
	public boolean test() throws ParseException {
		return Interpreter
				.run(CodeFactory
						.makeReaderFromString("                     "
								+ "class B { z=1                       \n"
								+ " def getZ() { z }                   \n "
								+ "}                          \n"
								+ "class C extends B {                       \n"
								+ "x = 0;                                   \n"
								+ "this.y = 0;\n"
								+ "def addXandYandZ(){ this.x + y + getZ() }; \n                "
								+ "}                       \n                      "
								+ "c=C.new();\n                                       "
								+ "c.x=1;c.y=1;\n                                              "
								+ "c.addXandYandZ() == 3    \n                             "))
				.equals(true);
	}

}
