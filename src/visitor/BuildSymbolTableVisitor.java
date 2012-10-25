package visitor;

import java.util.Iterator;

import parser.tree.*;
import parser.tree.expression.*;
import parser.tree.expression.literals.*;
import parser.tree.expression.operators.*;
import parser.tree.expression.operators.binary.*;
import parser.tree.statement.*;
import parser.tree.types.*;

import symbol.Class;
import symbol.Method;
import symbol.Symbol;
import symbol.Table;
import symbol.Variable;

/*
 * This visitor traverser the abstract syntax tree and generates a symbol table.
 */
public final class BuildSymbolTableVisitor implements Visitor {
	
	public final Table table;
	
	private Class currentClass;
	private Method currentMethod;
	
	public BuildSymbolTableVisitor() {
		table = new Table();
	}
	
	@Override
	public void visit(Program n) {
		// First add the main class. No need to check for name collision, this always works.
		table.mainClass = new Class(n.main.id); 
		currentClass = table.mainClass;
		table.putClass(Symbol.symbol(n.main.id.name), table.mainClass);
		n.main.accept(this);
		
		// Now visit all the classes in order
		if(n.classes != null)
			for(ClassDecl c : n.classes) {
				c.accept(this);
			}
	}

	@Override
	public void visit(MainClass n) {
		Method main = new Method(new Id("main"), new IdType(new Id("void")));
		currentMethod = main;
		
		if(n.vars != null) {
			Iterator<VarDecl> it = n.vars.iterator();
			while(it.hasNext()) {
				VarDecl var = it.next();
				if( currentMethod.get(Symbol.symbol(var.id.name)) != null)
					throw new Error(var.line + ": Variable " + var.id.name + " is already defined" + " in method " 
							+ currentClass.id.name + "." + currentMethod.id.name + "()");
				currentMethod.putLocal(Symbol.symbol(var.id.name), new Variable(var.id, var.type));
			}
		}
		table.mainClass.putMethod(Symbol.symbol("main"), main);
		currentMethod = null;
		currentClass = null;
	}
	
	@Override
	public void visit(ClassDecl n) {
		Class cl = new Class(n.id); currentClass = cl;
		if(n.cb != null) n.cb.accept(this);
		
		if( !table.putClass(Symbol.symbol(n.id.name), cl)) {
			throw new Error(n.line + ":Class " + n.id.name + " is already defined");
		}
	}

	@Override
	public void visit(ClassBody n) {
		
		if(n.vars != null) {
			Iterator<VarDecl> it = n.vars.iterator();
			while(it.hasNext()) {
				it.next().accept(this);
			}
		}
		
		if(n.methods != null) {
			Iterator<MethodDecl> it = n.methods.iterator();
			while(it.hasNext()) {
				it.next().accept(this);
			}
		}
		
	}

	@Override
	public void visit(VarDecl n) {
				
		if( currentMethod == null ) {
			if( !currentClass.putField(Symbol.symbol(n.id.name), new Variable(n.id, n.type)) )
				throw new Error(n.line + ":Variable " + n.id.name + " is already defined in class " + currentClass.id.name);
		} else {
			if( currentMethod.get(Symbol.symbol(n.id.name)) != null)
				throw new Error(n.line + ":Variable " + n.id.name + " is already defined" + " in method " 
						+ currentClass.id.name + "." + currentMethod.id.name + "()");
			currentMethod.putLocal(Symbol.symbol(n.id.name), new Variable(n.id, n.type));
		}
	}

	@Override
	public void visit(MethodDecl n) {
		Method m = new Method(n.id, n.returnType);
		if( !currentClass.putMethod(Symbol.symbol(n.id.name), m) ) {
			throw new Error(n.line + ":Method " + n.id.name + " is already defined in class " + currentClass.id.name);
		}
		
		currentMethod = m;
		
		if( n.fl != null) {
			Iterator<Formal> f_it = n.fl.iterator();
			while(f_it.hasNext()) {
				Formal f = f_it.next();
				f.accept(this);
			}
		}
		
		if( n.vars != null) {
			Iterator<VarDecl> v_it = n.vars.iterator();
			while(v_it.hasNext()) {
				VarDecl v = v_it.next();
				v.accept(this);
			}
		}
				
		currentMethod = null;
	}

	@Override
	public void visit(Formal n) {
		if( currentMethod.get(Symbol.symbol(n.id.name)) != null)
			throw new Error(n.line + ":Variable " + n.id.name + " is already defined" + " in method " 
					+ currentClass.id.name + "." + currentMethod.id.name + "()");
		
		currentMethod.putParam(Symbol.symbol(n.id.name), new Variable(n.id, n.type));
	}

	@Override
	public void visit(Id n) { /* nothing to do */ }

	@Override
	public void visit(New n) { /* nothing to do */ }

	@Override
	public void visit(NewIntArray n) { /* nothing to do */ }

	@Override
	public void visit(IdLiteral n) { /* nothing to do */ }

	@Override
	public void visit(IntLiteral n) { /* nothing to do */ }

	@Override
	public void visit(BooleanLiteral n) { /* nothing to do */ }

	@Override
	public void visit(ArrayLength n) { /* nothing to do */ }

	@Override
	public void visit(ArrayLookup n) { /* nothing to do */ }

	@Override
	public void visit(Call n) { /* nothing to do */ }

	@Override
	public void visit(NegExpr n) { /* nothing to do */ }

	@Override
	public void visit(AddExpr n) { /* nothing to do */ }

	@Override
	public void visit(AndExpr n) { /* nothing to do */ }
	
	@Override
	public void visit(EqExpr n) { /* nothing to do */ }

	@Override
	public void visit(GeqExpr n) { /* nothing to do */ }

	@Override
	public void visit(GreaterExpr n) { /* nothing to do */ }

	@Override
	public void visit(LeqExpr n) { /* nothing to do */ }

	@Override
	public void visit(LessExpr n) { /* nothing to do */ }

	@Override
	public void visit(MulExpr n) { /* nothing to do */ }

	@Override
	public void visit(NeqExpr n) { /* nothing to do */ }

	@Override
	public void visit(OrExpr n) { /* nothing to do */ }

	@Override
	public void visit(SubExpr n) { /* nothing to do */ }

	@Override
	public void visit(Assign n) { /* nothing to do */ }

	@Override
	public void visit(AssignIndexedElement n) { /* nothing to do */ }

	@Override
	public void visit(Block n) { /* nothing to do */ }

	@Override
	public void visit(If n) { /* nothing to do */ }

	@Override
	public void visit(IfElse n) { /* nothing to do */ }

	@Override
	public void visit(Print n) { /* nothing to do */ }

	@Override
	public void visit(Return n) { /* nothing to do */ }
	
	@Override
	public void visit(While n) { /* nothing to do */ }

	@Override
	public void visit(BoolType n) { /* nothing to do */ }

	@Override
	public void visit(IdType n) { /* nothing to do */ }

	@Override
	public void visit(IntArrayType n) { /* nothing to do */ }

	@Override
	public void visit(IntType n) { /* nothing to do */ }

	@Override
	public void visit(This n) { /* nothing to do */ }

	@Override
	public void visit(ParensExpr n) { /* nothing to do */ }

	@Override
	public void visit(LongLiteral n) { /* nothing to do */ }

	@Override
	public void visit(LongType n) { /* nothing to do */ }

	@Override
	public void visit(LongArrayType n) { /* nothing to do */ }

	@Override
	public void visit(NewLongArray n) { /* nothing to do */ }
}
