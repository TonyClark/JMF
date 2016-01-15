package value;

import static value.Cons.asList;
import static value.Cons.isProperList;
import static value.Cons.length;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import cache.MessageCache;
import cache.OpAndSuperObj;
import cache.OperationCache;
import serialize.Inflater;
import serialize.Serializer;
import test.Diagrams;
import value.ConcreteFun.F1;
import value.ConcreteFun.F2;
import value.ConcreteFun.F3;
import value.ConcreteFun.F4;
import value.ConcreteFun.F5;

public interface Obj extends Serializable {

  // The registers are integer flags that are used to control the system...
  public static int[]                registers           = new int[] { 0, 0 };

  // The value registers[CONSTRAINTS] must be 0 in order to apply constraints. Increment and then
  // decrement the value to temporarily suspend constraint checking...

  public static int                  CONSTRAINTS         = 0;

  // Control whether daemons are active or not. Increment for the scope of inactivity...

  public static int                  DAEMONS             = 0;

  public static Debug                debug               = new Debug();

  // The kernel classes that are required in the bootstrap. Note that these must be known by the
  // Serializer and Inflater in order that they are saved and restored properly...

  public static ConcreteObj          Atom                = new ConcreteObj(null);
  public static ConcreteObj          Atomic              = new ConcreteObj(null);
  public static ConcreteObj          Attribute           = new ConcreteObj(null);
  public static ConcreteObj          Bool                = new ConcreteObj(null);
  public static ConcreteObj          Class               = new ConcreteObj(null);
  public static ConcreteObj          Classifier          = new ConcreteObj(null);
  public static ConcreteObj          ConcreteFun         = new ConcreteObj(null);
  public static ConcreteObj          ConsOf              = new ConcreteObj(null);
  public static ConcreteObj          Container           = new ConcreteObj(null);
  public static ConcreteObj          ContainerOf         = new ConcreteObj(null);
  public static ConcreteObj          DocumentedElement   = new ConcreteObj(null);
  public static ConcreteObj          Float               = new ConcreteObj(null);
  public static ConcreteObj          Function            = new ConcreteObj(null);
  public static ConcreteObj          FunctionOf          = new ConcreteObj(null);
  public static ConcreteObj          Inherits            = new ConcreteObj(null);
  public static ConcreteObj          Int                 = new ConcreteObj(null);
  public static ConcreteObj          JavaClass           = new ConcreteObj(null);
  public static ConcreteObj          ListOf              = new ConcreteObj(null);
  public static ConcreteObj          NameSpaceOf         = new ConcreteObj(null);
  public static ConcreteObj          NamedElement        = new ConcreteObj(null);
  public static ConcreteObj          NilOf               = new ConcreteObj(null);
  public static ConcreteObj          Null                = new ConcreteObj(null);
  public static ConcreteObj          Number              = new ConcreteObj(null);
  public static ConcreteObj          Obj                 = new ConcreteObj(null);
  public static ConcreteObj          Package             = new ConcreteObj(null);
  public static ConcreteObj          PackageController   = new ConcreteObj(null);
  public static ConcreteObj          Snapshot            = new ConcreteObj(null);
  public static ConcreteObj          TypedElement        = new ConcreteObj(null);
  public static ConcreteObj          Self                = new ConcreteObj(null);
  public static ConcreteObj          StaticSelf          = new ConcreteObj(null);
  public static ConcreteObj          Str                 = new ConcreteObj(null);
  public static ConcreteObj          Slot                = new ConcreteObj(null);
  public static ConcreteObj          Symbol              = new ConcreteObj(null);
  public static ConcreteObj          Table               = new ConcreteObj(null);
  public static ConcreteObj          TableOf             = new ConcreteObj(null);
  public static ConcreteObj          Walker              = new ConcreteObj(null);

  public static Obj                  theObjFalse         = value.Bool.theObjFalse;
  public static Obj                  theObjNil           = new Nil(Obj);
  public static Obj                  theObjNull          = value.Null.theObjNull;
  public static Obj                  theObjTrue          = value.Bool.theObjTrue;
  public static Obj                  Kernel              = new ConcreteObj(null);

  static Hashtable<Obj, ConcreteObj> listOfCache         = new Hashtable<Obj, ConcreteObj>();
  static Hashtable<Obj, ConcreteObj> consOfCache         = new Hashtable<Obj, ConcreteObj>();
  static Hashtable<Obj, ConcreteObj> nilOfCache          = new Hashtable<Obj, ConcreteObj>();
  static HashSet<Obj>                getCache            = new HashSet<Obj>();
  static MessageCache                sendCache           = new MessageCache();

  public static F3                   Table_typeCheckGet  = (tableType, so, keyType) -> isTrue(send(keyType, "inherits", get(tableType, "keyType"))) ? get(tableType, "valueType") : theObjNull;

  public static F4                   Table_typeCheckSet  = (tableType, so, keyType, valueType) ->
                                                         {
                                                           return isTrue(send(keyType, "inherits", get(tableType, "keyType"))) && isTrue(send(valueType, "inherits", get(tableType, "valueType"))) ? tableType : theObjNull;
                                                         };

  public static F4                   Table_typeCheckSend = (tableType, so, name, argTypes) -> name.toString().equals("get") ? Table_typeCheckGet.apply(tableType, so, head(argTypes)) : name.toString().equals("set") ? Table_typeCheckSet.apply(tableType, so, head(argTypes), head(tail(argTypes))) : theObjNull;

