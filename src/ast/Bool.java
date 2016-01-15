package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

@BoaConstructor(fields = { "value" })
public class Bool extends AST {

  public boolean value;

  public Bool() {
  }

  public String toString() {
    return "Bool(" + value + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    return value ? theObjTrue : theObjFalse;
  }

	public Obj typeCheck(Obj tself, Env<String, Obj> tenv,Env<String, Obj> env) {
		return Bool;
	}

}
