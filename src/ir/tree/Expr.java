package ir.tree;

abstract public class Expr {
	abstract public ExpList kids();
	abstract public Expr build(ExpList kids);
	
	public abstract void print(String prefix, boolean isTail);
}
