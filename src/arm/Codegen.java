package arm;

import java.util.LinkedList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import temp.Label;
import temp.LabelList;
import temp.Temp;
import temp.TempList;
import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.Stmt;
import ir.tree.expression.BINOP;
import ir.tree.expression.CALL;
import ir.tree.expression.CONST;
import ir.tree.expression.ESEQ;
import ir.tree.expression.MEM;
import ir.tree.expression.NAME;
import ir.tree.expression.TEMP;
import ir.tree.statement.CJUMP;
import ir.tree.statement.EXP;
import ir.tree.statement.JUMP;
import ir.tree.statement.LABEL;
import ir.tree.statement.MOVE;
import ir.tree.statement.SEQ;
import assem.Instr;
import assem.InstrList;
import assem.OPER;

public class Codegen {

	private final Frame frame;
	
	public Codegen(Frame f) {
		frame = f;
	}
	
	private InstrList ilist = null, last = null;
	
	private TempList L(Temp h) { return new TempList(h,null); }
	private TempList L(Temp h, TempList t) { return new TempList(h,t);}
	
	private void emit(Instr instr) {
		if(last != null)
			last = last.tail = new InstrList(instr, null);
		else
			last = ilist = new InstrList(instr, null);
	}
	
	InstrList codegen(Stmt s) {
		InstrList l;
		munchStmt(s);
		l = ilist;
		ilist = last = null;
		return l;
	}
	
	/*************************************** 
	 *  munchStmt
	 ***************************************/
	private void munchStmt(Stmt s) {
		
		// SEQ(a,b)
		if( s instanceof SEQ) {
			SEQ seq = (SEQ)s;
			munchStmt(seq.left);
			munchStmt(seq.right);
		}
		// EXP(e)
		else if( s instanceof EXP) {
			EXP exp = (EXP)s;
			if(exp.exp instanceof CALL) // call to external library with no value
				saveCallersave();
			munchExp(exp.exp);
			if(exp.exp instanceof CALL) // call to external library with no value
				restoreCallersave();
		}
		// MOVE(d,e)
		else if( s instanceof MOVE ) {
			MOVE m = (MOVE)s;
			munchMove( m.dst, m.src );			
		}
		// JUMP(exp, targets)
		else if( s instanceof JUMP ) {
			JUMP j = (JUMP)s;
			if(j.exp instanceof NAME) {
				Label lab = ((NAME)j.exp).label;
				Instr i = new assem.OPER( 
					"b " + lab.toString(), null, null, new LabelList(lab)				
				);
				i.isUncondJump = true;
				emit(i);
			} else {
				Temp t = munchExp(j.exp);
				Instr i = new assem.OPER( 
					"b `s0", null, L(t) // This is missing a LabelList, because there is no label. Hopefully this works.			
				);
				i.isUncondJump = true;
				emit(i);
			}
		}
		// CJUMP(op, l, r, true, false)
		else if ( s instanceof CJUMP ) {
			CJUMP cj = (CJUMP)s;
			String br = getBranchString(cj.cond);
			emit(new assem.OPER( 
					"cmp `s0, `s1", null, L(munchExp(cj.left), L(munchExp(cj.right)))				
				));
			emit(new assem.OPER( 
					br + " " + cj.iftrue.toString(), null, null, new LabelList(cj.iftrue, new LabelList(cj.iffalse))				
				));
		} 
		// LABEL
		else if( s instanceof LABEL ) { 
			Label lab = ((LABEL)s).label;
			emit(
				new assem.LABEL( lab.toString() + ":", lab) 
			);
		}
	}
	
