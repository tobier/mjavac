package arm;

import java.util.HashMap;

import symbol.Symbol;
import temp.Label;
import frame.Access;

public class Record implements frame.Record {
	
	private final HashMap<Symbol, Access> fields;
	private int offset;
	private final Label name;
	
	public Record(Label name) {
		fields = new HashMap<Symbol, Access>();
		offset = 0;
		this.name = name;
	}
	
	@Override
	public void allocField(Symbol s) {
		fields.put(s, new InMemory(offset));
		offset += Hardware.WORDSIZE;
	}

	@Override
	public int size() {
		return fields.size();
	}

	@Override
	public Label name() {
		return name;
	}
	
	@Override
	public Access get(Symbol s) {
		return fields.get(s);
	}

}
