/**
 * Compiler for the MiniJava language (see Appel, "Modern Compiler Implementation in Java"), 
 * with modified grammar by Torbjörn Granlund and extensions by Tobias Eriksson and Sebastian Sjögren.
 *
 * Copyright (c) 2012 Tobias Eriksson, Sebastian Sjögren
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package mjc;
import frame.Proc;
import ir.translate.ProcFragment;
import ir.tree.StmList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.io.FilenameUtils;

import parser.Parser;
import parser.tree.Program;
import regalloc.RegAlloc;
import temp.TempMap;
import visitor.BuildSymbolTableVisitor;
import visitor.DepthFirstTypeVisitor;
import visitor.Translator;
import canon.BasicBlocks;
import canon.RemoveTrivialJumps;
import canon.TraceSchedule;

import assem.InstrList;

public class ARMMain {

	// Used to create new frames
	public static frame.Factory factory = new arm.Factory();

	public static void main(String[] args) {

		for(String arg : args) {
			if( !arg.startsWith("-") ) { // ignore flags
				try {
					File in = new File(arg);
					File out = new File(FilenameUtils.removeExtension(in.getName()) + ".s");
					compile(new FileInputStream(in), new PrintStream(out));
				} catch (FileNotFoundException e) {
					System.err.println("Error: " + e.getMessage());
					System.exit(1);
				} catch (Throwable t) {
					System.err.println("Could not compile: " + t.getMessage());
					System.exit(1);
				}
			}
		}

		// All went well, exit with status 0.
		System.exit(0);

	}

	public static void compile(InputStream in, PrintStream out) throws Exception {

		// First generate a header to the file and make "main" visible to the linker
		out.println("@ Generated by mjavac\n@ Authors: Tobias Eriksson, Sebastian Sjögren");
		out.println("\t.global main\n");

		// Parse the InputStream "in"
		Parser p = new Parser(in);
		p.Parse();

		Program program = (Program)p.getRoot();

		//program.print("", true);
		
		// Build the symbol table and perform type checking.
		BuildSymbolTableVisitor stv = new BuildSymbolTableVisitor();
		stv.visit(program);
		DepthFirstTypeVisitor typeCheck = new DepthFirstTypeVisitor(stv.table);
		typeCheck.visit(program);

		// Translate to intermediate code
		Translator translator = new Translator(stv.table);
		translator.Translate(program, factory);
		//translator.printResults();
		
		// Generate instructions
		for ( ProcFragment pf : translator.fragments ) {
			StmList stmlist = canon.Canon.linearize(pf.body);
			BasicBlocks bb = new BasicBlocks(stmlist);
			TraceSchedule ts = new TraceSchedule(bb);
			//ts.stms.print("", true);
			
			// Remove trivial jumps
			RemoveTrivialJumps rtj = new RemoveTrivialJumps(ts.stms);

			stmlist = rtj.stms();

			InstrList body = null;

			while(stmlist != null) {
				InstrList islist = pf.frame.codegen(stmlist.head);
				body = pf.frame.append(body, islist);
				stmlist = stmlist.tail;
			}
			
			body = pf.frame.procEntryExit2(body);

			Proc proc = pf.frame.procEntryExit3(body);

			//emitInstructions(pf.frame, proc, out);
			
			RegAlloc regalloc = new RegAlloc(pf.frame, proc.body);
			emitInstructions(regalloc, proc, out);
		}
	}

	private static void emitInstructions(TempMap tm, Proc proc, PrintStream out) {
		out.print(proc.begin);
		InstrList islist = proc.body;
		while(islist != null) {
			out.println(islist.head.format(tm));
			islist = islist.tail;
		}
		out.print(proc.end);
	}
}