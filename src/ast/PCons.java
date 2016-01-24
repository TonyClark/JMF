package ast;

import static value.Obj.*;

import java.util.Arrays;
import java.util.function.BiFunction;

import env.Env;
import exp.BoaConstructor;
import value.Cons;
import value.Obj;

@BoaConstructor(fields = { "head", "tail" })
public class PCons extends Pattern {

  public Pattern head;
  public Pattern tail;

  public PCons() {
  }

  public String toString() {
    return "PCons(" + head + "," + tail + ")";
  }

  public Env<String, Obj> match(Obj v, Obj self, Env<String, Obj> env) {
    if (isTrue(send(ConsOf(Obj), "isInstance", v))) {
      env = head.match(head(v), self, env);
      if (env != null)
        return tail.match(tail(v), self, env);
      else return null;
    } else return null;
  }

  public Obj typeCheck(Obj tval, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env, BiFunction<Obj, Env<String, Obj>, Obj> results) {
    if (isTrue(send(tval, "inherits", ListOf(Obj)))) {
      return head.typeCheck(get(tval, "elementType"), tself, tenv, env, (type, tenv2) -> tail.typeCheck(tval, tself, tenv2, env, results));
    } else return null;
  }

}
