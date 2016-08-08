package stone;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

	public static String regexPat = "\\s*((//.*)|([0-9]+)|(\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\")"
			+ "|[A-Z_a-z][A-Z_a-z0-9]*|==|<=|>=|&&|\\|\\||\\p{Punct})?";

	private static final Pattern pattern = Pattern.compile(regexPat);

	private ArrayList<Token> buffer = new ArrayList<>();

	private LineNumberReader reader;

	// means not reached EOF
	private boolean hasMore = true;

	public Lexer(Reader r) {
		reader = new LineNumberReader(r);
	}

	public Token read() throws ParseException {

		if (fillBuffer(0)) {
			return buffer.remove(0);
		} else {
			return Token.EOF;
		}

	}

	/**
	 * Lexerのストリームが現在位置からi個先にあるTokenを返す
	 * Lexerのストリームの位置は変わらない
	 * 1文字先読みしたい場合はiに0を指定する(1でないので注意)
	 * @param i
	 * @return
	 * @throws ParseException
	 */
	public Token peek(int i) throws ParseException {

		if (fillBuffer(i)) {
			return buffer.get(i);
		} else {
			return Token.EOF;
		}

	}
	
	/**
	 * 1文字先読みする
	 * @return
	 * @throws ParseException 
	 */
	public Token lookAhead1() throws ParseException {
		return peek(0);
	}

	private boolean fillBuffer(int i) throws ParseException {

		while (i >= buffer.size()) {
			if (hasMore) {
				readLine();
			} else {
				return false;
			}
		}
		return true;

	}

	private void readLine() throws ParseException {
		String line;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			throw new ParseException(e);
		}
		if (line != null) {
			int linenum = reader.getLineNumber();
			buffer.addAll(tokenizeLine(linenum, line));
		} else { // EOF
			hasMore = false;
		}
		return;

	}

	private static ArrayList<Token> tokenizeLine(int linenum, String line)
			throws ParseException {

		ArrayList<Token> tokens = new ArrayList<>();

		Matcher matcher = pattern.matcher(line);

		int pos = 0;
		final int endPos = line.length();
		// 最後にマッチした位置の次の位置から行末までの範囲で
		// tokenにマッチするかを判別する
		while (pos < endPos) {
			matcher.region(pos, endPos);
			boolean matched = matcher.lookingAt();
			pos = matcher.end();
			if (matched) {
				Token token = makeToken(linenum, matcher);
				if (token != null) {
					tokens.add(token);
				}
			} else {
				// 空白、コメント、tokenのどれでもなかった場合
				throw new ParseException("bad token at line " + linenum);
			}
		}

		// ふざけるな
		// 改行が変数扱いされるじゃないか
		// tokens.add(new IdToken(linenum, Token.EOL));
		tokens.add(new EOLToken(linenum));

		return tokens;

	}

	/*
	 * matcherにマッチしている部分のtokenを返す。 空白及びコメントにマッチしたらnullを返す。
	 */
	private static Token makeToken(int linenum, Matcher matcher) {

		String m = matcher.group(1);

		if (m == null) { // if only space
			return null;
		}

		if (matcher.group(2) != null) { // if comment
			return null;
		}

		if (matcher.group(3) != null) {
			return new NumToken(linenum, Integer.parseInt(m));
		} else if (matcher.group(4) != null) {
			return new StrToken(linenum, m);
		} else {
			return new IdToken(linenum, m);
		}

	}
	
	private static class EOLToken extends Token{
		
		public EOLToken(int lineNo) {
			super(lineNo);
		}

		@Override
		public String getText() {
			return Token.EOL;
		}
	}

	private static class NumToken extends Token {
		private int number;

		public NumToken(int lineNo, int number) {
			super(lineNo);
			this.number = number;
		}

		public boolean isNumber() {
			return true;
		}

		public int getNumber() {
			return number;
		}

		public String getText() {
			return Integer.toString(number);
		}
	}

	private static class IdToken extends Token {
		private String text;

		public IdToken(Integer lineNo, String id) {
			super(lineNo);
			text = id;
		}

		@Override
		public boolean isIdentifer() {
			return true;

		}

		@Override
		public String getText() {
			return text;
		}
	}

	private static class StrToken extends Token {
		private String literal;

		public StrToken(int lineNo, String str) {
			super(lineNo);
			literal = str;
		}

		@Override
		public boolean isString() {
			return true;

		}

		@Override
		public String getText() {
			return literal;
		}
	}

}
