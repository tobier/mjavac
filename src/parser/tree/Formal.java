package parser.tree;

import parser.Parser;
import parser.tree.types.Type;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class Formal extends Node {
	public final Type type;
	public final Id id;
	
	public Formal(Type type, Id id) {
		this.type = type;
		this.id = id;
	}
	
	@Override
	public Type accept(TypeVisitor v) {
		return v.visit(this);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);		
	}
	
	@Override
	public Expr accept(TranslateVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "Formal");		
		type.print(prefix + (isTail ? "    " : "│   "), false);
		id.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
