package parser.tree.statement;

import parser.Parser;
import parser.tree.Id;
import parser.tree.expression.Expr;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class AssignIndexedElement extends Stmt {

	public final Id target;
	public final Expr indexExpr;
	public final Expr assignValue;
	
	public AssignIndexedElement(Id target, Expr indexExpr, Expr assignValue) {
		this.target = target;
		this.indexExpr = indexExpr;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "AssignIndexedElement");		
		target.print(prefix + (isTail ? "    " : "│   "), false);
		indexExpr.print(prefix + (isTail ? "    " : "│   "), false);
		assignValue.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
