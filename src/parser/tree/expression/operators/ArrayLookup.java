package parser.tree.expression.operators;

import parser.Parser;
import parser.tree.expression.Expr;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class ArrayLookup extends Expr {

	public final Expr array;
	public final Expr index;
	
	public ArrayLookup(Expr lookup, Expr which) {
		this.array = lookup; 
		this.index = which;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "[]");
		array.print(prefix + (isTail ? "    " : "│   "), false);
		index.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
