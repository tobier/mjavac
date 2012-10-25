package canon;

import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.StmList;
import ir.tree.Stmt;
import ir.tree.expression.CALL;
import ir.tree.expression.CONST;
import ir.tree.expression.ESEQ;
import ir.tree.expression.NAME;
import ir.tree.expression.TEMP;
import ir.tree.statement.EXP;
import ir.tree.statement.MOVE;
import ir.tree.statement.SEQ;
  
public class Canon {

	static boolean isNop(Stmt a) {
		return a instanceof EXP
		&& ((EXP)a).exp instanceof CONST;
	}

	static Stmt seq(Stmt a, Stmt b) {
		if (isNop(a)) return b;
		else if (isNop(b)) return a;
		else return new SEQ(a,b);
	}

	static boolean commute(Stmt a, Expr b) {
		return isNop(a)
		|| b instanceof NAME
		|| b instanceof CONST;
	}

	static Stmt do_stm(SEQ s) { 
		return seq(do_stm(s.left), do_stm(s.right));
	}

	static Stmt do_stm(MOVE s) { 
		if (s.dst instanceof TEMP 
				&& s.src instanceof CALL) 
			return reorder_stm(new MoveCall((TEMP)s.dst,
					(CALL)s.src));
		else if (s.dst instanceof ESEQ)
			return do_stm(new SEQ(((ESEQ)s.dst).stm,
					new MOVE(((ESEQ)s.dst).exp,
							s.src)));
		else return reorder_stm(s);
	}

	static Stmt do_stm(EXP s) { 
		if (s.exp instanceof CALL)
			return reorder_stm(new ExpCall((CALL)s.exp));
		else return reorder_stm(s);
	}

	static Stmt do_stm(Stmt s) {
		if (s instanceof SEQ) return do_stm((SEQ)s);
		else if (s instanceof MOVE) return do_stm((MOVE)s);
		else if (s instanceof EXP) return do_stm((EXP)s);
		else return reorder_stm(s);
	}

	static Stmt reorder_stm(Stmt s) {
		StmExpList x = reorder(s.kids());
		return seq(x.stm, s.build(x.exps));
	}

	static ESEQ do_exp(ESEQ e) {
		Stmt stms = do_stm(e.stm);
		ESEQ b = do_exp(e.exp);
		return new ESEQ(seq(stms,b.stm), b.exp);
	}

	static ESEQ do_exp(Expr e) {
		if (e instanceof ESEQ) return do_exp((ESEQ)e);
		else return reorder_exp(e);
	}

	static ESEQ reorder_exp(Expr e) {
		StmExpList x = reorder(e.kids());
		return new ESEQ(x.stm, e.build(x.exps));
	}

	static StmExpList nopNull = new StmExpList(new EXP(new CONST(0)), null);

	static StmExpList reorder(ExpList exps) {
		if (exps==null) return nopNull;
		else {
			Expr a = exps.head;
			if (a instanceof CALL) {
				temp.Temp t = new temp.Temp();
				Expr e = new ESEQ(new MOVE(new TEMP(t), a),
						new TEMP(t));
				return reorder(new ExpList(e, exps.tail));
			} else {
				ESEQ aa = do_exp(a);
				StmExpList bb = reorder(exps.tail);
				if (commute(bb.stm, aa.exp))
					return new StmExpList(seq(aa.stm,bb.stm), 
							new ExpList(aa.exp,bb.exps));
				else {
					temp.Temp t = new temp.Temp();
					return new StmExpList(
							seq(aa.stm, 
									seq(new MOVE(new TEMP(t),aa.exp),
											bb.stm)),
											new ExpList(new TEMP(t), bb.exps));
				}
			}
		}
	}

	static StmList linear(SEQ s, StmList l) {
		return linear(s.left,linear(s.right,l));
	}
	static StmList linear(Stmt s, StmList l) {
		if (s instanceof SEQ) return linear((SEQ)s, l);
		else return new StmList(s,l);
	}

	static public StmList linearize(Stmt s) {
		return linear(do_stm(s), null);
	}
}
