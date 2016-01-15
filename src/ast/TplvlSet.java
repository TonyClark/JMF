package ast;

import exp.BoaConstructor;
import machine.Machine;
import value.Obj;
import values.JavaObject;

@BoaConstructor(fields = { "name", "exp" })
public class TplvlSet extends JMFCommand {

  public String name;
  public AST    exp;

  public TplvlSet() {
  }

  public boolean perform(Machine machine) {
    Obj value = exp.eval(null, getEnv(machine));
    machine.bind(name, new JavaObject(value));
    System.out.println(name + " => " + value);
    return false;
  }
}
