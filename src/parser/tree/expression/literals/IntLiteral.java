package parser.tree.expression.literals;

import java.math.BigInteger;

import parser.Parser;
import parser.tree.expression.Expr;
import parser.tree.types.Type;


import visitor.TranslateVisitor;
import visitor.TypeVisitor;
import visitor.Visitor;

public class IntLiteral extends Expr {

	public final BigInteger value;
	
	public IntLiteral(BigInteger value) {
		this.value = value;
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
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "IntLiteral[ " + value + " ]");
	}
}
