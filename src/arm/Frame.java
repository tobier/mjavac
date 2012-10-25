package arm;

import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.Stmt;
import ir.tree.expression.TEMP;
import ir.tree.statement.MOVE;
import ir.tree.statement.SEQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import assem.Instr;
import assem.InstrList;
import assem.OPER;

import frame.Access;
import frame.Proc;

import symbol.Symbol;
import temp.Label;
import temp.Temp;
import temp.TempList;

public class Frame extends frame.Frame {

	private Label name;
	
	private int nextFormal = 0;
	private int nextLocalInMemory = 0;
	private int formalInFrame = 0;
	
	private int mostArgsInMem = 0;
	
	// used to lookup variables when building the IR tree
	public HashMap<Symbol, Access> formals;
	public HashMap<Symbol, Access> locals;
	
	private ArrayList<Symbol> formalsInOrder;
	
	private static List<Temp> registers = null;
	
	public Frame(Label n) {
		name = n;
		formals = new HashMap<Symbol, Access>();
		locals = new HashMap<Symbol, Access>();
		formalsInOrder = new ArrayList<Symbol>();
		allocFormal(Symbol.symbol("this"), false);
	}
	
	@Override
	public Label name() { return name; }

	@Override
	public void formalListSize(int i) {
		int available = Hardware.argRegs.length - 1;
		if(i > available) {
			int argsInMem = i - available;
			if(argsInMem > mostArgsInMem)
				mostArgsInMem = argsInMem;
		}
		
		
	}
	
	@Override
	public int size() {	
		return ( mostArgsInMem + Hardware.calleeSave.length + Hardware.argRegs.length + 1)* Hardware.WORDSIZE;
		//return ( mostArgsInMem + Hardware.calleeSave.length )* Hardware.WORDSIZE;
	}

	/**
	 * This is based on what we can deduce from GCC assembly (we find the AAPCS is somewhat
	 * vague).
	 * 
	 * We let the first four parameters be found in registers (r0-r3), and the rest are at
	 * fp + 4 and upwards. 
	 */
	public void allocFormal(Symbol s, boolean escape) {
		
		Access a;
		
		if( escape || nextFormal >= 4 ) {
			a = new InMemory( (1 + formalInFrame) * Hardware.WORDSIZE);
			formalInFrame++;
		} else {
			//a= new InReg(Hardware.argRegs[nextFormal]);
			a= new InReg();
		}
		
		formalsInOrder.add(s);
		formals.put(s, a);
		nextFormal++;
		
	}
	
	public Temp FP() {
		return Hardware.fp;
	}
	
	public Temp RA() {
		return Hardware.lr;
	}
	
	public Temp RV() {
		return Hardware.r0;
	}

	public int wordSize() {
		return Hardware.WORDSIZE;
	}

	@Override
	public Access get(Symbol s) {
		Access ret = formals.get(s);
		if(ret == null)
			ret = locals.get(s);
		
		return ret;
	}

	@Override
	public Access allocLocal(Symbol s, boolean onStack) {
		
		Access a;
		
		if (onStack) {
			a = new InMemory( -8 - (nextLocalInMemory++)*4 );
		} else {
			a = new InReg();
		}
		
		locals.put(s, a);
		return a;
		
	}

	@Override
	public Expr externalCall(String func, ExpList args) {
		return new ir.tree.expression.CALL(new ir.tree.expression.NAME(new temp.Label(func)), args);
	}

	@Override
	public InstrList codegen(Stmt stmt) {
		return (new Codegen(this)).codegen(stmt);
	}

	@Override
	public String tempMap(Temp t) {	
		String tempString = tempMap.get(t);
		return tempString != null ? tempString : t.toString();
	}
	
	@Override
	public List<Temp> registers() {
		
		if(registers == null) {
			registers = new LinkedList<Temp>();
			registers.add(Hardware.r0);
		    registers.add(Hardware.r1);
		    registers.add(Hardware.r2);
		    registers.add(Hardware.r3);
		    registers.add(Hardware.r4);
		    registers.add(Hardware.r5);
		    registers.add(Hardware.r6);
		    registers.add(Hardware.r7);
		    registers.add(Hardware.r8);
		    registers.add(Hardware.r9);
		    registers.add(Hardware.r10);
		    registers.add(Hardware.fp);
		    registers.add(Hardware.ip);
		    registers.add(Hardware.sp);
		    registers.add(Hardware.lr);
		    registers.add(Hardware.pc);
		}
		
		return registers;
	}
	
	public Temp[] specialRegs() {
		return Hardware.specialRegs;
	}
	
	public TempList calldefs() {
		return Hardware.calldefs;
	}
	
	public Temp[] argRegs() {
		return Hardware.argRegs;
	}
	
	@Override
	public Temp[] callerSaveRegs() {
		return Hardware.callerSave;
	}
	
