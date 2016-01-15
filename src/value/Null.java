package value;

import static value.Cons.asList;
import static value.Obj.Classifier_send;
import static value.Obj.Str;

public class Null implements Obj, Kernel {

  private static final long serialVersionUID = 7977893838037211155L;
  public static Null        theObjNull       = new Null();

  public String toString() {
    return "null";
  }

  public Obj of() {
    return Null;
  }

  public Obj slots() {
    return theObjNil;
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(Null, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }

  public value.Obj get(String name) {
    throw new Error("null has no state: " + name);
  }

  public value.Obj set(String name, value.Obj value) {
    throw new Error("null has no state: " + name);
  }

}
