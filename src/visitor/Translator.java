package visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import frame.Access;
import frame.Factory;
import frame.Frame;
import frame.Record;
import ir.translate.*;
import ir.tree.ExpList;
import ir.tree.Expr;
import ir.tree.expression.*;
import ir.tree.statement.*;

import parser.tree.*;
import parser.tree.expression.*;
import parser.tree.expression.literals.*;
import parser.tree.expression.operators.*;
import parser.tree.expression.operators.binary.*;
import parser.tree.statement.*;
import parser.tree.types.*;
import symbol.Symbol;
import symbol.Table;
import temp.Label;
import temp.Temp;

public class Translator implements TranslateVisitor {

	private Factory factory;
	private Frame currentFrame;
	private Record currentRecord;
	private final Table table;
	private Temp objPtr;
	
	private HashMap<Symbol, Record> records;
	public List<ProcFragment> fragments;

	public Translator(Table table) {
		fragments = new ArrayList<ProcFragment>();
		records = new HashMap<Symbol, Record>();
		this.table = table;
	}

	public void Translate(Program p, Factory factory) {
		this.factory = factory;
		// Initialize records for each class
		for(symbol.Class c : table.getAll())
			records.put(Symbol.symbol(c.id.name), factory.newRecord(new Label(c.id.name)));

		p.accept(this);
	}

	/**
	 * Remember a new tree.
	 * @param body The tree to store away.
	 */
	public void procEntryExit(ir.tree.Stmt body) {
		fragments.add(new ProcFragment(currentFrame, body));
		currentFrame = null;
	}

	/**
	 * Print all the fragments collected so far.
	 */
	public void printResults() {
		for(ProcFragment f : fragments) {
			System.out.println();
			System.out.println("Method: " + f.frame.name());
			f.body.print("", true);
		}
	}

	@Override
	public ir.translate.Expr visit(Program n) {

		n.main.accept(this);

		if(n.classes != null)
			for (ClassDecl c : n.classes) {
				c.accept(this);
			}

		return null;
	}

	@Override
	public ir.translate.Expr visit(MainClass n) {
		
		currentFrame = factory.newFrame(new Label("main"));
		currentRecord = records.get(Symbol.symbol(n.id.name));
		
		if(n.vars != null) {
			for(VarDecl v : n.vars) {
				v.accept(this);
			}
		}
		
		ir.tree.Stmt body = new MOVE(new TEMP(currentFrame.RV()), new CONST(0));
		
		if(n.stmts != null) {			
			ir.translate.Expr left;
			
			ir.translate.Expr right = new Nx(body);

			for (int i = n.stmts.size()-1; i >= 0; --i) {
				left = n.stmts.get(i).accept(this);
				body = new SEQ(left.unNx(), right.unNx());
				right = new Nx(body);
			}	
		}
		
		procEntryExit(body);
		objPtr = null;
		
		return null;
	}

	@Override
	public ir.translate.Expr visit(ClassDecl n) {
		currentRecord = records.get(Symbol.symbol(n.id.name));
		if(n.cb != null)
			n.cb.accept(this);
		return null;
	}

	@Override
	public ir.translate.Expr visit(ClassBody n) {
		
		if(n.vars != null) {
			for( VarDecl v : n.vars) {
				v.accept(this);
			}
		}
		
		if(n.methods != null) {
			for ( MethodDecl m : n.methods) {
				m.accept(this);
			}
		}
		
		return null;
	}

	@Override
	public ir.translate.Expr visit(VarDecl n) { 
		Access ac;
		
		if( currentFrame != null) { 
			ac = currentFrame.allocLocal(Symbol.symbol(n.id.name), false);
			return new Nx(new MOVE(ac.exp(new TEMP(currentFrame.FP())),new CONST(0)));
		} else { /* allocating fields */
			currentRecord.allocField(Symbol.symbol(n.id.name));
			return null;
		}
	}

