package frame;

import assem.InstrList;

public class Proc 
{
    public String begin, end;
    public InstrList body;
    
    public Proc(String bg, InstrList bd, String ed) {
    	begin = bg; 
    	end = ed; 
    	body = bd;	
    }
}