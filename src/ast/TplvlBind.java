package ast;

import static value.Obj.get;

import exp.BoaConstructor;
import machine.Machine;
import value.Obj;
import values.JavaObject;

@BoaConstructor(fields = { "def" })
public class TplvlBind extends JMFCommand {

  public Object def;

  public TplvlBind() {
  }

  public TplvlBind(Object def) {
    this.def = def;
  }

  public boolean perform(Machine machine) {
    if (def instanceof Class) {
      Class c = (Class) def;
      c.typeCheck(null, getTypeEnv(machine), getEnv(machine));
      Obj o = c.eval(null, getEnv(machine));
      machine.bind(get(o, "name").toString(), new JavaObject(o));
      System.out.println("define " + o);
    }
    if (def instanceof Package) {
      Package p = (Package) def;
      p.typeCheck(null, getTypeEnv(machine), getEnv(machine));
      Obj o = p.eval(null, getEnv(machine));
      machine.bind(get(o, "name").toString(), new JavaObject(o));
      System.out.println("define " + o);
    }
    if (def instanceof Op) {
      Op o = (Op) def;
      o.typeCheck(null, getTypeEnv(machine), getEnv(machine));
      Obj op = o.eval(null, getEnv(machine));
      machine.bind(get(op, "name").toString(), new JavaObject(op));
      System.out.println("define " + op);
    }
    return false;
  }
}
