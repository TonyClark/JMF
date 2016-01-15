package ast;

import static value.Obj.head;
import static value.Obj.list;
import static value.Obj.tail;

import env.Env;
import exp.BoaConstructor;
import value.ConcreteFun;
import value.Cons;
import value.Obj;

@BoaConstructor(fields = { "name", "formals", "exp" })
public class Cnstr extends Op {

  public Obj eval(Obj self, Env<String, Obj> env) {
    String[] names = new String[formals.length + 2];
    names[0] = "self";
    names[1] = "super";
    for (int i = 2; i < formals.length + 2; i++)
      names[i] = formals[i - 2].name;
    ConcreteFun fun = null;
    if (formals.length == 0)
      fun = new ConcreteFun(name, (o, so) -> apply(list(o, so), env), names);
    else if (formals.length == 1)
      fun = new ConcreteFun(name, (o, so, v1) -> apply(list(o, so, v1), env), names);
    else if (formals.length == 2)
      fun = new ConcreteFun(name, (o, so, v1, v2) -> apply(list(o, so, v1, v2), env), names);
    else if (formals.length == 3)
      fun = new ConcreteFun(name, (o, so, v1, v2, v3) -> apply(list(o, so, v1, v2, v3), env), names);
    else if (formals.length == 4)
      fun = new ConcreteFun(name, (o, so, v1, v2, v3, v4) -> apply(list(o, so, v1, v2, v3, v4), env), names);
    else throw new Error("Arity > 3 not supported in evaluator.");
    return fun;
  }

  public Obj apply(Obj args, Env<String, Obj> env) {
    if (formals.length + 2 == Cons.length(args)) {
      Obj self = head(args);
      env = env.bind("self", head(args));
      args = tail(args);
      env = env.bind("super", head(args));
      args = tail(args);
      for (int i = 0; i < formals.length; i++) {
        // Check the types!!!!
        env = env.bind(formals[i].name, head(args));
        args = tail(args);
      }
      return exp.eval(self, env);
    }
    throw new Error("expecting " + formals.length + " args but supplied with " + Cons.length(args));
  }

}
