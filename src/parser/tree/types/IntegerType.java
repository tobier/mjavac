package parser.tree.types;

import parser.tree.types.Type;

public abstract class IntegerType extends Type {

	@Override
	public abstract String toString();
	@Override
	public abstract void print(String prefix, boolean isTail);

}
