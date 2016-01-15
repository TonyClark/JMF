package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

@BoaConstructor(fields = { "name", "value" })
public class Update extends AST {

  public String name;
  public AST    value;

  public String toString() {
    return "Update(" + "," + name + "," + value + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj v = value.eval(self, env);
    return self.set(name, v);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    if (tself.of() == Class)
      return standardMOPTypeCheck(tself, tenv, env);
    else {
      Obj type = tself.send("typeCheckSet", Str, value.typeCheck(null, tenv, env));
      if (type == theObjNull)
        throw new Error("type error in " + this);
      else return type;
    }
  }

  public Obj standardMOPTypeCheck(Obj type, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj atts = send(type, "allAttributes");
    for (Obj a : iterate(atts)) {
      if (get(a, "name").toString().equals(name)) {
        Obj valueType = value.typeCheck(type, tenv, env);
        if (isTrue(send(valueType, "inherits", get(a, "type"))))
          return type;
        else throw new Error("cannot assign " + valueType + " to " + get(a, "type"));
      }
    }
    throw new Error("cannot find attribute " + name);
  }

}
