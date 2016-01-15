package ast;

import static value.Obj.Null;
import static value.Obj.isTrue;
import static value.Obj.iterate;
import static value.Obj.send;
import static value.Obj.theObjNull;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

@BoaConstructor(fields = { "name", "type", "list", "body" })
public class For extends AST {

  public String name;
  public Type   type;
  public AST    list;
  public AST    body;

  public For() {
  }

  public String toString() {
    return "For(" + name + "," + type + "," + list + "," + body + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    for (Obj o : iterate(list.eval(self, env)))
      body.eval(null, env.bind(name, o));
    return theObjNull;
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj listType = list.typeCheck(tself, tenv, env);
    Obj varType = type.eval(env);
    if (isTrue(send(listType, "inherits", varType))) body.typeCheck(tself, tenv.bind(name, varType), env);
    return Null;
  }

}
