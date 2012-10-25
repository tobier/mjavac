/* Generated By:JavaCC: Do not edit this line. Parser.java */
package parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.io.PrintStream;

import parser.tree.*;
import parser.tree.expression.*;
import parser.tree.expression.literals.*;
import parser.tree.expression.operators.*;
import parser.tree.expression.operators.binary.*;
import parser.tree.statement.*;
import parser.tree.types.*;

public class Parser implements ParserConstants {
        private Program p;
        public static PrintStream out = System.out; // default

  public void Parse() throws ParseException
  {
                p = this.Program();
  }

  public void printTree()
  {
        p.print("", true);
  }

  public Node getRoot()
  {
        return p;
  }

/********************************
 * MINIJAVA GRAMMAR STARTS HERE *
 ********************************/

/* Top level production */
  final public Program Program() throws ParseException {
  MainClass mc;
  ArrayList<ClassDecl> cdl;
  ClassDecl c;
    /* The first class must be the main class */
            mc = MainClass();
    /* Then zero or more other class declarations */
            cdl = ClassDeclList();
    jj_consume_token(0);
          {if (true) return new Program(mc, cdl);}
    throw new Error("Missing return statement in function");
  }

  final public MainClass MainClass() throws ParseException {
  Id id; ArrayList<VarDecl> vars; ArrayList<Stmt> stmts;
    jj_consume_token(CLASS);
    id = Id();
    jj_consume_token(LBRACE);
    jj_consume_token(PUBLIC);
    jj_consume_token(STATIC);
    jj_consume_token(VOID);
    jj_consume_token(MAIN);
    jj_consume_token(LPAREN);
    jj_consume_token(STRING);
    jj_consume_token(LBRACKET);
    jj_consume_token(RBRACKET);
    Id();
    jj_consume_token(RPAREN);
    jj_consume_token(LBRACE);
    vars = Vars();
    stmts = Stmts();
    jj_consume_token(RBRACE);
    jj_consume_token(RBRACE);
    {if (true) return new MainClass(id, vars, stmts);}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<ClassDecl> ClassDeclList() throws ParseException {
  ArrayList<ClassDecl> cdl = new ArrayList<ClassDecl>(); ClassDecl c;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CLASS:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      c = ClassDecl();
                           cdl.add(c);
    }
          {if (true) return cdl.size() > 0 ? cdl : null;}
    throw new Error("Missing return statement in function");
  }

  final public ClassDecl ClassDecl() throws ParseException {
  Id id; ClassBody cb;
    jj_consume_token(CLASS);
    id = Id();
    jj_consume_token(LBRACE);
    cb = ClassBody();
    jj_consume_token(RBRACE);
    {if (true) return new ClassDecl(id, cb);}
    throw new Error("Missing return statement in function");
  }

/* Class Body */
  final public ClassBody ClassBody() throws ParseException {
  ArrayList<VarDecl> vdl; ArrayList<MethodDecl> mdl;
    vdl = VarDeclList();
    mdl = MethodDeclList();
    {if (true) return (vdl == null) && (mdl == null) ? null : new ClassBody(vdl, mdl);}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<VarDecl> VarDeclList() throws ParseException {
  ArrayList<VarDecl> vdl = new ArrayList<VarDecl>(); VarDecl v;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INT:
      case BOOLEAN:
      case MAIN:
      case IDENTIFIER:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
      v = VarDecl();
                   vdl.add(v);
    }
    {if (true) return vdl.size() > 0 ? vdl : null;}
    throw new Error("Missing return statement in function");
  }

/* Variable declaration */
  final public VarDecl VarDecl() throws ParseException {
  Type type; Id id;
    type = Type();
    id = Id();
    jj_consume_token(SEMICOLON);
          {if (true) return new VarDecl(type, id);}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<MethodDecl> MethodDeclList() throws ParseException {
  ArrayList<MethodDecl> mdl = new ArrayList<MethodDecl>(); MethodDecl m;
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_3;
      }
      m = MethodDecl();
                      mdl.add(m);
    }
    {if (true) return mdl.size() > 0 ? mdl : null;}
    throw new Error("Missing return statement in function");
  }

