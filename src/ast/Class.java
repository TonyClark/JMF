package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;

import static value.Obj.*;

import java.util.Arrays;

@BoaConstructor(fields = { "name", "metaclass", "supers", "attributes", "slots", "constructors", "methods" })
public class Class extends AST {

  public String      name;
  public AST         metaclass;
  public AST[]       supers;
  public Attribute[] attributes;
  public Slot[]      slots;
  public Cnstr[]     constructors;
  public Method[]    methods;

  public Class() {
  }

  public Class(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String toString() {
    return "Class(" + name + "," + Arrays.toString(attributes) + "," + Arrays.toString(methods) + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj mc = metaclass.eval(self, env);
    Obj cs = list();
    for (AST e : supers)
      cs = cs.send("append", list(e.eval(self, env)));
    Obj as = list();
    for (Attribute a : attributes) {
      as = cons(a.eval(env), as);
    }
    Obj c = send(mc, "new", Str(name), as, cs);
    for (Slot s : slots)
      c.set(s.name, s.value.eval(c, env));
    for (Cnstr cnstr : constructors)
      send(c, "addConstructor", cnstr.eval(self, env));
    for (Method method : methods)
      send(c, "addOperation", method.eval(self, env));
    return c;
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    System.out.println("ignoring type check class definition");
    // The body of a class can refer to itself. Create a dummy class..
    // Obj dummyClass = send(Class, "new", Str(name), list(), list(Obj));
    // env = env.bind(name, dummyClass);
    // Populate the dummy class before checking the body of the class...
    // for (Method method : methods) {
    // method.typeCheck(env.bind("self",dummyClass));
    // }
    // Needs to take account of its meta-class...
    return Class;
  }

}
