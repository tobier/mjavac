package visitor;

import java.math.BigInteger;

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

public final class DepthFirstTypeVisitor implements TypeVisitor {

	public final Table table;

	private Class currentClass;
	private Method currentMethod;

	public DepthFirstTypeVisitor(Table table) {
		this.table = table;
	}

	@Override
	public Type visit(Program n) {
		n.main.accept(this);

		if (n.classes != null)
			for (ClassDecl c : n.classes)
				c.accept(this);

		return null;
	}

	public Type visit(MainClass n) {
		currentClass = table.mainClass;
		currentMethod = currentClass.getMethod(Symbol.symbol("main"));

		// First check all variable declarations
		if (n.vars != null)
			for (VarDecl v : n.vars)
				v.accept(this);

		// Then make sure all statements are correctly typed
		if (n.stmts != null)
			for (Stmt s : n.stmts)
				s.accept(this);

		currentMethod = null;
		currentClass = null;
		return null;
	}

	@Override
	public Type visit(ClassDecl n) {
		currentClass = table.getClass(Symbol.symbol(n.id.name));
		
		if(n.cb != null)
			n.cb.accept(this);

		currentClass = null;
		return null;
	}

	@Override
	public Type visit(ClassBody n) {

		// Typecheck all the fields
		if (n.vars != null)
			for (VarDecl v : n.vars)
				v.accept(this);

		// Typecheck all the methods
		if (n.methods != null)
			for (MethodDecl m : n.methods)
				m.accept(this);

		return null;
	}

	@Override
	public Type visit(VarDecl n) {
		Type t = n.type.accept(this);
		if (t == null)
			throw new Error(n.line + ": Variable " + n.id.name
					+ " is of unknown type " + ((IdType) n.type).id.name);
		return null;
	}

	@Override
	public Type visit(MethodDecl n) {

		currentMethod = currentClass.getMethod(Symbol.symbol(n.id.name));
		
		// Check that the return type is a defined class or int/int[]
		Type returnType = n.returnType.accept(this);
		if (returnType == null)
			throw new Error(n.line + ": The return type of method "
					+ n.returnType.toString() + " is undefined.");

		// Check the formal parameters
		if (n.fl != null)
			for (Formal f : n.fl)
				f.accept(this);

		// Check the variable declarations
		if (n.vars != null)
			for (VarDecl v : n.vars)
				v.accept(this);

		// Check the statements
		if (n.stmts != null)
			for (Stmt s : n.stmts)
				s.accept(this);

		// Check the return statement
		n.returnStmt.accept(this);

		currentMethod = null;
		return null;
	}

	@Override
	public Type visit(Formal n) {
		if (n.type.accept(this) == null)
			throw new Error(n.line + ": Formal parameter " + n.id.name
					+ " is of undefined type " + n.type.toString());
		return null;
	}

	@Override
	public Type visit(Id n) {
		return null;
	}

	@Override
	public Type visit(New n) {
		if (table.getClass(Symbol.symbol(n.id.name)) == null)
			throw new Error(n.line + ": Cannot create new instance of undefined type " + n.id.name);
		return new IdType(n.id);
	}

	@Override
	public Type visit(NewIntArray n) {

		Type t = n.expr.accept(this);

		if (!(t instanceof IntType))
			throw new Error(n.line + ": New array size must be of type integer.");

		return new IntArrayType();
	}

	@Override
	public Type visit(IdLiteral n) {
		// First we have to look up the literal to find out it's type
		Variable v = currentMethod.get(Symbol.symbol(n.id.name));
		if (v == null) { // OK, it might be a field in the class
			v = currentClass.getField(Symbol.symbol(n.id.name));
			if (v == null) // this literal refers to an undefined variable
				throw new Error(n.line + ": Identifier " + n.id.name
						+ " is undefined.");
		}

		return v.type.accept(this); // types may return null, but not in this
									// case because the variable is
		// declared and thus it's type is also declared.
	}

	@Override
	public Type visit(IntLiteral n) {

		/* Check for integer constant overflow */
		BigInteger maxIntConstant = new BigInteger(String
				.valueOf(Integer.MAX_VALUE));
		if (n.value.compareTo(maxIntConstant) > 0)
			throw new Error(n.line + ": Integer constant overflow");

		return new IntType();
	}

	@Override
	public Type visit(LongLiteral n) {

		/* Check for long constant overflow */
		BigInteger maxLongConstant = new BigInteger(String
				.valueOf(Long.MAX_VALUE));
		if (n.value.compareTo(maxLongConstant) > 0)
			throw new Error(n.line + ": Long integer constant overflow");

		return new LongType();
	}

	@Override
	public Type visit(BooleanLiteral n) {
		return new BoolType();
	}

