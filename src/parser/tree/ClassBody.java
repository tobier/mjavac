package parser.tree;

import ir.translate.Expr;

import java.util.ArrayList;

import parser.Parser;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class ClassBody extends Node {

	public final ArrayList<VarDecl> vars;
	public final ArrayList<MethodDecl> methods;
	
	public ClassBody(ArrayList<VarDecl> vars, ArrayList<MethodDecl> methods) {
		this.vars = vars;
		this.methods = methods;
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
		if(vars != null) {
			Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "ClassBody");
			for (int i = 0; i < vars.size() - 1; i++) {
                vars.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (vars.size() >= 1) {
                vars.get(vars.size() - 1).print(prefix + (isTail ?"    " : "│   "), methods == null);
            }
		}
		if(methods != null) {
			if(vars == null) Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "ClassBody");
			for (int i = 0; i < methods.size() - 1; i++) {
				methods.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (methods.size() >= 1) {
            	methods.get(methods.size() - 1).print(prefix + (isTail ?"    " : "│   "), true);
            }
		}
	}
}
