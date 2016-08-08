package stone.ast;

import java.util.Iterator;

import stone.Environment;

public abstract class ASTree {
	
	public abstract ASTree child(int i);
	public abstract int numChildren();
	public abstract Iterator<ASTree> children();
	public abstract String location();
	public Iterator<ASTree> ireIterator(){
		return children();
	}
//	public Object eval() {
//		throw new RuntimeException("bad eval" + this);
//	}
	
	public abstract Object eval(Environment env);

}
