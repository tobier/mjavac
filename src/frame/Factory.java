package frame;

import arm.Frame;
import temp.Label;

public interface Factory {

	public Frame newFrame(Label name);
	public Record newRecord(Label name);

}
