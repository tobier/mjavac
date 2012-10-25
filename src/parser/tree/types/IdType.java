package parser.tree.types;

import parser.Parser;
import parser.tree.Id;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class IdType extends Type {

	public final Id id;
	
	public IdType(Id id) {
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "IdType");
		id.print(prefix + (isTail ?"    " : "│   "), true);
	}

	@Override
	public String toString() {
		return id.name;
	}
	
}