/* Method declaration */
  final public MethodDecl MethodDecl() throws ParseException {
 Type returnType; Id id; ArrayList<Formal> fl; ArrayList<VarDecl> vars; ArrayList<Stmt> stmts; Return returnStmt;
    jj_consume_token(PUBLIC);
    returnType = Type();
    id = Id();
    jj_consume_token(LPAREN);
    fl = FormalList();
    jj_consume_token(RPAREN);
    jj_consume_token(LBRACE);
    vars = Vars();
    stmts = Stmts();
         Expr e;
    jj_consume_token(RETURN);
                                int returnLine = token.beginLine;
    e = Expr();
    jj_consume_token(SEMICOLON);
                                                                                             returnStmt = new Return(e); returnStmt.line = returnLine;
    jj_consume_token(RBRACE);
    {if (true) return new MethodDecl(returnType, id, fl, vars, stmts, returnStmt);}
    throw new Error("Missing return statement in function");
  }

/* Formal list */
  final public ArrayList<Formal> FormalList() throws ParseException {
  ArrayList<Formal> fl = new ArrayList<Formal>();
  Formal f;
  Type t;
  Id id;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
    case BOOLEAN:
    case MAIN:
    case IDENTIFIER:
      t = Type();
      id = Id();
                           f = new Formal(t,id); fl.add(f);
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_4;
        }
        f = FormalRest();
                          fl.add(f);
      }
          {if (true) return fl;}
      break;
    default:
      jj_la1[4] = jj_gen;
    {if (true) return null;}
    }
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<VarDecl> Vars() throws ParseException {
  ArrayList<VarDecl> vars = new ArrayList<VarDecl>();
  VarDecl v;
    label_5:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_5;
      }
      v = VarDecl();
                             vars.add(v);
    }
   {if (true) return vars.size() > 0 ? vars : null;}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<Stmt> Stmts() throws ParseException {
  ArrayList<Stmt> stmts = new ArrayList<Stmt>();
  Stmt s;
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IF:
      case WHILE:
      case PRINT:
      case MAIN:
      case LBRACE:
      case IDENTIFIER:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_6;
      }
      s = Stmt();
             stmts.add(s);
    }
   {if (true) return stmts.size() > 0 ? stmts : null;}
    throw new Error("Missing return statement in function");
  }

  final public Formal FormalRest() throws ParseException {
  Formal f;
  Type t;
  Id id;
    jj_consume_token(COMMA);
    t = Type();
    id = Id();
                               f = new Formal(t,id);
    {if (true) return f;}
    throw new Error("Missing return statement in function");
  }

/* Identifier rule, may come up with something better ... */
  final public Id Id() throws ParseException {
  String tokenstr;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
      jj_consume_token(IDENTIFIER);
                         tokenstr = token.toString() ;
      break;
    case MAIN:
      jj_consume_token(MAIN);
                                                                     tokenstr = "main";
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return new Id(tokenstr);}
    throw new Error("Missing return statement in function");
  }

/* Type */
  final public Type Type() throws ParseException {
 Type t; Id i;
    if (jj_2_2(2)) {
      jj_consume_token(INT);
      jj_consume_token(LBRACKET);
      jj_consume_token(RBRACKET);
                                            t = new IntArrayType();
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INT:
        jj_consume_token(INT);
                  t = new IntType();
        break;
      case BOOLEAN:
        jj_consume_token(BOOLEAN);
                      t = new BoolType();
        break;
      case MAIN:
      case IDENTIFIER:
        i = Id();
                 t = new IdType(i);
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return t;}
    throw new Error("Missing return statement in function");
  }

  final public Block Block() throws ParseException {
  Stmt s; ArrayList<Stmt> stmts;
    stmts = Stmts();
    {if (true) return new Block(stmts);}
    throw new Error("Missing return statement in function");
  }

