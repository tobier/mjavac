package canon;

import java.util.Dictionary;

import ir.tree.StmList;
import ir.tree.Stmt;
import ir.tree.statement.CJUMP;
import ir.tree.statement.JUMP;
import ir.tree.statement.LABEL;

public class TraceSchedule {

	public StmList stms;
	private BasicBlocks theBlocks;
	@SuppressWarnings("rawtypes")
	private Dictionary table = new java.util.Hashtable();

	StmList getLast(StmList block) {
		StmList l=block;
		while (l.tail.tail!=null)  
			l=l.tail;
		return l;
	}
	
	@SuppressWarnings("unchecked")
	public TraceSchedule(BasicBlocks b) {
		theBlocks=b;
		for(StmListList l = b.blocks; l!=null; l=l.tail)
			table.put(((LABEL)l.head.head).label, l.head);
		stms=getNext();
		table=null;
	} 

	void trace(StmList l) {
		for(;;) {
			LABEL lab = (LABEL)l.head;
			table.remove(lab.label);
			StmList last = getLast(l);
			Stmt s = last.tail.head;
			if (s instanceof JUMP) {
				JUMP j = (JUMP)s;
				StmList target = (StmList)table.get(j.targets.head);
				if (j.targets.tail==null && target!=null) {
					last.tail=target;
					l=target;
				}
				else {
					last.tail.tail=getNext();
					return;
				}
			}
			else if (s instanceof CJUMP) {
				CJUMP j = (CJUMP)s;
				StmList t = (StmList)table.get(j.iftrue);
				StmList f = (StmList)table.get(j.iffalse);
				if (f!=null) {
					last.tail.tail=f; 
					l=f;
				}
				else if (t!=null) {
					last.tail.head=new CJUMP(CJUMP.notRel(j.cond),
							j.left,j.right,
							j.iffalse,j.iftrue);
					last.tail.tail=t;
					l=t;
				}
				else {
					temp.Label ff = new temp.Label();
					last.tail.head=new CJUMP(j.cond,j.left,j.right,
							j.iftrue,ff);
					last.tail.tail=new StmList(new LABEL(ff),
							new StmList(new JUMP(j.iffalse),
									getNext()));
					return;
				}
			}
			else throw new Error("Bad basic block in TraceSchedule");
		}
	}

	StmList getNext() {
		if (theBlocks.blocks==null) 
			return new StmList(new LABEL(theBlocks.done), null);
		else {
			StmList s = theBlocks.blocks.head;
			LABEL lab = (LABEL)s.head;
			if (table.get(lab.label) != null) {
				trace(s);
				return s;
			}
			else {
				theBlocks.blocks = theBlocks.blocks.tail;
				return getNext();
			}
		}
	}       
}


