package ir.tree.statement;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.Stmt;
import ir.tree.expression.NAME;
import temp.Label;
import temp.LabelList;

public class JUMP extends Stmt {
	public Expr exp;
	public LabelList targets;
	public JUMP(Expr e, LabelList t) {exp=e; targets=t;}
	
	public JUMP(Label target) {
		this(new NAME(target), new LabelList(target));
	}
	
	public ExpList kids() {return new ExpList(exp);}
	
	public Stmt build(ExpList kids) {
		return new JUMP(kids.head,targets);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "JUMP");
		exp.print(prefix + (isTail ? "    " : "│   "), true);
	}
}
