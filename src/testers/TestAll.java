package testers;

import stone.ParseException;

public class TestAll {
	public static void main(String[] args) throws ParseException {
		Tester testers[] = { new FuncEvalTester(), new ClassEvalTester() };
		for (Tester tester : testers) {
			if (!tester.test()) {
				throw new RuntimeException("test failed at " + tester);
			}
		}

	}
}
