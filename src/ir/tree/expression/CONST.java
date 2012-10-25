package ir.tree.expression;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;

public class CONST extends Expr {

	public int value;
	
	public CONST(int value) { this.value = value; }
	
	@Override
	public ExpList kids() { return null; }

	@Override
	public Expr build(ExpList kids) { return this; }
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "CONST " + value);		
	}
	
}