	@Override
	public Type visit(ArrayLength n) {
		
		Type callerType = n.caller.accept(this);
		
		if (!(callerType instanceof IntArrayType) && !(callerType instanceof LongArrayType))
			throw new Error(n.line + ": Only integer arrays have lengths.");

		return new IntType();
	}

	@Override
	public Type visit(ArrayLookup n) {

		if (n.array instanceof NewIntArray) {
			throw new Error(n.line + ": Multidimensional arrays are not supported.");
		}

		Type arrayType = n.array.accept(this);
		if (!(arrayType instanceof IntArrayType))
			throw new Error(n.line + ": Array lookups can only be performed on integer arrays.");

		Type indexType = n.index.accept(this);
		if (!(indexType instanceof IntType))
			throw new Error(n.line + ": Index must be an integer type.");

		return new IntType();
	}

	@Override
	public Type visit(Call n) {
		
		// Check that the callee is actually a class object.
		Type callerType = n.caller.accept(this);
				
		if (!(callerType instanceof IdType))
			throw new Error(n.line + ": Method calls may only be invoked on class objects.");

		// Now check that the called method actually exists.
		Class callerClass = table.getClass(Symbol
				.symbol(((IdType) callerType).id.name)); // already typechecked,
															// so no need for
															// nullcheck
		Method m = callerClass.getMethod(Symbol.symbol(n.method.name));
		if (m == null) // this method does not exist
			throw new Error(n.line + ": Unknown method " + n.method.name);

		// Sanity check: the number of arguments must match the method signature
		if (n.args != null && n.args.size() != m.param_list.size())
			throw new Error(n.line + ": The number of calling arguments does not match the method signature.");
		if (n.args == null && m.param_list.size() > 0)
			throw new Error(n.line + ": The number of calling arguments does not match the method signature.");

		// Now make sure the arguments are correctly typed
		if(n.args != null)
			for (int i = 0; i < n.args.size(); i++) {
				Type argumentType = n.args.get(i).accept(this);
				Type formalType = m.param_list.get(i).type;
				if (!argumentType.equals(formalType)) {
					if ((argumentType instanceof IntType)
							&& (formalType instanceof LongType))
						continue;
					throw new Error(n.line + ": The type of argument #" + (i + 1)
							+ " does not match method signature.");
				}
			}
			
		n.returnType = m.returnType;
		return m.returnType;
	}

	@Override
	public Type visit(NegExpr n) {
		
		if (!(n.e.accept(this) instanceof BoolType))
			throw new Error(n.line + ": Negation can only be performed on boolean expressions.");

		return new BoolType();
	}

