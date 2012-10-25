package canon;

import ir.tree.ExpList;
import ir.tree.Stmt;
import ir.tree.expression.CALL;
import ir.tree.expression.TEMP;
import ir.tree.statement.MOVE;

class MoveCall extends Stmt {
	TEMP dst;
	CALL src;
	
	MoveCall(TEMP d, CALL s) {
		dst = d; 
		src = s;
	}
	
	public ExpList kids() {return src.kids();}
	
	public Stmt build(ExpList kids) {
		return new MOVE(dst, src.build(kids));
	}

	@Override
	public void print(String prefix, boolean isTail) {
		dst.print(prefix, false);
		src.print(prefix, true);
	}
} 
