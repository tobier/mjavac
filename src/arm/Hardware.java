package arm;

import temp.Temp;
import temp.TempList;

public class Hardware {

	/**
	 * A word is 4 bytes (32 bits) on the ARM architecture.
	 */
	public static final int WORDSIZE = 4;

	/**
	 * ARM Registers
	 */
	final static Temp r0 = new Temp(); // Argument register 1 and result register
	final static Temp r1 = new Temp(); // Argument register 2
	final static Temp r2 = new Temp(); // Argument register 3
	final static Temp r3 = new Temp(); // Argument register 4
	final static Temp r4 = new Temp(); // Variable registers
	final static Temp r5 = new Temp();
	final static Temp r6 = new Temp();
	final static Temp r7 = new Temp(); // The Frame Pointer (FP)
	final static Temp r8 = new Temp(); // Variable registers
	final static Temp r9 = new Temp();
	final static Temp r10 = new Temp();
	final static Temp fp = new Temp();
	final static Temp ip = new Temp(); // The Intra-Procedure-Call scratch register
	final static Temp sp = new Temp(); // The Stack Pointer (SP)
	final static Temp lr = new Temp(); // The Link Register (LR), return address
	final static Temp pc = new Temp(); // The Program Counter (PC)
	
	/**
	 * Register lists. Must not overlap and must include every register that might show
	 * up in code.
	 */
	static final Temp[]
			specialRegs = { fp, sp, lr, pc },
			argRegs = { r0, r1, r2, r3 },
			callerSave = { ip },
			calleeSave = { r4, r5, r6, r7, r8, r9, r10};
	
	/**
	 * Registers that may be trashed by a called function
	 */
	static TempList calldefs = null;
	
	
	static {
		calldefs = new TempList(lr, null);
		/*calldefs = new TempList(ip, calldefs);
		calldefs = new TempList(r0, calldefs);
		calldefs = new TempList(r1, calldefs);
		calldefs = new TempList(r2, calldefs);
		calldefs = new TempList(r3, calldefs);*/
	}
	
	
	/*
	static {
		calldefs = new TempList(lr, null);
		calldefs = new TempList(r0, calldefs); // argRegs
		calldefs = new TempList(r1, calldefs);
		calldefs = new TempList(r2, calldefs);
		calldefs = new TempList(r3, calldefs);
		calldefs = new TempList(ip, calldefs); // callerSave
	
	}*/
	
	/**
	 * Register that are life at procedure exit.
	 */
	static TempList returnSink = null;
	
	static {
		returnSink = new TempList(fp, null);
		returnSink = new TempList(sp, returnSink);
		returnSink = new TempList(lr, returnSink);
		returnSink = new TempList(pc, returnSink);
		returnSink = new TempList(r4, returnSink);
		returnSink = new TempList(r5, returnSink);
		returnSink = new TempList(r6, returnSink);
		returnSink = new TempList(r7, returnSink);
		returnSink = new TempList(r8, returnSink);
		returnSink = new TempList(r9, returnSink);
		returnSink = new TempList(r10, returnSink);
		returnSink = new TempList(r0, returnSink); // return value register should probably be alive at the end of the method
	}
}
