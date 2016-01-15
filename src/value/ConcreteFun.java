package value;

import static value.Cons.asList;
import static value.Obj.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ConcreteFun extends Atom {

  public interface F extends Serializable {
  }

  public interface F0 extends F {
    Obj apply();
  }

  public interface F1 extends F {
    Obj apply(Obj o);
  }

  public interface F2 extends F {
    Obj apply(Obj o1, Obj o2);
  }

  public interface F3 extends F {
    Obj apply(Obj o1, Obj o2, Obj o3);
  }

  public interface F4 extends F {
    Obj apply(Obj o1, Obj o2, Obj o3, Obj o4);
  }

  public interface F5 extends F {
    Obj apply(Obj o1, Obj o2, Obj o3, Obj o4, Obj o5);
  }

  public interface F6 extends F {
    Obj apply(Obj o1, Obj o2, Obj o3, Obj o4, Obj o5, Obj o6);
  }

  static final long serialVersionUID = 6621869590475077519L;

  String            name             = "anonymous";
  F                 f;
  Obj               type;
  int               arity            = 0;
  Obj               slots            = null;
  String[]          args             = new String[0];
  boolean           isVarArgs        = false;

  public ConcreteFun(F0 f, String... args) {
    super(Function);
    this.f = f;
    this.args = args;
  }

  public ConcreteFun(F1 f, String... args) {
    super(Function);
    this.f = f;
    this.args = args;
    arity = 1;
  }

  public ConcreteFun(F2 f, String... args) {
    super(Function);
    this.f = f;
    this.args = args;
    arity = 2;
  }

  public ConcreteFun(F3 f, String... args) {
    super(Function);
    this.f = f;
    this.args = args;
    arity = 3;
  }

  public ConcreteFun(F4 f, String... args) {
    super(Function);
    this.f = f;
    arity = 4;
    this.args = args;
  }

  public ConcreteFun(F5 f, String... args) {
    super(Function);
    this.f = f;
    this.args = args;
    arity = 5;
  }

  public ConcreteFun(F6 f, String... args) {
    super(Function);
    this.f = f;
    this.args = args;
    arity = 6;
  }

  public ConcreteFun(Obj type, F0 f, String... args) {
    super(Function);
    this.f = f;
    this.type = type;
    this.args = args;
  }

  public ConcreteFun(Obj type, F1 f, String... args) {
    super(Function);
    this.f = f;
    this.type = type;
    this.args = args;
    arity = 1;
  }

  public ConcreteFun(Obj type, F2 f, String... args) {
    super(Function);
    this.f = f;
    this.type = type;
    this.args = args;
    arity = 2;
  }

  public ConcreteFun(Obj type, F3 f, String... args) {
    super(Function);
    this.f = f;
    this.type = type;
    this.args = args;
    arity = 3;
  }

  public ConcreteFun(Obj type, F4 f, String... args) {
    super(Function);
    this.f = f;
    this.type = type;
    arity = 4;
  }

  public ConcreteFun(Obj type, F5 f, String... args) {
    super(Function);
    this.f = f;
    this.type = type;
    this.args = args;
    arity = 5;
  }

  public ConcreteFun(Obj type, F6 f, String... args) {
    super(Function);
    this.f = f;
    this.type = type;
    this.args = args;
    arity = 6;
  }

  public ConcreteFun(String name, F0 f, String... args) {
    super(Function);
    this.name = name;
    this.f = f;
    this.args = args;
  }

  public ConcreteFun(String name, F1 f, String... args) {
    super(Function);
    this.name = name;
    this.f = f;
    this.args = args;
    arity = 1;
  }

  public ConcreteFun(String name, F2 f, String... args) {
    super(Function);
    this.name = name;
    this.f = f;
    this.args = args;
    arity = 2;
  }

  public ConcreteFun(String name, F3 f, String... args) {
    super(Function);
    this.name = name;
    this.f = f;
    this.args = args;
    arity = 3;
  }

  public ConcreteFun(String name, F4 f, String... args) {
    super(Function);
    this.name = name;
    this.f = f;
    this.args = args;
    arity = 4;
  }

  public ConcreteFun(String name, F5 f, String... args) {
    super(Function);
    this.name = name;
    this.f = f;
    this.args = args;
    arity = 5;
  }

  public ConcreteFun(String name, F6 f, String... args) {
    super(Function);
    this.name = name;
    this.f = f;
    this.args = args;
    arity = 6;
  }

  public ConcreteFun(String name, Obj type, F0 f, String... args) {
    super(Function);
    this.name = name;
    this.type = type;
    this.args = args;
    this.f = f;
  }

  public ConcreteFun(String name, Obj type, F1 f, String... args) {
    super(Function);
    this.name = name;
    this.type = type;
    this.f = f;
    this.args = args;
    arity = 1;
  }

  public ConcreteFun(String name, Obj type, F2 f, String... args) {
    super(Function);
    this.name = name;
    this.type = type;
    this.f = f;
    this.args = args;
    arity = 2;
  }

  public ConcreteFun(String name, Obj type, F3 f, String... args) {
    super(Function);
    this.name = name;
    this.type = type;
    this.f = f;
    this.args = args;
    arity = 3;
  }

  public ConcreteFun(String name, Obj type, F4 f, String... args) {
    super(Function);
    this.name = name;
    this.type = type;
    this.f = f;
    this.args = args;
    arity = 4;
  }

  public ConcreteFun(String name, Obj type, F5 f, String... args) {
    super(Function);
    this.name = name;
    this.type = type;
    this.f = f;
    this.args = args;
    arity = 5;
  }

  public ConcreteFun(String name, Obj type, F6 f, String... args) {
    super(Function);
    this.name = name;
    this.type = type;
    this.f = f;
    this.args = args;
    arity = 6;
  }

  private Obj args() {
    if (args.length == 0) {
      Obj l = theObjNil;
      Method m = null;
      for (Method mm : f.getClass().getMethods())
        if (mm.getName().equals("apply")) m = mm;
      if (m == null) throw new Error("cannot find the apply method in " + f);
      for (int i = m.getParameterCount() - 1; i >= 0; i--)
        l = cons(Str(m.getParameters()[i].getName()), l);
      return l;
    } else return convertArgs();
  }

  public String argString() {
    Obj args = args();
    String s = "";
    while (!isNil(args)) {
      s = s + head(args);
      args = tail(args);
      if (!isNil(args)) s = s + ",";
    }
    return s;
  }

  private Obj consDomain() {
    Obj domain = list();
    for (int i = 0; i < arity; i++)
      domain = cons(Obj, domain);
    return domain;
  }

  private Obj convertArgs() {
    Obj l = theObjNil;
    for (int i = args.length - 1; i >= 0; i--)
      l = cons(Str(args[i]), l);
    return l;
  }

  public Obj get(String name) {
    if (name.equals("of")) return of();
    if (name.equals("arity")) return Int(arity);
    if (name.equals("args")) return args();
    if (name.equals("name")) return new value.Str(this.name);
    if (name.equals("type")) if (type != null)
      return type;
    else return FunctionOf(consDomain(), Obj);
    if (name.equals("isVarArgs")) return isVarArgs ? theObjTrue : theObjFalse;
    return super.get(name);
  }

  public Obj invoke(Obj[] args) {
    if (isVarArgs)
      return invokeVarArgs(args);
    else return invokeFixed(args);
  }

  public Obj invokeFixed(Obj[] args) {
    if (arity != args.length)
      throw new Error("arity mismatch for " + name + " expects " + arity + " but was supplied with " + Arrays.toString(args));
    else {
      switch (arity) {
        case 0:
          return ((F0) f).apply();
        case 1:
          return ((F1) f).apply(args[0]);
        case 2:
          return ((F2) f).apply(args[0], args[1]);
        case 3:
          return ((F3) f).apply(args[0], args[1], args[2]);
        case 4:
          return ((F4) f).apply(args[0], args[1], args[2], args[3]);
        case 5:
          return ((F5) f).apply(args[0], args[1], args[2], args[3], args[4]);
        case 6:
          return ((F6) f).apply(args[0], args[1], args[2], args[3], args[4], args[5]);
        default:
          throw new Error("operation arity " + arity + " not supported");
      }
    }
  }

  public Obj invokeMethod(Obj target, Obj superObj, Obj[] args) {
    if (isVarArgs)
      return invokeMethodVarArgs(target, superObj, args);
    else return invokeMethodFixed(target, superObj, args);
  }

  public Obj invokeMethodFixed(Obj target, Obj superObj, Obj[] args) {
    if (args.length < arity - 3) throw new Error("arity mismatch for " + name + " expects " + arity + " but was supplied with " + Arrays.toString(args));
    switch (arity) {
      case 2:
        return ((F2) f).apply(target, superObj);
      case 3:
        return ((F3) f).apply(target, superObj, args[0]);
      case 4:
        return ((F4) f).apply(target, superObj, args[0], args[1]);
      case 5:
        return ((F5) f).apply(target, superObj, args[0], args[1], args[2]);
      case 6:
        return ((F6) f).apply(target, superObj, args[0], args[1], args[2], args[3]);
      default:
        throw new Error("operation arity " + arity + " not supported");
    }
  }

  public Obj invokeMethodVarArgs(Obj target, Obj superObj, Obj[] args) {
    if (args.length < arity - 3)
      throw new Error("varArgs mismatch for " + name + " expects " + (arity - 2) + "+ but was supplied with " + Arrays.toString(args));
    else {
      switch (arity) {
        case 2:
          throw new Error("cannot have a 0-arity method with varArgs");
        case 3:
          return ((F3) f).apply(target, superObj, Cons.asList(0, args));
        case 4:
          return ((F4) f).apply(target, superObj, args[0], Cons.asList(1, args));
        case 5:
          return ((F5) f).apply(target, superObj, args[0], args[1], Cons.asList(2, args));
        case 6:
          return ((F6) f).apply(target, superObj, args[0], args[1], args[2], Cons.asList(3, args));
        default:
          throw new Error("operation arity " + arity + " not supported");
      }
    }
  }

  public Obj invokeVarArgs(Obj[] args) {
    if (args.length < (arity - 1))
      throw new Error("varArgs mismatch for " + name + " expects at least " + (arity - 1) + " args but was supplied with " + Arrays.toString(args));
    else {
      switch (arity) {
        case 0:
          throw new Error("cannot have a 0-arity function with varArgs");
        case 1:
          return ((F1) f).apply(Cons.asList(0, args));
        case 2:
          return ((F2) f).apply(args[0], Cons.asList(1, args));
        case 3:
          return ((F3) f).apply(args[0], args[1], Cons.asList(2, args));
        case 4:
          return ((F4) f).apply(args[0], args[1], args[2], Cons.asList(3, args));
        case 5:
          return ((F5) f).apply(args[0], args[1], args[2], args[3], Cons.asList(4, args));
        case 6:
          return ((F6) f).apply(args[0], args[1], args[2], args[3], args[4], Cons.asList(5, args));
        default:
          throw new Error("operation arity " + arity + " not supported");
      }
    }
  }

  public Obj of() {
    return ConcreteFun;
  }

  public value.Obj send(String name, value.Obj... args) {
    // This can be done much more efficiently so that no cons-ing takes place...
    if (name.equals("invoke"))
      return invoke(args);
    else return Classifier_send(of(), ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
  }

  public Obj set(String name, Obj value) {
    switch (name) {
      case "type":
        type = value;
        Classifier_throwFailures(ConcreteFun, this);
        return this;
      case "isVarArgs":
        isVarArgs = isTrue(value);
        Classifier_throwFailures(ConcreteFun, this);
        return this;
      default:
        return super.set(name, value);
    }
  }

  public void setVarArgs(boolean isVarArgs) {
    this.isVarArgs = isVarArgs;
  }

  public value.Obj slots() {
    if (slots == null) slots = list(Slot("name", Str(name)), Slot("args", args()));
    return slots;
  }

  public String toString() {
    return name + "(" + argString() + "):" + type;
  }

}
