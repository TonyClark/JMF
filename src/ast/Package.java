package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

import java.util.Arrays;

@BoaConstructor(fields = { "name", "contents" })
public class Package extends AST {

  public String name;
  public AST[]  contents;

  public Package() {
  }

  public String toString() {
    return "Package(" + name + "," + Arrays.toString(contents) + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj p = send(Package, "new", Str(name));
    for (AST a : contents) {
      send(p, "addObject", a.eval(null, env));
    }
    return p;
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    for (AST e : contents)
      e.typeCheck(null, tenv, env);
    return Package;
  }

}
