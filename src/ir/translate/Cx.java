package ir.translate;

import ir.tree.expression.*;
import ir.tree.statement.*;
import temp.Label;
import temp.Temp;

public abstract class Cx extends Expr {

	@Override
	public abstract ir.tree.Stmt unCx(Label t, Label f);

	@Override
	public ir.tree.Expr unEx() {
		System.out.println("körde unEx i Cx");
		
		Temp r = new Temp();
		Label t = new Label();
		Label f = new Label();
		
		return new ESEQ(
					new SEQ(
						new MOVE(new TEMP(r), new CONST(1)),
						new SEQ(
							unCx(t,f),
							new SEQ(
								new LABEL(f),
								new SEQ(
									new MOVE(new TEMP(r), new CONST(0)),
									new LABEL(t)
									)
								)
							)
						),
						new TEMP(r));
	}

	@Override
	public abstract ir.tree.Stmt unNx();
}
