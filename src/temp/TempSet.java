package temp;

import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Representation of a set of temporaries, that can perform the usual
 * set operations.
 */
public class TempSet extends TreeSet<Temp> {

	private static final long serialVersionUID = 7038805703433190805L;

	public TempSet(SortedSet<Temp> s) {
		super(s);
	}

	public TempSet() {
		super();
	}

	public boolean union(TempSet t) {
		return addAll(t);
	}

	public boolean difference(TempSet t) {
		return removeAll(t);
	}

	public boolean intersection(TempSet t) {
		return retainAll(t);
	}

	public static TempSet union(TempSet t1, TempSet t2) {
		TempSet temp = new TempSet(t1);
		temp.addAll(t2);

		return temp;
	}

	public static TempSet difference(TempSet t1, TempSet t2) {
		TempSet temp = new TempSet(t1);
		temp.removeAll(t2);

		return temp;
	}

	public static TempSet intersection(TempSet t1, TempSet t2) {
		TempSet temp = new TempSet(t1);
		temp.retainAll(t2);

		return temp;
	}

}
