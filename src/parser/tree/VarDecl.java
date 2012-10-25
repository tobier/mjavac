package parser.tree;

import parser.Parser;
import parser.tree.types.Type;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class VarDecl extends Node {
	public final Type type;
	public final Id id;
	
	public VarDecl(Type type, Id id) {
		this.type = type;
		this.id = id;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "VarDecl");		
		type.print(prefix + (isTail ? "    " : "│   "), false);
		id.print(prefix + (isTail ?"    " : "│   "), true);
	}
}
