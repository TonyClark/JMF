package ast;

import static value.Obj.FunctionOf;
import static value.Obj.head;
import static value.Obj.isTrue;
import static value.Obj.list;
import static value.Obj.send;
import static value.Obj.tail;

import java.util.Arrays;

import env.Env;
import exp.BoaConstructor;
import value.ConcreteFun;
import value.Cons;
import value.Obj;

@BoaConstructor(fields = { "name", "formals", "type", "exp" })
public class Op extends AST {

  public String   name;
  public Formal[] formals;
  public Type     type;
  public AST      exp;

  public Op() {
  }

  public String toString() {
    return "Op(" + name + "," + Arrays.toString(formals) + "," + type + "," + exp + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    String[] names = new String[formals.length];
    for (int i = 0; i < formals.length; i++)
      names[i] = formals[i].name;
    ConcreteFun fun = null;
    if(formals.length == 0)
      fun = new ConcreteFun(name, () -> apply(list(), env), names);
    else if(formals.length == 1)
      fun = new ConcreteFun(name, (v1) -> apply(list(v1), env), names);
    else if(formals.length == 2)
      fun = new ConcreteFun(name, (v1,v2) -> apply(list(v1,v2), env), names);
    else if(formals.length == 3)
      fun = new ConcreteFun(name, (v1,v2,v3) -> apply(list(v1,v2,v3), env), names);
    else throw new Error("Arity > 3 not supported in evaluator.");
    fun.set("type", getType(env));
    return fun;
  }
  
  public Obj getType(Env<String, Obj> env) {
    return FunctionOf(domain(env),type.eval(env));
  }

  public Obj domain(Env<String, Obj> env) {
    Obj domain = list();
    for(Formal formal : formals) {
      domain = domain.send("append", list(formal.type.eval(env)));
    }
    return domain;
  }

  public Obj apply(Obj args, Env<String, Obj> env) {
    if (formals.length == Cons.length(args)) {
      for (int i = 0; i < formals.length; i++) {
        // Check the types!!!!
        env = env.bind(formals[i].name, head(args));
        args = tail(args);
      }
      return exp.eval(null, env);
    }
    throw new Error("expecting " + formals.length + " args but supplied with " + Cons.length(args));
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    for (Formal f : formals) {
      tenv = tenv.bind(f.name, f.type.eval(env));
    }
    Obj valueType = exp.typeCheck(tself, tenv, env);
    Obj declaredType = type.eval(env);
    if (isTrue(send(valueType, "inherits", declaredType)))
      return getType(tenv);
    else throw new Error("operation " + name + " declares return type " + declaredType + " but produces " + valueType);
  }

}
