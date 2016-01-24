package ast;

import java.util.Arrays;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

@BoaConstructor(fields = { "exps", "arms", "alt" })
public class Case extends AST {

  public Arm[] arms;
  public AST[] exps;
  public AST   alt;

  public Case() {
  }

  public String toString() {
    return "Case(" + Arrays.toString(exps) + "," + Arrays.toString(arms) + "," + alt + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj[] values = new Obj[exps.length];
    for (int i = 0; i < exps.length; i++)
      values[i] = exps[i].eval(self, env);
    for (Arm arm : arms) {
      Env<String, Obj> matchedEnv = arm.match(values, self, env);
      if (matchedEnv != null) return arm.eval(self, matchedEnv);
    }
    return alt.eval(self, env);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj[] types = new Obj[exps.length];
    for (int i = 0; i < exps.length; i++)
      types[i] = exps[i].typeCheck(tself, tenv, env);
    // Check that the type matches each pattern and return the highest
    // pattern expression and default type...
    Obj typeCheck = Null;
    for (Arm arm : arms) {
      Obj armType = arm.typeCheck(types, tself, tenv, env);
      if (isTrue(typeCheck.send("inherits", armType)))
        typeCheck = armType;
      else if (!isTrue(armType.send("inherits", typeCheck))) throw new Error("incompatible case arms and case value expression type :" + armType + " and " + typeCheck);
    }
    Obj altType = alt.typeCheck(tself, tenv, env);
    if (isTrue(altType.send("inherits", typeCheck)))
      return typeCheck;
    else if (isTrue(typeCheck.send("inherits", altType)))
      return altType;
    else throw new Error("incomatible case alt type: " + altType + " and " + typeCheck);

  }
}