	/*************************************** 
	 *  munchMove
	 ***************************************/
	private void munchMove(Expr dst, Expr src) {		
		
		if(src instanceof CALL) // call to external library with no value
			saveCallersave();
		
		// MOVE(MEM(d), s) => STORE
		if(dst instanceof MEM) {
			munchMove((MEM)dst, src);
		} 
		// MOVE(d, MEM(s) => LOAD
		else if(src instanceof MEM) {
			munchMove(dst, (MEM)src);
		} 
		// MOVE(TEMP, s)
		else if(dst instanceof TEMP) {
			munchMove((TEMP)dst, src);
		} else if(src instanceof CALL) {
			
		} else {
			throw new NotImplementedException();
		}	
		
		if(src instanceof CALL) // call to external library with no value
			restoreCallersave();
	}
	
	// MOVE(MEM(e), s) => STORE
	private void munchMove(MEM dst, Expr src) {
		
		// MOVE(MEM(+(TEMP, ...)), s)
		if( dst.exp instanceof BINOP) {
			BINOP adress = (BINOP)dst.exp;
			TEMP basePtr = new TEMP(munchExp(adress.left));
			
			if(adress.right instanceof BINOP) {				
				if( ((BINOP)adress.right).left instanceof CONST ) {
					int constOffset = loadStoreConstantOffset(adress.right);
					emit(new OPER(
							"str `s0, [`s1, #" + constOffset + "]", null, L(munchExp(src), L(basePtr.t))	
						));		
					return;
				}
			} else if (adress.right instanceof CONST) {
				int value = adress.binop == BINOP.Op.MINUS ? -((CONST)adress.right).value : ((CONST)adress.right).value;
				emit(new OPER(
						"str `s0, [`s1, #" + value + "]", null, L(munchExp(src), L(basePtr.t))		
					));		
				return;
			}
		} 
		// This should always be done if the two if's above doesn't hold.
		emit(new OPER(
				"str `s0, [`s1]", null, L(munchExp(src), L(munchExp(dst.exp)))	
		));
		
	}
	
	// MOVE(e, MEM(s) => LOAD
	private void munchMove(Expr e, MEM s) {
		// MOVE(e, MEM(+(TEMP, ...)))
		if( s.exp instanceof BINOP) {
			BINOP adress = (BINOP)s.exp;
			TEMP basePtr = new TEMP(munchExp(adress.left));
			
			if(adress.right instanceof BINOP) {
				if( ((BINOP)adress.right).left instanceof CONST ) {
					int constOffset = loadStoreConstantOffset(adress.right);
					emit(new OPER(
							"ldr `d0, [`s0, #" + constOffset + "]", L(munchExp(e)), L(basePtr.t)	
						));		
					return;
				}
			} else if (adress.right instanceof CONST) {
				int value = adress.binop == BINOP.Op.MINUS ? -((CONST)adress.right).value : ((CONST)adress.right).value;
				emit(new OPER(
						"ldr `d0, [`s0, #" + value + "]", L(munchExp(e)), L(basePtr.t)	
					));		
				return;
			}
		}
			
		// This should always be done if the two if's above doesn't hold.
		emit(new OPER(
				"ldr `d0, [`s0]", L(munchExp(e)), L(munchExp(s.exp))
			));
	}
	
	// MOVE(TEMP, s)
	private void munchMove(TEMP dst, Expr src) {

		// MOVE(TEMP, CONST)
		if(src instanceof CONST) {
			int value = ((CONST)src).value;
			emit(new OPER(
					"ldr `d0, =" + value, L(dst.t), null
					));
		} 
		// MOVE(TEMP, BINOP(op, e1, e2)
		else if (src instanceof BINOP) {
			munchMove(dst, (BINOP)src);
		} 
		else {
			emit(new assem.MOVE(
					"mov `d0, `s0", dst.t, munchExp(src)
					));
		}
		
	}
	
	// MOVE(TEMP, BINOP(op, e1, e2)
	private void munchMove(TEMP dst, BINOP src) {
		generateBinopInstruction(dst.t, src);
	}
	
