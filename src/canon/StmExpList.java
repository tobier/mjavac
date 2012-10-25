package canon;

import ir.tree.ExpList;
import ir.tree.Stmt;

class StmExpList {
	Stmt stm;
	ExpList exps;
	
	StmExpList(Stmt s, ExpList e) {
		stm = s; 
		exps = e;
	}
}
