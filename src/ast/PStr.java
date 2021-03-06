package ast;

import static value.Obj.Str;

import java.util.function.BiFunction;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "value" })
public class PStr extends Pattern {

  public String value;

  public PStr() {
  }

  public String toString() {
    return "PStr(" + value + ")";
  }

  public Env<String, Obj> match(Obj v, Obj self, Env<String, Obj> env) {
    if (v.toString().equals(value))
      return env;
    else return null;
  }

  public Obj typeCheck(Obj tval, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    return results.apply(Str, tenv);
  }

}
