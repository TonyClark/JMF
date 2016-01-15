package ast;

import java.util.Arrays;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "bindings", "exp" })
public class Let extends AST {

  public Binding[] bindings;
  public AST       exp;

  public Let() {
  }

  public String toString() {
    return "Let(" + Arrays.toString(bindings) + "," + exp + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Env<String, Obj> newEnv = env;
    for (Binding b : bindings) {
      newEnv = newEnv.bind(b.name, b.value.eval(self, env));
    }
    return exp.eval(self, newEnv);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Env<String, Obj> newEnv = tenv;
    for (Binding b : bindings) {
      Obj type = b.typeCheck(tenv, env);
      newEnv = newEnv.bind(b.name, type);
    }
    return exp.typeCheck(tself, newEnv, env);
  }
}
