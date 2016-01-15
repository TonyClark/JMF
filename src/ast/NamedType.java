package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "name" })
public class NamedType extends Type {

  public String name;

  public NamedType() {
  }

  public NamedType(String name) {
    this.name = name;
  }

  public String toString() {
    return "NamedType(" + name + ")";
  }

  public Obj eval(Env<String, Obj> env) {
    if (env.binds(name))
      return env.lookup(name);
    else throw new Error("cannot find type named " + name + " in " + env);
  }

}
