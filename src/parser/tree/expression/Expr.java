package parser.tree.expression;

import parser.tree.Node;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public abstract class Expr extends Node {

	@Override
	public abstract void accept(Visitor v);
	@Override
	public abstract Type accept(TypeVisitor v);
	@Override
	public abstract ir.translate.Expr accept(TranslateVisitor v);
	@Override
	public abstract void print(String prefix, boolean isTail);
}
