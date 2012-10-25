package canon;

import ir.tree.StmList;
import ir.tree.Stmt;
import ir.tree.statement.CJUMP;
import ir.tree.statement.JUMP;
import ir.tree.statement.LABEL;

public class BasicBlocks {
	public StmListList blocks;
	public temp.Label done;

	private StmListList lastBlock;
	private StmList lastStm;
	
	public BasicBlocks(StmList stms) {
		done = new temp.Label();
		mkBlocks(stms);
	}

	private void addStm(Stmt s) {
		lastStm = lastStm.tail = new StmList(s);
	}

	private void doStms(StmList l) {
		if (l==null) 
			doStms(new StmList(new JUMP(done)));
		else if (l.head instanceof JUMP  || l.head instanceof CJUMP) {
			addStm(l.head);
			mkBlocks(l.tail);
		} 
		else if (l.head instanceof LABEL)
			doStms(new StmList(new JUMP(((LABEL)l.head).label), l));
		else {
			addStm(l.head);
			doStms(l.tail);
		}
	}

	void mkBlocks(StmList l) {
		if (l==null) 
			return;
		else if (l.head instanceof LABEL) {
			lastStm = new StmList(l.head);
			if (lastBlock==null)
				lastBlock= blocks= new StmListList(lastStm);
			else
				lastBlock = lastBlock.tail = new StmListList(lastStm);
			doStms(l.tail);
		}
		else mkBlocks(new StmList(new LABEL(new temp.Label()), l));
	}
}
