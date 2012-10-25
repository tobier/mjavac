package ir.translate;

import ir.tree.statement.EXP;
import temp.Label;

public class Ex extends Expr {

	private ir.tree.Expr exp;
	
	public Ex(ir.tree.Expr e) {
		exp = e;
	}
	
	@Override
	public ir.tree.Stmt unCx(Label t, Label f) {
		
		if( exp instanceof ir.tree.expression.CONST ) {
			return ((ir.tree.expression.CONST)exp).value != 0 ? 
					new ir.tree.statement.JUMP(t) : new ir.tree.statement.JUMP(f);
		}
		
		ir.tree.Expr zero = new ir.tree.expression.CONST(0);
		ir.tree.statement.CJUMP.Cond neq = ir.tree.statement.CJUMP.Cond.NE;
		
		return new ir.tree.statement.CJUMP(neq, exp, zero, t, f);
	}

	@Override
	public ir.tree.Expr unEx() {
		return exp;
	}

	@Override
	public ir.tree.Stmt unNx() {
		return new EXP(exp);
	}

}
