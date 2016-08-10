package stone;

import java.awt.Panel;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CodeFactory {
	
	public static Reader makeReaderFromString(String code) {
		return new StringReader(code);
	}
	
	public static Reader getReaderFromDialog(){

		return new StringReader(showDialog());
	}

	private static String showDialog() {
		JTextArea area = new JTextArea(20, 40);
		JScrollPane pane = new JScrollPane(area);
		int result = JOptionPane.showOptionDialog(null, pane, "input",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null);

		if (result == JOptionPane.OK_OPTION) {
			return area.getText();
		} else {
			return "";
		}
	}
	


}