	@Override
	public Type visit(AddExpr n) {
		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof IntegerType)
				|| !(rightType instanceof IntegerType))
			throw new Error(n.line + ": Addition can only be performed on integer types.");

		if ((leftType instanceof LongType) || (rightType instanceof LongType))
			return new LongType();

		return new IntType();
	}

	@Override
	public Type visit(AndExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof BoolType) || !(rightType instanceof BoolType))
			throw new Error(n.line + ": Logical AND can only be performed on boolean expressions.");

		return new BoolType();
	}

	@Override
	public Type visit(EqExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!leftType.equals(rightType))
			throw new Error(n.line + ": Equality comparisions may only be performed with compatible types.");

		return new BoolType();
	}

	@Override
	public Type visit(GeqExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof IntegerType)
				|| !(rightType instanceof IntegerType))
			throw new Error(n.line + ": Relational 'greater than or equal to' can only be performed on integer types.");

		return new BoolType();
	}

	@Override
	public Type visit(GreaterExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof IntegerType)
				|| !(rightType instanceof IntegerType))
			throw new Error(n.line + ": Relational 'greater than' can only be performed on integer types.");

		return new BoolType();
	}

	@Override
	public Type visit(LeqExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof IntegerType)
				|| !(rightType instanceof IntegerType))
			throw new Error(n.line + ": Relational 'less than or equal to' can only be performed on integer types.");

		return new BoolType();
	}

	@Override
	public Type visit(LessExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof IntegerType)
				|| !(rightType instanceof IntegerType))
			throw new Error(n.line + ": Relational 'less than' can only be performed on integer types.");

		return new BoolType();
	}

	@Override
	public Type visit(MulExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof IntegerType)
				|| !(rightType instanceof IntegerType))
			throw new Error(n.line + ": Multiplication can only be performed on integer types.");

		if ((leftType instanceof LongType) || (rightType instanceof LongType))
			return new LongType();

		return new IntType();
	}

	@Override
	public Type visit(NeqExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!leftType.equals(rightType))
			throw new Error(n.line + ": Equality comparisions may only be performed with compatible types.");

		return new BoolType();
	}

	@Override
	public Type visit(OrExpr n) {
		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof BoolType) || !(rightType instanceof BoolType))
			throw new Error(n.line + ": Logical OR can only be performed on boolean expressions.");

		return new BoolType();
	}

	@Override
	public Type visit(SubExpr n) {

		Type leftType = n.left.accept(this);
		Type rightType = n.right.accept(this);

		if (!(leftType instanceof IntegerType)
				|| !(rightType instanceof IntegerType))
			throw new Error(n.line + ": Subtraction can only be performed on integer types.");

		if ((leftType instanceof LongType) || (rightType instanceof LongType))
			return new LongType();

		return new IntType();
	}

	@Override
	public Type visit(Assign n) {

		Variable left = currentMethod.get(Symbol.symbol(n.target.name));
		if (left == null) {
			left = currentClass.getField(Symbol.symbol(n.target.name));
			if (left == null)
				throw new Error(n.line + ": Identifier " + n.target.name
						+ " is undefined.");
		}

		Type rightType = n.assignValue.accept(this);

		if (!left.type.equals(rightType)) {
			if ((left.type instanceof LongType)
					&& (rightType instanceof IntType))
				return null;
			throw new Error(n.line + ": Assignment type mismatch: left side "
					+ left.type.toString() + " does not match right side "
					+ rightType.toString());
		}

		return null;
	}

	@Override
	public Type visit(AssignIndexedElement n) {
		
		Variable targetArray = currentMethod.get(Symbol.symbol(n.target.name));
		if (targetArray == null) {
			targetArray = currentClass.getField(Symbol.symbol(n.target.name));
			if (targetArray == null)
				throw new Error(n.line + ": Identifier " + n.target.name
						+ " is undefined.");
		}
		
		Type targetType = targetArray.type.accept(this);		
		Type indexType = n.indexExpr.accept(this);
		Type assignType = n.assignValue.accept(this);

		if (!(indexType instanceof IntType))
			throw new Error(n.line + ": Index must be of type integer.");
		
		if ( targetType instanceof IntArrayType ) {
			
			if (!(assignType instanceof IntType))
				throw new Error(n.line + ": Assigned value must be of type integer.");
			
		} else if ( targetType instanceof LongArrayType ) {
			
			if (!(assignType instanceof IntegerType))
				throw new Error(n.line + ": Assigned value must be of type integer.");
			
		} else {
			throw new Error(n.line + ": Target must be an integer array: actual type is " + targetType.toString());
		}
		
		return null;
	}

	@Override
	public Type visit(Block n) {
		if(n.stmts != null)
			for (Stmt s : n.stmts)
				s.accept(this);
		return null;
	}

	@Override
	public Type visit(If n) {

		Type t = n.boolExpr.accept(this);

		if (!(t instanceof BoolType))
			throw new Error(n.line + ": If-condition is not of boolean type.");

		n.statement.accept(this);
		return null;
	}

	@Override
	public Type visit(IfElse n) {
		n.ifStmt.accept(this);
		n.elseStmt.accept(this);
		return null;
	}

	@Override
	public Type visit(Print n) {

		Type printExprType = n.expr.accept(this);

		if (!(printExprType instanceof IntegerType)
				&& !(printExprType instanceof BoolType))
			throw new Error(n.line + ": Only integer and boolean expressions may be printed to stdout.");

		return null;
	}

	@Override
	public Type visit(Return n) {
		Type expectedReturnType = currentMethod.returnType;
		Type actualReturnType = n.returnExpr.accept(this);

		if (!expectedReturnType.equals(actualReturnType)) {
			if ((expectedReturnType instanceof LongType)
					&& (actualReturnType instanceof IntType))
				return null;
			throw new Error(n.line + ": Returned expression type does not match method return type.");
		}
		return null;
	}

	@Override
	public Type visit(While n) {
		Type t = n.boolExpr.accept(this);
		if (!(t instanceof BoolType))
			throw new Error(n.line + ": While-condition is not of boolean type.");
		
		if(n.statement != null)
			n.statement.accept(this);
			
		return null;
	}

	@Override
	public Type visit(IdType n) {
		if (table.getClass(Symbol.symbol(n.id.name)) != null)
			return n;
		return null;
	}

	@Override
	public Type visit(BoolType n) {
		return n;
	}

	@Override
	public Type visit(IntArrayType n) {
		return n;
	}

	@Override
	public Type visit(IntType n) {
		return n;
	}

	@Override
	public Type visit(This n) {
		if( currentClass == table.mainClass)
			throw new Error(n.line + ": 'this' may not be used in the main method: method is static");
			
		return new IdType(currentClass.id);
	}

	@Override
	public Type visit(ParensExpr n) {
		return n.e.accept(this);
	}

	@Override
	public Type visit(LongType n) {
		return n;
	}

	@Override
	public Type visit(LongArrayType n) {
		return n;
	}

	@Override
	public Type visit(NewLongArray n) {
		
		Type t = n.expr.accept(this);

		if (!(t instanceof IntType))
			throw new Error(n.line + ": New array size must be of type integer.");
		
		return new LongArrayType();
	}
}
