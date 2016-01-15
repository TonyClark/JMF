package value;

import static value.Cons.asList;
import static value.Obj.*;

public class Str extends Atom {

  private static final long serialVersionUID = 7453269256715114251L;

  String                    value;

  public Str(String value) {
    this(Str, value);
  }

  public Str(Obj type, String value) {
    super(type);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String toString() {
    return value;
  }

  public Obj of() {
    return Str;
  }

  public Obj get(String name) {
    if (name.equals("value")) return Str(value);
    return super.get(name);
  }

  public Obj set(String name, Obj value) {
    if (name.equals("value")) {
      Obj old = Str(this.value);
      Str i = (Str) value;
      this.value = i.getValue();
      Classifier_throwFailures(type, this);
      Class_invokeDaemons(type, this, Str("value"), this, old);
      return this;
    } else return super.set(name, value);
  }

  public boolean equals(Object other) {
    if (other instanceof Str) {
      Str s = (Str) other;
      return value.equals(s.value);
    } else return super.equals(other);
  }

  public Obj slots() {
    return theObjNil;
  }

  public int hashCode() {
    return value.hashCode();
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(Str, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }

}
