package testers;

import stone.CodeFactory;
import stone.Interpreter;
import stone.ParseException;

public class ClassEvalTester implements Tester {
	public boolean test() throws ParseException {
		return Interpreter
				.run(CodeFactory
						.makeReaderFromString("                     "
								+ "B = (fun(){"
								+ "    z=1"
								+ "    class B {                       \n"
								+ "        def getZ() { z }                   \n "
								+ "    }                          \n"
								+ "     B                          \n"
								+ "})()                             \n"
								+ "class C extends B {                       \n"
								+ "x = 0;                                   \n"
								+ "this.y = 0;\n"
								+ "def addXandYandZ(){ this.x + y + getZ() }; \n                "
								+ "}                       \n                      "
								+ "c=C.new();\n                                       "
								+ "c.x + c.y\n                                           "
								+ "c.x=1;c.y=1;\n                                              "
								+ "c.addXandYandZ() == 3    \n                             "))
				.equals(true);
	}

}