	/*************************************** 
	 *  munchExp
	 ***************************************/
	private Temp munchExp(Expr e) {
		
		//ESEQ(s, e)
		if(e instanceof ESEQ) {
			ESEQ eseq = (ESEQ)e;
			munchStmt(eseq.stm);
			return munchExp(eseq.exp);
		}
		
		// MEM(e)
		if(e instanceof MEM) {
			return munchExp((MEM)e);
		}
		
		// CALL(func, args)
		if(e instanceof CALL) {
			return munchExp((CALL)e);
		}
		
		// BINOP(OP, e1, e2)
		if(e instanceof BINOP) {
			return munchExp((BINOP)e);
		}
		
		// TEMP(t)
		if(e instanceof TEMP) {
			return munchExp((TEMP)e);
		}
		
		// CONST(c)
		if(e instanceof CONST) {
			return munchExp((CONST)e);
		}
		
		// NAME(l)
		if(e instanceof NAME) {
			throw new Error("NAME should never be munched in munchExp.");
		}
		
		throw new Error("Unexpected Expr node, giving up..");
	}
	
	// MEM(e) => LDR, because MEM(e) only only a STR
	// if it's the right node of a MOVE, which is already
	// taken care of.
	private Temp munchExp(MEM mem) {
		Temp res = new Temp();
		
		if(mem.exp instanceof BINOP) {
			BINOP adress = (BINOP)mem.exp;
			Temp basePtr = munchExp(adress.left);
		
			if(adress.right instanceof BINOP) {
				if( ((BINOP)adress.right).left instanceof CONST ) {
					int offset = loadStoreConstantOffset(adress.right);
					emit(new OPER(
							"ldr `d0, [`s0, #" + offset + "]", L(res),  L(basePtr)	
						));
					return res;
				}
			} else if (adress.right instanceof CONST) {
				int value = adress.binop == BINOP.Op.MINUS ? -((CONST)adress.right).value : ((CONST)adress.right).value;
				emit(new OPER(
						"ldr `d0, [`s0, #" + value + "]" , L(res),  L(basePtr)	
					));		
				return res;
			}
		} 
		
		emit(new OPER(
					"ldr `d0, [`s0]", L(res), L(munchExp(mem.exp))
			));
		
		return res;
	}
	
	// CALL(e)
	
	private Temp munchExp(CALL call) {
				
		TempList args = munchArgs(0, reverseArgList(call.args));
		
		if( call.func instanceof NAME ) {
			String sub = ((NAME)call.func).label.toString();
			emit(new OPER(
			"bl " + sub, frame.calldefs(), args, new LabelList(((NAME)call.func).label)
			));
			
		} else {
			Temp sub = munchExp(call.func);
			emit(new OPER(
				"bl `s0", frame.calldefs(), L(sub, args), new LabelList(((NAME)call.func).label)
			));
		}
		
		return frame.RV();
	}
	
	// BINOP(OP, e1, e2)
	private Temp munchExp(BINOP e) {
		Temp res = new Temp();
			
		generateBinopInstruction(res, e);
		
		return res;
	}
	
	// TEMP(t)
	private Temp munchExp(TEMP t) {
		return t.t;
	}
	
	// CONST(t)
	private Temp munchExp(CONST c) {
		Temp t = new Temp();
		emit(new OPER(
			"ldr `d0, =" + c.value, L(t), null
		));
		
		return t;
	}
	
	/*************************************** 
	 *  munchArgs
	 ***************************************/
	private TempList munchArgs(int i, ExpList args) {
		if(args == null) return null;
		
		Temp[] argregs = frame.argRegs();
		if(i >= argregs.length) {
			emit(new OPER(
					"str `s0, [`s1, #" + (i - argregs.length)*frame.wordSize() + "]", null, L(munchExp(args.head), L(Hardware.sp))
					));
			return munchArgs(i+1, args.tail);
		} else {
			if(args.head instanceof CONST) {
				emit(new assem.OPER(
						"ldr `d0, =" + ((CONST)args.head).value, L(argregs[i]), null
						));
			} else {
				emit(new assem.MOVE(
						"mov `d0, `s0", argregs[i], munchExp(args.head)
						));
			}
			return new TempList(argregs[i], munchArgs(i+1, args.tail));
		}
	}
	
