package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

@BoaConstructor(fields = { "value" })
public class Int extends AST {

	public int value;

	public Int() {
	}

	public String toString() {
		return "Int(" + value + ")";
	}

	public Obj eval(Obj self, Env<String, Obj> env) {
		return Int(value);
	}

	public Obj typeCheck(Obj tself, Env<String, Obj> tenv,Env<String, Obj> env) {
		return Int;
	}

}
