package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;
import static value.Obj.*;

@BoaConstructor(fields = { "pattern", "exp" })

public class Arm {

  public Pattern pattern;
  public AST     exp;

  public Arm() {
  }

  public Env<String, Obj> match(Obj value, Obj self, Env<String, Obj> env) {
    return pattern.match(value, self, env);
  }

  public Obj eval(Obj self, Env<String, Obj> matchedEnv) {
    return exp.eval(self, matchedEnv);
  }

  public Obj typeCheck(Obj type, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj ptype = pattern.typeCheck(tself, tenv, env);
    if (isTrue(type.send("inherits", ptype)))
      return exp.typeCheck(tself, tenv, env);
    else throw new Error("incompatible arm pattern: " + ptype + " does not match value type " + type);
  }

}
