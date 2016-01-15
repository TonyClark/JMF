package value;

import static value.Cons.asList;
import static value.Obj.*;

public class Float extends Atom {

  static final long serialVersionUID = -2711408576855837155L;
  double            value;

  public Float(Obj type, double value) {
    super(type);
    this.value = value;
  }

  public Float(double value) {
    this(Float, value);
  }

  public Obj get(String name) {
    if (name.equals("value")) return Float(value);
    return super.get(name);
  }

  public Obj set(String name, Obj value) {
    if (name.equals("value")) {
      Obj old = Float(this.value);
      Float i = (Float) value;
      this.value = i.getValue();
      Classifier_throwFailures(type, this);
      Class_invokeDaemons(type, this, Str("value"), this, old);
      return this;
    } else return super.set(name, value);
  }

  public double getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public Obj slots() {
    return theObjNil;
  }

  public boolean equals(Object other) {
    if (other instanceof Float) {
      Float i = (Float) other;
      return value == i.value;
    } else return super.equals(other);
  }

  public String toString() {
    return send("asString").toString();
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(Float, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }
}
