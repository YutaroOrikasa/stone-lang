package stone.ast;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import stone.Environment;

public abstract class ASTList extends ASTree {

	protected List<ASTree> children;

	public ASTList(List<ASTree> list) {
		children = list;
	}

	public ASTList(ASTree... trees) {
		this(Arrays.asList(trees));
	}

	@Override
	public ASTree child(int i) {
		return children.get(i);
	}

	public int numChildren() {
		return children.size();
	}

	public Iterator<ASTree> children() {
		return children.iterator();
	}

	// s式に変換するデフォルト実装
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		String sep = "";

		for (ASTree t : children) {
			builder.append(sep);
			sep = " ";
			builder.append(t.toString());
		}
		builder.append(')');

		return builder.toString();

	}

	public String location() {
		for (ASTree t : children) {
			String s = t.location();

			// 復習
			// sがnullになるのはどんな場合か?
			if (s != null) {
				return s;
			}
		}
		return null;
	}
	
}
