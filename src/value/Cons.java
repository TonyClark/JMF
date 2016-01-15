package value;

import static value.Cons.asList;
import static value.Obj.*;

public class Cons implements Obj, Kernel {

  static final long serialVersionUID = 8149855013353457139L;
  Obj               head;
  Obj               tail;

  public Cons(Obj head, Obj tail) {
    super();
    if (head == null) throw new Error("null head component.");
    if (tail == null) throw new Error("tail is null, head = " + head);
    this.head = head;
    this.tail = tail;
  }

  public Obj getHead() {
    return head;
  }

  public void setHead(Obj head) {
    this.head = head;
  }

  public Obj getTail() {
    return tail;
  }

  public void setTail(Obj tail) {
    this.tail = tail;
  }

  public String toString() {
    if (isProperList(this))
      return properListToString();
    else return "(" + head + " . " + tail + ")";
  }

  private String properListToString() {
    Cons c = this;
    String s = "[";
    while (!isNil(c.getTail())) {
      s = s + c.getHead() + ",";
      c = (Cons) c.getTail();
    }
    return s + c.getHead() + "]";
  }

  public static boolean isProperList(Obj o) {
    if (isNil(o))
      return true;
    else if (isCons(o)) {
      Cons c = (Cons) o;
      return isProperList(c.getTail());
    } else return false;
  }

  public static boolean isCons(Obj o) {
    return o instanceof Cons;
  }

  public static boolean isNil(Obj o) {
    return o instanceof Nil;
  }

  public Obj of() {
    return ConsOf(head.of());
  }

  public Obj get(String name) {
    if (name.equals("of")) return of();
    if (name.equals("head")) return getHead();
    if (name.equals("tail")) return getTail();
    return send("noSlotFound", new value.Str(name));
  }

  public Obj set(String name, Obj value) {
    throw new Error("update if supplied with value");
  }

  public static Obj asList(int i, Obj[] objs) {
    if (i >= objs.length)
      return Nil.theObjNil;
    else return new Cons(objs[i], asList(i + 1, objs));
  }

  public static int length(Obj l) {
    if (isProperList(l))
      return properListLength(l);
    else throw new Error("cannot take length of " + l);
  }

  private static int properListLength(Obj o) {
    if (isNil(o))
      return 0;
    else {
      Cons c = (Cons) o;
      return properListLength(c.getTail()) + 1;
    }
  }

  public value.Obj slots() {
    return list(Slot("head", head), Slot("tail", tail));
  }

  public static Obj[] asArray(Obj cons) {
    Obj[] a = new Obj[length(cons)];
    for (int i = 0; i < a.length; i++) {
      a[i] = value.Obj.get(cons, "head");
      cons = value.Obj.get(cons, "tail");
    }
    return a;
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(of(), ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }
}
