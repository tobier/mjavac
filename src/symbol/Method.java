package symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import parser.tree.Id;
import parser.tree.types.Type;

public class Method {
	public Id id;
	public Type returnType;
	
	public HashMap<Symbol, Variable> params;
	public ArrayList<Variable> param_list; // needed to check parameter order, as hashmaps keyset is in no particual order
	public HashMap<Symbol, Variable> locals;
	
	public Method(Id id, Type returnType) { 
		this.id = id; 
		this.returnType = returnType;
		params = new HashMap<Symbol, Variable>();
		locals = new HashMap<Symbol, Variable>();
		param_list = new ArrayList<Variable>();
	}
	
	public void putParam(Symbol s, Variable v) {
		params.put(s,v);
		param_list.add(v);
	}
	
	public void putLocal(Symbol s, Variable v) {
		locals.put(s,v);
	}
	
	public Variable get(Symbol s) {
		Variable v = params.get(s);
		if( v != null) return v;
		return locals.get(s);
	}
	
	public void print(String prefix) {
		Table.out.println(prefix + returnType.toString() + " " + id.name);
		Table.out.println(prefix + "   Params:");
		Iterator<Symbol> it = params.keySet().iterator();
		while(it.hasNext()) {
			params.get(it.next()).print(prefix + "        ");
		}
		if(params.size() > 0) Table.out.println();
		
		Table.out.println(prefix + "   Locals:");
		it = locals.keySet().iterator();
		while(it.hasNext()) {
			locals.get(it.next()).print(prefix + "        ");
		}
	}
}
