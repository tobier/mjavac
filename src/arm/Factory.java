package arm;

import java.io.PrintStream;

import arm.Frame;
import arm.Record;
import temp.Label;

public class Factory implements frame.Factory {

	public static PrintStream out = System.out;
	
	@Override
	public Frame newFrame(Label name) {		
		return new Frame(name);
	}

	@Override
	public Record newRecord(Label name) {
		return new Record(name);
	}
}
