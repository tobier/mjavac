package ir.tree;

public class ExpList {
	public Expr head;
	public ExpList tail;
	
	public ExpList(Expr h, ExpList t) {
		head = h; 
		tail = t;
	}
	
	public ExpList(Expr h) {
		head = h; 
		tail = null;
	}
	
	public ExpList(Expr e1, Expr e2) {
		head = e1; 
		tail = new ExpList(e2);
	}
	
	public void print(String prefix, boolean isTail) {
		head.print(prefix, tail == null ? true : false);
		if(tail != null) tail.print(prefix, false);
	}
}
