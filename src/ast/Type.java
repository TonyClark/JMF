package ast;

import env.Env;
import value.Obj;

public abstract class Type {

  public abstract Obj eval(Env<String, Obj> env) ;

}
