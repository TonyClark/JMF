package ast;

import static value.Obj.*;

import env.Env;
import exp.BoaConstructor;
import value.ConcreteFun;
import value.Cons;
import value.Obj;

@BoaConstructor(fields = { "selfSuper", "name", "formals", "type", "exp" })
public class Method extends Op {

  public String[] selfSuper;

  public Obj eval(Obj self, Env<String, Obj> env) {
    String[] names = new String[formals.length + 2];
    names[0] = selfSuper[0];
    names[1] = selfSuper[1];
    for (int i = 2; i < formals.length + 2; i++)
      names[i] = formals[i-2].name;
    ConcreteFun fun = null;
    if(formals.length == 0)
      fun = new ConcreteFun(name, (o,so) -> apply(list(o,so), env), names);
    else if(formals.length == 1)
      fun = new ConcreteFun(name, (o,so,v1) -> apply(list(o,so,v1), env), names);
    else if(formals.length == 2)
      fun = new ConcreteFun(name, (o,so,v1,v2) -> apply(list(o,so,v1,v2), env), names);
    else if(formals.length == 3)
      fun = new ConcreteFun(name, (o,so,v1,v2,v3) -> apply(list(o,so,v1,v2,v3), env), names);
    else throw new Error("Arity > 3 not supported in evaluator.");
    fun.set("type", getType(env));
    return fun;
  }

  public Obj domain(Env<String, Obj> env) {
    Obj domain = list(Self,Obj);
    for(Formal formal : formals) {
      domain = domain.send("append", list(formal.type.eval(env)));
    }
    return domain;
  }

  public Obj apply(Obj args, Env<String, Obj> env) {
    if (formals.length + 2 == Cons.length(args)) {
      Obj self = head(args);
      env = env.bind(selfSuper[0], head(args));
      args = tail(args);
      env = env.bind(selfSuper[1], head(args));
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
