package parser.tree;

import parser.Parser;
import parser.tree.types.Type;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class ClassDecl extends Node {
	public final Id id;
	public final ClassBody cb;
	
	public ClassDecl(Id id, ClassBody cb) {
		this.id = id;
		this.cb = cb;
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
	
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "ClassDecl");
		if (cb != null) {
			id.print(prefix + (isTail ? "    " : "│   "), false);
			cb.print(prefix + (isTail ?"    " : "│   "), true);
		} else {
			id.print(prefix + (isTail ? "    " : "│   "), true);
		}
		
	}
}
