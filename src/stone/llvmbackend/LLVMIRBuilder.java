package stone.llvmbackend;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/* 
 * loaclVari -> assign 
 * out -> localVariableTable: hash, codes: Formattable StringBuilder
 * 
 */
public class LLVMIRBuilder {

	private int tmpNum = 0;

	private String builtCodes = "";

	private StringBuilder funcBuilder = new StringBuilder();

	private final LinkedHashMap<String, Register> globalVariables = new LinkedHashMap<>();

	private final LinkedHashMap<String, Register> localVariables = new LinkedHashMap<>();

	private String defineFuncString = null;

	public void branch(Label label) {
		appendln("br label " + label);

	}

	public void branch(Value cmp, Label l1, Label l2) {
		appendf("br i1 %s, label %s, label %s\n", cmp, l1, l2);

	}

	/**
	 * 
	 * @param cmpType
	 *            "eq" とか "sgt" とか "sge" とか。 llvmのicmp命令の第一引数
	 * @param v1
	 * @param v2
	 * @return
	 */
	public Register cmp(String cmpType, Value v1, Value v2) {
		Register tmp = genTmpRegister();
		appendf("%s = icmp %s i32 %s, %s\n", tmp, cmpType, v1, v2);
		return tmp;
	}

	public void enter(Label label) {
		appendln();
		appendln(label.getLabelName() + ":");

	}

	public Register get(String name) throws NoSuchLocalVariableException {
		// Value var = localVariables.get(name);
		// if (var == null) {
		// var = globalVariables.get(name);
		// if (var == null) {
		// throw new NoSuchLocalVariableException(name);
		// }
		// }
		// Register tmp = genTmpRegister();
		// appendln(tmp + " = load i32, i32* " + var);
		//
		// return tmp;

		return getLocalValue(name);

	}

	public static class NoSuchLocalVariableException extends Exception {

		public NoSuchLocalVariableException(String name) {
			super("name");
		}

	}

	public Value assignLocalVariable(String name, Value value) {
		Register loc = new Register(name);
		localVariables.put(name, loc);

		appendf("store i32 %s, i32* %s\n", value, loc);
		return value;
	}

	/**
	 * 
	 * @param name
	 *            ローカル変数の名前
	 * @return ローカル変数に入っている値が入っているレジスタ(ローカル変数のポインタではない)
	 * @throws NoSuchLocalVariableException
	 */
	public Register getLocalValue(String name)
			throws NoSuchLocalVariableException {
		Register local = localVariables.get(name);
		if (local == null) {
			throw new NoSuchLocalVariableException(name);
		}
		Register tmp = genTmpRegister();
		appendln(tmp + " = load i32, i32* " + local);

		return tmp;

	}

	/**
	 * 
	 * @param arg1
	 *            i32*
	 * @param arg2
	 *            i32*
	 * @return i32
	 */
	public Register add(Value arg1, Value arg2) {
		Register ret = genTmpRegister();

		appendln(ret + " = add i32 " + arg1 + ", " + arg2);

		return ret;

	}

	/**
	 * 
	 * @param arg1
	 *            i32*
	 * @param arg2
	 *            i32*
	 * @return i32
	 */
	public Register sub(Value arg1, Value arg2) {
		Register ret = genTmpRegister();

		appendln(ret + " = sub i32 " + arg1 + ", " + arg2);

		return ret;

	}

	public Register mul(Value arg1, Value arg2) {
		Register ret = genTmpRegister();

		appendln(ret + " = mul i32 " + arg1 + ", " + arg2);

		return ret;

	}

	public Register sdiv(Value arg1, Value arg2) {
		Register ret = genTmpRegister();

		appendln(ret + " = sdiv i32 " + arg1 + ", " + arg2);

		return ret;

	}

	public void ret(Value reg) {
		appendf("ret i32 %s\n", reg);

	}

	public Register[] defineFunction(Type returnType, String functionName,
			Type... argTypes) {
		StringBuilder def = new StringBuilder();
		def.append(String.format("define %s @%s(", returnType, functionName));

		ArrayList<Register> reglist = new ArrayList<Register>();
		String sep = "";
		for (Type type : argTypes) {
			Register reg = genTmpRegister();
			reglist.add(reg);
			def.append(String.format("%s", sep));
			def.append(String.format("%s %s", type, reg));
			sep = ", ";
		}

		def.append(String.format(") {\n"));
		def.append(".entry:\n");

		defineFuncString = def.toString();

		return reglist.toArray(new Register[argTypes.length]);
	}

	public Register genTmpRegister() {
		return new Register(".tmp" + tmpNum++);
	}

	public void endFunction() {
		appendln("}");

		StringBuilder locals = new StringBuilder();
		for (Register local : localVariables.values()) {
			locals.append(String.format("%s = alloca i32\n", local));
			locals.append(String.format("store i32 0, i32* %s\n", local));

		}

		locals.append("\n");

		builtCodes += defineFuncString + locals.toString()
				+ funcBuilder.toString();

		funcBuilder = new StringBuilder();
		tmpNum = 0;
	}

	public void addEmptyLine() {
		appendln();
	}

	public void addComment(String comment) {
		appendf("; %s\n", comment);

	}

	public Label genTmpLabel() {
		return new Label(genTmpRegister());
	}

	@Override
	public String toString() {

		return builtCodes;

	}

	private void appendf(String format, Object... args) {
		funcBuilder.append(String.format(format, args));
	}

	private void appendln() {
		funcBuilder.append("\n");
	}

	private void appendln(String string) {
		funcBuilder.append(string);
		funcBuilder.append("\n");
	}

	public Value call(Value funcSymbol, Value... args) {
		Register tmp = genTmpRegister();
		funcBuilder.append(String.format("%s = call i32 %s(", tmp, funcSymbol));
		String sep = "";
		for (Value arg : args) {
			funcBuilder.append("i32 ");
			funcBuilder.append(arg);
			funcBuilder.append(sep);
			sep = ", ";
		}
		funcBuilder.append(")\n");
		return tmp;
	}
}