/* Statement */
  final public Stmt Stmt() throws ParseException {
  Stmt s; int line = 0;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LBRACE:
      jj_consume_token(LBRACE);
      s = Block();
      jj_consume_token(RBRACE);
      break;
    case IF:
    Expr boolExpr; Stmt ifStmt, elseStmt = null;
      jj_consume_token(IF);
      jj_consume_token(LPAREN);
      boolExpr = Expr();
      jj_consume_token(RPAREN);
      ifStmt = Stmt();
      if (jj_2_3(2)) {
        jj_consume_token(ELSE);
        elseStmt = Stmt();
      } else {
        ;
      }
          if(elseStmt != null)
          {
            s = new IfElse(new If(boolExpr, ifStmt), elseStmt);
          } else {
            s = new If(boolExpr, ifStmt);
          }
      break;
    case WHILE:
          Expr boolExp;
      jj_consume_token(WHILE);
      jj_consume_token(LPAREN);
      boolExp = Expr();
      jj_consume_token(RPAREN);
      s = Stmt();
          s = new While(boolExp, s);
      break;
    case PRINT:
    Expr printExpr;
      jj_consume_token(PRINT);
                    line = token.beginLine;
      jj_consume_token(LPAREN);
      printExpr = Expr();
      jj_consume_token(RPAREN);
      jj_consume_token(SEMICOLON);
                s = new Print(printExpr);
      break;
    default:
      jj_la1[8] = jj_gen;
      if (jj_2_4(2)) {
         Id id; Expr assignExpr;
        id = Id();
        jj_consume_token(ASSIGN);
                              line = token.beginLine;
        assignExpr = Expr();
        jj_consume_token(SEMICOLON);
                s = new Assign(id, assignExpr);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case MAIN:
        case IDENTIFIER:
         Id id; Expr index, assignExpr;
          id = Id();
          jj_consume_token(LBRACKET);
          index = Expr();
          jj_consume_token(RBRACKET);
          jj_consume_token(ASSIGN);
          assignExpr = Expr();
          jj_consume_token(SEMICOLON);
                s = new AssignIndexedElement(id, index, assignExpr);
          break;
        default:
          jj_la1[9] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
          s.line = line; {if (true) return s;}
    throw new Error("Missing return statement in function");
  }

/*********************** 
 * Expressions with || *
 ***********************/
  final public Expr Expr() throws ParseException {
  Expr e; Expr eprim;
    e = A();
          e.line = token.beginLine;
    eprim = Exprprim(e);
    {if (true) return eprim != null ? eprim: e;}
    throw new Error("Missing return statement in function");
  }

  final public Expr Exprprim(Expr left) throws ParseException {
  Expr right, eprim;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OR:
      jj_consume_token(OR);
      right = A();
      eprim = Exprprim(right);
                                             {if (true) return new OrExpr(left, eprim == null ? right : eprim);}
      break;
    default:
      jj_la1[10] = jj_gen;
                                {if (true) return null;}
    }
    throw new Error("Missing return statement in function");
  }

/***********************
 * Expressions with && *
 ***********************/
  final public Expr A() throws ParseException {
  Expr e, eprim;
    e = B();
          e.line = token.beginLine;
    eprim = Aprim(e);
    {if (true) return eprim != null ? eprim: e;}
    throw new Error("Missing return statement in function");
  }

  final public Expr Aprim(Expr left) throws ParseException {
  Expr right, eprim;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AND:
      jj_consume_token(AND);
      right = B();
      eprim = Aprim(right);
                                            {if (true) return new AndExpr(left, eprim == null ? right : eprim);}
      break;
    default:
      jj_la1[11] = jj_gen;
                                {if (true) return null;}
    }
    throw new Error("Missing return statement in function");
  }

/******************************
 * Expressions with == and != *
 ******************************/
  final public Expr B() throws ParseException {
  Expr e, eprim;
    e = C();
          e.line = token.beginLine;
    eprim = Bprim(e);
    {if (true) return eprim != null ? eprim: e;}
    throw new Error("Missing return statement in function");
  }

  final public Expr Bprim(Expr left) throws ParseException {
  Expr right, eprim;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EQ:
      jj_consume_token(EQ);
      right = C();
      eprim = Bprim(right);
                                        {if (true) return new EqExpr(left, eprim == null ? right : eprim);}
      break;
    case NEQ:
      jj_consume_token(NEQ);
      right = C();
      eprim = Bprim(right);
                                         {if (true) return new NeqExpr(left, eprim == null ? right : eprim);}
      break;
    default:
      jj_la1[12] = jj_gen;
                                {if (true) return null;}
    }
    throw new Error("Missing return statement in function");
  }

/************************************
 * Expressions with >, >=, < and <= *
 ************************************/
  final public Expr C() throws ParseException {
  Expr e, eprim;
    e = D();
          e.line = token.beginLine;
    eprim = Cprim(e);
    {if (true) return eprim != null ? eprim: e;}
    throw new Error("Missing return statement in function");
  }

  final public Expr Cprim(Expr left) throws ParseException {
  Expr right, eprim;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LT:
      jj_consume_token(LT);
      right = D();
      eprim = Cprim(right);
                                        {if (true) return new LessExpr(left, eprim == null ? right : eprim);}
      break;
    case LEQ:
      jj_consume_token(LEQ);
      right = D();
      eprim = Cprim(right);
                                         {if (true) return new LeqExpr(left, eprim == null ? right : eprim);}
      break;
    case GT:
      jj_consume_token(GT);
      right = D();
      eprim = Cprim(right);
                                        {if (true) return new GreaterExpr(left, eprim == null ? right : eprim);}
      break;
    case GEQ:
      jj_consume_token(GEQ);
      right = D();
      eprim = Cprim(right);
                                         {if (true) return new GeqExpr(left, eprim == null ? right : eprim);}
      break;
    default:
      jj_la1[13] = jj_gen;
                                {if (true) return null;}
    }
    throw new Error("Missing return statement in function");
  }

