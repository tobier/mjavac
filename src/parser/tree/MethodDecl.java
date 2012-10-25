package parser.tree;

import ir.translate.Expr;

import java.util.List;

import parser.Parser;
import parser.tree.statement.Return;
import parser.tree.statement.Stmt;
import parser.tree.types.Type;


import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class MethodDecl extends Node {

	public final Type returnType;
	public final Id id;
	public final List<Formal> fl;
	public final List<VarDecl> vars;
	public final List<Stmt> stmts;
	public final Return returnStmt;
	
	public MethodDecl(Type returnType, Id id, List<Formal> fl, List<VarDecl> vars, List<Stmt> stmts, Return returnStmt) {
		this.returnType = returnType;
		this.id = id;
		this.fl = fl;
		this.vars = vars;
		this.stmts = stmts;
		this.returnStmt = returnStmt;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "MethodDecl");		
		returnType.print(prefix + (isTail ? "    " : "│   "), false);
		id.print(prefix + (isTail ?"    " : "│   "), false);
		if(fl != null) {
			for (int i = 0; i < fl.size() - 1; i++) {
				fl.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (fl.size() >= 1) {
            	fl.get(fl.size() - 1).print(prefix + (isTail ?"    " : "│   "), false);
            }
		}
		
		if(vars != null) {
			for (int i = 0; i < vars.size() - 1; i++) {
				vars.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (vars.size() >= 1) {
            	vars.get(vars.size() - 1).print(prefix + (isTail ?"    " : "│   "), false);
            }
		}
		
		if(stmts != null) {
			for (int i = 0; i < stmts.size() - 1; i++) {
				stmts.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (stmts.size() >= 1) {
            	stmts.get(stmts.size() - 1).print(prefix + (isTail ?"    " : "│   "), false);
            }
		}
		
		returnStmt.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
