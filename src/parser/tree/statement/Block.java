package parser.tree.statement;

import ir.translate.Expr;

import java.util.List;

import parser.Parser;
import parser.tree.types.Type;


import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class Block extends Stmt {

	public final List<Stmt> stmts;
	
	public Block(List<Stmt> stmts) {
		this.stmts = stmts;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "Block");
		if(stmts != null) {
			for (int i = 0; i < stmts.size() - 1; i++) {
	               stmts.get(i).print(prefix + (isTail ? "    " : "│   "), false);
	        }
	        if (stmts.size() >= 1) {
	        	stmts.get(stmts.size() - 1).print(prefix + (isTail ?"    " : "│   "), true);
	        }
		}
	}

}
