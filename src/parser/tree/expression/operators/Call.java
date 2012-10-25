package parser.tree.expression.operators;

import java.util.ArrayList;

import parser.Parser;
import parser.tree.Id;
import parser.tree.expression.Expr;
import parser.tree.types.Type;


import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class Call extends Expr {

	public final Expr caller;
	public final Id method;
	public final ArrayList<Expr> args;
	public Type returnType = null;
	
	public Call(Expr caller, Id method, ArrayList<Expr> args) {
		this.caller = caller;
		this.method = method;
		this.args = args;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "Call");		
		caller.print(prefix + (isTail ? "    " : "│   "), false);
		method.print(prefix + (isTail ?"    " : "│   "), args == null);
		if(args != null) {
			for (int i = 0; i < args.size() - 1; i++) {
				args.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (args.size() >= 1) {
            	args.get(args.size() - 1).print(prefix + (isTail ?"    " : "│   "), true);
            }
		}

	}

}
