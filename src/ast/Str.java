package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

@BoaConstructor(fields = { "value" })
public class Str extends AST {

  public String value;

  public Str() {
  }

  public String toString() {
    return "Str(" + value + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    return Str(value);
  }

	public Obj typeCheck(Obj tself, Env<String, Obj> tenv,Env<String, Obj> env) {
		return Str;
	}

}
