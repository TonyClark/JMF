package value;

import static value.Cons.asList;
import static value.Obj.*;

public class Int extends Atom {

  static final long serialVersionUID = -2711408576855837155L;
  int               value;

  public Int(Obj type, int value) {
    super(type);
    this.value = value;
  }

  public Int(int value) {
    this(Int, value);
  }

  public Obj get(String name) {
    if (name.equals("value")) return Int(value);
    return super.get(name);
  }

  public Obj set(String name, Obj value) {
    if (name.equals("value")) {
      Obj old = Int(this.value);
      Int i = (Int) value;
      this.value = i.getValue();
      Classifier_throwFailures(type, this);
      Class_invokeDaemons(type, this, Str("value"), this, old);
      return this;
    } else return super.set(name, value);
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public Obj slots() {
    return theObjNil;
  }

  public boolean equals(Object other) {
    if (other instanceof Int) {
      Int i = (Int) other;
      return value == i.value;
    } else return super.equals(other);
  }

  public String toString() {
    return send("asString").toString();
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(Int, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }
}
