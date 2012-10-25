package parser.tree.types;

import parser.tree.Node;

public abstract class Type extends Node {	
	
	public boolean equals(Type t) {
		return this.toString().equals(t.toString());
	}
	
	@Override
	public abstract String toString();
	public abstract void print(String prefix, boolean isTail);
}
