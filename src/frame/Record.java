package frame;

import symbol.Symbol;
import temp.Label;

/**   
   Interface for handling of heap objects. The interface is
   independent of the target architecture, but it is designed for real
   processors.
 */
public interface Record
{	

	/**
	    The label corresponding to the assembler entry point
	    for the function using the frame.
	    @return A Label object that contains the label.
	 */
	public Label name();

	/**
       Allocates an Access object corresponding to a member
       of the record. Record members are allocated consecutively,
       starting at offset zero.

	   @param s The symbol associated with the field.
	 */
	public void allocField(Symbol s);

	/**
       @return The size of the record in bytes.
	 */
	public int size();
	
	/**
     * Get the access object of a specific variable.
     * @return The access object as pointed out by the symbol s.
     */
    public Access get(Symbol s);
};
