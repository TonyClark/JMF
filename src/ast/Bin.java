package ast;

import static value.Obj.*;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "left", "op", "right" })
public class Bin extends AST {

  public AST    left;
  public String op;
  public AST    right;

  public Bin() {
  }

  public String toString() {
    return "Bin(" + left + "," + op + "," + right + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    return left.eval(self, env).send(op, right.eval(self, env));
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj lt = left.typeCheck(tself, tenv, env);
    Obj rt = right.typeCheck(tself, tenv, env);
    if (op.equals("=")) return Bool;
    if (op.equals("+")) {
      if (lt == Int && rt == Int)
        return Int;
      else if (lt == Str || rt == Str)
        return Str;
      else return Obj;
    }
    throw new Error("unknown operator: " + op);
  }

}
