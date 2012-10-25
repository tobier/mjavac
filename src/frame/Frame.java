package frame;

import java.util.List;

import assem.InstrList;
import ir.tree.Stmt;
import symbol.Symbol;
import temp.Label;
import temp.Temp;
import temp.TempMap;

/**
   Interface for handling of frames on the runtime stack. The
   interface is independent of the target architecture, but it is
   designed for real processors. 
*/
public abstract class Frame implements TempMap
{

    /**
       The label corresponding to the assembler entry point
       for the function using the frame.
       @return A Label object that contains the label.
    */
    public abstract Label name();

    /**
     * Call this to indicate to the frame how much memory
     * has to be allocated to fit the method call with the
     * longest formal list.
     */
    public abstract void formalListSize(int i);
    
    /**
       @return The size of the frame in bytes.
    */
    public abstract int size();
    
    /**
     * Allocate a new formal
     */
    public abstract void allocFormal(Symbol s, boolean escape);
    
    /**
     * Allocate a new local
     */
    public abstract Access allocLocal(Symbol s, boolean onStack);
    
    /**
     * The list of machine registers.
     */
    public abstract List<Temp> registers();
    
    /**
     * The list of argument registers.
     */
    public abstract Temp[] argRegs();
    
    /**
     * The list of special registers.
     */
    public abstract Temp[] specialRegs();
    
    /**
     * The list of caller save registers.
     */
    public abstract Temp[] callerSaveRegs();
    
    /**
     * Frame Pointer (FP) used for all frames.
     */
    public abstract Temp FP();
    
    /**
     * Return Adress (RA) register.
     */
    public abstract Temp RA();
    
    /**
     * Return Value (RV) register.
     */
    public abstract Temp RV();
    
    /**
     * The word size of the machine.
     * @return The word size.
     */
    public abstract int wordSize();
    
    /**
     * Get the access object of a specific variable.
     * @return The access object as pointed out by the symbol s.
     */
    public abstract  Access get(Symbol s);
    
    /**
     * Generate an expression tree that calls an external function.
     * 
     * @param func The function to call.
     * @param args A list of arguments.
     * @return A Tree expression that calls the function.
     */
    public abstract ir.tree.Expr externalCall(String func, ir.tree.ExpList args);
    
    /**
     * Generate assembly language instructions of the given statement tree.
     * @param stmt The tree statement to generate code from.
     * @return A list of assembly language instructions.
     */
    public abstract InstrList codegen(Stmt stmt);
    
    /**
     * Append two instructions lists.
     */
    public abstract InstrList append(InstrList a, InstrList b);
    
    /**
     * Appends tree code to move incoming arguments into the
     * places where the function body is expecting them to be.
     *
     * @param body The function body.
     * 
     * @return Tree code that first moves incoming arguments
     * to their correct places and then executes the function body.
     */
    public abstract Stmt procEntryExit1(Stmt body);
    
    /**
     * Appends a "sink" instruction that "uses" special registers
     * i.e., frame pointer, return value register etc.
	 *
     * @param inst The list of instructions to which the sink instruction
     * should be appended.
	 *
     * @return The modified List with the sink instruction.
     */
    public abstract InstrList procEntryExit2(InstrList inst);
    
    /**
     * Produces first part of prologue:
     * pseudo instructions to announce procedure start
     * a label for the procedure name
     * instruction to adjust stack pointer
	 *
     * Also produces last part of epilogue:
     * Instruction to reset stack pointer
     * The return instruction
     * pseudo instructions to indicate procedure end
	 * 
     * @param inst The list of instructions to which the sink instruction
     * should be appended.
	 *
     * @return a Proc object
     */
    public abstract Proc procEntryExit3(InstrList body);
    
    /**
     * All frames implement TempMap by extending this class.
     */
    @Override
    public abstract String tempMap(Temp t);
};
