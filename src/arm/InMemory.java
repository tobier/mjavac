package arm;

import ir.tree.expression.BINOP;
import ir.tree.expression.CONST;
import ir.tree.expression.MEM;
import frame.Access;

public class InMemory implements Access {
	
	private final int offset;
	
	public InMemory(int o) {
		offset = o;
	}
	
	@Override
	public ir.tree.Expr exp(ir.tree.Expr basePointer) {		
		return new MEM(new BINOP(BINOP.Op.PLUS, basePointer, new CONST(offset)));
	}
	
	@Override
	public String toString() {
		return "arm.InFrame(" + offset + ")";
	}

}
