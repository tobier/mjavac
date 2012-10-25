package ir.tree;

abstract public class Stmt {
	abstract public ExpList kids();
	abstract public Stmt build(ExpList kids);
	
	public abstract void print(String prefix, boolean isTail);
}
