package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;
import static value.Obj.*;

@BoaConstructor(fields = { "patterns", "exp" })

public class Arm {

  public Pattern[] patterns;
  public AST       exp;

  public Arm() {
  }

  public Env<String, Obj> match(Obj[] values, Obj self, Env<String, Obj> env) {
    if (patterns.length == values.length) {
      for (int i = 0; i < patterns.length; i++)
        if (env != null) env = patterns[i].match(values[i], self, env);
      return env;
    } else throw new Error("case arm expects " + patterns.length + " values but supplied with " + values.length);
  }

  public Obj eval(Obj self, Env<String, Obj> matchedEnv) {
    return exp.eval(self, matchedEnv);
  }

  public Obj typeCheck(Obj[] types, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    if (patterns.length == types.length)
      return typeCheck(0, types, tself, tenv, env);
    else throw new Error("case arm expects " + patterns.length + " values but supplied with " + types.length);
  }

  private Obj typeCheck(int i, Obj[] types, Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    if (i == patterns.length)
      return exp.typeCheck(tself, tenv, env);
    else {
      return patterns[i].typeCheck(types[i], tself, tenv, env, (ptype, tenv2) ->
      {
        if (isFalse(ptype.send("inherits", types[i]))) throw new Error("incompatible case arm pattern " + patterns[i] + ":" + ptype + " corresponding value is of type " + types[i]);
        return typeCheck(i + 1, types, tself, tenv2, env);
      });
    }
  }

}
