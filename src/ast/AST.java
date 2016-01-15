package ast;

import env.Empty;
import env.Env;
import value.Obj;

import static value.Obj.*;

public abstract class AST {

  static Env<String, Obj> topLvlEnv = new Empty<String, Obj>();

  static {
    Obj kernel = getKernel();
    Obj classes = get(kernel, "objects");
    for (Obj c : iterate(classes))
      topLvlEnv = topLvlEnv.bind(get(c, "name").toString(), c);
  }

  public abstract Obj eval(Obj self, Env<String, Obj> env);

  public abstract Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> venv);

  public Obj eval() {
    return eval(null, topLvlEnv);
  }

  public static Env<String, Obj> getTypeEnv() {
    Env<String, Obj> typeEnv = new Empty<String, Obj>();
    for (String name : topLvlEnv.dom())
      typeEnv = typeEnv.bind(name, topLvlEnv.lookup(name).of());
    return typeEnv;
  }

  public static Env<String, Obj> getTopLvlEnv() {
    return topLvlEnv;
  }

  public static void setTopLvlEnv(Env<String, Obj> topLvlEnv) {
    AST.topLvlEnv = topLvlEnv;
  }

}
