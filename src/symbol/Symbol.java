package symbol;

import java.util.HashMap;
import java.util.Map;

public final class Symbol {

	private final String name;
	private static Map<String, Symbol> symbols = new HashMap<String, Symbol>();
	
	private Symbol(String name) { this.name = name; }
	
	@Override
	public String toString() { return name; }

	public static Symbol symbol(String n) {
		String u = n.intern();
		Symbol s = symbols.get(u);
		if(s == null) { s = new Symbol(u); symbols.put(u, s); }
		return s;
	}
}
