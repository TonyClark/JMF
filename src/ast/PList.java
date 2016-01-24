package ast;

import static value.Obj.*;

import java.util.Arrays;
import java.util.function.BiFunction;

import env.Env;
import exp.BoaConstructor;
import value.Cons;
import value.Obj;

@BoaConstructor(fields = { "elements" })
public class PList extends Pattern {

  public Pattern[] elements;

  public PList() {
  }

  public String toString() {
    return "PList(" + Arrays.toString(elements) + ")";
  }

  public Env<String, Obj> match(Obj v, Obj self, Env<String, Obj> env) {
    if (isTrue(send(ListOf(Obj), "isInstance", v))) {
      if (Cons.length(v) == elements.length) {
        for (int i = 0; i < elements.length; i++) {
          if (env != null) {
            env = elements[i].match(head(v), self, env);
            v = tail(v);
          }
        }
        return env;
      } else return null;
    } else return null;
  }

  public Obj typeCheck(Obj tval, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    if (isTrue(send(tval, "inherits", ListOf(Obj)))) {
      return typeCheck(0, tval, tself, tenv, env, results);
    } else throw new Error("expecting a list type: " + tval);
  }

  private Obj typeCheck(int i, Obj tval, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    if (i == elements.length)
      return results.apply(tval, tenv);
    else {
      return elements[i].typeCheck(get(tval, "elementType"), tself, tenv, env, (type, tenv2) -> typeCheck(i + 1, tval, tself, tenv2, env, results));
    }
  }

}
