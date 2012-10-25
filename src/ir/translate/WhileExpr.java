package ir.translate;

import ir.tree.Stmt;
import ir.tree.statement.JUMP;
import ir.tree.statement.LABEL;
import ir.tree.statement.SEQ;
import temp.Label;

public class WhileExpr extends Cx {

	Expr testExpr, body;
	
	Label t = new Label(); // used for checking the true condition
	//Label f = new Label(); // used for checking the else condition
	
	public WhileExpr(Expr test, Expr body) {
		this.testExpr = test;
		this.body = body;
	}
	
	@Override
	public Stmt unCx(Label test, Label done) {
		
		Stmt testStmt = testExpr.unCx(t, done);		
		Stmt bodyStmt = body.unNx();
		
		SEQ bodySeq = new SEQ(new LABEL(t), new SEQ(bodyStmt, new JUMP(test)));
		SEQ testSeq = new SEQ(new LABEL(test), testStmt);
		
		SEQ rootSeq = new SEQ(new SEQ(testSeq, bodySeq), new LABEL(done));
		
		return rootSeq;
	}

	@Override
	public Stmt unNx() {
		return unCx(new Label(), new Label());
	}

}
