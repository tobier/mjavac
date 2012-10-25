package parser.tree.types;

import parser.Parser;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class IntArrayType extends Type {

	@Override
	public void accept(Visitor v) {
		v.visit(this);		
	}
	
	@Override
	public Type accept(TypeVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public Expr accept(TranslateVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "IntArrayType");
	}
	
	@Override
	public String toString() {
		return "int[]";
	}

}
