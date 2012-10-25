package ir.translate;

import temp.Label;

public abstract class Expr {
	public abstract ir.tree.Expr unEx();
	public abstract ir.tree.Stmt unNx();
	public abstract ir.tree.Stmt unCx(Label t, Label f);
}