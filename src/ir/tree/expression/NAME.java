package ir.tree.expression;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;
import temp.Label;

public class NAME extends Expr {

	public Label label;
	
	public NAME(Label l) { label = l; }
	
	@Override
	public ExpList kids() { return null; }

	@Override
	public Expr build(ExpList kids) { return this; }
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "NAME " + label.toString());		
	}

}
