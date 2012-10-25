package parser.tree.expression.operators.binary;

import parser.tree.expression.Expr;

public abstract class BinOpExpr extends Expr {

	public final Expr left;
	public final Expr right;
	
	public BinOpExpr(Expr left, Expr right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public abstract void print(String prefix, boolean isTail);

}
