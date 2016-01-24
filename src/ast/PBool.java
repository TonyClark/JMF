package ast;

import static value.Obj.Bool;
import static value.Obj.asBool;

import java.util.function.BiFunction;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "value" })
public class PBool extends Pattern {

  public boolean value;

  public PBool() {
  }

  public String toString() {
    return "PBool(" + value + ")";
  }

  public Env<String, Obj> match(Obj v, Obj self, Env<String, Obj> env) {
    if (asBool(v) == value)
      return env;
    else return null;
  }

  public Obj typeCheck(Obj tval, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    return results.apply(Bool, tenv);
  }

}
