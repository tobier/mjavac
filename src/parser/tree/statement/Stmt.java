package parser.tree.statement;

import parser.tree.Node;
import parser.tree.types.Type;
import visitor.TypeVisitor;
import visitor.Visitor;

public abstract class Stmt extends Node {

	@Override
	public abstract void accept(Visitor v);
	@Override
	public abstract Type accept(TypeVisitor v);
	@Override
	public abstract void print(String prefix, boolean isTail);

}
