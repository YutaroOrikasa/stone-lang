import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import somepackage.SomeThing;
import stone.Lexer;
import stone.ParseException;
import stone.Token;

public class Main {

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		StringReader r = new StringReader("1+2//adding\nreturn 1");

		Lexer lexer = new Lexer(r);

		System.out.println("peek");
		for (int i = 0;; i++) {
			Token token = lexer.peek(i);
			System.out.println(token.getText() + " at lineNo "
					+ token.getLineNumber());
			if (token == Token.EOF) {
				break;
			}
		}
		
		System.out.println();
		System.out.println("read");

		for (;;) {
			Token token = lexer.read();
			System.out.println(token.getText() + " at lineNo "
					+ token.getLineNumber());
			if (token == Token.EOF) {
				break;
			}
		}

	}

}
