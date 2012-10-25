package assem;

import java.util.Iterator;

public class InstrList implements Iterable<Instr>{
	public Instr head;
	public InstrList tail;
	public InstrList(Instr h, InstrList t) {
		head=h; tail=t;
	}
	@Override
	public Iterator<Instr> iterator() {
		return new It(this);
	}

	private class It implements Iterator<Instr>{

		InstrList l;

		public It(InstrList lst) {
			l = lst;
		}

		public boolean hasNext() {
			return l != null;
		}

		public Instr next() {
			Instr h = l.head;
			l = l.tail;
			return h;
		}

		public void remove() {
			l = l.tail;
		}

	}
}
