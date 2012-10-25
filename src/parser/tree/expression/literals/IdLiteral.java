package parser.tree.expression.literals;

import parser.Parser;
import parser.tree.Id;
import parser.tree.expression.Expr;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class IdLiteral extends Expr {

	public final Id id;
	
	public IdLiteral(Id id) {
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
	public ir.translate.Expr accept(TranslateVisitor v) {
		return v.visit(this);
	}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "IdLiteral");
		id.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
