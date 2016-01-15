package ast;

import static value.Obj.*;

import java.util.Arrays;

import env.Env;
import exp.BoaConstructor;
import value.Cons;
import value.Obj;

@BoaConstructor(fields = { "object", "args" })
public class Apply extends AST {

  public AST   object;
  public AST[] args;

  public String toString() {
    return "Apply(" + object + Arrays.toString(args) + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    return desugar().eval(self, env);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj t = object.typeCheck(tself, tenv, env);
    if (t.of() == FunctionOf)
      return checkFunctionApply(tself, t, tenv, env);
    else if (t.of() == Class)
      return checkClassApply(tself, t, tenv, env);
    else return desugar().typeCheck(tself, tenv, env);
  }

  private Obj checkFunctionApply(Obj tself, Obj t, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj domain = t.get("domain");
    Obj range = t.get("type");
    if (args.length == Cons.length(domain)) {
      for (int i = 0; i < args.length; i++) {
        Obj valueType = args[i].typeCheck(tself, tenv, env);
        Obj argType = head(domain);
        domain = tail(domain);
        if (!isTrue(valueType.send("inherits", argType))) throw new Error("illegal value: " + valueType + " does not match declared type " + argType);
      }
      return range;
    } else throw new Error("expecting " + Cons.length(domain) + " args but supplied with " + args.length);
  }

  private Obj checkClassApply(Obj tself, Obj t, Env<String, Obj> tenv, Env<String, Obj> env) {
    throw new Error("check class application " + t);
  }

  private AST desugar() {
    return new Send(object, "invoke", args);
  }

}
