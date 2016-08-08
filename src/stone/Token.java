package stone;

public class Token {

	public static final Token EOF = new Token(-1);
	public static final String EOL = "\\n";


	private int lineNumber;

	public static class TokenTypeMismatchException extends RuntimeException {

		public TokenTypeMismatchException(String string) {
			super(string);
		}

	}
	public Token(int lineNo) {
		this.lineNumber = lineNo;

	}

	public int getLineNumber() {
		return lineNumber;
	}

	public boolean isIdentifer() {
		return false;
	}

	public boolean isNumber() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public int getNumber() {
		throw new TokenTypeMismatchException("not number token");
	}

	public String getText() {
		return "";
	}

}
