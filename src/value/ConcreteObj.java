package value;

import static value.Obj.*;
import static value.Cons.*;

import java.util.Arrays;

public class ConcreteObj implements Obj {

  static final long serialVersionUID = 5007692731289826397L;
  Obj               type;
  Slot[]            slots;

  public ConcreteObj(Obj type, Obj slots) {
    super();
    this.type = type;
    this.slots = new Slot[length(slots)];
    int i = 0;
    for (Obj s : iterate(slots))
      this.slots[i++] = (Slot) s;
  }

  public ConcreteObj(Obj type, Slot... slots) {
    super();
    this.type = type;
    this.slots = slots;
  }

  public void addSlot(Slot slot) {
    Slot[] newSlots = new Slot[slots.length + 1];
    for (int i = 0; i < slots.length; i++)
      newSlots[i] = slots[i];
    newSlots[slots.length] = slot;
    slots = newSlots;
  }

  public void addSlot(String name, Obj value) {
    addSlot(Slot(name, value));
  }

  public void addSlot(String name, String value) {
    addSlot(Slot(name, value));
  }

  public void addSlot(String name, int value) {
    addSlot(Slot(name, value));
  }

  public void extendSlot(String name, Obj value) {
    Slot slot = getSlot(name);
    if (slot != null) {
      slot.setValue(cons(value, slot.getValue()));
    } else throw new Error("cannot find slot " + name);
  }

  public Slot getSlot(String name) {
    for (Slot slot : slots)
      if (slot.getName().equals(name)) return slot;
    return null;
  }

  public Slot[] getSlots() {
    return slots;
  }

  public Obj getType() {
    return type;
  }

  public Obj of() {
    return type;
  }

  public void setSlots(Slot... slots) {
    this.slots = slots;
  }

  public void setType(Obj type) {
    this.type = type;
  }

  public void setSlotValue(String name, Obj value) {
    getSlot(name).setValue(value);
  }

  public Obj slots() {
    return Cons.asList(0, slots);
  }

  public String toString() {
    if (debug.isNested())
      return "(" + value.Obj.get(type, "name") + ")" + Arrays.toString(slots);
    else return value.Obj.send(this, "asString").toString();
  }

  public value.Obj get(String name) {
    Slot slot = getSlot(name);
    if (slot == null)
      return send("slotNotFound", Str(name));
    else return slot.value;
  }

  public value.Obj set(String name, value.Obj value) {
    Slot slot = getSlot(name);
    if (slot == null)
      return send("slotNotFound", Str(name));
    else {
      slot.setValue(value);
      return this;
    }
  }

  public static Obj defaultSuperObj = new Obj() {

                                      private static final long serialVersionUID = 1L;

                                      public value.Obj of() {
                                        return Obj;
                                      }

                                      public value.Obj get(String name) {
                                        throw new Error("super does not have a slot " + name);
                                      }

                                      public value.Obj set(String name, value.Obj value) {
                                        throw new Error("super does not have a slot " + name);
                                      }

                                      public value.Obj send(String name, value.Obj... args) {
                                        throw new Error("super does not have a message " + name);
                                      }

                                      public value.Obj slots() {
                                        return theObjNil;
                                      }
                                    };

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(type, defaultSuperObj, this, Str(name), asList(0, args));
  }
}
