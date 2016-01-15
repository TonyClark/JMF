package value;

import static value.Cons.asList;
import static value.Obj.*;

public class Attribute implements Obj,Kernel {

  static final long serialVersionUID = 699718850943619207L; 
  String            name;
  Obj               type;
  Obj               supers           = theObjNil;
  Obj               init             = Op((o, a) -> value.Obj.set(o, name, value.Obj.get(this.type, "initial")));

  public Attribute(String name, Obj type) {
    super();
    this.name = name;
    this.type = type;
  }

  public Attribute(String name, Obj type, Obj supers, Obj init) {
    this(name, type);
    this.supers = supers;
    this.init = init;
  }

  public Attribute(String name, Obj type, Obj init) {
    this(name, type);
    this.init = init;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Obj getType() {
    return type;
  }

  public void setType(Obj type) {
    this.type = type;
  }

  public Obj getSupers() {
    return supers;
  }

  public void setSupers(Obj supers) {
    this.supers = supers;
  }

  public Obj getInit() {
    return init;
  }

  public void setInit(Obj init) {
    this.init = init;
  }

  public String toString() {
    return name + ":" + type;
  }

  public Obj of() {
    return Attribute;
  }

  public Obj get(String name) {
    if (name.equals("name")) return new value.Str(getName());
    if (name.equals("type")) return type;
    if (name.equals("supers")) return supers;
    if (name.equals("init")) return init;
    return send("noSlotFound", new value.Str(name));
  }

  public Obj set(String name, Obj value) {
    if (name.equals("name"))
      this.name = value.toString();
    else if (name.equals("type"))
      type = value;
    else if (name.equals("supers"))
      supers = value;
    else if (name.equals("init"))
      init = value;
    else return send("noSlotFound", new value.Str(name));
    return this;
  }

  public value.Obj slots() {
    return list(Slot("name", Str(name)), Slot("type", type), Slot("supers", supers), Slot("init", init));
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    return Classifier_send(Attribute, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }

}
