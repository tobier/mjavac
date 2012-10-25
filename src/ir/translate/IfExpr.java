package ir.translate;

import ir.tree.Stmt;
import ir.tree.statement.LABEL;
import ir.tree.statement.SEQ;
import temp.Label;

public class IfExpr extends Cx {

	Expr cond, ifCase;
	
	public IfExpr(Expr cond, Expr ifCase) {
		this.cond = cond;
		this.ifCase = ifCase;
	}

	@Override
	public Stmt unCx(Label tt, Label ff) {
		
		// tt is the true label, ff is the false label.
		// note that there is no "join" label.
	
		Stmt condStmt = cond.unCx(tt, ff);
		Stmt ifCaseStmt = ifCase.unNx();
		
		SEQ trueSeq = new SEQ(new LABEL(tt), ifCaseStmt);
		
		SEQ rootSeq = new SEQ(condStmt, new SEQ(trueSeq, new LABEL(ff)));
			
		return rootSeq;
	}
	
	@Override
	public Stmt unNx() {
		return unCx(new Label(), new Label());
	}
}