/****************************
 * Expressions with + and - *
 ****************************/
  final public Expr D() throws ParseException {
  Expr e, eprim;
    e = E();
          e.line = token.beginLine;
    eprim = Dprim(e);
    {if (true) return eprim != null ? eprim: e;}
    throw new Error("Missing return statement in function");
  }

  final public Expr Dprim(Expr left) throws ParseException {
  Expr e, right, eprim; int line;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SUB:
      jj_consume_token(SUB);
            line = token.beginLine;
      right = E();
                                                  e = new SubExpr(left, right); e.line = line;
      eprim = Dprim(e);
                                                                                                                    {if (true) return eprim != null ? eprim : e;}
      break;
    case ADD:
      jj_consume_token(ADD);
            line = token.beginLine;
      right = E();
                                                  e = new AddExpr(left, right); e.line = line;
      eprim = Dprim(e);
                                                                                                                    {if (true) return eprim != null ? eprim : e;}
      break;
    default:
      jj_la1[14] = jj_gen;
                                {if (true) return null;}
    }
    throw new Error("Missing return statement in function");
  }

/***************************************
 * Expressions with * (multiplication) *
 ***************************************/
  final public Expr E() throws ParseException {
  Expr e, eprim;
    e = F();
    eprim = Eprim(e);
    {if (true) return eprim != null ? eprim : e ;}
    throw new Error("Missing return statement in function");
  }

  final public Expr Eprim(Expr left) throws ParseException {
  Expr right, eprim;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MUL:
      jj_consume_token(MUL);
      right = F();
      eprim = Eprim(right);
                                         {if (true) return new MulExpr(left, eprim == null ? right : eprim);}
      break;
    default:
      jj_la1[15] = jj_gen;
                                {if (true) return null;}
    }
    throw new Error("Missing return statement in function");
  }

/**********************************************
 * Expressions that have ! in them *
 **********************************************/
  final public Expr F() throws ParseException {
  Expr e; Id i; int line;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BANG:
      jj_consume_token(BANG);
            line = token.beginLine;
      e = F();
                                              NegExpr neg = new NegExpr(e); neg.line = line; {if (true) return neg;}
      break;
    case TRUE:
    case FALSE:
    case THIS:
    case NEW:
    case MAIN:
    case INT_LIT:
    case LPAREN:
    case IDENTIFIER:
      e = G();
           {if (true) return e;}
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/**********************************************
 * Expressions that have ., [] and () in them *
 **********************************************/
  final public Expr G() throws ParseException {
  Expr e, eprim;
    e = X();
    eprim = Gprim(e);
    {if (true) return eprim != null ? eprim : e ;}
    throw new Error("Missing return statement in function");
  }

  final public Expr Gprim(Expr left) throws ParseException {
  Expr e, eprim;
    if (jj_2_5(2)) {
      jj_consume_token(DOT);
      jj_consume_token(LENGTH);
                                    e= new ArrayLength(left);
      eprim = Gprim(e);
                                                                                 {if (true) return eprim == null ? e : eprim;}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DOT:
    Id i; ArrayList<Expr> args;
        jj_consume_token(DOT);
        i = Id();
        jj_consume_token(LPAREN);
        args = ExprList();
        jj_consume_token(RPAREN);
                                                                                         e = new Call(left, i, args);
        eprim = Gprim(e);
                                                                                                                                         {if (true) return eprim == null ? e : eprim;}
        break;
      case LBRACKET:
    Expr right;
        jj_consume_token(LBRACKET);
        right = Expr();
        jj_consume_token(RBRACKET);
                                                          e = new ArrayLookup(left, right);
        eprim = Gprim(e);
                                                                                                               {if (true) return eprim == null ? e : eprim;}
        break;
      default:
        jj_la1[17] = jj_gen;
                                {if (true) return null;}
      }
    }
    throw new Error("Missing return statement in function");
  }

