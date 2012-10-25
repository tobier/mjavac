package parser.tree.statement;

import parser.Parser;
import parser.tree.Id;
import parser.tree.expression.Expr;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class Assign extends Stmt {

	public final Id target;
	public final Expr assignValue;
	
	public Assign(Id target, Expr assignValue) {
		this.target = target;
		this.assignValue = assignValue;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "Assign");		
		target.print(prefix + (isTail ? "    " : "│   "), false);
		assignValue.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
