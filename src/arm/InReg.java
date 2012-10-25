package arm;

import frame.Access;
import ir.tree.expression.TEMP;
import temp.Temp;

public class InReg implements Access {

	private final Temp reg;
	
	public InReg() {
		reg = new Temp();
	}
	
	public InReg(Temp r) {
		reg = r;
	}
	
	@Override
	public ir.tree.Expr exp(ir.tree.Expr basePointer) {
		return new TEMP(reg);
	}

	@Override
	public String toString() {
		return "arm.InReg(" + reg.toString() + ")";
	}
}
