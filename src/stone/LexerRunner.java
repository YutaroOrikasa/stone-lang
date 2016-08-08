package stone;

public class LexerRunner {
	
	public static void main(String[] args) throws ParseException {
		Lexer lexer = new Lexer(CodeFactory.getReaderFromDialog());

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
