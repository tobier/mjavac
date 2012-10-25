package parser.tree;

import parser.Parser;
import parser.tree.types.Type;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class Id extends Node {
	public final String name;
	
	public Id(String id) {
		this.name = id;
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
	public Expr accept(TranslateVisitor v) {
		return v.visit(this);
	}
	
	@Override	
	public void print(String prefix, boolean isTail) {
        Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "Identifier[ " + name + " ]");
    }
}
