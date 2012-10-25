package ir.tree.statement;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.Stmt;

public class EXP extends Stmt {

	public Expr exp;
	
	public EXP(Expr e) { exp = e; }
	
	@Override
	public ExpList kids() { return new ExpList(exp); }

	@Override
	public Stmt build(ExpList kids) { return new EXP(kids.head); }

	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "EXP");
		exp.print(prefix + (isTail ? "    " : "│   "), true);
	}
}
