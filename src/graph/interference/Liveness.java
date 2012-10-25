package graph.interference;

import java.util.HashMap;
import java.util.Iterator;

import graph.Node;
import graph.flow.FlowGraph;
import temp.Temp;
import temp.TempSet;

public class Liveness extends InterferenceGraph {

	private FlowGraph flow;

	private HashMap<Node, TempSet> liveIn = new HashMap<Node, TempSet>();
	private HashMap<Node, TempSet> liveOut = new HashMap<Node, TempSet>();

	private HashMap<Node, Temp> revMap = new HashMap<Node, Temp>();
    private HashMap<Temp, Node> map = new HashMap<Temp, Node>();
    
	public Liveness(FlowGraph fg) {
		flow = fg;
		
		for(Node n : fg.nodes()) {
			liveIn.put(n, new TempSet());
			liveOut.put(n, new TempSet());
		}

		iterate();
		createGraph();
	}
	
	@Override
	public void addUEdge(Node src, Node dst)
    {
        if ( src != dst && !dst.adj(src))
            super.addUEdge(src, dst);
    }

	private void iterate() {
		boolean changed;
		Iterator<Node> li;
		
		do {
			li = flow.descendingIterator();
			changed = false;

			while(li.hasNext()) {
	
				Node n = li.next();
				
				TempSet in = liveIn.get(n);
				TempSet out = liveOut.get(n);
				
				TempSet inPrime = (TempSet)in.clone();
				TempSet outPrime = (TempSet)out.clone();
				
				// in[n] <- use[n] U (out[n] - def[n])
				in = TempSet.union(flow.use(n), TempSet.difference(out, flow.def(n)));
				
				if(!changed && !in.equals(inPrime))
					changed = true;
				
				// out[n] <- U in[s] where s is a successor of n
				out.clear();
				for(Node s : n.succ()) {
					out.union(liveIn.get(s));
				}
				
				if(!changed && !out.equals(outPrime))
					changed = true;
					
				liveIn.put(n, in);
				liveOut.put(n, out);
			}
		} while(changed);
	}
	
	private void createGraph() {
		
		for(Node n  : flow.nodes()) {
			if( flow.isMove(n) )
				handleMove(n);
			else
				handle(n);
		}
		
	}
	
	private void handleMove(Node n) {
		
		Node dst = tnode(flow.def(n).first());
		Node src = tnode(flow.use(n).first());
		
		for(Temp t : liveOut.get(n)) {
			Node currentOut = tnode(t);
			
			if( currentOut != src ) {
				addUEdge(dst, currentOut);
			}
		}
	}

	private void handle(Node n) {
		
		for(Temp t1 : flow.def(n)) {
			
			Node currentTemp = tnode(t1);
			
			for(Temp t2 : liveOut.get(n)) {
				Node currentLiveOut = tnode(t2);
				addUEdge(currentTemp, currentLiveOut);
			}
		}
	}
		 
	@Override
	public Node tnode(Temp temp) {
		Node n = map.get(temp);
        
        if ( n == null )
        {
            map.put(temp, n = newNode(temp) );
            revMap.put(n, temp);
        }
        
        return n;
	}

	@Override
	public Temp gtemp(Node node) {
		return revMap.get(node);
	}
}