  public static ConcreteFun.F3       Table_new           = (t, so, args) ->
                                                         {
                                                           Hashtable<Obj, Obj> table = new Hashtable<Obj, Obj>();
                                                           return new Obj() {

                                                             static final long serialVersionUID = -3496488845135251068L;

                                                             public Obj get(Obj key) {
                                                               return table.get(key);
                                                             }

                                                             public value.Obj get(String name) {
                                                               return get(Str(name));
                                                             }

                                                             public String toString() {
                                                               return table.toString();
                                                             }

                                                             public Obj of() {
                                                               return t;
                                                             }

                                                             public value.Obj send(String name, value.Obj... args) {
                                                               if (name.equals("hasKey") && args.length == 1)
                                                                 return table.containsKey(args[0]) ? theObjTrue : theObjFalse;
                                                               else if (name.equals("asString"))
                                                                 return Str(table.toString());
                                                               else if (name.equals("clear")) {
                                                                 table.clear();
                                                                 return this;
                                                               } else if (name.equals("set") && args.length == 2)
                                                                 return set(args[0], args[1]);
                                                               else if (name.equals("get") && args.length == 1)
                                                                 return get(args[0]);
                                                               else return Classifier_send(of(), ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
                                                             }

                                                             public value.Obj set(Obj key, Obj value) {
                                                               table.put(key, value);
                                                               return this;
                                                             }

                                                             public value.Obj set(String name, value.Obj value) {
                                                               return set(Str(name), value);
                                                             }

                                                             public value.Obj slots() {
                                                               Obj slots = theObjNil;
                                                               for (Obj name : table.keySet())
                                                                 slots = cons(Slot(name.toString(), table.get(name)), slots);
                                                               return slots;
                                                             }
                                                           };
                                                         };

  public static Obj And(Obj o1, Obj o2) {
    if (isTrue(o1) && isTrue(o2))
      return theObjTrue;
    else return theObjFalse;
  }

  public static Obj append(Obj o, String name, Obj v) {
    return set(o, name, send(v, "append", get(o, name)));
  }

  public static boolean asBool(Obj obj) {
    if (isBool(obj)) {
      Bool b = (Bool) obj;
      return b.getValue();
    } else throw new Error("cannot cast " + obj + " to bool");
  }

  public static int asInt(Obj obj) {
    if (isInt(obj)) {
      Int i = (Int) obj;
      return i.getValue();
    } else throw new Error("cannot cast " + obj + " to int");
  }

  public static Obj Atomic_get(Obj atomicType, Obj superObj, Obj atom, Obj name) {
    if (atom instanceof Int) {
      Int i = (Int) atom;
      return i.get(name.toString());
    } else throw new Error("cannot get the slot of an unknown type " + atom);
  }

  public static Obj Atomic_new(Obj atomicType, Obj superObj, Obj values) {

    // We are creating an instance of a type that has been designated 'atomic'.
    // This means that the objects will be represented as a Java object that
    // is a sub-class of Atomic. These manage their own storage and allows
    // otherwise 'final' classes to be extensible...

    if (atomicType == Int || isTrue(send(send(atomicType, "allSupers"), "contains", Int))) {
      Obj o = Int(atomicType, 0);
      registers[CONSTRAINTS]++;
      o = send(atomicType, "initObj", o, values);
      registers[CONSTRAINTS]--;
      Classifier_throwFailures(atomicType, o);
      return o;
    } else if (atomicType == Str || isTrue(send(send(atomicType, "allSupers"), "contains", Str))) {
      Obj o = Str(atomicType, "");
      registers[CONSTRAINTS]++;
      o = send(atomicType, "initObj", o, values);
      registers[CONSTRAINTS]--;
      Classifier_throwFailures(atomicType, o);
      return o;
    } else throw new Error("cannot find the atomic type " + atomicType);
  }

  public static Obj Atomic_set(Obj atomicType, Obj superObj, Obj atom, Obj slot, Obj value) {
    if (atom instanceof Int) {
      Int i = (Int) atom;
      i.set(slot.toString(), value);
      return atom;
    } else if (atom instanceof Str) {
      Str i = (Str) atom;
      i.set(slot.toString(), value);
      return atom;
    } else throw new Error("cannot set atom of unknown type " + atom);
  }

  public static Attribute Attribute(String name, Obj type) {
    return new Attribute(name, type);
  }

  public static Attribute Attribute(String name, Obj type, Obj init) {
    return new Attribute(name, type, init);
  }

  public static Attribute Attribute(String name, Obj type, Obj supers, Obj init) {
    return new Attribute(name, type, supers, init);
  }

  public static Obj Attribute_asString(Obj a, Obj so) {
    return Str(get(a, "name") + ":" + get(a, "type"));
  }

  public static Obj Attribute_inherits(Obj self, Obj superObj, Obj a) {
    for (Obj parent : iterate(get(self, "supers"))) {
      if (parent == a)
        return theObjTrue;
      else if (isTrue(send(parent, "inherits", a))) return theObjTrue;
    }
    return theObjFalse;
  }

  public static Obj Attribute_initInit(Obj att, Obj type) {
    return set(att, "init", Op((o, a) -> set(o, get(a, "name").toString(), get(get(a, "type"), "initial"))));
  }

  public static Obj Attribute_limitHandler(Obj o, Obj a) {
    return set(o, "limitHandler", Op((x) -> x));
  }

  public static Obj Bool(boolean b) {
    return b ? theObjTrue : theObjFalse;
  }

  public static Obj Bool_and(Obj self, Obj superObj, Obj other) {
    return (self == theObjTrue) ? other : theObjFalse;
  }

  public static Obj Bool_asString(Obj o, Obj superObj) {
    return Str(o.toString());
  }

  public static Obj Bool_implies(Obj self, Obj superObj, Obj other) {
    return (self == theObjTrue) ? other : theObjTrue;
  }

  public static Obj Bool_not(Obj o, Obj superObj) {
    return isTrue(o) ? theObjFalse : theObjTrue;
  }

  public static Obj Bool_or(Obj self, Obj superObj, Obj other) {
    return (self == theObjFalse) ? other : theObjTrue;
  }

  public static void bootstrap() {

    // Create the cyclic structure that forms the core of the language...

    registers[DAEMONS]++;

    initClass(Atom, "Atom", list(Obj));
    Atom.extendSlot("attributes", Attribute("value", Obj));
    Atom.setSlotValue("isAbstract", theObjTrue);

    initClass(Atomic, "Atomic", list(Class));
    Atomic.extendSlot("operations", Op("new", (F3) value.Obj::Atomic_new));
    Atomic.extendSlot("operations", Op("get", (F4) value.Obj::Atomic_get));
    Atomic.extendSlot("operations", Op("set", (F5) value.Obj::Atomic_set));

    initClass(Obj, "Obj", theObjNil);
    Obj.extendSlot("constraints", Op("allSlotNamesDifferent", (F1) value.Obj::Obj_allSlotNamesDifferent));
    Obj.extendSlot("operations", Op("asString", (F2) value.Obj::Obj_asString));
    Obj.extendSlot("operations", Op("println", (F2) value.Obj::Obj_println));
    Obj.extendSlot("operations", Op("getSlots", (F2) value.Obj::Obj_getSlots));
    Obj.extendSlot("operations", Op("get", (F3) value.Obj::Obj_get));
    Obj.extendSlot("operations", Op("set", (F4) value.Obj::Obj_set));
    Obj.extendSlot("operations", Op("=", (F3) value.Obj::Obj_equals));
    Obj.extendSlot("operations", Op("==", (F3) value.Obj::Obj_theSame));
    Obj.extendSlot("operations", Op("<>", (F3) value.Obj::Obj_notEquals));
    Obj.extendSlot("operations", Op("noOperationFound", (F4) value.Obj::Obj_noOperationFound));
    Obj.extendSlot("operations", Op("noSlotFound", (F3) value.Obj::Obj_noSlotFound));
    Obj.extendSlot("operations", Op("of", (F2) (o, s) -> o.of()));
    Obj.extendSlot("operations", Op("save", (F3) value.Obj::Obj_save));
    Obj.extendSlot("operations", Op("startDaemons", (F2) value.Obj::Obj_startDaemons));
    Obj.extendSlot("operations", Op("stopDaemons", (F2) value.Obj::Obj_stopDaemons));

    initClass(Attribute, "Attribute", list(NamedElement, TypedElement, DocumentedElement));
    Attribute.extendSlot("attributes", Attribute("init", Function, theObjNil, Op((F2) value.Obj::Attribute_initInit)));
    Attribute.extendSlot("operations", Op("asString", (F2) value.Obj::Attribute_asString));
    Attribute.extendSlot("operations", Op("inherits", (F3) value.Obj::Attribute_inherits));
    Attribute.extendSlot("constructors", Op((self, superObj, name, type) -> set(set(self, "name", name), "type", type), "name", "type"));

    initClass(Class, "Class", list(Classifier));
    Class.extendSlot("constraints", Op("allAttNamesDifferent", (F1) value.Obj::Class_allAttNamesDifferent));
    Class.extendSlot("constraints", Op("allCnstrsNameAtts", (F1) value.Obj::Class_allCnstrsNameAtts));
    Class.extendSlot("operations", Op("new", (F3) value.Obj::Class_new));
    Class.extendSlot("operations", Op("addConstructor", (F3) value.Obj::Class_addConstructor));
    Class.extendSlot("operations", Op("allAttributes", (F2) value.Obj::Class_allAttributes));
    Class.extendSlot("operations", Op("initObj", (F4) value.Obj::Class_initObj));
    Class.extendSlot("operations", Op("invoke", (F3) value.Obj::Class_invoke));
    Class.extendSlot("operations", Op("typeCheckGet", (F3) value.Obj::Class_typeCheckGet));
    Class.extendSlot("operations", Op("typeCheckSet", (F4) value.Obj::Class_typeCheckSet));
    Class.extendSlot("operations", Op("typeCheckSend", (F4) value.Obj::Class_typeCheckSend));
    Class.extendSlot("operations", Op("get", (F4) value.Obj::Class_get));
    Class.extendSlot("operations", Op("set", (F5) value.Obj::Class_set));
    Class.extendSlot("daemons", Op("NoAttributeChanges", (F4) value.Obj::Class_noAttributeChanges));
    Class.extendSlot("constructors", Op((self, so, name, atts, supers) ->
    {
      registers[DAEMONS]++;
      set(self, "name", name);
      set(self, "attributes", atts);
      set(self, "supers", supers);
      registers[DAEMONS]--;
      return self;
    } , "name", "attributes", "supers"));

    initClass(Atomic, Bool, "Bool", list(Atom));
    Bool.setSlotValue("initial", theObjFalse);
    Bool.extendSlot("operations", Op("and", (F3) value.Obj::Bool_and));
    Bool.extendSlot("operations", Op("implies", (F3) value.Obj::Bool_implies));
    Bool.extendSlot("operations", Op("or", (F3) value.Obj::Bool_or));
    Bool.extendSlot("operations", Op("asString", (F2) value.Obj::Bool_asString));
    Bool.extendSlot("operations", Op("not", (F2) value.Obj::Bool_not));

    initClass(Classifier, "Classifier", list(NamedElement, DocumentedElement));
    Classifier.extendSlot("operations", Op("getOp", (F3) value.Obj::Classifier_getOp));
    Classifier.extendSlot("operations", Op("inherits", (F3) value.Obj::Classifier_inherits));
    Classifier.extendSlot("operations", Op("asString", (F2) value.Obj::Classifier_asString));
    Classifier.extendSlot("operations", Op("initial", (F2) value.Obj::Classifier_initial));
    Classifier.extendSlot("operations", Op("new", (F3) value.Obj::Classifier_new));
    Classifier.extendSlot("operations", Op("send", (c, so, t, m, vs) -> t.send(m.toString(), Cons.asArray(vs))));
    Classifier.extendSlot("operations", Op("allSupers", (F2) value.Obj::Classifier_allSupers));
    Classifier.extendSlot("operations", Op("flatten", (F2) value.Obj::Classifier_flatten));
    Classifier.extendSlot("operations", Op("isInstance", (F3) value.Obj::Classifier_isInstance));
    Classifier.extendSlot("operations", Op("subst", (F4) value.Obj::Classifier_subst));
    Classifier.setSlotValue("isAbstract", theObjTrue);
    Classifier.extendSlot("constraints", Op("allOpNamesDifferent", (F1) value.Obj::Classifier_allOpNamesDifferent));
    Classifier.extendSlot("constraints", Op("allCheckNamesDifferent", (F1) value.Obj::Classifier_allCheckNamesDifferent));

    initClass(ConcreteFun, "ConcreteFun", list(Function));

    initClass(ConsOf, "ConsOf", list(ListOf));
    ConsOf.extendSlot("operations", Op("isInstance", (F3) value.Obj::ConsOf_isInstance));
    ConsOf.extendSlot("constructors", Op((self, so, type) -> ConsOf(type), "elementType"));

    initClass(Container, "Container", list(Obj));
    Container.setSlotValue("isAbstract", theObjTrue);

    initClass(ContainerOf, "ContainerOf", list(Class));
    ContainerOf.extendSlot("attributes", Attribute("contentName", Str));
    ContainerOf.extendSlot("attributes", Attribute("contentType", Classifier));
    ContainerOf.extendSlot("constructors", Op((self, so, name, type) -> ContainerOf(name.toString(), type), "name", "type"));

    initClass(DocumentedElement, "DocumentedElement", list(Obj));
    DocumentedElement.extendSlot("attributes", Attribute("doc", Str));

    initClass(Atomic, Float, "Float", list(Number));
    Float.setSlotValue("initial", Int(0));
    Float.extendSlot("operations", Op("asString", (o, so) -> Str(Double.toString(((Float) o).getValue()))));
    Float.extendSlot("operations", Op("+", (F3) value.Obj::Int_add));
    Float.extendSlot("operations", Op(">", (F3) value.Obj::Int_gre));
    Float.extendSlot("operations", Op("<", (F3) value.Obj::Int_less));
    Float.extendSlot("operations", Op("sqrt", (F2) value.Obj::Int_sqrt));

    initClass(Function, "Function", list(NamedElement, DocumentedElement, TypedElement, Atom));
    Function.extendSlot("attributes", Attribute("arity", Int));
    Function.extendSlot("attributes", Attribute("isVarArgs", Bool));
    Function.setSlotValue("isAbstract", theObjTrue);
    Function.extendSlot("operations", Op("invoke", (F3) value.Obj::Function_invoke));
    Function.extendSlot("constraints", Op("typeIsFunctionType", (F1) value.Obj::Function_typeIsFunctionType));
    Function.extendSlot("constraints", Op("allArgNamesDifferent", (F1) value.Obj::Function_allArgNamesDifferent));

    initClass(FunctionOf, "FunctionOf", list(TypedElement, Class));
    FunctionOf.extendSlot("attributes", Attribute("isVarArgs", Bool));
    FunctionOf.extendSlot("operations", Op("subst", (F4) value.Obj::FunctionOf_subst));
    FunctionOf.extendSlot("constructors", Op((self, so, domain, range) -> FunctionOf(domain, range), "domain", "type"));
    FunctionOf.extendSlot("constructors", Op((self, so, domain, isVarArgs, range) -> FunctionOf(domain, isVarArgs, range), "domain", "isVarArgs", "type"));

    initClass(Inherits, "InheritsOf", list(Class));
    Inherits.extendSlot("attributes", Attribute("inheritedName", Str));
    Inherits.extendSlot("attributes", Attribute("verticalCompose", Function));
    Inherits.extendSlot("attributes", Attribute("horizonalCompose", Function));
    Inherits.extendSlot("constructors", Op("init", (F5) value.Obj::Inherits_init, "inheritedName", "verticalCompose", "horizontalCompose"));

    initClass(Atomic, Int, "Int", list(Number));
    Int.setSlotValue("initial", Int(0));
    Int.extendSlot("operations", Op("asString", (o, so) -> Str(Integer.toString(((Int) o).getValue()))));
    Int.extendSlot("operations", Op("+", (F3) value.Obj::Int_add));
    Int.extendSlot("operations", Op("-", (F3) value.Obj::Int_sub));
    Int.extendSlot("operations", Op("*", (F3) value.Obj::Int_mul));
    Int.extendSlot("operations", Op("/", (F3) value.Obj::Int_div));
    Int.extendSlot("operations", Op(">", (F3) value.Obj::Int_gre));
    Int.extendSlot("operations", Op("<", (F3) value.Obj::Int_less));
    Int.extendSlot("operations", Op("isCapital", (F2) value.Obj::Int_isCapital));
    Int.extendSlot("operations", Op("sqrt", (F2) value.Obj::Int_sqrt));
    Int.extendSlot("constructors", Op((o1, so, o2) ->
    {
      Int i1 = (Int) o1;
      Int i2 = (Int) o2;
      i1.setValue(i2.getValue());
      return i1;
    } , "value"));

    initClass(JavaClass, "JavaClass", list(Class));
    JavaClass.extendSlot("operations", Op("new", (F3) value.Obj::JavaClass_new));
    JavaClass.extendSlot("operations", Op("get", (F4) value.Obj::JavaClass_get));
    JavaClass.extendSlot("operations", Op("send", (F5) value.Obj::JavaClass_send));
    JavaClass.extendSlot("operations", Op("set", (F5) value.Obj::JavaClass_set));
    JavaClass.extendSlot("constructors", Op((jc, so, name) ->
    {
      try {
        String s = name.toString();
        Class<?> c;
        c = java.lang.Class.forName(s);
        Obj supers = c == Object.class ? list(Obj) : list(send(JavaClass, "new", Str(c.getSuperclass().getName())));
        set(jc, "supers", supers);
        set(jc, "name", name);
        return jc;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    } , "name"));

    initClass(ListOf, "ListOf", list(Class));
    ListOf.extendSlot("attributes", Attribute("elementType", Classifier));
    ListOf.setSlotValue("isAbstract", theObjTrue);
    ListOf.extendSlot("operations", Op("isInstance", (self, so, l) -> (l instanceof Cons || l instanceof Nil) ? theObjTrue : theObjFalse));
    ListOf.extendSlot("operations", Op("inherits", (F3) value.Obj::ListOf_inherits));
    ListOf.extendSlot("constructors", Op((self, so, type) -> ListOf(type), "elementType"));

    initClass(NamedElement, "NamedElement", list(Obj));
    NamedElement.extendSlot("attributes", Attribute("name", Str));

    initClass(NameSpaceOf, "NameSpaceOf", list(ContainerOf));
    NameSpaceOf.extendSlot("constructors", Op((self, so, name, type) -> NameSpaceOf(name.toString(), type), "name", "type"));

    initClass(NilOf, "NilOf", list(ListOf));
    NilOf.extendSlot("operations", Op("isInstance", (F3) value.Obj::NilOf_isInstance));
    NilOf.extendSlot("constructors", Op((self, so, type) -> ListOf(type), "elementType"));

    initClass(Null, "Null", list(Obj));
    Null.extendSlot("operations", Op("asString", (F2) value.Obj::Null_asString));

    initClass(Number, "Number", list(Atom));
    Number.setSlotValue("isAbstract", theObjTrue);

    initClass(Self, "Self", list(Class));

    initClass(StaticSelf, "StaticSelf", list(Class));

    initClass(Slot, "Slot", list(NamedElement));
    Slot.extendSlot("attributes", Attribute("value", Obj));
    Slot.extendSlot("constructors", Op((self, so, name, value) -> Slot(name.toString(), value), "name", "value"));

    initClass(Atomic, Str, "Str", list(Atom));
    Str.setSlotValue("initial", Str(""));
    Str.extendSlot("operations", Op("asString", (F2) value.Obj::Str_asString));
    Str.extendSlot("operations", Op("asSeq", (F2) value.Obj::Str_asSeq));
    Str.extendSlot("operations", Op("javaObj", (F3) value.Obj::Str_javaObj));
    Str.extendSlot("operations", Op("+", (F3) value.Obj::Str_add));
    Str.extendSlot("constructors", Op((o1, so, o2) ->
    {
      Str i1 = (Str) o1;
      Str i2 = (Str) o2;
      i1.setValue(i2.getValue());
      return i1;
    } , "value"));

    initClass(Atomic, Symbol, "Symbol", list(Str));

    initClass(TypedElement, "TypedElement", list(Obj));
    TypedElement.extendSlot("attributes", Attribute("type", Classifier));

    // Parametric types cause problems because they send messages to
    // calculate names. Add them at the end.

    Attribute.extendSlot("supers", ContainerOf("super", Classifier));

    Classifier.extendSlot("supers", NameSpaceOf("super", Classifier));
    Classifier.extendSlot("supers", NameSpaceOf("operation", Function));
    Classifier.extendSlot("supers", NameSpaceOf("constraint", Function));
    Obj append = Op((l1, l2) -> send(send(l1, "append", l2), "removeDuplicates"));
    Classifier.extendSlot("supers", Inherits("operations", append, append));
    Classifier.extendSlot("supers", Inherits("constraints", append, append));
    Classifier.extendSlot("attributes", Attribute("initial", Obj));
    Classifier.extendSlot("constraints", Op("atLeastOneParent", (F1) value.Obj::Classifier_atLeastOneParent));
    set(send(Classifier, "getOp", Str("new")), "isVarArgs", theObjTrue);

    Class.extendSlot("supers", NameSpaceOf("attribute", Attribute));
    set(send(send(Class, "allAttributes"), "selectOne", Op((a) -> send(get(a, "name"), "=", Str("attributes")))), "init", Op((o, a) -> set(o, get(a, "name").toString(), get(get(a, "type"), "initial"))));
    Class.extendSlot("supers", ContainerOf("constructor", Function));
    Class.extendSlot("supers", Inherits("constructors", append, append));
    Class.extendSlot("supers", Inherits("attributes", append, append));
    Class.extendSlot("attributes", Attribute("isAbstract", Bool));
    System.out.println("HERE1");
    Class.extendSlot("attributes", Attribute("daemons", ListOf(Function)));
    System.out.println("HERE2 " + Class.send("allAttributes"));
    set(send(Class, "getOp", Str("new")), "isVarArgs", theObjTrue);
    set(send(Class, "getOp", Str("invoke")), "isVarArgs", theObjTrue);

    ConsOf(Obj).extendSlot("constructors", Op((self, so, head, tail) -> cons(head, tail), "head", "tail"));
    set(send(JavaClass, "getOp", Str("new")), "isVarArgs", theObjTrue);

    FunctionOf.extendSlot("attributes", Attribute("domain", ListOf(Classifier)));
    Function.extendSlot("attributes", Attribute("args", ListOf(Str)));
    set(send(Function, "getOp", Str("invoke")), "isVarArgs", theObjTrue);

    set(send(Atomic, "getOp", Str("new")), "isVarArgs", theObjTrue);

    registers[DAEMONS]--;
  }

  public static Obj Class_addConstructor(Obj c, Obj superObj, Obj cnstr) {
    return set(c, "constructors", cons(cnstr, get(c, "constructors")));
  }

  public static Obj Class_allAttNamesDifferent(Obj c) {
    return send(send(c, "allAttributes"), "forall", Op((a1) -> send(send(c, "allAttributes"), "forall", Op((a2) -> a1 != a2 ? (isTrue(send(get(a1, "name"), "=", get(a2, "name"))) ? Or(send(a1, "inherits", a2), send(a2, "inherits", a1)) : theObjTrue) : theObjTrue))));
  }

  public static Obj Class_allAttributes(Obj type, Obj superObj) {
    Obj supers = send(type, "allSupers");
    Obj allAtts = get(type, "attributes");
    for (Obj s : iterate(supers))
      allAtts = send(allAtts, "append", get(s, "attributes"));
    // Attributes shadow their parents...
    return removeShadowedAttributes(allAtts);
  }

  public static Obj Class_allCnstrsNameAtts(Obj c) {
    return send(get(c, "constructors"), "forall", Op((cnstr) -> send(tail(tail(get(cnstr, "args"))), "forall", Op((n) -> send(send(c, "allAttributes"), "exists", Op((a) -> send(get(a, "name"), "=", n)))))));
  }

  public static Obj Class_get(Obj self, Obj so, Obj target, Obj slot) {

    // Default slot access protocol...

    return target.get(slot.toString());
  }

  public static Obj Class_initObj(Obj type, Obj so, Obj obj, Obj args) {

    // This is called after a class performs 'new' on the freshly created object.
    // The args can then be unpacked and used to initialize the object. Constructors
    // are defined by all classes. Find the constructor with the matching arity
    // and call it...

    return Class_initObj(Classifier_traverseFrom(type), 0, obj, args);
  }

  public static Obj Class_initObj(Vector<Obj> types, int i, Obj obj, Obj args) {
    if (i == types.size())
      return obj;
    else {
      Obj type = types.elementAt(i);
      Obj cnstrs = get(type, "constructors");
      int argLength = length(args);
      for (Obj cnstr : iterate(cnstrs)) {
        int arity = asInt(get(cnstr, "arity")) - 2;
        boolean isVarArgs = asBool(get(cnstr, "isVarArgs"));
        Obj so = Op((self, _so, newArgs) -> Class_initObj(types, i + 1, self, newArgs));
        if (isVarArgs) {
          if (argLength >= arity - 1) { return send$(cnstr, "invoke", cons(obj, cons(so, args))); }
        } else if (arity == argLength) return send$(cnstr, "invoke", cons(obj, cons(so, args)));
      }
      return Class_initObj(types, i + 1, obj, args);
    }
  }

  public static Obj Class_initObj_original(Obj _class, Obj so, Obj obj, Obj args) {

    // This is called after a class performs 'new' on the freshly created object.
    // The args can then be unpacked and used to initialize the object. Constructors
    // are defined by all classes. Find the constructor with the matching arity
    // and call it...

    Vector<Obj> types = Classifier_traverseFrom(_class);
    for (Obj type : types) {
      Obj cnstrs = get(type, "constructors");
      int argLength = length(args);
      for (Obj cnstr : iterate(cnstrs)) {
        int arity = asInt(get(cnstr, "arity")) - 2;
        boolean isVarArgs = asBool(get(cnstr, "isVarArgs"));
        if (isVarArgs) {
          if (argLength >= arity - 1) return send$(cnstr, "invoke", cons(obj, cons(so, args)));
        } else if (arity == argLength) {
          Obj result = send$(cnstr, "invoke", cons(obj, cons(so, args)));
          return result;
        }
      }
    }
    return obj;
  }

  public static Obj Class_invoke(Obj self, Obj so, Obj args) {

    // Invoking a class is equivalent to calling its 'new' operation...

    return send$(self, "new", args);
  }

  public static void Class_invokeDaemons(Obj type, Obj target, Obj slot, Obj newValue, Obj oldValue) {
    if (registers[DAEMONS] == 0) {
      Stack<Obj> types = Classifier_traverseFrom(type);
      registers[DAEMONS]++;
      for (Obj t : types) {
        for (Obj daemon : iterate(get(t, "daemons"))) {
          send(daemon, "invoke", target, slot, newValue, oldValue);
        }
      }
      registers[DAEMONS]--;
    }
  }

  public static Obj Class_new(Obj type, Obj superObj, Obj args) {

    // This is the standard MOP for object creation:
    // (1) Allocate the object.
    // (2) Add the slots for the attributes.
    // (3) Send the new object 'init'.

    Obj atts = send(type, "allAttributes");
    value.Slot[] slots = new value.Slot[Cons.length(atts)];
    int i = 0;
    for (Obj att : iterate(atts)) {
      Obj name = get(att, "name");
      slots[i++] = Slot(name.toString(), theObjNull);
    }
    registers[CONSTRAINTS]++;
    Obj o = new ConcreteObj(type, slots);
    for (Obj att : iterate(atts)) {
      send(get(att, "init"), "invoke", o, att);
    }
    o = send(type, "initObj", o, args);
    registers[CONSTRAINTS]--;
    Classifier_throwFailures(type, o);
    return o;
  }

  public static Obj Class_noAttributeChanges(Obj c, Obj slot, Obj newValue, Obj oldValue) {
    if (slot.toString().equals("attributes")) {
      System.out.println("No changes to the attributes of " + c + " are allowed.");
      return set(c, "attributes", oldValue);
    } else return c;
  }

  public static Obj Class_set(Obj type, Obj superObj, Obj target, Obj slot, Obj newValue) {

    // The default slot-updater. We can rely on the object representation
    // being a ConcreteObj. This will be specialized in sub-classes
    // of Class to reflect different object representations...

    Obj oldValue = target.get(slot.toString());
    target.set(slot.toString(), newValue);
    Class_invokeDaemons(type, target, slot, newValue, oldValue);
    Classifier_throwFailures(type, target);
    return target;
  }

  public static Obj Class_slots(Obj type, Obj superObj, Obj target) {

    // The default object exploder. Objects can be mapped to a
    // sequence of slots. The mechanism depends on the representation
    // and therefore the meta-class...

    return type.slots();
  }

  public static Obj Class_typeCheckGet(Obj self, Obj so, Obj slot) {

    // Default slot type check protocol...

    for (Obj att : iterate(self.send("getAllAttributes"))) {
      if (att.get("name").toString().equals(slot.toString())) return att.get("type");
    }
    throw new Error("Class_typeCheckGet cannot find attribute named " + slot);
  }

  public static Obj Class_typeCheckSet(Obj self, Obj so, Obj slot, Obj type) {

    // Default slot type check protocol...

    for (Obj att : iterate(self.send("getAllAttributes"))) {
      if (att.get("name").toString().equals(slot.toString())) {
        Obj slotType = att.get("type");
        if (isTrue(type.send("inherits", slotType)))
          return self;
        else throw new Error("cannot set slot " + slot + ":" + slotType + " to a value of type " + type);
      }
    }
    throw new Error("Class_typeCheckSet cannot find attribute named " + slot);
  }

  public static Obj Class_typeCheckSend(Obj self, Obj so, Obj message, Obj types) {

    // Default slot type check protocol...

    for (Obj op : iterate(self.send("getAllOperations"))) {
      if (message.toString().equals(op.get("name").toString())) {
        // Check the arg types!
        boolean isVarArgs = isTrue(op.get("isVarArgs"));
        if (isVarArgs && Cons.length(types) >= asInt(op.get("arity")) - 3) return op.get("type").get("type");
        if (Cons.length(types) == asInt(op.get("arity")) - 2) return op.get("type").get("type");
      }
    }
    throw new Error("Class_typeCheckSend cannot find op named " + message);
  }

  public static Obj Classifier_allCheckNamesDifferent(Obj c) {
    return send(get(c, "constraints"), "forall", Op((o1) -> send(get(c, "constraints"), "forall", Op((o2) -> o1 != o2 ? send(get(o1, "name"), "<>", get(o2, "name")) : theObjTrue))));
  }

  public static Obj Classifier_allOpNamesDifferent(Obj c) {
    return send(get(c, "operations"), "forall", Op((o1) -> send(get(c, "operations"), "forall", Op((o2) -> o1 != o2 ? send(get(o1, "name"), "<>", get(o2, "name")) : theObjTrue))));
  }

  public static Obj Classifier_allSupers(Obj type, Obj superObj) {
    Obj supers = get(type, "supers");
    Obj allSupers = theObjNil;
    for (Obj t : iterate(supers))
      allSupers = send(allSupers, "append", send(t, "allSupers"));
    return send(supers, "append", send(allSupers, "removeDuplicates"));
  }

  public static Obj Classifier_allSupersOriginal(Obj type, Obj superObj) {
    Obj supers = get(type, "supers");
    for (Obj t : iterate(supers))
      supers = send(supers, "append", send(t, "allSupers"));
    return send(supers, "removeDuplicates");
  }

  public static Obj Classifier_asString(Obj target, Obj superObj) {
    return get(target, "name");
  }

  public static Obj Classifier_atLeastOneParent(Obj c) {
    return send(send(get(c, "supers"), "length"), ">", Int(0));
  }

  public static Obj Classifier_cachedSend(Obj target, String message, Obj[] args) {
    OperationCache ocache = sendCache.get(target.of());
    if (ocache.containsKey(message)) {
      Vector<OpAndSuperObj> v = ocache.get(message);
      if (v.size() > (args.length + 2) && v.get(args.length + 2) != null) {
        OpAndSuperObj pair = v.get(args.length + 2);
        ConcreteFun op = pair.getOperation();
        return op.invokeMethod(target, pair.getSuperObj(), args);
      } else return send(target.of(), "send", target, new Str(message), Cons.asList(0, args));
    } else return send(target.of(), "send", target, new Str(message), Cons.asList(0, args));
  }

  public static Obj Classifier_checkConstraints(Obj type, Obj candidate) {
    Obj failures = theObjNil;
    for (Obj c : iterate(cons(type, send(type, "allSupers")))) {
      for (Obj constraint : iterate(get(c, "constraints"))) {
        if (isFalse(send(constraint, "invoke", candidate))) failures = cons(constraint, failures);
      }
    }
    return failures;
  }

  static Obj Classifier_consFrom(int i, Vector<Obj> v) {
    if (i == v.size())
      return theObjNil;
    else return cons(v.elementAt(i), Classifier_consFrom(i + 1, v));
  }

  public static Obj Classifier_deliver(int i, Vector<Obj> types, Obj originalType, Obj target, Obj message, Obj args) {

    // findOp is supplied with a list of types that have been constructed by flattening the
    // lattice via a depth first left-to right walk rooted at the type of the target. Find
    // and invoke the most-specific appropriate operation...

    if (i == types.size())
      return debug.sendResult(send(target, "noOperationFound", message, args));
    else {
      Obj type = types.elementAt(i);
      int arity = Cons.length(args) + 2;
      Obj operation = Classifier_lookup(type, message, arity);
      if (operation != null) {
        Obj superClass = new ConcreteObj(Class, Slot("name", Str("<super@" + message + ">")), Slot("supers", Classifier_consFrom(i + 1, types)), Slot("operations", theObjNil));
        Obj superObj = new Obj() {

          private static final long serialVersionUID = 1L;

          public value.Obj get(String name) {
            return target.get(name);
          }

          public value.Obj of() {
            return superClass;
          }

          public value.Obj send(String name, value.Obj... args) {
            return Classifier_deliver(i + 1, types, originalType, target, Str(name), Cons.asList(0, args));
          }

          public value.Obj set(String name, value.Obj value) {
            return target.set(name, value);
          }

          public value.Obj slots() {
            return target.slots();
          }
        };
        if (operation instanceof ConcreteFun) sendCache.cache(originalType, message.toString(), arity, (ConcreteFun) operation, superObj);
        return debug.sendResult(send(operation, "invoke", Cons.asArray(cons(target, cons(superObj, args)))));
      } else return Classifier_deliver(i + 1, types, originalType, target, message, args);
    }
  }

  public static Obj Classifier_flatten(Obj type, Obj superObj) {
    return cons(type, send(type, "allSupers"));
  }

  public static Obj Classifier_get(Obj type, Obj superObj, Obj target, Obj slot) {
    throw new Error("Classifier is abstract, access to state information is not defined for: " + type);
  }

  public static Obj Classifier_getOp(Obj self, Obj superObj, Obj name) {
    for (Obj op : iterate(get(self, "operations")))
      if (get(op, "name").equals(name)) return op;
    return theObjNull;
  }

  public static Obj Classifier_inherits(Obj self, Obj superObj, Obj c) {

    // Null inherits from everything...

    if (self == Null) return theObjTrue;

    // otherwise see if we can chain to the target class...

    Obj supers = cons(self, send(self, "allSupers"));
    for (Obj s : iterate(supers))
      if (c == s) return theObjTrue;
    return theObjFalse;
  }

  public static Obj Classifier_initial(Obj type, Obj superObj) {
    return get(type, "initial");
  }

  public static Obj Classifier_isInstance(Obj self, Obj superObj, Obj obj) {
    return obj.of() == self ? theObjTrue : send(send(obj.of(), "allSupers"), "contains", self);
  }

  static Obj Classifier_lookup(Obj type, Obj message, int arity) {
    Obj operations = get(type, "operations");
    for (Obj operation : iterate(operations)) {
      int messageArity = asInt(get(operation, "arity"));
      Obj isVarArgs = get(operation, "isVarArgs");
      if (message.equals(get(operation, "name"))) {
        if (isTrue(isVarArgs)) {
          if (arity >= messageArity - 1) return operation;
        } else if (messageArity == arity) return operation;
      }
    }
    return null;
  }

  public static Obj Classifier_new(Obj type, Obj superObj, Obj args) {
    throw new Error("Classifier is abstract, instance creation is not defined for: " + type);
  }

  static void Classifier_pushReverse(Obj types, Stack<Obj> s) {
    if (!isNil(types)) {
      Classifier_pushReverse(tail(types), s);
      s.push(head(types));
    }
  }

  public static Obj Classifier_send(Obj type, Obj superObj, Obj target, Obj message, Obj args) {

    // This operation is supplied with the target of the message, its name and a list
    // of arguments. Since we are here, we know that the target must implement the
    // default MOP for send. This involves: looking up the operation with the supplied
    // name and arity and then invoking it...

    debug.send(target, message, args);
    return Classifier_deliver(0, Classifier_traverseFrom(type), type, target, message, args);
  }

  public static Obj Classifier_set(Obj type, Obj superObj, Obj target, Obj slot, Obj value) {
    throw new Error("Classifier is abstract, state update is not defined for: " + type);
  }

  public static Obj Classifier_slots(Obj type, Obj superObj, Obj target) {
    throw new Error("Classifier is abstract, access to slots is not defined for: " + type);
  }

  public static Obj Classifier_subst(Obj self, Obj superObj, Obj _new, Obj old) {
    return self == old ? _new : self;
  }

  public static void Classifier_throwFailures(Obj type, Obj o) {
    if (registers[CONSTRAINTS] == 0) {
      Obj failures = Classifier_checkConstraints(type, o);
      String message = "";
      for (Obj constraint : iterate(failures))
        message = message + "constraint " + get(constraint, "name") + " fails for " + send(o, "asString") + "\n";
      if (message.length() > 0) throw new Error(message);
    }
  }

  static void Classifier_traverse(Obj types, Stack<Obj> s) {
    if (!isNil(types)) {
      for (Obj type : iterate(types))
        Classifier_traverse(get(type, "supers"), s);
      Classifier_pushReverse(types, s);
    }
  }

  public static Stack<Obj> Classifier_traverseFrom(Obj type) {

    // Calculate a sequence of types including the argument based on a lattice traversal...

    Obj supers = get(type, "supers");
    Stack<Obj> types = new Stack<Obj>();
    Classifier_traverse(supers, types);
    types.push(type);
    Collections.reverse(types);
    return types;
  }

  public static void clearCaches() {
    listOfCache.clear();
    consOfCache.clear();
    nilOfCache.clear();
    getCache.clear();
    sendCache.clear();
  }

  public static Cons cons(Obj head, Obj tail) {
    return new Cons(head, tail);
  }

  public static F3 Cons_append(Obj type) {
    return (list1, superObj, list2) -> cons(get(list1, "head"), send(get(list1, "tail"), "append", list2));
  }

  public static Obj Cons_asString(Obj cons, Obj superObj) {
    if (send(cons, "isProperList") == theObjTrue) {
      String s = "[";
      while (!isNil(cons)) {
        s = s + send(get(cons, "head"), "asString");
        cons = get(cons, "tail");
        if (!isNil(cons)) s = s + ",";
      }
      return Str(s + "]");
    } else return Str("(" + send(get(cons, "head"), "asString") + "." + send(get(cons, "tail"), "asString"));
  }

  public static F3 Cons_cons(Obj type) {
    return (cons, superObj, head) -> cons(head, cons);
  }

  public static Obj Cons_contains(Obj self, Obj superObj, Obj element) {
    if (get(self, "head").equals(element))
      return theObjTrue;
    else return send(get(self, "tail"), "contains", element);
  }

  public static Obj Cons_exists(Obj self, Obj superObj, Obj predicate) {
    return send(send(predicate, "invoke", get(self, "head")), "or", send(get(self, "tail"), "exists", predicate));
  }

  public static F3 Cons_filter(Obj type) {
    return (cons, superObj, predicate) ->
    {
      if (isTrue(send(predicate, "invoke", head(cons))))
        return cons(head(cons), send(tail(cons), "filter", predicate));
      else return send(tail(cons), "filter", predicate);
    };
  }

  public static Obj Cons_forall(Obj self, Obj superObj, Obj predicate) {
    return send(send(predicate, "invoke", get(self, "head")), "and", send(get(self, "tail"), "forall", predicate));
  }

  public static Obj Cons_isProperList(Obj self, Obj superObj) {
    Obj tail = get(self, "tail");
    if (isTrue(send(NilOf(Obj), "isInstance", tail)))
      return theObjTrue;
    else {
      Obj isCons = send(ConsOf(Obj), "isInstance", tail);
      if (isCons == theObjTrue)
        return send(tail, "isProperList");
      else return send(NilOf(Obj), "isInstance", tail);
    }
  }

  public static Obj Cons_length(Obj self, Obj superObj) {
    return Int(length(self));
  }

  public static F3 Cons_remove(Obj type) {
    return (cons, superObj, element) ->
    {
      if (element.equals(get(cons, "head")))
        return send(get(cons, "tail"), "remove", element);
      else return cons(get(cons, "head"), send(get(cons, "tail"), "remove", element));
    };
  }

  public static F2 Cons_removeDuplicates(Obj type) {
    // Remove duplicates and leave the last occurrence...
    return (list, superObj) -> (isTrue(send(tail(list), "contains", head(list)))) ? send(tail(list), "removeDuplicates") : cons(head(list), send(tail(list), "removeDuplicates"));
  }

  public static F3 Cons_selectOne(Obj type) {
    return (cons, superObj, predicate) ->
    {
      if (isTrue(send(predicate, "invoke", head(cons))))
        return head(cons);
      else return send(tail(cons), "selectOne", predicate);
    };
  }

  public static ConcreteObj ConsOf(Obj type) {
    if (consOfCache.containsKey(type))
      return consOfCache.get(type);
    else {
      ConcreteObj o = new ConcreteObj(ConsOf);
      consOfCache.put(type, o);
      o.addSlot("supers", list(ListOf(type)));
      o.addSlot("attributes", theObjNil);
      o.extendSlot("attributes", Attribute("head", type));
      o.extendSlot("attributes", Attribute("tail", Obj));
      o.addSlot("operations", theObjNil);
      o.extendSlot("operations", Op("append", Cons_append(type)));
      o.extendSlot("operations", Op("asString", (F2) value.Obj::Cons_asString));
      o.extendSlot("operations", Op("cons", Cons_cons(type)));
      o.extendSlot("operations", Op("contains", (F3) value.Obj::Cons_contains));
      o.extendSlot("operations", Op("exists", (F3) value.Obj::Cons_exists));
      o.extendSlot("operations", Op("forall", (F3) value.Obj::Cons_forall));
      o.extendSlot("operations", Op("isProperList", (F2) value.Obj::Cons_isProperList));
      o.extendSlot("operations", Op("length", (F2) value.Obj::Cons_length));
      o.extendSlot("operations", Op("removeDuplicates", Cons_removeDuplicates(type)));
      o.extendSlot("operations", Op("remove", Cons_remove(type)));
      o.extendSlot("operations", Op("selectOne", Cons_selectOne(type)));
      o.extendSlot("operations", Op("filter", Cons_filter(type)));
      o.addSlot("elementType", type);
      o.addSlot("initial", theObjNull);
      o.addSlot("name", "Cons(" + type + ")");
      o.addSlot("constructors", theObjNil);
      o.addSlot("constraints", theObjNil);
      o.addSlot("daemons", theObjNil);
      o.addSlot("isAbstract", theObjFalse);
      return o;
    }
  }

  public static Obj ConsOf_isInstance(Obj type, Obj superObj, Obj candidate) {
    if (isCons(candidate)) {
      Obj head = get(candidate, "head");
      return send(get(type, "elementType"), "isInstance", head);
    } else return theObjFalse;
  }

  public static boolean constructorMatches(Constructor<?> cnstr, Obj args) {
    // This should check the types and the arity...
    if (cnstr.getParameterTypes().length == Cons.length(args))
      return true;
    else return false;
  }

  public static Obj ContainerOf(String name, Obj type) {
    Obj c = ContainerOf_shell(ContainerOf, name, type);
    return ContainerOf_init(c, null, list(Str(name), type));
  }

  public static Obj ContainerOf_init(Obj containerOf, Obj superObj, Obj args) {
    Obj name = head(args);
    String n = name.toString();
    String ns = n.endsWith("s") ? n : n + "s";
    Obj type = head(tail(args));
    String N = Character.toString(n.charAt(0)).toUpperCase() + n.substring(1);
    registers[DAEMONS]++;
    set(containerOf, "name", Str("Container(" + ns + "," + type + ")"));
    set(containerOf, "contentName", Str(ns));
    set(containerOf, "contentType", type);
    extend(containerOf, "supers", Container);
    extend(containerOf, "attributes", Attribute(ns, ListOf(type)));
    extend(containerOf, "operations", Op("get" + N, (o, so) -> get(o, ns)));
    extend(containerOf, "operations", Op("add" + N, (o, so, v) -> extend(o, ns, v)));
    extend(containerOf, "operations", Op("add" + N + "s", (o, so, v) -> append(o, ns, v)));
    extend(containerOf, "operations", Op("has" + N, (o, so, v) -> send(get(o, ns), "contains", v)));
    extend(containerOf, "operations", Op("remove" + N, (o, so, v) -> set(o, ns, send(get(o, ns), "remove", v))));
    registers[DAEMONS]--;
    return containerOf;
  }

  public static Obj ContainerOf_shell(Obj metaType, String name, Obj type) {
    ConcreteObj c = new ConcreteObj(metaType);
    c.addSlot("attributes", theObjNil);
    c.addSlot("constructors", theObjNil);
    c.addSlot("constraints", theObjNil);
    c.addSlot("initial", theObjNull);
    c.addSlot("isAbstract", theObjFalse);
    c.addSlot("name", "");
    c.addSlot("operations", theObjNil);
    c.addSlot("supers", list(Obj));
    c.addSlot("contentName", Str(name));
    c.addSlot("contentType", type);
    c.addSlot("daemons", theObjNil);
    return c;
  }

  public static void createKernel() {

    // Create the kernel package. Careful since the parents of the kernel are specified as
    // Obj which means that a package gets Obj::asString if no more specific operations
    // are defined. Since Kernel is its own meta-package, a slot-based output will loop...

    register(send(Package, "new", Str("Kernel"), theObjNil, list(Obj)), Kernel);
    set(Kernel, "package", Kernel);
    extend(Kernel, "operations", Op("test", (self, superObj) -> Str("hooray")));
    extend(Kernel, "operations", Op("asString", (self, superObj) -> get(self, "name")));
    send(Kernel, "addBinding", Slot("nil", theObjNil));
    send(Kernel, "addBinding", Slot("null", theObjNull));
    set(Kernel, "objects", list(Atom, Atomic, Obj, Class, DocumentedElement, Attribute, Bool, Container, ContainerOf, Float, NameSpaceOf, Int, JavaClass, Str, Classifier, Function, NamedElement, Obj, Null, Number, Slot, Package, PackageController, TableOf, Table, ListOf, ListOf(Obj), ConsOf, ConsOf(Obj), NilOf, NilOf(Obj), Slot, ConcreteFun, FunctionOf,
        TableOf, Self, StaticSelf, Snapshot, Symbol, Inherits));
  }

  public static Obj debug(Obj value) {
    System.out.println("DEBUG -----> " + value);
    return value;
  }

  public static String domainString(Obj domain) {
    String ts = "(";
    if (Cons.length(domain) > 0) {
      ts = ts + head(domain);
      for (Obj t : iterate(tail(domain)))
        ts = ts + "," + t;
    }
    ts = ts + ")";
    return ts;
  }

  public static Obj Equals(Obj o1, Obj o2) {
    return send(o1, "=", o2);
  }

  public static Obj extend(Obj o, String name, Obj v) {
    return set(o, name, cons(v, get(o, name)));
  }

  public static Obj Float(double d) {
    return new Float(d);
  }

  public static Obj Function_allArgNamesDifferent(Obj c) {
    return send(send(get(c, "args"), "length"), "=", send(send(get(c, "args"), "removeDuplicates"), "length"));
  }

  public static Obj Function_invoke(Obj self, Obj superObj, Obj args) {
    throw new Error("Function is an abstract class.");
  }

  public static Obj Function_typeIsFunctionType(Obj self) {
    return send(FunctionOf, "isInstance", get(self, "type"));
  }

  public static Obj FunctionOf(Obj domain, Obj type) {
    return FunctionOf(domain, theObjFalse, type);
  }

  public static Obj FunctionOf(Obj domain, Obj isVarArgs, Obj type) {
    ConcreteObj o = new ConcreteObj(FunctionOf);
    o.addSlot("supers", list(Obj));
    o.addSlot("attributes", theObjNil);
    o.addSlot("operations", theObjNil);
    o.addSlot("domain", domain);
    o.addSlot("type", type);
    o.addSlot("initial", theObjNull);
    o.addSlot("name", (isTrue(isVarArgs) ? varDomainString(domain) : domainString(domain)) + "->" + type);
    o.addSlot("constructors", theObjNil);
    o.addSlot("constraints", theObjNil);
    o.addSlot("daemons", theObjNil);
    o.addSlot("isVarArgs", isVarArgs);
    return o;
  }

  public static Obj FunctionOf_subst(Obj self, Obj superObj, Obj _new, Obj old) {
    Obj domain = self.get("domain");
    Obj range = self.get("type");
    Obj isVarArgs = self.get("isVarArgs");
    Obj newDomain = theObjNil;
    for (Obj type : iterate(domain))
      newDomain = send(newDomain, "append", list(send(type, "subst", _new, old)));
    Obj subst = FunctionOf(newDomain, isVarArgs, send(range, "subst", _new, old));
    return subst;
  }

  public static Obj get(Obj target, String name) {
    if (target instanceof Kernel)
      return target.get(name);
    else if (target.of().of() != Class && target.of() != Package) {
      return target.of().send("get", target, Str(name));
    } else return target.get(name);
  }

  public static Constructor<?> getConstructor(java.lang.Class<?> c, Obj args) {
    Constructor<?>[] cnstrs = c.getConstructors();
    for (Constructor<?> cnstr : cnstrs) {
      if (constructorMatches(cnstr, args)) return cnstr;
    }
    return null;
  }

  public static Obj getKernel() {

    // Must be called in order to initialize everything...

    if (Kernel.of() == null) {
      bootstrap();
      getSnapshot();
      getPackage();
      getTable();
      getWalker();
      setTypes();
      startTypeChecking();
      createKernel();
      return Kernel;
    } else return Kernel;
  }

  public static void getPackage() {
    // Create the class Package...
    Obj classifierNameSpace = NameSpaceOf("object", Classifier);
    Obj superAtt = send(send(Snapshot, "allAttributes"), "selectOne", Op((x) -> send(get(x, "name"), "=", Str("objects"))));
    set(send(classifierNameSpace, "getAttributeNamed", Str("objects")), "supers", list(superAtt));
    set(classifierNameSpace, "supers", list(Snapshot));
    register(send(Class, "new", Str("PackageController"), list(), list(Class)), PackageController);
    register(send(PackageController, "new", Str("Package"), list(), list(Class, classifierNameSpace)), Package);
    Package.extendSlot("supers", NameSpaceOf("superPackages", Package));
    extend(Package, "operations", Op("new", (self, superObj, initArgs) ->
    {
      Obj s = send(Snapshot, "new");
      set(s, "package", self);
      return s;
    }));
    set(send(Package, "getOp", Str("new")), "isVarArgs", theObjTrue);
    extend(Package, "constraints", Op("allObjectsAreClassifiers", (p) -> send(get(p, "objects"), "forall", Op((o) -> send(Classifier, "isInstance", o)))));
    extend(Package, "operations", Op("asString", (F2) (p, so) -> get(p, "name")));
    extend(Package, "operations", Op("toDiagram", value.Obj::Package_toDiagram));
    extend(Package, "constructors", Op((self, so, name) -> set(set(set(self, "supers", list(Obj)), "package", Kernel), "name", name), "name"));
    extend(Package, "constructors", Op((self, so, name, metaPackage) -> set(set(set(self, "supers", list(Obj)), "package", metaPackage), "name", name), "name", "package"));
    // extend(Package, "constructors", Op((self, so, name, metaPackage, supers) -> set(set(set(set(self, "supers", list(Obj)), "superPackage", supers), "package", metaPackage),
    // "name", name), "name", "package", "superPackages"));
    extend(PackageController, "operations", Op("send", (F5) value.Obj::PackageController_send));
    register(send(Package, "new", Str("Kernel"), theObjNil, list(Obj)), Kernel);
  }

  public static Obj Package_toDiagram(Obj p, Obj so, Obj name,Obj excluded) {
    Diagrams.generateTypeDiagramForElements(name.toString(),get(p,"objects"), excluded);
    return p;
  }

  public static void getSnapshot() {

    // Create the class Snapshot. The idea is that a snapshot has a package. The snapshot should contain objects
    // that are instances of the classes contained in the associated package. Note that Package inherits from
    // Snapshot implying that each package has a meta-package that types its elements...

    Obj containerOfObjs = send(ContainerOf, "new", Str("object"), Obj);
    // register(send(theClassClass, "new", list(Str("Snapshot"), list(Attribute("objects", ListOf(theClassObj))), list(theClassObj))), theClassSnapshot);
    Obj atts = list(send(Attribute, "new", Str("package"), Package));
    register(send(Class, "new", Str("Snapshot"), atts, list(containerOfObjs)), Snapshot);
    extend(Snapshot, "constraints", Op("allObjectsCorrectlyTyped", (F1) value.Obj::Snapshot_allObjectsCorrectlyTyped));
    extend(Snapshot, "constructors", Op((self, so, _package) -> set(self, "package", _package), "package"));
    Snapshot.extendSlot("supers", NameSpaceOf("binding", Slot));
    extend(Snapshot, "operations", Op("lookup", (self, so, name) ->
    {
      if (isTrue(send(self, "hasBindingNamed", name)))
        return get(send(self, "getBindingNamed", name), "name");
      else throw new Error("cannot find binding for " + name);
    }));
    set(send(Snapshot, "getOp", Str("lookup")), "type", FunctionOf(list(Snapshot, Obj, Str), Obj));
  }

  public static void getTable() {

    // The class Table is a meta-class whose instantiation is parameterized with respect to the types
    // of the keys and values in the tables that are instances of its instances. Create a new table-type
    // by instantiating Table and supplying the key and value types. The resulting table supports messages
    // "get" and "set". Where the key-type is Str then the operations Obj.get and Obj.set can be used.

    // The implementation of Table is based on a MOP:
    // (1) Table::new creates a new Java Hashtable<Obj,Obj> and adds it to the global called 'tables'
    // defined below. This is an example of a bespoke object representation for an object.
    // (2) Table::send implements the "set" and "get" messages by mapping the target to its hash table
    // and then using the supplied key. Note that type-checks are imposed. If the message is not
    // handled specially within the operation then the super object allows the message to be passed
    // back to the default mechanism.
    // (3) Table::get handles the case where the key is of type Str.
    // (4) Table::set handles the case where the key is of type Str.
    // Each instance of Table must inherit from SuperTable so that general-purpose table operations
    // such as "asString" can be defined there.

    register(send(Class, "new", Str("TableOf"), list(Attribute("keyType", Class), Attribute("valueType", Class)), list(Classifier)), TableOf);
    register(send(Class, "new", Str("Table"), list(), list(Obj)), Table);
    extend(TableOf, "operations", Op("new", Table_new));
    extend(TableOf, "operations", Op("get", (type, so, target, name) -> target.get(name.toString())));
    extend(TableOf, "operations", Op("set", (type, so, target, name, value) -> target.set(name.toString(), value)));
    extend(TableOf, "operations", Op("typeCheckSend", Table_typeCheckSend));
    extend(TableOf, "operations", Op("typeCheckGet", Table_typeCheckGet));
    extend(TableOf, "operations", Op("typeCheckSet", Table_typeCheckSet));
    extend(TableOf, "constructors", Op((F4) (self, so, keyType, valueType) -> TableOf_init(self, so, keyType, valueType), "keyType", "valueType"));
    set(send(TableOf, "getOp", Str("new")), "type", FunctionOf(list(TableOf, Obj, ListOf(Obj)), Table));
    set(send(TableOf, "getOp", Str("new")), "isVarArgs", theObjTrue);
  }

  public static void getWalker() {

    // A walker is a dispatch table whose handlers perform actions on a type-basis. The visited table in the
    // walker is used to ensure that cycles are detected using the operation revisit...

    Obj handlerTableType = send(TableOf, "new", Class, Function);
    Obj visitedTableType = send(TableOf, "new", Obj, Obj);
    register(send(Class, "new", Str("Walker"), list(Attribute("limitHandler", Function, Op((o, a) -> Attribute_limitHandler(o, a))), Attribute("excluded", ListOf(Obj)), Attribute("handlers", handlerTableType), Attribute("visited", visitedTableType), Attribute("limit", Int)), list(Obj)), Walker);
    extend(Walker, "constructors", Op((self, so, excluded) -> set(set(set(self, "excluded", excluded), "visited", send(visitedTableType, "new")), "handlers", send(handlerTableType, "new")), "excluded"));
    extend(Walker, "operations", Op("addHandler", (o, so, h) -> Walker_addWalker(o, so, h)));
    extend(Walker, "operations", Op("asString", (self, so) -> Str("Walker(" + send(get(self, "handlers"), "asString") + ")")));
    extend(Walker, "operations", Op("reset", (self, so) -> send(get(set(set(self, "limit", Int(0)), "excluded", theObjNil), "visited"), "clear")));
    extend(Walker, "operations", Op("revisit", (self, so, o) -> o));
    extend(Walker, "operations", Op("atLimit", (w, so, o) -> send(get(w, "limitHandler"), "invoke", o)));
    extend(Walker, "operations", Op("decLimitCount", (w, so) -> set(w, "limit", send(get(w, "limit"), "-", Int(1)))));
    extend(Walker, "operations", Op("incLimitCount", (w, so) -> set(w, "limit", send(get(w, "limit"), "+", Int(1)))));
    extend(Walker, "operations", Op("walk", (w, so, o) -> Walker_walk(w, so, o)));
  }

  public static Obj head(Obj list) {
    return get(list, "head");
  }

  public static Obj Inherits(String name, Obj verticalCompose, Obj horizonalCompose) {
    ConcreteObj o = new ConcreteObj(Inherits);
    o.addSlot("supers", list(Obj));
    o.addSlot("attributes", theObjNil);
    o.addSlot("operations", theObjNil);
    o.addSlot("inheritedName", Str(name));
    o.addSlot("verticalCompose", verticalCompose);
    o.addSlot("horizonalCompose", horizonalCompose);
    o.addSlot("initial", theObjNull);
    o.addSlot("isAbstract", theObjFalse);
    o.addSlot("name", "Inherits(" + name + ")");
    o.addSlot("constructors", theObjNil);
    o.addSlot("constraints", theObjNil);
    o.addSlot("daemons", theObjNil);
    return Inherits_init(o, null, list(Str(name), horizonalCompose, verticalCompose));
  }

  public static Obj Inherits_init(Obj inherits, Obj superObj, Obj args) {
    Obj name = head(args);
    Obj horizontalCompose = head(tail(args));
    Obj verticalCompose = head(tail(tail(args)));
    return Inherits_init(inherits, superObj, name, horizontalCompose, verticalCompose);
  }

  public static Obj Inherits_init(Obj inherits, Obj superObj, Obj name, Obj horizontalCompose, Obj verticalCompose) {
    set(inherits, "inheritedName", name);
    set(inherits, "horizonalCompose", horizontalCompose);
    set(inherits, "verticalCompose", verticalCompose);
    String n = name.toString();
    String ns = n.endsWith("s") ? n : n + "s";
    String N = Character.toString(ns.charAt(0)).toUpperCase() + ns.substring(1);
    extend(inherits, "operations", Op("getAll" + N, (i, so) -> inheritsCompose(i, name, horizontalCompose, verticalCompose)));
    return inherits;
  }

  public static Obj inheritsCompose(Obj o, Obj name, Obj horizonalCompose, Obj verticalCompose) {
    // This is a mixin for an object with a slot called 'supers'. Use the
    // horizonal composition operation to merge at the same level and use the
    // vertical operation to merge parents and children.
    Obj values = send(o, "get", name);
    Obj supers = get(o, "supers");
    return send(verticalCompose, "invoke", values, inheritsComposeSupers(o, name, horizonalCompose, verticalCompose, supers));
  }

  public static Obj inheritsComposeSupers(Obj o, Obj name, Obj horizonalCompose, Obj verticalCompose, Obj supers) {
    if (isNil(supers)) {
      return theObjNil;
    } else return send(horizonalCompose, "invoke", inheritsCompose(head(supers), name, horizonalCompose, verticalCompose), inheritsComposeSupers(o, name, horizonalCompose, verticalCompose, tail(supers)));
  }

  public static void initClass(ConcreteObj c, String name, Obj supers) {
    initClass(Class, c, name, supers);
  }

  public static void initClass(Obj meta, ConcreteObj c, String name, Obj supers) {
    c.setType(meta);
    c.addSlot("attributes", theObjNil);
    c.addSlot("constructors", theObjNil);
    c.addSlot("constraints", theObjNil);
    c.addSlot("daemons", theObjNil);
    c.addSlot("initial", theObjNull);
    c.addSlot("isAbstract", theObjFalse);
    c.addSlot("name", name);
    c.addSlot("operations", theObjNil);
    c.addSlot("supers", supers);
    c.addSlot("doc", Str(""));
  }

  public static Obj Int(int i) {
    return new Int(i);
  }

  public static Obj Int(Obj type, int i) {
    return new Int(type, i);
  }

  public static Obj Int_add(Obj i, Obj superObj, Obj j) {
    if (i instanceof value.Int && j instanceof value.Int)
      return Int(((value.Int) i).getValue() + ((value.Int) j).getValue());
    else throw new Error("I don't know how to add " + i + " and " + j);
  }

  public static Obj Int_div(Obj i, Obj superObj, Obj j) {
    if (i instanceof value.Int && j instanceof value.Int)
      return Float(((value.Int) i).getValue() / ((value.Int) j).getValue());
    else throw new Error("I don't know how to divide " + i + " and " + j);
  }

  public static Obj Int_gre(Obj self, Obj superObj, Obj value) {
    value.Int i = (value.Int) self;
    value.Int j = (value.Int) value;
    return i.getValue() > j.getValue() ? theObjTrue : theObjFalse;
  }

  public static Obj Int_isCapital(Obj o, Obj superObj) {
    Int i = (Int) o;
    return 'A' <= i.getValue() && 'Z' >= i.getValue() ? theObjTrue : theObjFalse;
  }

  public static Obj Int_less(Obj self, Obj superObj, Obj value) {
    value.Int i = (value.Int) self;
    value.Int j = (value.Int) value;
    return i.getValue() < j.getValue() ? theObjTrue : theObjFalse;
  }

  public static Obj Int_mul(Obj i, Obj superObj, Obj j) {
    if (i instanceof value.Int && j instanceof value.Int)
      return Int(((value.Int) i).getValue() * ((value.Int) j).getValue());
    else throw new Error("I don't know how to multiply " + i + " and " + j);
  }

  public static Obj Int_sqrt(Obj self, Obj superObj) {
    value.Int i = (value.Int) self;
    return Float(Math.sqrt(i.getValue()));
  }

  public static Obj Int_sub(Obj i, Obj superObj, Obj j) {
    if (i instanceof value.Int && j instanceof value.Int)
      return Int(((value.Int) i).getValue() - ((value.Int) j).getValue());
    else throw new Error("I don't know how to subtract " + i + " and " + j);
  }

  public static boolean isBool(Obj o) {
    return o.of() == Bool;
  }

  public static boolean isCons(Obj o) {
    return o instanceof Cons;
  }

  public static boolean isFalse(Obj o) {
    return o == theObjFalse;
  }

  public static boolean isInt(Obj o) {
    return o.of() == Int;
  }

  public static boolean isNil(Obj o) {
    return o instanceof Nil;
  }

  public static boolean isNull(Obj o) {
    return o == theObjNull;
  }

  public static boolean isStr(Obj o) {
    return o.of() == Str;
  }

  public static boolean isTrue(Obj o) {
    return o == theObjTrue;
  }

  public static Iterable<Obj> iterate(final Obj o) {
    if (isProperList(o)) {
      return new Iterable<Obj>() {
        public Iterator<Obj> iterator() {
          return new Iterator<Obj>() {
            Obj list = o;

            public boolean hasNext() {
              return !isNil(list);
            }

            public Obj next() {
              Obj next = head(list);
              list = tail(list);
              return next;
            }

            public void remove() {
            }
          };
        }
      };
    } else throw new Error("not iterable: " + o);
  }

  public static Obj JavaClass_get(Obj type, Obj superObj, Obj target, Obj slot) {
    throw new Error("JavaClass_get not implemented.");
  }

  public static Obj JavaClass_new(Obj javaClass, Obj superObj, Obj args) {
    String name = get(javaClass, "name").toString();
    try {
      Class<?> c = java.lang.Class.forName(name);
      Constructor<?> cnstr = getConstructor(c, args);
      if (cnstr == null) {
        if (Cons.length(args) == 0)
          return newJavaInstance(javaClass, c);
        else throw new Error("cannot find a constructor that matches the supplied args for " + c + ".new" + args);
      } else return newJavaInstance(javaClass, cnstr, args);
    } catch (ClassNotFoundException e) {
      System.out.println("cannot find class named " + name);
      return theObjNull;
    }
  }

  public static Obj JavaClass_send(Obj type, Obj superObj, Obj target, Obj message, Obj args) {
    throw new Error("JavaClass_send not implemented: " + type + " " + target + " " + message + " " + args);
  }

  public static Obj JavaClass_set(Obj type, Obj superObj, Obj target, Obj slot, Obj value) {
    throw new Error("JavaClass_set not implemented.");
  }

  public static Obj list(Obj... elements) {
    Obj list = theObjNil;
    for (int i = elements.length - 1; i >= 0; i--)
      list = cons(elements[i], list);
    return list;
  }

  public static F3 List_append(Obj type) {
    return (list1, superObj, list2) ->
    {
      throw new Error("List::append is abstract");
    };
  }

  public static F3 List_cons(Obj type) {
    return (cons, superObj, head) ->
    {
      throw new Error("List::cons is abstract");
    };
  }

  public static Obj List_contains(Obj self, Obj superObj, Obj element) {
    throw new Error("List::contains is abstract");
  }

  public static Obj List_exists(Obj self, Obj superObj, Obj predicate) {
    throw new Error("List::exists is abstract");
  }

  public static F3 List_filter(Obj type) {
    return (cons, superObj, predicate) ->
    {
      throw new Error("List::filter is abstract");
    };
  }

  public static Obj List_forall(Obj self, Obj superObj, Obj predicate) {
    throw new Error("List::forall is abstract");
  }

  public static Obj List_isProperList(Obj self, Obj superObj) {
    throw new Error("List::isProperList is abstract");
  }

  public static Obj List_length(Obj self, Obj superObj) {
    throw new Error("List::length is abstract");
  }

  public static F3 List_remove(Obj type) {
    return (cons, superObj, element) ->
    {
      throw new Error("List::remove is abstract");
    };
  }

  public static F2 List_removeDuplicates(Obj type) {
    return (list, superObj) ->
    {
      throw new Error("List::removeDuplicates is abstract");
    };
  }

  public static F3 List_selectOne(Obj type) {
    return (cons, superObj, predicate) ->
    {
      throw new Error("List::selectOne is abstract");
    };
  }

  public static Obj ListOf(Obj type) {
    if (listOfCache.containsKey(type))
      return listOfCache.get(type);
    else {
      ConcreteObj o = new ConcreteObj(ListOf);
      listOfCache.put(type, o);
      o.addSlot("supers", type == Obj ? list(Obj) : list(ListOf(Obj)));
      o.addSlot("attributes", theObjNil);
      o.extendSlot("attributes", Attribute("head", type));
      o.extendSlot("attributes", Attribute("tail", o));
      o.addSlot("operations", theObjNil);
      o.extendSlot("operations", Op("append", List_append(type)));
      o.extendSlot("operations", Op("cons", List_cons(type)));
      o.extendSlot("operations", Op("contains", (F3) value.Obj::List_contains));
      o.extendSlot("operations", Op("exists", (F3) value.Obj::List_exists));
      o.extendSlot("operations", Op("forall", (F3) value.Obj::List_forall));
      o.extendSlot("operations", Op("isProperList", (F2) value.Obj::List_isProperList));
      o.extendSlot("operations", Op("length", (F2) value.Obj::List_length));
      o.extendSlot("operations", Op("removeDuplicates", List_removeDuplicates(type)));
      o.extendSlot("operations", Op("remove", List_remove(type)));
      o.extendSlot("operations", Op("selectOne", List_selectOne(type)));
      o.extendSlot("operations", Op("filter", List_filter(type)));
      o.addSlot("elementType", type);
      o.addSlot("initial", nilOf(type));
      o.addSlot("name", "[" + type + "]");
      o.addSlot("constructors", theObjNil);
      o.addSlot("constraints", theObjNil);
      o.addSlot("daemons", theObjNil);
      o.addSlot("isAbstract", theObjTrue);
      return o;
    }
  }

  public static Obj ListOf_inherits(Obj self, Obj superObj, Obj type) {
    System.out.println("List of inherits " + type + " " + self + " " + (type.of() == ListOf));
    if (type.of() == ListOf)
      return self.get("elementType").send("inherits", type.get("elementType"));
    else if (isTrue(type.send("inherits", Classifier)))
      return theObjTrue;
    else if (type == Obj)
      return theObjTrue;
    else return theObjFalse;
  }

  public static Obj methodType(Obj ftype) {
    return FunctionOf(tail(tail(get(ftype, "domain"))), get(ftype, "isVarArgs"), get(ftype, "type"));
  }

  public static Obj NameSpaceOf(String name, Obj type) {
    Obj ns = ContainerOf_shell(NameSpaceOf, name, type);
    return NameSpaceOf_init(ns, null, list(Str(name), type));
  }

  public static Obj NameSpaceOf_init(Obj nameSpaceOf, Obj superObj, Obj args) {
    Obj name = head(args);
    String n = name.toString();
    String ns = n.endsWith("s") ? n : n + "s";
    String N = Character.toString(n.charAt(0)).toUpperCase() + n.substring(1);
    Obj type = head(tail(args));
    ContainerOf_init(nameSpaceOf, superObj, args);
    set(nameSpaceOf, "name", Str("NameSpace(" + ns + "," + type + ")"));
    extend(nameSpaceOf, "operations", Op("get" + N + "Named", (o, so, key) -> send(get(o, ns), "selectOne", Op((x) -> send(get(x, "name"), "=", key)))));
    extend(nameSpaceOf, "operations", Op("has" + N + "Named", (o, so, key) -> send(get(o, ns), "exists", Op((x) -> send(get(x, "name"), "=", key)))));
    extend(nameSpaceOf, "operations", Op("remove" + N + "Named", (o, so, key) ->
    {
      if (isTrue(send(o, "has" + N + "Named", key))) set(o, ns, (send(get(o, ns), "remove", send(o, "get" + N + "Named", key))));
      return o;
    }));
    extend(nameSpaceOf, "constraints", Op(ns + "AreAllNamed", (o) -> send(get(o, ns), "forall", Op((x) -> send(NamedElement, "isInstance", x)))));
    return nameSpaceOf;
  }

  public static Obj newJavaInstance(Obj javaClass, Constructor<?> cnstr, Obj args) {
    Object o;
    try {
      o = cnstr.newInstance((Object[]) Cons.asArray(args));
      return newJavaInstance(javaClass, o);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return theObjNull;
  }

  public static Obj newJavaInstance(Obj javaClass, java.lang.Class<?> c) {
    try {
      Object o = c.newInstance();
      return newJavaInstance(javaClass, o);
    } catch (InstantiationException e) {
      e.printStackTrace();
      return theObjNull;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return theObjNull;
    }
  }

  public static Obj newJavaInstance(Obj javaClass, Object o) {
    return new Obj() {
      private static final long serialVersionUID = 1L;

      public value.Obj get(String name) {
        throw new Error("JavaClass.get MOP not implemented.");
      }

      public value.Obj of() {
        return javaClass;
      }

      public value.Obj send(String name, value.Obj... args) {
        throw new Error("JavaClass.send MOP not implemented.");
      }

      public value.Obj set(String name, value.Obj value) {
        throw new Error("JavaClass.set MOP not implemented.");
      }

      public value.Obj slots() {
        throw new Error("JavaClass.slots MOP not implemented.");
      }
    };
  }

  public static F3 Nil_append(Obj type) {
    return (list1, superObj, list2) -> list2;
  }

  public static Obj Nil_asString(Obj cons, Obj superObj) {
    return Str("[]");
  }

  public static F3 Nil_cons(Obj type) {
    return (cons, superObj, head) -> cons(head, cons);
  }

  public static Obj Nil_contains(Obj self, Obj superObj, Obj element) {
    return theObjFalse;
  }

  public static Obj Nil_exists(Obj self, Obj superObj, Obj predicate) {
    return theObjFalse;
  }

  public static Obj Nil_filter(Obj self, Obj superObj, Obj predicate) {
    return self;
  }

  public static Obj Nil_forall(Obj self, Obj superObj, Obj predicate) {
    return theObjTrue;
  }

  public static Obj Nil_isProperList(Obj self, Obj superObj) {
    return theObjTrue;
  }

  public static Obj Nil_length(Obj self, Obj superObj) {
    return Int(0);
  }

  public static F3 Nil_remove(Obj type) {
    return (nil, superObj, element) -> nil;
  }

  public static F2 Nil_removeDuplicates(Obj type) {
    return (nil, superObj) -> nil;
  }

  public static Obj Nil_selectOne(Obj self, Obj superObj, Obj predicate) {
    throw new Error("cannot select one from []");
  }

  public static Obj nilOf(Obj type) {
    return new Nil(type);
  }

  public static Obj NilOf(Obj type) {
    if (nilOfCache.containsKey(type))
      return nilOfCache.get(type);
    else {
      ConcreteObj o = new ConcreteObj(NilOf);
      nilOfCache.put(type, o);
      o.addSlot("supers", type == Obj ? list(ListOf(type)) : list(NilOf(Obj)));
      o.addSlot("attributes", theObjNil);
      o.addSlot("operations", theObjNil);
      o.extendSlot("operations", Op("append", Nil_append(type)));
      o.extendSlot("operations", Op("removeDuplicates", Nil_removeDuplicates(type)));
      o.extendSlot("operations", Op("remove", Nil_remove(type)));
      o.extendSlot("operations", Op("cons", Nil_cons(type)));
      o.extendSlot("operations", Op("asString", (F2) value.Obj::Nil_asString));
      o.extendSlot("operations", Op("isProperList", (F2) value.Obj::Nil_isProperList));
      o.extendSlot("operations", Op("contains", (F3) value.Obj::Nil_contains));
      o.extendSlot("operations", Op("forall", (F3) value.Obj::Nil_forall));
      o.extendSlot("operations", Op("exists", (F3) value.Obj::Nil_exists));
      o.extendSlot("operations", Op("length", (F2) value.Obj::Nil_length));
      o.extendSlot("operations", Op("selectOne", (F3) value.Obj::Nil_selectOne));
      o.extendSlot("operations", Op("filter", (F3) value.Obj::Nil_filter));
      o.addSlot("elementType", type);
      o.addSlot("initial", nilOf(type));
      o.addSlot("name", "Nil(" + type + ")");
      o.addSlot("constructors", theObjNil);
      o.addSlot("constraints", theObjNil);
      o.addSlot("daemons", theObjNil);
      o.addSlot("isAbstract", theObjFalse);
      return o;
    }
  }

  public static Obj NilOf_isInstance(Obj type, Obj superObj, Obj candidate) {
    return isNil(candidate) ? theObjTrue : theObjFalse;
  }

  public static Obj Null_asString(Obj o, Obj so) {
    return Str("null");
  }

  public static Obj Obj_allSlotNamesDifferent(Obj o) {
    return send(send(o, "getSlots"), "forall", Op((s1) -> send(send(o, "getSlots"), "forall", Op((s2) -> s1 != s2 ? send(get(s1, "name"), "<>", get(s2, "name")) : theObjTrue))));
  }

  public static Obj Obj_allSlotValuesTypeCheck(Obj o) {
    return send(send(o, "getSlots"), "forall", Op((s) -> send(send(o.of(), "allAttributes"), "exists", Op((a) -> send(send(get(a, "name"), "=", get(s, "name")), "implies", send(get(a, "type"), "isInstance", get(s, "value")))))));
  }

  public static Obj Obj_asString(Obj target, Obj superObj) {
    if (debug.isNested())
      return Str(target.toString());
    else {
      String typeName = value.Obj.get(target.of(), "name").toString();
      Obj slots = target.slots();
      String s = "(" + typeName + ")[";
      while (slots != theObjNil) {
        Obj slot = get(slots, "head");
        s = s + get(slot, "name");
        s = s + "=";
        s = s + send(get(slot, "value"), "asString");
        slots = get(slots, "tail");
        if (slots != theObjNil) s = s + ",";
      }
      return Str(s + "]");
    }
  }

  public static Obj Obj_equals(Obj o1, Obj so, Obj o2) {
    return o1.equals(o2) ? theObjTrue : theObjFalse;
  }

  public static Obj Obj_get(Obj self, Obj superObj, Obj name) {
    return send(self.of(), "get", self, name);
  }

  public static Obj Obj_set(Obj self, Obj superObj, Obj name, Obj value) {
    return send(self.of(), "set", self, name, value);
  }

  public static Obj Obj_getSlots(Obj self, Obj superObj) {
    return self.slots();
  }

  public static Obj Obj_noOperationFound(Obj target, Obj superObj, Obj message, Obj args) {
    throw new Error("no operation found: " + target + ".send(" + message + "," + args + ")");
  }

  public static Obj Obj_noSlotFound(Obj target, Obj superObj, Obj name) {
    throw new Error("no slot found: " + target + ".get(" + name + ")  slots are\n" + send(target, "getSlots"));
  }

  public static Obj Obj_notEquals(Obj o1, Obj so, Obj o2) {
    return o1.equals(o2) ? theObjFalse : theObjTrue;
  }

  public static Obj Obj_println(Obj target, Obj superObj) {
    System.out.println(target);
    return target;
  }

  public static Obj Obj_save(Obj target, Obj superObj, Obj file) {
    try {
      System.out.println("[ saving " + file + "]");
      String fileName = file.toString();
      File f = new File(fileName);
      ObjectOutputStream out = new Serializer(new FileOutputStream(f));
      out.writeObject(target);
      out.close();
      System.out.println("[ written " + file + "]");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return target;
  }

  public static Obj Obj_startDaemons(Obj o, Obj so) {
    registers[DAEMONS]++;
    return o;
  }

  public static Obj Obj_stopDaemons(Obj o, Obj so) {
    registers[DAEMONS]--;
    return o;
  }

  public static Obj Obj_theSame(Obj o1, Obj so, Obj o2) {
    return o1 == o2 ? theObjTrue : theObjFalse;
  }

  public static ConcreteFun Op(ConcreteFun.F0 f, String... args) {
    return new ConcreteFun(f, args);
  }

  public static ConcreteFun Op(ConcreteFun.F1 f, String... args) {
    return new ConcreteFun(f, args);
  }

  public static ConcreteFun Op(ConcreteFun.F2 f, String... args) {
    return new ConcreteFun(f, args);
  }

  public static ConcreteFun Op(ConcreteFun.F3 f, String... args) {
    return new ConcreteFun(f, args);
  }

  public static ConcreteFun Op(ConcreteFun.F4 f, String... args) {
    return new ConcreteFun(f, args);
  }

  public static ConcreteFun Op(ConcreteFun.F5 f, String... args) {
    return new ConcreteFun(f, args);
  }

  public static ConcreteFun Op(Obj type, ConcreteFun.F0 f, String... args) {
    return new ConcreteFun(type, f, args);
  }

  public static ConcreteFun Op(Obj type, ConcreteFun.F1 f, String... args) {
    return new ConcreteFun(type, f, args);
  }

  public static ConcreteFun Op(Obj type, ConcreteFun.F2 f, String... args) {
    return new ConcreteFun(type, f, args);
  }

  public static ConcreteFun Op(Obj type, ConcreteFun.F3 f, String... args) {
    return new ConcreteFun(type, f, args);
  }

  public static ConcreteFun Op(Obj type, ConcreteFun.F4 f, String... args) {
    return new ConcreteFun(type, f, args);
  }

  public static ConcreteFun Op(Obj type, ConcreteFun.F5 f, String... args) {
    return new ConcreteFun(type, f, args);
  }

  public static ConcreteFun Op(String name, ConcreteFun.F0 f, String... args) {
    return new ConcreteFun(name, f, args);
  }

  public static ConcreteFun Op(String name, ConcreteFun.F1 f, String... args) {
    return new ConcreteFun(name, f, args);
  }

  public static ConcreteFun Op(String name, ConcreteFun.F2 f, String... args) {
    return new ConcreteFun(name, f, args);
  }

  public static ConcreteFun Op(String name, ConcreteFun.F3 f, String... args) {
    return new ConcreteFun(name, f, args);
  }

  public static ConcreteFun Op(String name, ConcreteFun.F4 f, String... args) {
    return new ConcreteFun(name, f, args);
  }

  public static ConcreteFun Op(String name, ConcreteFun.F5 f, String... args) {
    return new ConcreteFun(name, f, args);
  }

  public static Obj Or(Obj o1, Obj o2) {
    if (o1 == theObjTrue)
      return theObjTrue;
    else if (o2 == theObjTrue)
      return theObjTrue;
    else return theObjFalse;
  }

  public static Obj PackageController_deliver(int i, Vector<Obj> types, Obj originalType, Obj target, Obj message, Obj args) {

    // findOp is supplied with a list of types that have been constructed by flattening the
    // lattice via a depth first left-to right walk rooted at the type of the target. Find
    // and invoke the most-specific appropriate operation...

    if (i == types.size())
      return Classifier_deliver(0, Classifier_traverseFrom(originalType), originalType, target, message, args);
    else {
      Obj type = types.elementAt(i);
      int arity = Cons.length(args) + 2;
      Obj operation = Classifier_lookup(type, message, arity);
      if (operation != null) {
        Obj superClass = new ConcreteObj(Class, Slot("name", Str("<super@" + message + ">")), Slot("supers", Classifier_consFrom(i + 1, types)), Slot("operations", theObjNil));
        Obj superObj = new Obj() {

          private static final long serialVersionUID = 1L;

          public value.Obj get(String name) {
            return target.get(name);
          }

          public value.Obj of() {
            return superClass;
          }

          public value.Obj send(String name, value.Obj... args) {
            return Classifier_deliver(i + 1, types, originalType, target, Str(name), Cons.asList(0, args));
          }

          public value.Obj set(String name, value.Obj value) {
            return target.set(name, value);
          }

          public value.Obj slots() {
            return target.slots();
          }
        };
        if (operation instanceof ConcreteFun) sendCache.cache(originalType, message.toString(), arity, (ConcreteFun) operation, superObj);
        return debug.sendResult(send(operation, "invoke", Cons.asArray(cons(target, cons(superObj, args)))));
      } else return PackageController_deliver(i + 1, types, originalType, target, message, args);
    }
  }

  public static Obj PackageController_send(Obj type, Obj superObj, Obj target, Obj message, Obj args) {

    // A snapshot (and therefore a package) handles messages with respect to the inheritance hierarchy of
    // its meta-package followed by its type...

    debug.send(target, message, args);
    Obj p = get(target, "package");
    if (p == theObjNull)
      return Classifier_deliver(0, Classifier_traverseFrom(type), type, target, message, args);
    else return PackageController_deliver(0, Classifier_traverseFrom(p), type, target, message, args);
  }

  public static Obj read(String path) {
    try {
      System.out.println("[ reading " + path + " ]");
      File f = new File(path);
      ObjectInputStream in = new Inflater(new FileInputStream(f));
      Obj o = (Obj) in.readObject();
      in.close();
      System.out.println("[ read " + path + " ]");
      clearCaches();
      return o;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void register(Obj os, Obj ot) {
    // Used to create kernel elements that rely on earlier definitions.
    // Move the contents of the source into the target...
    if (os instanceof ConcreteObj && ot instanceof ConcreteObj) {
      ConcreteObj source = (ConcreteObj) os;
      ConcreteObj target = (ConcreteObj) ot;
      target.setType(source.getType());
      target.setSlots(source.getSlots());
    } else throw new Error("register expects concrete objects: " + os + " " + ot);
  }

  public static Obj removeShadowedAttributes(Obj atts) {
    return send(atts, "filter", Op(a1 -> send(send(atts, "exists", Op(a2 -> send(a2, "inherits", a1))), "not")));
  }

  public static Obj send(Obj target, String message, Obj... args) {
    if (target instanceof Kernel)
      return target.send(message, args);
    else if (target.of().of() != Class && target.of().of() != Package)
      return target.of().send("send", target, Str(message), asList(0, args));
    else return target.send(message, args);
  }

  public static Obj send$(Obj target, String message, Obj args) {

    // This is the equivalent of applying send where the args are already
    // packages up as a list...
    return target.send(message, Cons.asArray(args));
  }

  public static Obj set(Obj target, String name, Obj value) {
    if (target instanceof Kernel)
      return target.set(name, value);
    else if (target.of().of() != Class && target.of() != Package) {
      return target.of().send("set", target, Str(name), value);
    } else return target.set(name, value);
  }

  public static void setTypes() {
    set(send(Atomic, "getOp", Str("get")), "type", FunctionOf(list(Atomic, Obj, Atom, Str), Obj));
    set(send(Atomic, "getOp", Str("new")), "type", FunctionOf(list(Atomic, Obj, ListOf(Obj)), theObjTrue, Atomic));
    set(send(Atomic, "getOp", Str("set")), "type", FunctionOf(list(Atomic, Obj, Atom, Str, Obj), Atom));
    set(send(Attribute, "getOp", Str("asString")), "type", FunctionOf(list(Attribute, Obj), Str));
    set(send(Bool, "getOp", Str("asString")), "type", FunctionOf(list(Bool, Obj), Str));
    set(send(Bool, "getOp", Str("not")), "type", FunctionOf(list(Bool, Obj), Bool));
    set(send(Bool, "getOp", Str("and")), "type", FunctionOf(list(Bool, Obj, Bool), Bool));
    set(send(Bool, "getOp", Str("implies")), "type", FunctionOf(list(Bool, Obj, Bool), Bool));
    set(send(Bool, "getOp", Str("or")), "type", FunctionOf(list(Bool, Obj, Bool), Bool));
    set(send(Class, "getOp", Str("allAttributes")), "type", FunctionOf(list(Class, Obj), ListOf(Attribute)));
    set(send(Class, "getOp", Str("addConstructor")), "type", FunctionOf(list(Class, Obj, Function), Class));
    set(send(Class, "getOp", Str("new")), "type", FunctionOf(list(Class, Obj, ListOf(Obj)), theObjTrue, StaticSelf));
    set(send(Class, "getOp", Str("initObj")), "type", FunctionOf(list(Class, Obj, Obj, ListOf(Obj)), Obj));
    set(send(Classifier, "getOp", Str("isInstance")), "type", FunctionOf(list(Classifier, Obj, Obj), Bool));
    set(send(Classifier, "getOp", Str("new")), "type", FunctionOf(list(Classifier, Obj, ListOf(Obj)), theObjTrue, Classifier));
    set(send(Classifier, "getOp", Str("asString")), "type", FunctionOf(list(Classifier, Obj), Str));
    set(send(Classifier, "getOp", Str("inherits")), "type", FunctionOf(list(Classifier, Obj, Classifier), Bool));
    set(send(Classifier, "getOp", Str("getOp")), "type", FunctionOf(list(Classifier, Obj, Str), Function));
    set(send(ConsOf, "getOp", Str("isInstance")), "type", FunctionOf(list(ConsOf, Obj, Obj), Bool));
    set(send(Float, "getOp", Str("<")), "type", FunctionOf(list(Float, Obj, Number), Bool));
    set(send(Float, "getOp", Str(">")), "type", FunctionOf(list(Float, Obj, Number), Bool));
    set(send(Float, "getOp", Str("+")), "type", FunctionOf(list(Float, Obj, Number), Float));
    set(send(Float, "getOp", Str("asString")), "type", FunctionOf(list(Float, Obj), Str));
    set(send(Int, "getOp", Str("isCapital")), "type", FunctionOf(list(Int, Obj), Bool));
    set(send(Int, "getOp", Str("<")), "type", FunctionOf(list(Int, Obj, Number), Bool));
    set(send(Int, "getOp", Str(">")), "type", FunctionOf(list(Int, Obj, Number), Bool));
    set(send(Int, "getOp", Str("+")), "type", FunctionOf(list(Int, Obj, Number), Int));
    set(send(Int, "getOp", Str("asString")), "type", FunctionOf(list(Int, Obj), Str));
    set(send(Function, "getOp", Str("invoke")), "type", FunctionOf(list(Function, Obj, ListOf(Obj)), theObjTrue, Obj));
    set(send(ListOf, "getOp", Str("isInstance")), "type", FunctionOf(list(ListOf, Obj, Obj), Bool));
    set(send(ListOf(Obj), "getOp", Str("filter")), "type", FunctionOf(list(ListOf(Obj), Obj, FunctionOf(list(Obj), Bool)), ListOf(Obj)));
    set(send(ListOf(Obj), "getOp", Str("selectOne")), "type", FunctionOf(list(ListOf(Obj), Obj, FunctionOf(list(Obj), Bool)), Obj));
    set(send(ListOf(Obj), "getOp", Str("forall")), "type", FunctionOf(list(ListOf(Obj), Obj, FunctionOf(list(Obj), Bool)), Bool));
    set(send(ListOf(Obj), "getOp", Str("exists")), "type", FunctionOf(list(ListOf(Obj), Obj, FunctionOf(list(Obj), Bool)), Bool));
    set(send(ListOf(Obj), "getOp", Str("remove")), "type", FunctionOf(list(ListOf(Obj), Obj, Obj), ListOf(Obj)));
    set(send(ListOf(Obj), "getOp", Str("removeDuplicates")), "type", FunctionOf(list(ListOf(Obj), Obj), ListOf(Obj)));
    set(send(ListOf(Obj), "getOp", Str("length")), "type", FunctionOf(list(ListOf(Obj), Obj), Int));
    set(send(ListOf(Obj), "getOp", Str("isProperList")), "type", FunctionOf(list(ListOf(Obj), Obj), Bool));
    set(send(ListOf(Obj), "getOp", Str("contains")), "type", FunctionOf(list(ListOf(Obj), Obj, Obj), Bool));
    set(send(ListOf(Obj), "getOp", Str("cons")), "type", FunctionOf(list(ListOf(Obj), Obj, Obj), ListOf(Obj)));
    set(send(ListOf(Obj), "getOp", Str("append")), "type", FunctionOf(list(ListOf(Obj), Obj, ListOf(Obj)), ListOf(Obj)));
    set(send(NilOf, "getOp", Str("isInstance")), "type", FunctionOf(list(NilOf, Obj, Obj), Bool));
    set(send(Null, "getOp", Str("asString")), "type", FunctionOf(list(Null, Obj), Str));
    set(send(Obj, "getOp", Str("asString")), "type", FunctionOf(list(Obj, Obj), Str));
    set(send(Obj, "getOp", Str("stopDaemons")), "type", FunctionOf(list(Obj, Obj), Obj));
    set(send(Obj, "getOp", Str("startDaemons")), "type", FunctionOf(list(Obj, Obj), Obj));
    set(send(Obj, "getOp", Str("save")), "type", FunctionOf(list(Obj, Obj, Str), Obj));
    set(send(Obj, "getOp", Str("get")), "type", FunctionOf(list(Obj, Obj, Str), Obj));
    set(send(Obj, "getOp", Str("getSlots")), "type", FunctionOf(list(Obj, Obj), ListOf(Slot)));
    set(send(Obj, "getOp", Str("of")), "type", FunctionOf(list(Obj, Obj), Classifier));
    set(send(Obj, "getOp", Str("<>")), "type", FunctionOf(list(Obj, Obj, Obj), Bool));
    set(send(Obj, "getOp", Str("=")), "type", FunctionOf(list(Obj, Obj, Obj), Bool));
    set(send(Obj, "getOp", Str("==")), "type", FunctionOf(list(Obj, Obj, Obj), Bool));
    set(send(Obj, "getOp", Str("noSlotFound")), "type", FunctionOf(list(Obj, Obj, Str), Obj));
    set(send(Obj, "getOp", Str("noOperationFound")), "type", FunctionOf(list(Obj, Obj, Str, ListOf(Obj)), Obj));
    set(send(Package, "getOp", Str("asString")), "type", FunctionOf(list(Package, Obj), Str));
    set(send(Package, "getOp", Str("new")), "type", FunctionOf(list(Package, Obj, ListOf(Obj)), theObjTrue, Package));
    set(send(Str, "getOp", Str("asSeq")), "type", FunctionOf(list(Str, Obj), ListOf(Int)));
    set(send(Str, "getOp", Str("asString")), "type", FunctionOf(list(Str, Obj), Str));
    set(send(Walker, "getOp", Str("walk")), "type", FunctionOf(list(Walker, Obj, Obj), Obj));
    set(send(Walker, "getOp", Str("revisit")), "type", FunctionOf(list(Walker, Obj, Obj), Obj));
    set(send(Walker, "getOp", Str("reset")), "type", FunctionOf(list(Walker, Obj, Obj), Walker));
    set(send(Walker, "getOp", Str("asString")), "type", FunctionOf(list(Walker, Obj), Str));
    set(send(Walker, "getOp", Str("addHandler")), "type", FunctionOf(list(Walker, Obj, Function), Walker));
    // set(send(Walker, "getOp", Str("init")), "type", FunctionOf(list(Walker, Obj, ListOf(Obj)), Walker));
  }

  public static Slot Slot(String name, int value) {
    return new Slot(name, Int(value));
  }

  public static Slot Slot(String name, Obj value) {
    return new Slot(name, value);
  }

  public static Slot Slot(String name, String value) {
    return new Slot(name, Str(value));
  }

  public static Obj Snapshot_allObjectsCorrectlyTyped(Obj s) {
    return send(get(s, "objects"), "forall", Op((o) -> send(get(get(s, "package"), "objects"), "exists", Op((c) -> send(c, "isInstance", o)))));
  }

  public static Obj standardMOPGet(Obj target, String name) {
    ConcreteObj o = (ConcreteObj) target;
    return o.getSlot(name).getValue();
  }

  public static void startTypeChecking() {

    // The following constraint must not be applied until the cyclic structure is tied up...

    Obj.extendSlot("constraints", Op("allSlotValuesTypeCheck", (F1) value.Obj::Obj_allSlotValuesTypeCheck));
  }

  public static Obj Str(Obj metaType, String s) {
    return new Str(metaType, s);
  }

  public static Obj Str(String s) {
    return new Str(s);
  }

  public static Obj Str_asSeq(Obj str, Obj superObj) {
    Str s = (Str) str;
    Obj l = theObjNil;
    for (int i = s.getValue().length() - 1; i >= 0; i--) {
      l = cons(Int(s.getValue().charAt(i)), l);
    }
    return l;
  }

  public static Obj javaToObj(Object o) {
    if (o instanceof String) return Str((String) o);
    if (o instanceof Integer) return Int((Integer) o);
    throw new Error("unknown type of java object for conversion: " + o);
  }

  public static Obj Str_javaObj(Obj str, Obj superObj, Obj of) {
    try {
      java.lang.Class<?> _class = java.lang.Class.forName(str.toString());

      Object instance = _class.newInstance();
      return new Obj() {

        private static final long serialVersionUID = 2891962966390259912L;

        public value.Obj get(String name) {
          Obj accessors = of.get("accessors");
          System.out.println(name + " " + accessors);
          for (Obj a : iterate(accessors)) {
            System.out.println(a.get("name").toString().equals(name));
            if (a.get("name").toString().equals(name.toString())) {
              Obj methodName = a.get("accessor");
              System.out.println(methodName + " " + Arrays.toString(_class.getMethods()));
              for (Method method : _class.getMethods()) {
                if (method.getName().equals(methodName.toString())) {
                  try {
                    return javaToObj(method.invoke(instance));
                  } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                  }
                }
              }
            }
          }
          return theObjNull;
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
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      return theObjNull;
    }
  }

  public static Obj Str_add(Obj str, Obj superObj, Obj o) {
    return Str(str.toString() + o.toString());
  }

  public static Obj Str_asString(Obj str, Obj superObj) {
    return str;
  }

  public static Obj TableOf_init(Obj self, Obj superObj, Obj keyType, Obj valueType) {
    return set(set(set(set(self, "supers", list(Table)), "name", Str("<Tables: " + keyType + "->" + valueType + ">")), "keyType", keyType), "valueType", valueType);
  }

  public static Obj tail(Obj list) {
    return get(list, "tail");
  }

  public static String varDomainString(Obj domain) {
    String ts = "(";
    int length = Cons.length(domain);
    for (int i = 0; i < length; i++) {
      if (i == (length - 1)) {
        ts = ts + "&" + get(head(domain), "elementType");
      } else ts = ts + head(domain);
      domain = tail(domain);
      if (!isNil(domain)) ts = ts + ",";
    }
    ts = ts + ")";
    return ts;
  }

  public static Obj Walker_addWalker(Obj self, Obj superObj, Obj handler) {
    Obj type = get(handler, "type");
    Obj dispatchType = head(get(type, "domain"));
    send(get(self, "handlers"), "set", dispatchType, handler);
    return self;
  }

  public static Obj Walker_walk(Obj walker, Obj superObj, Obj o) {
    if (isTrue(send(get(walker, "visited"), "hasKey", o)))
      return send(walker, "revisit", o);
    else if (isTrue(send(get(walker, "limit"), "=", Int(0)))) {
      return send(walker, "atLimit", o);
    } else {
      send(walker, "decLimitCount");
      send(get(walker, "visited"), "set", o, o);
      Obj result = walker;
      Obj handlers = get(walker, "handlers");
      Obj types = cons(o.of(), send(o.of(), "allSupers"));
      boolean isHandled = false;
      for (Obj type : iterate(types)) {
        if (isTrue(send(handlers, "hasKey", type))) {
          isHandled = true;
          result = send(send(handlers, "get", type), "invoke", o, walker);
          break;
        }
      }
      if (!isHandled) {
        send(walker, "walk", o.of());
        for (Obj slot : iterate(o.slots())) {
          send(walker, "walk", get(slot, "value"));
        }
      }
      send(walker, "incLimitCount");
      return result;
    }
  }

  public Obj get(String name);

  public Obj of();

  public Obj send(String name, Obj... args);

  public Obj set(String name, Obj value);

  public Obj slots();

}
