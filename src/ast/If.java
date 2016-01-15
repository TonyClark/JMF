package ast;

import static value.Obj.Bool;
import static value.Obj.isFalse;
import static value.Obj.isTrue;
import static value.Obj.send;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "test", "conseq", "alt" })
public class If extends AST {

  public AST test;
  public AST conseq;
  public AST alt;

  public If() {
  }

  public String toString() {
    return "If(" + test + "," + conseq + "," + alt + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj b = test.eval(self, env);
    if (isTrue(b))
      return conseq.eval(self, env);
    else if (isFalse(b))
      return alt.eval(self, env);
    else throw new Error("if expects a boolean: " + b);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    if (isTrue(send(test.typeCheck(tself, tenv, env), "inherits", Bool))) {
      Obj conseqType = conseq.typeCheck(tself, tenv, env);
      Obj altType = alt.typeCheck(tself, tenv, env);
      if (isTrue(send(conseqType, "inherits", altType)))
        return altType;
      else if (isTrue(send(altType, "inherits", conseqType)))
        return conseqType;
      else throw new Error("if: types incompatible " + conseqType + " and " + altType);
    } else throw new Error("if: expects a boolean " + test);
  }

}
