package parser.tree.statement;

import parser.Parser;
import parser.tree.types.Type;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class IfElse extends Stmt {

	public final If ifStmt;
	public final Stmt elseStmt;
	
	public IfElse(If ifStmt, Stmt elseStmt) {
		this.ifStmt = ifStmt;
		this.elseStmt = elseStmt;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "IfElse");		
		ifStmt.print(prefix + (isTail ?"    " : "│   "), false);
		elseStmt.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
