package ir.tree.statement;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.Stmt;
import temp.Label;

public class CJUMP extends Stmt {

	public enum Cond { EQ, NE, LT, GT, LE, GE, ULT, ULE, UGT, UGE };

	public Cond cond;

	public Expr left, right;
	public Label iftrue, iffalse;
	public CJUMP(Cond rel, Expr l, Expr r, Label t, Label f) {
		cond=rel; left=l; right=r; iftrue=t; iffalse=f;
	}
	
	public void neg() {
		switch(cond) {
			case EQ:
				cond = Cond.NE;
				break;
			case NE:
				cond = Cond.EQ;
				break;
			case LT:
				cond = Cond.GE;
				break;
			case GT:
				cond = Cond.LE;
				break;
			case LE:
				cond = Cond.GT;
				break;
			case GE:
				cond = Cond.LT;
				break;
			case ULT:
				cond = Cond.UGE;
				break;
			case ULE:
				cond = Cond.UGT;
				break;
			case UGT:
				cond = Cond.ULE;
				break;
			case UGE:
				cond = Cond.ULT;
				break;
		}
	}
	
	public static Cond notRel(Cond c) {
		Cond cond = null;
		switch(c) {
			case EQ:
				cond = Cond.NE;
				break;
			case NE:
				cond = Cond.EQ;
				break;
			case LT:
				cond = Cond.GE;
				break;
			case GT:
				cond = Cond.LE;
				break;
			case LE:
				cond = Cond.GT;
				break;
			case GE:
				cond = Cond.LT;
				break;
			case ULT:
				cond = Cond.UGE;
				break;
			case ULE:
				cond = Cond.UGT;
				break;
			case UGT:
				cond = Cond.ULE;
				break;
			case UGE:
				cond = Cond.ULT;
				break;
		}
		
		return cond;
	}
	
	public ExpList kids() {
		return new ExpList(left, new ExpList(right));
	}
	
	public Stmt build(ExpList kids) {
		return new CJUMP(cond,kids.head,kids.tail.head,iftrue,iffalse);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "CJUMP");
		Parser.out.println(prefix + (isTail ?"    " : "│   ") + "├── " + cond.name());
		left.print(prefix + (isTail ?"    " : "│   "), false);
		right.print(prefix + (isTail ?"    " : "│   "), false);
		Parser.out.println(prefix + (isTail ?"    " : "│   ") + "├── " + iftrue.toString());
		Parser.out.println(prefix + (isTail ?"    " : "│   ") + "└── " + iffalse.toString());
	}

}
