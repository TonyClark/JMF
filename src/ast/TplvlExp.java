package ast;

import exp.BoaConstructor;
import machine.Machine;

@BoaConstructor(fields = { "exp" })
public class TplvlExp extends JMFCommand {

  public AST exp;

  public TplvlExp() {
  }

  public TplvlExp(AST exp) {
    this.exp = exp;
  }

  public boolean perform(Machine machine) {
    try {
      exp.typeCheck(null, getTypeEnv(machine), getEnv(machine));
      System.out.println("=> " + exp.eval(null, getEnv(machine)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
