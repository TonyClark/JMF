package ast;

import static value.Obj.Int;
import static value.Obj.asInt;

import java.util.function.BiFunction;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "value" })
public class PInt extends Pattern {

  public int value;

  public PInt() {
  }

  public String toString() {
    return "PInt(" + value + ")";
  }

  public Env<String, Obj> match(Obj v, Obj self, Env<String, Obj> env) {
    if (asInt(v) == value)
      return env;
    else return null;
  }

  public Obj typeCheck(Obj tval, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    return results.apply(Int, tenv);
  }

}