/**********************************************
 * Expressions that have the highest priority *
 **********************************************/
  final public Expr X() throws ParseException {
  Expr e, sube; Id i; int line;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_LIT:
      jj_consume_token(INT_LIT);
                IntLiteral intlit = new IntLiteral(new BigInteger(token.toString())); intlit.line = token.beginLine; {if (true) return intlit;}
      break;
    case TRUE:
    case FALSE:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TRUE:
        jj_consume_token(TRUE);
        break;
      case FALSE:
        jj_consume_token(FALSE);
        break;
      default:
        jj_la1[18] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                         BooleanLiteral boolit = new BooleanLiteral(Boolean.parseBoolean(token.toString())); boolit.line = token.beginLine; {if (true) return boolit;}
      break;
    case MAIN:
    case IDENTIFIER:
      i = Id();
           IdLiteral idlit = new IdLiteral(i); idlit.line = token.beginLine; {if (true) return idlit;}
      break;
    case THIS:
      jj_consume_token(THIS);
             This thislit = new This(); thislit.line = token.beginLine; {if (true) return thislit;}
      break;
    case LPAREN:
      jj_consume_token(LPAREN);
              line = token.beginLine;
      e = Expr();
      jj_consume_token(RPAREN);
                                                              e.line = line; {if (true) return new ParensExpr(e);}
      break;
    case NEW:
      jj_consume_token(NEW);
           line = token.beginLine;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INT:
        jj_consume_token(INT);
        jj_consume_token(LBRACKET);
        e = Expr();
        jj_consume_token(RBRACKET);
                                                      NewIntArray newarray = new NewIntArray(e); newarray.line = line; {if (true) return newarray;}
        break;
      case MAIN:
      case IDENTIFIER:
        i = Id();
        jj_consume_token(LPAREN);
        jj_consume_token(RPAREN);
                                        New newid = new New(i); newid.line=line; {if (true) return newid;}
        break;
      default:
        jj_la1[19] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/*******************
 * Expression list *
 *******************/
  final public ArrayList<Expr> ExprList() throws ParseException {
  Expr e; ArrayList<Expr> el = new ArrayList<Expr>();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TRUE:
    case FALSE:
    case THIS:
    case NEW:
    case MAIN:
    case INT_LIT:
    case LPAREN:
    case BANG:
    case IDENTIFIER:
      e = Expr();
             el.add(e);
      label_7:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[21] = jj_gen;
          break label_7;
        }
        e = ExprRest();
                                         el.add(e);
      }
                                                          {if (true) return el;}
      break;
    default:
      jj_la1[22] = jj_gen;
   {if (true) return null;}
    }
    throw new Error("Missing return statement in function");
  }

  final public Expr ExprRest() throws ParseException {
  Expr e;
    jj_consume_token(COMMA);
    e = Expr();
    {if (true) return e;}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_3R_8() {
    if (jj_3R_11()) return true;
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3R_11() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_2()) {
    jj_scanpos = xsp;
    if (jj_3R_19()) {
    jj_scanpos = xsp;
    if (jj_3R_20()) {
    jj_scanpos = xsp;
    if (jj_3R_21()) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3_4() {
    if (jj_3R_10()) return true;
    if (jj_scan_token(ASSIGN)) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_scan_token(DOT)) return true;
    if (jj_scan_token(LENGTH)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3_3() {
    if (jj_scan_token(ELSE)) return true;
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3R_18() {
    if (jj_scan_token(MAIN)) return true;
    return false;
  }

  private boolean jj_3R_17() {
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  private boolean jj_3R_10() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) return true;
    }
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_scan_token(WHILE)) return true;
    return false;
  }

  private boolean jj_3R_15() {
    if (jj_scan_token(PRINT)) return true;
    return false;
  }

  private boolean jj_3R_12() {
    if (jj_scan_token(LBRACE)) return true;
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_scan_token(IF)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_12()) {
    jj_scanpos = xsp;
    if (jj_3R_13()) {
    jj_scanpos = xsp;
    if (jj_3R_14()) {
    jj_scanpos = xsp;
    if (jj_3R_15()) {
    jj_scanpos = xsp;
    if (jj_3_4()) {
    jj_scanpos = xsp;
    if (jj_3R_16()) return true;
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_21() {
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3R_20() {
    if (jj_scan_token(BOOLEAN)) return true;
    return false;
  }

  private boolean jj_3R_16() {
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(INT)) return true;
    if (jj_scan_token(LBRACKET)) return true;
    return false;
  }

  private boolean jj_3R_19() {
    if (jj_scan_token(INT)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[23];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x20,0x401400,0x40,0x0,0x401400,0x1041a000,0x400000,0x401400,0x1001a000,0x400000,0x0,0x0,0x0,0x0,0x0,0x0,0x57c0000,0x40000000,0xc0000,0x400400,0x57c0000,0x0,0x57c0000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x10,0x0,0x2,0x10,0x10,0x10,0x10,0x0,0x10,0x200,0x100,0x60000,0x1c400,0x1800,0x2000,0x18,0x4,0x0,0x10,0x10,0x2,0x18,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[5];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Parser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Parser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Parser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[57];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 23; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 57; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 5; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
