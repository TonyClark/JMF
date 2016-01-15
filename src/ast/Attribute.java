package ast;

import static value.Obj.Attribute;
import static value.Obj.Str;
import static value.Obj.send;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "name", "type" })
public class Attribute {

  public String name;
  public Type   type;

  public String toString() {
    return "Attribute(" + name + "," + type + ")";
  }

  public Obj eval(Env<String, Obj> env) {
    return send(Attribute, "new", Str(name), type.eval(env));
  }

}
