package parser.tree.statement;

import parser.Parser;
import parser.tree.expression.Expr;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class If extends Stmt {

	public final Expr boolExpr;
	public final Stmt statement;
	
	public If(Expr boolExpr, Stmt statement) {
		this.boolExpr = boolExpr;
		this.statement = statement;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "If");		
		boolExpr.print(prefix + (isTail ? "    " : "│   "), false);
		statement.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
