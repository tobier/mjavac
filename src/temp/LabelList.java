package temp;

import java.util.Iterator;

public class LabelList implements Iterable<Label> {
	public Label head;
	public LabelList tail;
	public LabelList(Label h, LabelList t) {head=h; tail=t;}
	public LabelList(Label h) {head=h; tail=null;}
	
	
	@Override
	public Iterator<Label> iterator() {
		return new It(this);
	}
	
	private class It implements Iterator<Label>{

		LabelList l;

		public It(LabelList lst) {
			l = lst;
		}

		public boolean hasNext() {
			return l != null;
		}

		public Label next() {
			Label h = l.head;
			l = l.tail;
			return h;
		}

		public void remove() {
			l = l.tail;
		}

	}
}