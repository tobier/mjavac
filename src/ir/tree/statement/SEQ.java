package ir.tree.statement;

import parser.Parser;
import ir.tree.ExpList;
import ir.tree.Stmt;

public class SEQ extends Stmt {
	  public Stmt left, right;
	  public SEQ(Stmt l, Stmt r) { left=l; right=r; }
	  public ExpList kids() {throw new Error("kids() not applicable to SEQ");}
	  public Stmt build(ExpList kids) {throw new Error("build() not applicable to SEQ");}
	  
	  @Override
		public void print(String prefix, boolean isTail) {
			Parser.out.println(prefix + (isTail ? "└── " : "├── ") + "SEQ");
			left.print(prefix + (isTail ? "    " : "│   "), false);
			right.print(prefix + (isTail ?"    " : "│   "), true);
		}
	}
