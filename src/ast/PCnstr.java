package ast;

import static value.Obj.*;

import java.util.Arrays;
import java.util.function.BiFunction;

import env.Env;
import exp.BoaConstructor;
import value.Cons;
import value.Obj;

@BoaConstructor(fields = { "name", "args" })
public class PCnstr extends Pattern {

  public String    name;
  public Pattern[] args;

  public PCnstr() {
  }

  public String toString() {
    return "PCnstr(" + name + "," + Arrays.toString(args) + ")";
  }

  public Env<String, Obj> match(Obj v, Obj self, Env<String, Obj> env) {
    if (env.binds(name)) {
      Obj type = env.lookup(name);
      if (isTrue(send(type, "isInstance", v))) {
        Obj cnstrs = send(type, "getAllConstructors");
        for (Obj cnstr : iterate(cnstrs)) {
          Obj names = get(cnstr, "args");
          if (Cons.length(names) == args.length) {
            for (int i = 0; i < args.length; i++) {
              env = args[i].match(get(v,head(names).toString()), self, env);
              names = tail(names);
              if (env == null) return null;
            }
            return env;
          }
        }
        throw new Error("cannot find a constructor matching " + this);
      } else return null;
    } else throw new Error("cannot find class " + name);
  }

  public Obj typeCheck(Obj tval, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    if (env.binds(name)) {
      Obj type = env.lookup(name);
      if (isTrue(send(type, "inherits", tval))) {
        Obj cnstrs = send(type, "getAllConstructors");
        for (Obj cnstr : iterate(cnstrs)) {
          Obj names = get(cnstr, "args");
          if (Cons.length(names) == args.length) return typeCheck(0, names, type, tself, tenv, env, results);
        }
        throw new Error("cannot find a constructor matching " + this);
      } else throw new Error(tval + " does not inherit from " + type);
    } else throw new Error("cannot find class " + name);
  }

  private Obj typeCheck(int i, Obj names, Obj type, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    if (i == args.length) return results.apply(type, tenv);
    Obj name = head(names);
    for (Obj att : iterate(send(type, "getAllAttributes"))) {
      if (get(att, "name").equals(name)) { return args[i].typeCheck(get(att, "type"), tself, tenv, env, (type2, tenv2) -> typeCheck(i + 1, tail(names), type, tself, tenv2, env, results)); }
    }
    throw new Error("cannot find attribute named " + name + " in " + type);
  }

}
