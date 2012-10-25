package symbol;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Table {

	private HashMap<Symbol, Class> classes;
	public static PrintStream out = System.out;
	
	public Class mainClass;
	
	public Table() {
		classes = new HashMap<Symbol, Class>();
	}
	
	public boolean putClass(Symbol s, Class c) {
		if( classes.containsKey(s) ) return false;
		classes.put(s,c);
		return true;
	}
	
	public Class getClass(Symbol s) {
		return classes.get(s);
	}
	
	public Collection<Class> getAll() {
		return classes.values();
	}
	
	public void print() {
		Table.out.println("MainClass: " + mainClass.id.name + "\n");
		
		Iterator<Symbol> it = classes.keySet().iterator();
		
		while(it.hasNext()) {
			classes.get(it.next()).print("    ");
		}
	}
}
