package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

@BoaConstructor(fields = { "object", "name" })
public class Get extends AST {

  public AST    object;
  public String name;

  public Get() {
  }

  public String toString() {
    return "Get(" + object + "," + name + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj o = object.eval(self, env);
    return get(o, name);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj t = object.typeCheck(tself, tenv, env);
    if (t.of() == Class)
      return standardMOPTypeCheck(t, env);
    else {
      Obj type = t.send("typeCheckGet", Str(name));
      if (type == theObjNull)
        throw new Error("type error in " + this);
      else return type;
    }
  }

  public Obj standardMOPTypeCheck(Obj type, Env<String, Obj> env) {
    Obj atts = send(type, "allAttributes");
    for (Obj a : iterate(atts)) {
      if (get(a, "name").toString().equals(name)) return get(a, "type");
    }
    throw new Error("cannot find attribute " + name + " in " + atts);
  }

}
