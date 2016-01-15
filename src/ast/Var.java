package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;
import static value.Obj.*;

@BoaConstructor(fields = { "name" })
public class Var extends AST {

  public String name;

  public Var() {
  }

  public Var(String name) {
    this.name = name;
  }

  public String toString() {
    return "Var(" + name + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    if (!env.binds(name)) {
      for (Obj slot : iterate(self.slots())) {
        if (slot.get("name").toString().equals(name)) return slot.get("value");
      }
      throw new Error("unbound variable " + name + " in " + env);
    } else return env.lookup(name);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    if (tenv.binds(name))
      return tenv.lookup(name);
    else throw new Error("unbound variable " + name);
  }

}
