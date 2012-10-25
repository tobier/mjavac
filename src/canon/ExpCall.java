package canon;

import ir.tree.ExpList;
import ir.tree.Stmt;
import ir.tree.expression.CALL;
import ir.tree.statement.EXP;

class ExpCall extends Stmt {
	CALL call;
	
	ExpCall(CALL c) {
		call = c;
	}
	
	public ExpList kids() {
		return call.kids();
	}
	
	public Stmt build(ExpList kids) {
		return new EXP(call.build(kids));
	}

	@Override
	public void print(String prefix, boolean isTail) {
		call.print(prefix, isTail);
	}
}  