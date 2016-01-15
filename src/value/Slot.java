package value;

import static value.Cons.asList;
import static value.Obj.*;

public class Slot implements Obj, Kernel {

  static final long serialVersionUID = -3604855441698606235L;
  String            name;
  Obj               value;
  Obj               slots            = null;

  public Slot(String name, Obj value) {
    super();
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Obj getValue() {
    return value;
  }

  public void setValue(Obj value) {
    this.value = value;
  }

  public String toString() {
    return name + "=" + value;
  }

  public Obj of() {
    return Slot;
  }

  public Obj get(String name) {
    if (name.equals("name")) return new value.Str(getName());
    if (name.equals("value")) return value;
    if (name.equals("operations")) return theObjNil;
    return send("noSlotFound", new value.Str(name));
  }

  public Obj set(String name, Obj value) {
    if (name.equals("name")) {
      this.name = value.toString();
      return this;
    }
    if (name.equals("value")) {
      this.value = value;
      return this;
    }
    throw new Error("cannot perform " + this + "." + name + " = " + value);
  }

  public value.Obj slots() {
    if (slots == null) {
      slots = list(Slot("name", Str(name)), Slot("value", value));
      return slots;
    } else return slots;
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(Slot, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }

}