	@Override
	public ir.translate.Expr visit(MethodDecl n) {
		currentFrame = factory.newFrame(new Label(currentRecord.name() + "$" + n.id.name));

		objPtr = ((TEMP)currentFrame.get(Symbol.symbol("this")).exp(null)).t;
		
		if(n.fl != null) {	
			for(Formal f : n.fl) {
				f.accept(this); // this puts the formal in a incoming register, or memory
			}
		}
		
		if(n.vars != null) {
			for(VarDecl v : n.vars) {
				v.accept(this);
			}
		}
		
		ir.tree.Stmt body =  n.returnStmt.accept(this).unNx();
		
		if(n.stmts != null) {
			
			ir.translate.Expr left;
			
			ir.translate.Expr right = n.returnStmt.accept(this);

			for (int i = n.stmts.size()-1; i >= 0; --i) {
				left = n.stmts.get(i).accept(this);
				body = new SEQ(left.unNx(), right.unNx());
				right = new Nx(body);
			}
			
		}
		
		procEntryExit(body);
		
		objPtr = null;
		
		return null;
	}

	@Override
	public ir.translate.Expr visit(Formal n) { 
		currentFrame.allocFormal(Symbol.symbol(n.id.name), false);
		return null;
	}

	@Override
	public ir.translate.Expr visit(Id n) {
		Expr e;
		Access a = currentFrame.get(Symbol.symbol(n.name));
		
		if(a == null) { // we're refering to a field
			a = currentRecord.get(Symbol.symbol(n.name));
			e = a.exp(new TEMP(objPtr));
		} else {
			e = a.exp(new TEMP(currentFrame.FP()));
		}

		return new Ex(e);
	}

	@Override
	public ir.translate.Expr visit(New n) {
		
		CONST size = new CONST( table.getClass(Symbol.symbol(n.id.name)).size() * currentFrame.wordSize() );
		
		return new Ex(currentFrame.externalCall("_minijavalib_allocate", new ExpList(size)));
	}

	@Override
	public ir.translate.Expr visit(NewIntArray n) {

		ir.translate.Expr arraySize = n.expr.accept(this);

		return new Ex(currentFrame.externalCall("_minijavalib_initarray", new ExpList(arraySize.unEx())));
	}

	@Override
	public ir.translate.Expr visit(IdLiteral n) {
		return n.id.accept(this);
	}

	@Override
	public ir.translate.Expr visit(IntLiteral n) {
		CONST val = new CONST(n.value.intValue());

		return new Ex(val);
	}

	@Override
	public ir.translate.Expr visit(BooleanLiteral n) {
		CONST boolVal = n.value ? new CONST(1) : new CONST(0);

		return new Ex(boolVal);
	}

	@Override
	public ir.translate.Expr visit(ArrayLength n) {

		ir.translate.Expr array = n.caller.accept(this);

		TEMP length = new TEMP(new Temp());
		BINOP lenLocation = new BINOP(BINOP.Op.MINUS, array.unEx(), new CONST(currentFrame.wordSize()));
		MOVE getLength = new MOVE(length, new MEM(lenLocation));

		return new Ex(new ESEQ(getLength, length));
	}

	@Override
	public ir.translate.Expr visit(ArrayLookup n) {

		ir.translate.Expr array = n.array.accept(this);
		ir.translate.Expr index = n.index.accept(this);
		
		BINOP offset = new BINOP(BINOP.Op.MUL, index.unEx(), new CONST(currentFrame.wordSize()));
		BINOP elementAddr = new BINOP(BINOP.Op.PLUS, array.unEx(), offset);

		return new Ex(new MEM(elementAddr));
	}

	@Override
	public ir.translate.Expr visit(Call n) {
		
		//System.out.println(n.method.name);
		String className = getClassLabel(n.caller);
		
		if(n.args != null)
			currentFrame.formalListSize(n.args.size());
		
		// Put all the formals in an ExpList (starting with the last so that the first formal is args.head)
		ExpList args =  new ExpList(n.caller.accept(this).unEx());
		if(n.args != null)
			for(int i = 0; i < n.args.size(); i++) {
				args = new ExpList(n.args.get(i).accept(this).unEx() , args);
			}
		
		CALL call = new CALL(new NAME(new Label(className + "$" + n.method.name)), args);
		
		return new Ex(call);
	}

