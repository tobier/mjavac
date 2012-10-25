package ir.tree.statement;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Stmt;
import temp.Label;

public class LABEL extends Stmt { 
	  public Label label;
	  public LABEL(Label l) {label=l;}
	  public ExpList kids() {return null;}
	  public Stmt build(ExpList kids) {
	    return this;
	  }
	  
	  @Override
		public void print(String prefix, boolean isTail) {
			Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "LABEL " + label.toString());
		}
}
