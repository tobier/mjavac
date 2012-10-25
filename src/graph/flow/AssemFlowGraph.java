package graph.flow;

import graph.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import temp.Label;
import temp.LabelList;
import temp.Temp;
import temp.TempSet;

import assem.Instr;
import assem.InstrList;
import assem.LABEL;
import assem.MOVE;

public class AssemFlowGraph extends FlowGraph {

	HashMap<Label, Node> labeledNodes;
	HashMap<Node, TempSet> defs;
	HashMap<Node, TempSet> uses;
	List<Node> moves;

	public AssemFlowGraph(InstrList instructions) {

		labeledNodes = new HashMap<Label, Node>();
		uses = new HashMap<Node, TempSet>();
		defs = new HashMap<Node, TempSet>();
		moves = new LinkedList<Node>();
		
		Node prev = null;

		for(Instr i : instructions) {
			
			Node n = newNode(i);

			if(i instanceof LABEL) {
				labeledNodes.put(((LABEL)i).label, n);
			} else if( i instanceof MOVE ) {
				moves.add(n);
			}

			TempSet ts_use = new TempSet();
			TempSet ts_def = new TempSet();

			if(i.use() != null)
				for(Temp t : i.use())
					ts_use.add(t);

			if(i.def() != null)
				for(Temp t : i.def())
					ts_def.add(t);

			uses.put(n, ts_use);
			defs.put(n, ts_def);

			addEdge(prev, n);

			prev = n;
		}

		// Don't forget to add edges that are a result of a jump
		for(ListIterator<Node> li = mynodes.listIterator(); li.hasNext(); ) {
			Node n = li.next();
			Instr i = (Instr)infoTable.get(n);

			if(i.jumps() != null) {
				LabelList list = i.jumps().labels;
				for(Label l : list)
					addEdge(n, labeledNodes.get(l));

				// Remove fall-through edge if this is a unconditional jump
				if(i.isUnconditionalJump()) {
					rmEdge(n, li.next());
					li.previous();
				}
			}
		}
	}

	@Override
	public TempSet def(Node node) {
		return defs.get(node);
	}

	@Override
	public TempSet use(Node node) {
		return uses.get(node);
	}

	@Override
	public boolean isMove(Node node) {
		return moves.contains(node);
	}

	@Override
	public List<Node> moves() {
		return moves;
	}

}
