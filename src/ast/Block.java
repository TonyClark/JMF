package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

import java.util.Arrays;

@BoaConstructor(fields = { "elements" })
public class Block extends AST {

  public AST[] elements;

  public Block() {
  }

  public String toString() {
    return "Block(" + Arrays.toString(elements) + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj value = theObjNull;
    for (int i = 0; i < elements.length; i++)
      value = elements[i].eval(self, env);
    return value;
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    for (AST e : elements)
      e.typeCheck(tself, tenv, env);
    return Null;
  }

}
