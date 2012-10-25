package graph.interference;
import graph.Node;
import graph.Graph;

abstract public class InterferenceGraph extends Graph {

	abstract public Node tnode(temp.Temp temp);

	abstract public temp.Temp gtemp(Node node);

	public int spillCost(Node node) {
		return node.degree();
	}

	/**
	 * Print a human-readable dump for debugging.
	 **/
	@Override
	public void show(java.io.PrintStream out) {
		for (Node u : mynodes) {
			out.print( gtemp(u).toString() );
			out.print(": ");
			for (Node v : u.adj()) {
				out.print( gtemp(v).toString() );
				out.print(" ");
			}
			out.println();
		}
	}
}
