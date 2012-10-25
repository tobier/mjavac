package ir.tree.expression;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;
import temp.Temp;

public class TEMP extends Expr {

	public Temp t;
	
	public TEMP(Temp t) { this.t = t; }
	
	@Override
	public ExpList kids() { return null; }

	@Override
	public Expr build(ExpList kids) { return this; }
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "TEMP " + t.toString());		
	}

}
