package stone;

public class ParseException extends Exception {
	
	public ParseException(String msg) {
		super(msg);
	}
	
	
	public ParseException(Exception e) {
		super(e);
	}
}
