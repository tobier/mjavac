package ir.tree;

public class StmList {
	public Stmt head;
	public StmList tail;
	
	public StmList(Stmt h, StmList t) {
		head = h; 
		tail = t;
	}
	
	public StmList(Stmt h) {
		head = h;
		tail = null;
	}
	
	public void print(String prefix, boolean isTail) {
		head.print(prefix, tail == null);
		if(tail != null) tail.print(prefix, isTail);
	}
}
