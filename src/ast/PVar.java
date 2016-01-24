package ast;

import java.util.function.BiFunction;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "name" })
public class PVar extends Pattern {

  public String name;

  public PVar() {
  }

  public String toString() {
    return "PVar(" + name + ")";
  }

  public Env<String, Obj> match(Obj v, Obj self, Env<String, Obj> env) {
    if (name.equals("")) return env;
    if (env.binds(name))
      if (env.lookup(name).equals(v))
        return env;
      else return null;
    else return env.bind(name, v);
  }

  public Obj typeCheck(Obj tval, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    if (name.equals(""))
      return results.apply(tval, tenv);
    else if (tenv.binds(name))
      return results.apply(tenv.lookup(name), tenv);
    else return results.apply(tval, tenv.bind(name, tval));
  }

}
