package ir.translate;

import ir.tree.Stmt;
import frame.Frame;

public class ProcFragment {

	public final Frame frame;
	public final Stmt body;
	
	public ProcFragment(Frame frame, Stmt body) {
		this.frame = frame;
		this.body = frame.procEntryExit1(body);
	}
	
}