	@Override
	public ir.translate.Expr visit(NegExpr n) {
		
		ir.translate.Expr e = n.e.accept(this);
		Expr expr = e.unEx();
		ir.translate.Expr ret;
		
		if(expr instanceof CONST) { // true => false, false => true
			ret = new Ex( new CONST( ((CONST)expr).value == 0 ? 1 : 0 ) );
		} else if ( (expr instanceof TEMP) || (expr instanceof MEM) || (expr instanceof ESEQ) || (expr instanceof CALL)) { 
			
			Expr toNeg;
			
			if(expr instanceof ESEQ) {
				toNeg = ((ESEQ)expr).exp;
			} else if(expr instanceof CALL) {
				toNeg = new TEMP(currentFrame.RV());
			} else {
				toNeg = expr;
			}
			TEMP oldValue = toNeg instanceof TEMP ? (TEMP)toNeg : new TEMP(new Temp());
			TEMP negValue = new TEMP(new Temp());
			
			Label t = new Label();
			Label f = new Label();
			Label join = new Label();
			
			CJUMP negJump = new CJUMP(CJUMP.Cond.EQ, oldValue, new CONST(1), t, f);
			SEQ trueSeq = new SEQ(new SEQ(new LABEL(t), new MOVE(negValue, new CONST(0))), new JUMP(join));
			SEQ falseSeq = new SEQ(new LABEL(f), new MOVE(negValue, new CONST(1)));
			
			SEQ left = new SEQ(trueSeq, falseSeq);
			LABEL right = new LABEL(join);
			
			SEQ rootSeq  = new SEQ(left, right);
			
			if(expr instanceof TEMP) {
				ret = new Ex(new ESEQ(new SEQ(negJump, rootSeq), negValue));
			} else if ( expr instanceof MEM ){
				MEM m0 = (MEM)expr;
				MOVE getVal = new MOVE(oldValue, m0);
				ret = new Ex(new ESEQ(new SEQ(getVal, new SEQ(negJump, rootSeq)), negValue));
			} else if(expr instanceof CALL) {
				ret = new Ex(new ESEQ(new SEQ( e.unNx() , new SEQ(negJump, rootSeq)), negValue));
			} else { // expr instanceof ESEQ
				ret = new Ex(new ESEQ(new SEQ( ((ESEQ)expr).stm , new SEQ(negJump, rootSeq)), negValue));
			}
			
		} else {
			throw new Error("NegExpr: unexpected expression");
		}
		
		return ret;
	}

	@Override
	public ir.translate.Expr visit(AddExpr n) {
		return arithmeticBinop(n, BINOP.Op.PLUS);
	}

	@Override
	public ir.translate.Expr visit(AndExpr n) {

		Label t = new Label();
		Label f = new Label();
		Label evalRight = new Label();

		return andOrExpr(n, t, f, evalRight, evalRight, f);

	}

	@Override
	public ir.translate.Expr visit(EqExpr n) {	
		return relativeOp(n, CJUMP.Cond.EQ);
	}

	@Override
	public ir.translate.Expr visit(GeqExpr n) {
		return relativeOp(n, CJUMP.Cond.GE);
	}

	@Override
	public ir.translate.Expr visit(GreaterExpr n) {
		return relativeOp(n, CJUMP.Cond.GT);
	}

	@Override
	public ir.translate.Expr visit(LeqExpr n) {
		return relativeOp(n, CJUMP.Cond.LE);
	}

	@Override
	public ir.translate.Expr visit(LessExpr n) {
		return relativeOp(n, CJUMP.Cond.LT);
	}

	@Override
	public ir.translate.Expr visit(MulExpr n) {
		return arithmeticBinop(n, BINOP.Op.MUL);
	}

	@Override
	public ir.translate.Expr visit(NeqExpr n) {
		return relativeOp(n, CJUMP.Cond.NE);
	}

	@Override
	public ir.translate.Expr visit(OrExpr n) {

		Label t = new Label();
		Label f = new Label();
		Label evalRight = new Label();

		return andOrExpr(n, t, f, evalRight, t, evalRight);
	}

	@Override
	public ir.translate.Expr visit(SubExpr n) {
		return arithmeticBinop(n, BINOP.Op.MINUS);
	}

