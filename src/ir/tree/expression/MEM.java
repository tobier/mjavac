package ir.tree.expression;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;

public class MEM extends Expr {

	public Expr exp;
	
	public MEM(Expr e) { exp = e; }
	
	@Override
	public ExpList kids() { return new ExpList(exp); }

	@Override
	public Expr build(ExpList kids) { return new MEM(kids.head); }
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "MEM");		
		exp.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
