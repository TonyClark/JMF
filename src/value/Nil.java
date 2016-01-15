package value;

import static value.Cons.asList;
import static value.Obj.*;

public class Nil implements Obj, Kernel {

  static final long serialVersionUID = 7282361791490604862L;
  Obj               type;

  public Nil(Obj type) {
    this.type = type;
  }

  public String toString() {
    return "[]";
  }

  public Obj of() {
    return NilOf(type);
  }

  public Obj get(String name) {
    if (name.equals("of")) return of();
    if (name.equals("head")) throw new Error("cannot take the head of [].");
    if (name.equals("tail")) throw new Error("cannot take the tail of [].");
    return send("noSlotFound", new value.Str(name));
  }

  public Obj set(String name, Obj value) {
    throw new Error("cannot set the value of a slot in []: " + name + " + value");
  }

  public Obj slots() {
    return theObjNil;
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(of(), ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }

}
