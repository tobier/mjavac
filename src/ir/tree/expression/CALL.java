package ir.tree.expression;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;

public class CALL extends Expr {

	public Expr func;
	public ExpList args;
	
	public CALL(Expr f, ExpList a) {
		func = f;
		args = a;
	}
	
	@Override
	public ExpList kids() { return new ExpList(func, args); }

	@Override
	public Expr build(ExpList kids) {
		return new CALL(kids.head, kids.tail);
	}

	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "CALL");		
		func.print(prefix + (isTail ? "    " : "│   "), false);
		args.print(prefix + (isTail ?"    " : "│   "), true);
	}
	
}
