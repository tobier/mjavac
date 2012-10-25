package ir.tree.expression;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;

public class BINOP extends Expr {

	public static enum Op { PLUS, MINUS, MUL, DIV, AND, OR, LSHIFT, RSHIFT, ARSHIFT, XOR };
	
	public Op binop;
	public Expr left, right;
	
	public BINOP(Op op, Expr l, Expr r) {
		binop = op;
		left = l;
		right = r;
	}
	
	@Override
	public ExpList kids() {
		return new ExpList(left, right);
	}

	@Override
	public Expr build(ExpList kids) {
		return new BINOP(binop, kids.head, kids.tail.head);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "BINOP");
		Parser.out.println(prefix + (isTail ? "    " : "│   ") + "├── " + binop.toString());
		left.print(prefix + (isTail ? "    " : "│   "), false);
		right.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
