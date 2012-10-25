package parser.tree;

import ir.translate.Expr;

import java.util.List;

import parser.Parser;
import parser.tree.statement.Stmt;
import parser.tree.types.Type;


import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class MainClass extends Node {
	public final Id id;
	public final List<VarDecl> vars;
	public final List<Stmt> stmts;
	
	public MainClass(Id id, List<VarDecl> vars, List<Stmt> stmts) {
		this.id = id;
		this.vars = vars;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "MainClass");
		id.print(prefix + (isTail ?"    " : "│   "), (vars == null) && (stmts == null));
		if(vars != null) {
			for (int i = 0; i < vars.size() - 1; i++) {
				vars.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (vars.size() >= 1) {
            	vars.get(vars.size() - 1).print(prefix + (isTail ?"    " : "│   "), stmts == null);
            }
		}
		
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
