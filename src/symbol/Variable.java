package symbol;

import parser.tree.Id;
import parser.tree.types.Type;

public class Variable {
	public Id id;
	public Type type;
	
	public Variable(Id id, Type type) { this.id = id; this.type = type; }
	
	public void print(String prefix) {
		Table.out.println(prefix + type.toString() + " " + id.name);
	}
}
