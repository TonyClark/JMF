package value;

import static value.Cons.asList;
import static value.Obj.*;

public class Bool extends Atom {

  static final long serialVersionUID = -1648280149502918918L;

  public static Obj theObjTrue  = new Bool(true);
  public static Obj theObjFalse = new Bool(false);

  boolean value;

  public Bool(Obj type, boolean value) {
    super(type);
    this.value = value;
  }

  public Bool(boolean value) {
    this(Bool, value);
  }

  public Obj get(String name) {
    if (name.equals("value")) return Bool(value);
    return super.get(name);
  }

  public Obj set(String name, Obj value) {
    if (name.equals("value")) {
      Obj old = Bool(this.value);
      Bool i = (Bool) value;
      this.value = i.getValue();
      Classifier_throwFailures(type, this);
      Class_invokeDaemons(type, this, Str("value"), this, old);
      return this;
    } else return super.set(name, value);
  }

  public boolean getValue() {
    return value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }

  public Obj slots() {
    return theObjNil;
  }

  public boolean equals(Object other) {
    if (other instanceof Bool) {
      Bool i = (Bool) other;
      return value == i.value;
    } else return super.equals(other);
  }

  public String toString() {
    return Boolean.toString(value);
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(Bool, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }
}