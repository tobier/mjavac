package parser.tree.expression.operators.binary;

import parser.Parser;
import parser.tree.expression.Expr;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class OrExpr extends BinOpExpr {

	public OrExpr(Expr left, Expr right) {
		super(left, right);
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);		
	}
	
	@Override
	public Type accept(TypeVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public ir.translate.Expr accept(TranslateVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "OR");
		left.print(prefix + (isTail ? "    " : "│   "), false);
		right.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