	/*************************************** 
	 *  Helpers
	 ***************************************/
	
	private void restoreCallersave() {
		
		int offset = -8 - (Hardware.calleeSave.length )*Hardware.WORDSIZE;
		
		emit(new OPER(
				"ldr `d0, [`s0, #" + offset + "]",  L(Hardware.ip), L(Hardware.fp)
				));
		
		for(int i = 0; i < Hardware.argRegs.length; i++) {
			offset = -12 - (Hardware.calleeSave.length + i)*Hardware.WORDSIZE;
			emit(new OPER(
					"ldr `d0, [`s0, #" + offset + "]",  L(Hardware.argRegs[i]), L(Hardware.fp)
					));
		}
		
	}
	
	private void saveCallersave() {
		
		int offset = -8 - (Hardware.calleeSave.length )*Hardware.WORDSIZE;
		
		emit(new OPER(
				"str `s0, [`s1, #" + offset + "]", null, L( Hardware.ip, L(Hardware.fp))
				));
		
		for(int i = 0; i < Hardware.argRegs.length; i++) {
			offset = -12 - (Hardware.calleeSave.length + i)*Hardware.WORDSIZE;
			emit(new OPER(
					"str `s0, [`s1, #" + offset + "]", null, L(Hardware.argRegs[i], L(Hardware.fp))
					));
		}
	}
	
	/*
	 * When we perform munchMove or munchExp on a
	 * BINOP, the generating code will look the same.
	 * This is because either we're looking at a "root"
	 * BINOP in which the destination register is the
	 * "final" destination register, or we're in the middle
	 * of some larger BINOP expression and need to store
	 * temporary values in a temporary destination register.
	 */
	private void generateBinopInstruction(Temp dst, BINOP src) {
				
		String op = getOpString(src.binop);
		
		if( (src.left instanceof CONST) && (src.right instanceof CONST) ) {
			int value = calculateConstValue(src.binop, (CONST)src.left, (CONST)src.right);
			emit(new OPER(
					"ldr `d0, =" + value, L(dst), null
					));
		} else {
			
			if(src.binop == BINOP.Op.MUL) {/* MUL is a special creature, handle it separately */
				handleMulBinop(dst, src.left, src.right);
			
			} else if(src.right instanceof CONST) { // optimize: if right is a constant, eliminate a MOV
				emit(new OPER(
						op + " `d0, `s0, #" + ((CONST)src.right).value, L(dst), L( (munchExp(src.left)))
						));
			} else if (src.left instanceof CONST && ( src.binop == BINOP.Op.PLUS )) {
				// optimize: if left is a constant and the operator is +, eliminate a MOV
				int constValue = ((CONST)src.left).value;
				 
				emit(new OPER(
						op + " `d0, `s0, #" + constValue, L(dst), L( (munchExp(src.right)))
						));
			} else {
				emit(new OPER(
						op + " `d0, `s0, `s1", L(dst), L( (munchExp(src.left) ), L(munchExp(src.right)) )
						));
			}
		}
	}
	
