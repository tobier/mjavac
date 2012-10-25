package canon;

import ir.tree.StmList;

public class StmListList {
	public StmList head;
	public StmListList tail;

	public StmListList(StmList h, StmListList t) {
		head = h; 
		tail = t;
	}
	
	public StmListList(StmList h) {
		head = h;
		tail = null;
	}
}

