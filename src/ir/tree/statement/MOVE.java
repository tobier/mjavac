package ir.tree.statement;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.Stmt;
import ir.tree.expression.MEM;

public class MOVE extends Stmt {

	public Expr dst, src;
	
	public MOVE(Expr d, Expr s) {
		dst = d;
		src = s;
	}
	
	@Override
	public ExpList kids() {
		if( dst instanceof MEM) {
			return new ExpList( ((MEM)dst).exp , src);
		} else {
			return new ExpList(src);
		}
	}

	@Override
	public Stmt build(ExpList kids) {
		if( dst instanceof MEM ) {
			return new MOVE(new MEM(kids.head), kids.tail.head);
		} else {
			return new MOVE(dst, kids.head);
		}
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "MOVE");
		dst.print(prefix + (isTail ? "    " : "│   "), false);
		src.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
