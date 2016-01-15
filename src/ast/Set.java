package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

@BoaConstructor(fields = { "object", "name", "value" })
public class Set extends AST {

  public AST    object;
  public String name;
  public AST    value;

  public Set() {
  }

  public String toString() {
    return "Set(" + object + "," + name + "," + value + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj o = object.eval(self, env);
    Obj v = value.eval(self, env);
    return set(o, name, v);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj t = object.typeCheck(tself, tenv, env);
    if (t.of() == Class)
      return standardMOPTypeCheck(tself, t, tenv, env);
    else {
      Obj type = t.send("typeCheckSet", Str(name), value.typeCheck(null, tenv, env));
      if (type == theObjNull)
        throw new Error("type error in " + this);
      else return type;
    }
  }

  public Obj standardMOPTypeCheck(Obj tself, Obj type, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj atts = send(type, "allAttributes");
    for (Obj a : iterate(atts)) {
      if (get(a, "name").toString().equals(name)) {
        Obj valueType = value.typeCheck(tself, tenv, env);
        if (isTrue(send(valueType, "inherits", get(a, "type"))))
          return type;
        else throw new Error("cannot assign " + valueType + " to " + get(a, "type"));
      }
    }
    throw new Error("cannot find attribute " + name);
  }

}
