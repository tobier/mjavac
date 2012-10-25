package symbol;

import java.util.HashMap;
import java.util.Iterator;

import parser.tree.Id;


public class Class {
	
	public Id id;
	private HashMap<Symbol, Variable> fields;
	private HashMap<Symbol, Method> methods;
	
	public Class(Id id) {
		this.id = id;
		fields = new HashMap<Symbol, Variable>();
		methods = new HashMap<Symbol, Method>();
	}
	
	public boolean putField(Symbol s, Variable v) {
		if( fields.containsKey(s) ) return false;
		fields.put(s, v);
		return true;
	}
	
	public Variable getField(Symbol s) {
		return fields.get(s);
	}
	
	public boolean putMethod(Symbol s, Method m) {
		if( methods.containsKey(s) ) return false;
		methods.put(s, m);
		return true;
	}
	
	public Method getMethod(Symbol s) {
		return methods.get(s);
	}
	
	public int size() {
		return fields.size();
	}
	
	public void print(String prefix) {
		Table.out.println(prefix + "class " + id.name);
	
		Table.out.println(prefix + "    Fields:");
		Iterator<Symbol> it = fields.keySet().iterator();
		while(it.hasNext()) {
			fields.get(it.next()).print(prefix + "        ");
		}
		if(fields.size() > 0) Table.out.println();
		
		Table.out.println(prefix + "    Methods:");
		it = methods.keySet().iterator();
		while(it.hasNext()) {
			methods.get(it.next()).print(prefix + "        ");
			Table.out.println();
		}
		if(methods.size() == 0) Table.out.println();
	}
}
