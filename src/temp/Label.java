package temp;

import symbol.Symbol;

public class Label {
	private static int count = 0;
	private final String label;
	
	public Label(String n) { label = n; }
	public Label() { this("L" + count++); }
	public Label(Symbol s) { this(s.toString()); }
	
	@Override
	public String toString() { return label; }
	
	@Override
	public int hashCode() {
		return label.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if( !(o instanceof Label) ) return false;
		return this.hashCode() == o.hashCode();
	}
}
