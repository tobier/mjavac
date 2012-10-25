package ir.translate;

import temp.Label;

public class Nx extends Expr {

	ir.tree.Stmt stm;
	
	public Nx(ir.tree.Stmt s) {
		stm = s;
	}
	
	@Override
	public ir.tree.Stmt unCx(Label t, Label f) {
		throw new Error("unimplemented Nx:unCx (should never happen)");
	}

	@Override
	public ir.tree.Expr unEx() {
		throw new Error("unimplemented Nx:unEx (should never happen)");
	}

	@Override
	public ir.tree.Stmt unNx() {
		return stm;
	}

}
