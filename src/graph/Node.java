package graph;

import java.util.LinkedList;
import java.util.List;

public class Node {

        Graph mygraph;
        private int mykey;

        LinkedList<Node> succs = new LinkedList<Node>();
        LinkedList<Node> preds = new LinkedList<Node>();
        LinkedList<Node> adjlist = new LinkedList<Node>();
        
        public Node(Graph g) {
                mygraph = g;
                setMykey(g.nodecount++);
                g.mynodes.add(this);
        }

        public Graph getMyGraph() {
                return mygraph;
        }
        
        public List<Node> adj() {
                return adjlist;
        }

        public boolean adj(Node n) {
                return adjlist.contains(n);
        }

        public boolean comesFrom(Node n) {
                return preds.contains(n);
        }

        public int degree() {
                return adjlist.size();
        }

        public boolean goesTo(Node n) {
                return succs.contains(n);
        }

        public int inDegree() {
                return preds.size();
        }

        public int outDegree() {
                return succs.size();
        }

        public List<Node> pred() {
                return preds;
        }

        public List<Node> succ() {
                return succs;
        }

        public String toString() {
                return String.valueOf(getMykey());
        }

        public void setMykey(int mykey) {
                this.mykey = mykey;
        }

        public int getMykey() {
                return mykey;
        }

}
