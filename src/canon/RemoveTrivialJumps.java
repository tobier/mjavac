package canon;

import ir.tree.StmList;
import ir.tree.Stmt;
import ir.tree.statement.JUMP;
import ir.tree.statement.LABEL;

public class RemoveTrivialJumps
{
	StmList stms;

	public StmList stms() 
	{
		return stms;
	}

	public  RemoveTrivialJumps(StmList s) 
	{
		stms = fix(s);	
	}

	private StmList L(Stmt a, StmList s)
	{
		return new StmList(a,s);
	}

	private StmList fix(StmList ss)
	{
		if (ss == null) return null;

		Stmt h = ss.head;	
		StmList t = fix(ss.tail);

		if ( t == null || ! (h instanceof JUMP && (t.head instanceof LABEL)))
			return L(h,t);

		JUMP jmp = (JUMP)  h;

		if (jmp.targets.tail != null)  
			return L(h, t);

		String labl = ((LABEL) t.head).label.toString();
		String labj = jmp.targets.head.toString();

		if (labl == labj) return t;

		return L(h,t);
	}
}


