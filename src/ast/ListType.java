package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;
import static value.Obj.*;

@BoaConstructor(fields = { "type" })
public class ListType extends Type {

  public Type type;

  public ListType() {
  }

  public String toString() {
    return "ListType(" + type + ")";
  }

  public Obj eval(Env<String, Obj> env) {
    return ListOf(type.eval(env));
  }

}
