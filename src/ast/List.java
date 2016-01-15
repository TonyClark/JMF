package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

import java.util.Arrays;

@BoaConstructor(fields = { "elements" })
public class List extends AST {

  public AST[] elements;

  public List() {
  }

  public String toString() {
    return "List(" + Arrays.toString(elements) + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj list = list();
    for (int i = elements.length - 1; i >= 0; i--)
      list = cons(elements[i].eval(self, env), list);
    return list;
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj t = Null;
    for (AST e : elements) {
      Obj valueType = e.typeCheck(tself, tenv, env);
      if (isTrue(send(t, "inherits", valueType)))
        t = valueType;
      else throw new Error("list has incompatible elements " + this + " " + t + " does not inherit from " + valueType);
    }
    return ListOf(t);
  }

}
