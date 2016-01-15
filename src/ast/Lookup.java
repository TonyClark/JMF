package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

@BoaConstructor(fields = { "object", "name" })
public class Lookup extends AST {

  public AST    object;
  public String name;

  public Lookup() {
  }

  public String toString() {
    return "Lookup(" + object + "," + name + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj o = object.eval(self, env);
    if (isTrue(send(Package, "isInstance", o)))
      if (isTrue(send(o, "hasObjectNamed", Str(name))))
        return send(o, "getObjectNamed", Str(name));
      else throw new Error("no object named " + name + " in " + o);
    else throw new Error("unknown object to lookup: " + name);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj type = object.typeCheck(tself, tenv, env);
    if (isTrue(send(type, "inherits", Package)))
      return Class;
    else throw new Error("type check :: expects a package: " + object);
  }

}