	@Override
	public ir.translate.Expr visit(Assign n) {

		ir.translate.Expr left = n.target.accept(this);
		ir.translate.Expr right = n.assignValue.accept(this);

		MOVE move = new MOVE(left.unEx(), right.unEx());

		return new Nx(move);
	}

	@Override
	public ir.translate.Expr visit(AssignIndexedElement n) {

		ir.translate.Expr pointerAddr = n.target.accept(this);
		ir.translate.Expr index = n.indexExpr.accept(this);
		ir.translate.Expr assignValue = n.assignValue.accept(this);

		Expr arrayPointer = pointerAddr.unEx();

		BINOP offset = new BINOP(BINOP.Op.MUL, index.unEx(), new CONST(currentFrame.wordSize()));
		
		BINOP elementAddr = new BINOP(BINOP.Op.PLUS, arrayPointer, offset);
		
		MEM element = new MEM(elementAddr);
		
		return new Nx(new MOVE(element, assignValue.unEx()));
	}

	@Override
	public ir.translate.Expr visit(Block n) {

		ir.translate.Expr left;
		ir.translate.Expr right = new Nx(new EXP(new CONST(0)));

		if(n.stmts != null) {

			SEQ seqTree;

			for (int i = n.stmts.size()-1; i >= 0; --i) {
				left = n.stmts.get(i).accept(this);
				seqTree = new SEQ(left.unNx(), right.unNx());
				right = new Nx(seqTree);
			}

		}

		return right;
	}

	@Override
	public ir.translate.Expr visit(If n) {

		ir.translate.Expr cond = n.boolExpr.accept(this);
		ir.translate.Expr ifBlock = n.statement.accept(this);

		return new IfExpr(cond, ifBlock);
	}

	@Override
	public ir.translate.Expr visit(IfElse n) {
		ir.translate.Expr cond = n.ifStmt.boolExpr.accept(this);
		ir.translate.Expr ifBlock = n.ifStmt.statement.accept(this);
		ir.translate.Expr elseBlock = n.elseStmt.accept(this);

		return new IfElseExpr(cond, ifBlock, elseBlock);
	}

	@Override
	public ir.translate.Expr visit(Print n) {

		ir.translate.Expr arg = n.expr.accept(this);

		return new Ex(currentFrame.externalCall("_minijavalib_println", new ExpList(arg.unEx())));
	}

	@Override
	public ir.translate.Expr visit(Return n) {
		ir.translate.Expr retValue = n.returnExpr.accept(this);
		return new Nx(new MOVE(new TEMP(currentFrame.RV()), retValue.unEx()));
	}

	@Override
	public ir.translate.Expr visit(While n) {

		ir.translate.Expr test = n.boolExpr.accept(this);
		ir.translate.Expr body = n.statement.accept(this);

		return new WhileExpr(test, body);
	}

	@Override
	public ir.translate.Expr visit(BoolType n) { /* nothing to do */ return null; }

	@Override
	public ir.translate.Expr visit(IdType n) { /* nothing to do */ return null; }

	@Override
	public ir.translate.Expr visit(IntArrayType n) { /* nothing to do */ return null; }

	@Override
	public ir.translate.Expr visit(IntType n) { /* nothing to do */ return null; }

	@Override
	public ir.translate.Expr visit(This n) {		
		return new Ex(new TEMP(objPtr));
	}

	@Override
	public ir.translate.Expr visit(ParensExpr n) {
		return n.e.accept(this);
	}

	/* Not implemented yet */
	@Override
	public ir.translate.Expr visit(LongLiteral n) {
		return null;
	}

	@Override
	public ir.translate.Expr visit(LongType n) {
		return null;
	}

	@Override
	public ir.translate.Expr visit(LongArrayType n) {
		return null;
	}

	@Override
	public ir.translate.Expr visit(NewLongArray n) {
		return null;
	}

	public ir.translate.Expr arithmeticBinop(BinOpExpr n, BINOP.Op op) {
		ir.translate.Expr left = n.left.accept(this);
		ir.translate.Expr right = n.right.accept(this);

		BINOP binop = new BINOP(op, left.unEx(), right.unEx());
		return new Ex(binop);
	}

