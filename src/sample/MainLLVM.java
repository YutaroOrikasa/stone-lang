package sample;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import stone.Compiler;
import stone.ParseException;

public class MainLLVM {
	public static void main(String[] args) throws ParseException, IOException {
		try (BufferedReader r = new BufferedReader(
				new FileReader("./tmp.stone"))) {
			String code = Compiler.compile(r);

			System.out.println(code);

			try (PrintStream out = new PrintStream("./tmp.ll")) {
				out.print(code);
			}

			ProcessBuilder pb = new ProcessBuilder("gnome-terminal",
					"--command", "./procll.sh tmp.ll");
			Process process = pb.start();
		}

	}
}
