package ast;

import env.Env;
import value.Obj;

public abstract class Pattern {

  public abstract Env<String, Obj> match(Obj value, Obj self, Env<String, Obj> env);

  public abstract Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env);

}