	/**
	 * Help generate a multiplication instruction on the form:
	 * 		
	 * 		MUL Rd, Rm, Rs
	 * 
	 * Multiplication in ARM is not straightforward to generate, because it must adhere to
	 * several rules:
	 * 
	 * 1. Multiplication must be done on registers and must not include constants.
	 * 2. Rd and Rm may not be the same registers.
	 * 
	 */
	private void handleMulBinop(Temp dst, Expr left, Expr right) {
		
		Temp rd = dst;
		
		// Make sure that there are no constants: generate MOV's if needed
		Temp rm = munchExp(left);
		Temp rs = munchExp(right);
		
		// Get a new register for Rm if Rm == Rd
		if( rm.equals(rd) ) {
			Temp oldRm = rm;
			rm = new Temp();
			
			// Emit a move so Rm is put in a new register
			// Both are set as destination registers, so that
			// the coloring algorithm allocate them in different registers.
			emit(new assem.OPER(
					"mov `s0, `s1",  null, L(oldRm, L(rm)) 
					));
		}
		
		// Emit mul Rd, Rm, Rs
		// Rd and Rm are set as destination registers, so that
		// the coloring algorithm allocate them in different registers.
		emit(new assem.OPER(
				"mul `d0, `d1, `s0", L(rd, L(rm)) , L(rs) 
				));
		
	}
	/*
	 * Map a BINOP.Op to a string that is an ARM instruction.
	 */
	private String getOpString(BINOP.Op op) {
		switch(op) {
		case PLUS:
			return "add";
		case MINUS:
			return "sub";
		case MUL:
			return "mul";
		case DIV:
			return "sdiv"; // division can be signed or unsigned in ARM; use signed division
		case AND:
			return "and";
		case OR:
			return "or";
		case LSHIFT:
			return "lsl";
		case RSHIFT:
			return "lsr";
		case ARSHIFT:
			return "asr";
		case XOR:
			return "eor";
		}
		
		throw new Error("unknown operand / should never happen");
	}
	
	/**
	 * Map a CJUMP.Cond c to an ARM branch instruction,
	 */
	private String getBranchString(CJUMP.Cond c) {
		switch(c) {
		case EQ:
			return "beq";
		case NE:
			return "bne";
		case LT:
			return "blt";
		case GT:
			return "bgt";
		case LE:
			return "ble";
		case GE:
			return "bge";
		case ULT:
		case ULE:
		case UGT:
		case UGE:
			throw new NotImplementedException();
		}
		
		throw new Error("unknown CJUMP conditional / should never happen");
	}
	
	/*
	 * For LDR and STR, we might have a register as a base, and an constant offset.
	 * If the referred memory is in an array, there will be an offset multiplied by
	 * word size. This can be calculated during compile time.
	 */
	private int loadStoreConstantOffset(Expr offset) {
		if( offset instanceof CONST ) {
			return ((CONST)offset).value;
		} else if( (offset instanceof BINOP) && ( ((BINOP)offset).binop == BINOP.Op.MUL ) ) { // offset is numWords * wordSize)
			BINOP binop = (BINOP)offset;
			CONST numWords = (CONST)binop.left;
			CONST wordSize = (CONST)binop.right;
			return numWords.value * wordSize.value;
		}
		
		throw new Error("loadStoreOffset error, should never happen");
	}
	
	/*
	 * Arguments in Call-nodes are in reverse order because otherwise
	 * there will be problems when coloring the graph: if the arguments
	 * is a long nested creature then the "this"-argument will have extremely
	 * long live-time and will make the register allocator fail.
	 */
	private ExpList reverseArgList(ExpList args) {
		
		ExpList reversed = null;
		LinkedList<Expr> head = new LinkedList<Expr>();
		
		while( args != null ) {
			head.add(args.head);
			args = args.tail;
		}
		
		for(Expr e : head) {
			reversed = new ExpList(e, reversed);
		}	
		
		return reversed;
	}
	
	/*
	 * If we have a BINOP with two constants, we can calculate the result
	 * at compile. 
	 */
	private int calculateConstValue(BINOP.Op op, CONST l, CONST r) {
		switch(op) {
			case PLUS: return l.value + r.value;
			case MINUS: return l.value - r.value;
			case MUL: return l.value * r.value;
			case DIV: return l.value / r.value;
			case AND: return l.value & r.value;
			case OR: return l.value | r.value;
			case LSHIFT: return l.value << r.value;
			case RSHIFT: return l.value >> r.value;
			case ARSHIFT: return l.value >>> r.value;
			case XOR: return l.value ^ r.value;
		}
		
		throw new Error("calculateConstValue error / should never happen");
	}
}
