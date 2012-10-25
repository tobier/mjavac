package parser.tree;

import parser.tree.types.Type;
import ir.translate.Expr;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public abstract class Node {
	
	public int line = 0;
	
	public abstract void accept(Visitor v);
	public abstract Type accept(TypeVisitor v);
	public abstract Expr accept(TranslateVisitor v);
	public abstract void print(String prefix, boolean isTail);
}
