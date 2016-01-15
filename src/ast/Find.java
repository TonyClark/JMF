package ast;

import static value.Obj.*;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "name", "type", "exp", "pred", "body", "alt" })
public class Find extends AST {

  public String name;
  public Type   type;
  public AST    exp;
  public AST    pred;
  public AST    body;
  public AST    alt;

  public Find() {
  }

  public String toString() {
    return "Find(" + name + "," + type + "," + exp + "," + pred + "," + body + "," + alt + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj l = exp.eval(self, env);
    for (Obj x : iterate(l)) {
      if (isTrue(pred.eval(self, env.bind(name, x)))) return body.eval(self, env.bind(name, x));
    }
    return alt.eval(self, env);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj t = exp.typeCheck(tself, tenv, env);
    if (isTrue(t.send("inherits", ListOf))) {
      Obj et = t.get("elementType");
      Obj dt = type.eval(env);
      if (isTrue(et.send("inherits", dt)))
        if (pred.typeCheck(tself, tenv, env) == Bool)
          return body.typeCheck(tself, tenv, env);
        else throw new Error("find predicate must return a boolean: " + pred);
      else throw new Error("find declared type does not agree with list type.");
    } else throw new Error("find must iterate over a list: " + exp);
  }

}
