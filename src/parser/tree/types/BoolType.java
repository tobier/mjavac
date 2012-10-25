package parser.tree.types;

import parser.Parser;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class BoolType extends Type {

	@Override
	public void accept(Visitor v) {
		v.visit(this);		
	}
	
	@Override
	public Type accept(TypeVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "BoolType");
	}
	
	@Override
	public Expr accept(TranslateVisitor v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		return "boolean";
	}
}
