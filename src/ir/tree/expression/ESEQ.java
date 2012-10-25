package ir.tree.expression;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.Stmt;

public class ESEQ extends Expr {

	public Stmt stm;
	public Expr exp;
	
	public ESEQ(Stmt s, Expr e) {
		stm = s;
		exp = e;
	}
	
	@Override
	public ExpList kids() { throw new Error("kids() not applicable to ESEQ"); }

	@Override
	public Expr build(ExpList kids) {throw new Error("build() not applicable to ESEQ");}
	
	@Override
	public void print(String prefix, boolean isTail) {
		Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "ESEQ");		
		stm.print(prefix + (isTail ? "    " : "│   "), false);
		exp.print(prefix + (isTail ?"    " : "│   "), true);
	}

}