	private static final HashMap<Temp, String> tempMap = new HashMap<Temp, String>(16);
	static {
	    tempMap.put(Hardware.r0,  "r0");
	    tempMap.put(Hardware.r1,  "r1");
	    tempMap.put(Hardware.r2,  "r2");
	    tempMap.put(Hardware.r3,  "r3");
	    tempMap.put(Hardware.r4,  "r4");
	    tempMap.put(Hardware.r5,  "r5");
	    tempMap.put(Hardware.r6,  "r6");
	    tempMap.put(Hardware.r7,  "r7");
	    tempMap.put(Hardware.r8,  "r8");
	    tempMap.put(Hardware.r9,  "r9");
	    tempMap.put(Hardware.r10, "r10");
	    tempMap.put(Hardware.fp, "fp");
	    tempMap.put(Hardware.ip, "ip");
	    tempMap.put(Hardware.sp, "sp");
	    tempMap.put(Hardware.lr, "lr");
	    tempMap.put(Hardware.pc, "pc");
	}
	
	/**
	 * Append two instruction lists
	 */
	@Override
	public InstrList append(InstrList a, InstrList b) {
		if(a == null) return b;
		else {
			InstrList p;
			for(p = a; p.tail != null; p = p.tail);
			p.tail = b;
			return a;
		}
		
	}

	@Override
	public Stmt procEntryExit1(Stmt body) {
		
		Stmt newBody = body;
		
		int formalReg = 0;
		
		for(int i = 0; i < formalsInOrder.size(); i++) {
			
			Symbol s = formalsInOrder.get(i);
			Access a = formals.get(s);
			
			if(a instanceof InReg) {
				newBody = new SEQ( new MOVE( a.exp(null), new TEMP(Hardware.argRegs[formalReg++])), newBody);
			} else {
				newBody = new SEQ(new MOVE(allocLocal(s, false).exp(null), a.exp(new TEMP(Hardware.fp))), newBody);
			}
		}
		
		/**
		 * Save the callee save registers
		 */
		for(Temp calleeSave : Hardware.calleeSave) {
			Symbol s = Symbol.symbol(calleeSave.toString());
			
			newBody = new SEQ( new MOVE(allocLocal(s, true).exp(new TEMP(Hardware.fp)) , new TEMP(calleeSave)), newBody);
		}
		
		/**
		 * At the end of the body, restore the callee save registers.
		 */
		Stmt restore = newBody;
		
		for(Temp calleeSave : Hardware.calleeSave) {
			Symbol s = Symbol.symbol(calleeSave.toString());
			
			restore = new SEQ( restore, new MOVE( new TEMP(calleeSave), locals.get(s).exp(new TEMP(Hardware.fp)) ));
		}
		
		return restore;
	}

	@Override
	public InstrList procEntryExit2(InstrList body) {
		return append(body, 
			new InstrList(
					new assem.OPER("@return", null, Hardware.returnSink), null));
	}
	
	@Override
	public Proc procEntryExit3(InstrList body) {
		
		/**
		 * The prologue consists of 4 steps:
		 * 
		 * 1. Save the frame pointer and return address register on the stack.
		 * 2. Set the new frame pointer to sp-4
		 * 3. Allocate memory for the frame by subtracting from the stack pointer.
		 * 4. Put all the callee-save registers on the stack, starting at fp-8 (already done in the translation phase)
		 */
		Instr saveFpLr = new OPER(
							"stmfd `d0!, {`s0, `s1}", new TempList(Hardware.sp, null), new TempList(Hardware.fp, new TempList(Hardware.lr, null))
							);
		Instr setFp = new OPER(
						"add `d0, `s0, #4", new TempList(Hardware.fp,null), new TempList(Hardware.sp,null)
						);
		Instr allocFrame = new OPER(
							"sub `d0, `s0, #" + this.size(), new TempList(Hardware.sp,null), new TempList(Hardware.sp,null)
						);
				
		InstrList prologue = new InstrList(saveFpLr, new InstrList(setFp, new InstrList(allocFrame, null)));
		
		/**
		 * The epilogue consist of three steps:
		 * 
		 * 1. Point the stack pointer where it should be 
		 * 2. Restore the callee-save registers (already done in the translation phase)
		 * 3. Restore the frame pointer and return address register.
		 */
		
		Instr restoreSp = new OPER(
							"sub `d0, `s0, #4", new TempList(Hardware.sp, null), new TempList(Hardware.fp, null)
							);
		
		Instr restoreFpLr = new OPER(
							"ldmfd `d0!, {`s0, `s1}", new TempList(Hardware.sp, null), new TempList(Hardware.fp, new TempList(Hardware.lr, null))
							);
		
		
		InstrList epilogue = new InstrList(restoreSp, new InstrList(restoreFpLr, null));
		
		body = append(prologue, body);
		body = append(body, epilogue);
		
		return new Proc(
				"@ fuction '" + name.toString() + "'\n" + name.toString() + ":\n",
				body,
				"@ end of function '" + name.toString() + "'\n\n"
				);
	}	
}
