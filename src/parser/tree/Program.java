package parser.tree;

import ir.translate.Expr;

import java.util.ArrayList;

import parser.Parser;
import parser.tree.types.Type;


import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class Program extends Node {
	public final MainClass main;
	public final ArrayList<ClassDecl> classes;
	
	public Program(MainClass mc, ArrayList<ClassDecl> cdl) {
		main = mc;
		classes = cdl;
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
		Parser.out.println("Program");
		if(classes != null) {
			main.print(prefix + (isTail ? "    " : "│   "), false);
			for (int i = 0; i < classes.size() - 1; i++) {
                classes.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (classes.size() >= 1) {
                classes.get(classes.size() - 1).print(prefix + (isTail ?"    " : "│   "), true);
            }
		} else {
			main.print(prefix + (isTail ? "    " : "│   "), true);
		}
	}

}
 