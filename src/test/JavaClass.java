package test;

import value.Obj;
import static value.Obj.*;

public class JavaClass implements Obj {

  java.lang.Class<?> _class;
  Obj                of;

  public JavaClass(java.lang.Class<?> _class, value.Obj of) {
    super();
    this._class = _class;
    this.of = of;
  }

  public JavaClass(Obj args) {
    String name = head(args).toString();
    of = head(tail(args));
    try {
      _class = java.lang.Class.forName(name);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public value.Obj get(String name) {
    if (name.equals("name"))
      return Str(_class.getName());
    else if (name.equals("supers")) {
      if (_class.getSuperclass() == Object.class)
        return list(Obj);
      else return list(new JavaClass(_class.getSuperclass(), of));
    } else throw new Error("no slot named " + name);
  }

  public value.Obj of() {
    return of;
  }

  public value.Obj send(String name, value.Obj... args) {
    if (name.equals("of"))
      return of;
    else if (name.equals("new"))
      return newInstance();
    else throw new Error("no operation named " + name);
  }

  private value.Obj newInstance() {
    return new Obj() {

      public value.Obj get(String name) {
        throw new Error("no slot named " + name);
      }

      public value.Obj of() {
        return of;
      }

      public value.Obj send(String name, value.Obj... args) {
        if (name.equals("of"))
          return of();
        else throw new Error("no operation named " + name);
      }

      public value.Obj set(String name, value.Obj value) {
        throw new Error("no slot named " + name);
      }

      public value.Obj slots() {
        return theObjNil;
      }

    };
  }

  public value.Obj set(String name, value.Obj value) {
    throw new Error("set not implemented: " + name);
  }

  public value.Obj slots() {
    throw new Error("slots not implemented.");
  }

}
