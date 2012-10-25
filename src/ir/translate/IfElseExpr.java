package ir.translate;

import ir.tree.Stmt;
import ir.tree.statement.JUMP;
import ir.tree.statement.LABEL;
import ir.tree.statement.SEQ;
import temp.Label;

public class IfElseExpr extends Cx {

	Expr cond, ifCase, elseCase;
	
	Label join = new Label();
	
	public IfElseExpr(Expr cond, Expr ifCase, Expr elseCase) {
		this.cond = cond;
		this.ifCase = ifCase;
		this.elseCase = elseCase;
	}

	@Override
	public Stmt unCx(Label tt, Label ff) {
			
		Stmt condStmt = cond.unCx(tt, ff);
		Stmt ifCaseStmt = ifCase.unNx();
		Stmt elseCaseStmt = elseCase.unNx();
		
		SEQ trueSeq = new SEQ(new LABEL(tt), new SEQ(ifCaseStmt, new JUMP(join)));
		SEQ falseSeq = new SEQ(new LABEL(ff), elseCaseStmt); 
		SEQ joinSeq = new SEQ(falseSeq, new LABEL(join));
			
		SEQ rootSeq = new SEQ(condStmt, new SEQ(trueSeq, joinSeq));
		
		return rootSeq;
	}
	
	@Override
	public Stmt unNx() {
		return unCx(new Label(), new Label());
	}
}