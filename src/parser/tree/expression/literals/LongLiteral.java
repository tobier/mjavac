package parser.tree.expression.literals;

import java.math.BigInteger;

import parser.Parser;
import parser.tree.expression.Expr;
import parser.tree.types.Type;
import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class LongLiteral extends Expr {

	// should this really be so?
	public final BigInteger value;
	
	public LongLiteral(BigInteger value) {
		this.value = value;
	}
	
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "IntLiteral[ " + value + "L ]");
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	@Override
	public ir.translate.Expr accept(TranslateVisitor v) {
		return v.visit(this);
	}

	@Override
	public Type accept(TypeVisitor v) {
		return v.visit(this);
	}
}
