package temp;

import java.util.Iterator;

public class TempList implements Iterable<Temp>{
   public Temp head;
   public TempList tail;
   public TempList(Temp h, TempList t) {head=h; tail=t;}
   
   @Override
	public Iterator<Temp> iterator() {
		return new It(this);
	}

	private class It implements Iterator<Temp>{

		TempList l;

		public It(TempList lst) {
			l = lst;
		}

		public boolean hasNext() {
			return l != null;
		}

		public Temp next() {
			Temp h = l.head;
			l = l.tail;
			return h;
		}

		public void remove() {
			l = l.tail;
		}

	}
}

