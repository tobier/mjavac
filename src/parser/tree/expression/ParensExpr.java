package parser.tree.expression;

import parser.Parser;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class ParensExpr extends Expr {
	public final Expr e;
	
	public ParensExpr(Expr e) {
		this.e = e;
		this.line = e.line;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);		
	}
	
	@Override
	public ir.translate.Expr accept(TranslateVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public Type accept(TypeVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "ParensExpr");
		e.print(prefix + (isTail ?"    " : "│   "), true);
	}
}
