package graph;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Graph implements Iterable<Node> {

	int nodecount = 0;
	protected LinkedList<Node> mynodes = new LinkedList<Node>();
	protected Dictionary<Node, Object> infoTable = new Hashtable<Node, Object>(); 

	public void addEdge(Node from, Node to) {
		if(from == null || to == null) return;

		check(from);
		check(to);

		if (from.goesTo(to)) return;

		to.preds.add(from);
		to.adjlist.add(from);

		from.succs.add(to);
		from.adjlist.add(to);
	}

	public void addUEdge(Node u, Node v) {
		if(u == null || v == null) return;

		check(u);
		check(v);

		if (u.adj(v) || v.adj(u)) return;

		u.adjlist.add(v);
		v.adjlist.add(u);
	}

	void check(Node n) {
		if (n.mygraph != this)
			throw new Error("Graph.addEdge using nodes from the wrong graph");
	}

	public Node newNode(Object info) {
		Node n = new Node(this);
		infoTable.put(n, info);

		return n;
	}

	public Object getNodeInfo(Node n) {
		return infoTable.get(n);
	}

	public int size() {
		return mynodes.size();
	}

	public List<Node> nodes() {
		return mynodes;
	}

	public void rmEdge(Node from, Node to) {
		if(from == null || to == null) return;

		to.preds.remove(from);
		from.succs.remove(to);
	}

	/**
	 * Print a human-readable dump for debugging.
	 */
	public void show(java.io.PrintStream out) {
		for (Node u : mynodes) {
			out.print(u.toString());
			out.print(": ");
			for (Node v : u.succs) {
				out.print(v.toString());
				out.print(" ");
			}
			out.println();
		}
	}

	@Override
	public Iterator<Node> iterator() {
		return mynodes.iterator();
	}

	public Iterator<Node> descendingIterator() {
		return mynodes.descendingIterator();
	}

}