	public ir.translate.Expr relativeOp(BinOpExpr n, CJUMP.Cond cond) {
		ir.translate.Expr left = n.left.accept(this);
		ir.translate.Expr right = n.right.accept(this);

		/** Get the true and false labels, and a new temporary register **/
		Label t = new Label();
		Label f = new Label();
		Label join = new Label();
		Temp r = new Temp();

		/** First, build the conditional jump that uses the operator and labels **/
		CJUMP cjump = new CJUMP(cond, left.unEx(), right.unEx(), t, f);
		
		/** Build the SEQs that store the truth value **/
		SEQ trueSeq = new SEQ(new SEQ(new LABEL(t), new MOVE(new TEMP(r), new CONST(1))), new JUMP(join));
		SEQ falseSeq = new SEQ(new LABEL(f), new MOVE(new TEMP(r), new CONST(0)));

		SEQ trueAndFalseSeq = new SEQ(new SEQ(trueSeq, falseSeq), new LABEL(join));
		
		/** Now bind everything together */
		SEQ eLeft = new SEQ(cjump, trueAndFalseSeq);
		TEMP eRight = new TEMP(r);
		
		/** And finally, return an ESEQ **/
		return new Ex( new ESEQ(eLeft, eRight) );
	}

	public ir.translate.Expr andOrExpr(BinOpExpr n, Label t, Label f, Label evalRight, Label x, Label y) {

		ir.translate.Expr left = n.left.accept(this);
		ir.translate.Expr right = n.right.accept(this);

		Label join = new Label();
		Temp r = new Temp();

		CJUMP checkLeft = new CJUMP(CJUMP.Cond.NE, left.unEx(), 
				new CONST(0), x, y);
		CJUMP checkRight = new CJUMP(CJUMP.Cond.NE, right.unEx(), 
				new CONST(0), t, f);

		SEQ trueSeq = new SEQ(new SEQ(new LABEL(t), new MOVE(new TEMP(r), new CONST(1))), new JUMP(join));
		SEQ falseSeq = new SEQ(new SEQ(new LABEL(f), new MOVE(new TEMP(r), new CONST(0))), new LABEL(join));

		SEQ eLeft = new SEQ(
				new SEQ(
						checkLeft,
						new SEQ(
								new SEQ(
										new LABEL(evalRight),
										checkRight
										),
										trueSeq
								)
						),
						falseSeq
				);

		TEMP eRight = new TEMP(r);

		return new Ex(new ESEQ(eLeft, eRight));

	}
	
	/**
	 * Private helper for the call node.
	 * This method recursively calls itself until it finds
	 * something that is not a ParensExpr.
	 */
	private String getClassLabel(parser.tree.expression.Expr caller) {
		
		// Are we done?
		if(caller instanceof ParensExpr)
			return getClassLabel( ((ParensExpr)caller).e);
		
		// Find out what class label to use
		if(caller instanceof IdLiteral) {
			// Get the caller id. 
			Id callerId = ((IdLiteral)caller).id;
			
			// Get the current class (named by the current record)
			symbol.Class currentClass = table.getClass(Symbol.symbol(currentRecord.name().toString()));
			
			symbol.Method currentMethod;
			
			if(currentClass == table.mainClass) {
				currentMethod = table.mainClass.getMethod(Symbol.symbol("main"));
			} else {
				// Get the current method (named by the frame)
				currentMethod = currentClass.getMethod(Symbol.symbol(currentFrame.name().toString().split("\\$")[1]));
			}
			
			// Check if the caller is a method temporary or formal
			symbol.Variable var = currentMethod.get(Symbol.symbol(callerId.name));
			if(var == null) // it's a field
				var = currentClass.getField(Symbol.symbol(callerId.name));
			
			return var.type.toString();
		} else if (caller instanceof This){
			return currentRecord.name().toString();
		} else if (caller instanceof New) { // A call to "new X()"
			return ((New)caller).id.name;
		} else if(caller instanceof Call) {
			return ((Call)caller).returnType.toString();
		} else {
			throw new Error("getClassLabel: should never happen");
		}
	}

}
