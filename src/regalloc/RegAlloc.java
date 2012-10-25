package regalloc;

import graph.Node;
import graph.flow.AssemFlowGraph;
import graph.flow.FlowGraph;
import graph.interference.InterferenceGraph;
import graph.interference.Liveness;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import frame.Frame;
import assem.InstrList;

import temp.Temp;
import temp.TempMap;

public class RegAlloc implements TempMap {

	private Frame frame;
	private FlowGraph fgraph;
    private InterferenceGraph igraph;
    
    private List<Node> simplifyWorklist;
	private List<Node> coloredNodes;
    
    private LinkedHashMap<Temp, String> colors = null;
    private LinkedHashMap<Temp, String> preColored = null;
	private int[] degree;
	
	private static Stack<Node> selectStack;
    
	public RegAlloc(Frame f, InstrList il) {
		frame = f;
		simplifyWorklist =  new LinkedList<Node>();
		coloredNodes = new LinkedList<Node>();
		selectStack = new Stack<Node>();
		
		prepareColors();
		
		Main(il);
	}

	private void Main(InstrList il) {
		fgraph = LivenessAnalysis(il);
		igraph = Build(fgraph);
		
		//fgraph.show(System.out);
		//System.out.println();
		//igraph.show(System.out);
		
		MakeWorklist();
		
		do {
			Simplify();
		} while ( !simplifyWorklist.isEmpty() );
		
		try {
			AssignColors();
		} catch(Exception e) {
			e.printStackTrace();
			throw new Error("Too many registers are alive, and spilling is not supported.");
		}
		
	}

	/**
	 * Perform liveness analysis and return the
	 * flow graph.
	 */
	private FlowGraph LivenessAnalysis(InstrList il) {
		return new AssemFlowGraph(il);
	}

	/**
	 * Build an interference graph and return it.
	 */
	private InterferenceGraph Build(FlowGraph fg) {
		return new Liveness(fg);
	}


	private void MakeWorklist() {
		degree = new int[igraph.nodes().size()];

		
		for( Node n : igraph.nodes() ) {
			if( !isPrecolored(n) ) {
				degree[n.getMykey()] = n.degree();	
				simplifyWorklist.add(n);
			}
		}
		
	}


	private void Simplify() {
		if( simplifyWorklist.isEmpty() ) return;
		
		Node n = simplifyWorklist.remove(0);
		selectStack.push(n);
		for(Node m : n.adj())
			decrementDegree(m);
	}
	
	@SuppressWarnings("unchecked")
	private void AssignColors() throws Exception {
		
		Node n;
		LinkedList<String> okColors;
		LinkedList<String> allColors = new LinkedList<String>(colors.values());
		
		// Don't even consider the special registers
		for(Temp t : frame.specialRegs())
			allColors.remove(frame.tempMap(t));
		
		while( !selectStack.empty() ) {
			
			n = selectStack.pop();
			
			// If n is precolored, don't color it again!
			if(isPrecolored(n)) {
				coloredNodes.add(n);
				continue;
			}
			
			okColors = (LinkedList<String>)allColors.clone();
						
			for(Node w : n.adj() ) {
				if(isPrecolored(w) || coloredNodes.contains(w) ) {
					String color = colors.get(igraph.gtemp(w));
					okColors.remove(color);
				}
			}
			
			colors.put(igraph.gtemp(n), okColors.getFirst());
			coloredNodes.add(n);
		} 
		
	}

	private void prepareColors() {
		
		if( colors == null ) {
			colors = new LinkedHashMap<Temp, String>();
			preColored = new LinkedHashMap<Temp, String>();
			
			for( Temp t : frame.registers())
				colors.put(t, frame.tempMap(t));
			
			// We sometime use real registers explicitly, so let all of
			// them be considered precolored
			for(Temp t : frame.registers() )
				preColored.put(t, frame.tempMap(t));
			
		}
	}
	
	private void decrementDegree(Node m) {
		int d = degree[m.getMykey()];
		
		degree[m.getMykey()] = d-1;
		
		if( d == frame.registers().size() )
			simplifyWorklist.add(m);
	}
	
	private boolean isPrecolored(Node n) {
		return preColored.get(igraph.gtemp(n)) != null;
	}
	
	@Override
	public String tempMap(Temp t) {
		return colors.get(t);
	}
	
}
