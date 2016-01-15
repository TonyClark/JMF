package ast;

import exp.BoaConstructor;

@BoaConstructor(fields = { "name", "type" })
public class Formal {

  public String name;
  public Type   type;

  public Formal() {
  }

  public String toString() {
    return "Formal(" + name + "," + type + ")";
  }

}
