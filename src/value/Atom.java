package value;

import static value.Obj.*;

public abstract class Atom implements Obj, Kernel {

  private static final long serialVersionUID = -8487143723991572566L;

  Obj                       type;
  Slot[]                    slots;

  public Atom(value.Obj type) {
    this.type = type;
    if (type != Int & type != Str & type != Bool & type != Function) {
      Obj attributes = value.Obj.send(type, "getAllAttributes");
      int length = Cons.length(attributes);
      slots = new Slot[length];
      for (int i = 0; i < length; i++) {
        slots[i] = Slot(value.Obj.get(head(attributes), "name").toString(), value.Obj.get(value.Obj.get(head(attributes), "type"), "initial"));
        attributes = tail(attributes);
      }
    }
  }

  public Slot[] getSlots() {
    return slots;
  }

  public Obj get(String name) {
    for (Slot slot : slots) {
      if (slot.getName().equals(name)) return slot.getValue();
    }
    return value.Obj.send(this, "noSlotFound", new value.Str(name));
  }

  public Obj set(String name, Obj value) {
    for (Slot slot : slots) {
      if (slot.getName().equals(name)) {
        Obj old = slot.getValue();
        slot.setValue(value);
        Classifier_throwFailures(type, this);
        Class_invokeDaemons(type, this, Str("value"), this, old);
      }
    }
    return this;
  }

  public Obj of() {
    return type;
  }

}
