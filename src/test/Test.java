package test;

import static value.Cons.asList;
import static value.Cons.length;
import static value.Obj.*;

import java.util.Arrays;
import java.util.Hashtable;

import value.ConcreteFun.F3;
import value.ConcreteObj;
import value.Cons;
import value.Obj;

public class Test {

  static Hashtable<Obj, Obj> allAtts = new Hashtable<Obj, Obj>();

  public static Obj AType_allAttributes(Obj c, Obj so) {
    if (allAtts.containsKey(c)) return allAtts.get(c);
    Obj attributes = theObjNil;
    for (Obj n : iterate(value.Obj.get(c, "names"))) {
      Obj s = value.Obj.Attribute(n.toString(), get(c.of(), "type"));
      attributes = value.Obj.send(attributes, "cons", s);
    }
    allAtts.put(c, attributes);
    return attributes;
  }

  public static Obj AType_new(Obj c, Obj so, Obj args) {
    Obj[] storage = new Obj[length(get(c, "names"))];
    Obj type = get(c.of(), "type");
    for (int i = 0; i < storage.length; i++)
      storage[i] = get(type, "initial");
    return new Obj() {
      Obj slots = theObjNil;

      public Obj get(String name) {
        return storage[index(name)];
      }

      private int index(String name) {
        int i = 0;
        for (Obj n : iterate(value.Obj.get(c, "names")))
          if (n.toString().equals(name)) return i;
        return -1;
      }

      public Obj of() {
        return c;
      }

      public Obj send(String n, Obj... vs) {
        return Classifier_send(c, ConcreteObj.defaultSuperObj, this, Str(n), Cons.asList(0, vs));
      }

      public Obj set(String name, Obj value) {
        storage[index(name)] = value;
        return this;
      }

      public Obj slots() {
        if (slots == theObjNil) {
          for (Obj n : iterate(value.Obj.get(c, "names"))) {
            Obj s = value.Obj.send(Slot, "new", n, storage[index(n.toString())]);
            slots = value.Obj.send(slots, "cons", s);
          }
        }
        return slots;
      }
    };

  }

  private static Obj checkProductLineInstance(Obj o) {
    // A product line instance is correct if its choice of attributes is consistent with its features...
    Obj metaclass = get(o, "metaclass");
    if (metaclass != theObjNull) {
      Obj atts = get(o, "attributes");
      Obj metaAtts = send(metaclass, "allAttributes");
      Obj features = get(o, "features");
      return send(atts, "forall", Op(a -> send(metaAtts, "exists", Op(ma -> And(Equals(get(a, "name"), get(ma, "name")), (send(features, "contains", get(ma, "feature"))))))));
    } else return theObjTrue;
  }

  public static Obj DBClass() {
    // Create a new type of class that manages its instances as though they were
    // rows in a database...
    Obj DBClass = send(Class, "new", Str("DBClass"), list(), list(Class));
    Hashtable<Obj, Hashtable<Obj, Hashtable<String, Obj>>> tables = new Hashtable<Obj, Hashtable<Obj, Hashtable<String, Obj>>>();
    send(DBClass, "addOperation", DBClass_new(tables));
    send(DBClass, "addOperation", DBClass_get(tables));
    send(DBClass, "addOperation", DBClass_set(tables));
    send(DBClass, "addOperation", Op("slots", Test::DBClass_slots));
    return DBClass;
  }

  private static Obj DBClass_get(Hashtable<Obj, Hashtable<Obj, Hashtable<String, Obj>>> tables) {
    return Op("get", (self, so, target, name) ->
    {
      return tables.get(self).get(target).get(name.toString());
    });
  }

  private static Obj DBClass_new(Hashtable<Obj, Hashtable<Obj, Hashtable<String, Obj>>> tables) {
    Obj _new = Op("new", (self, so, args) ->
    {
      if (!tables.containsKey(self)) tables.put(self, new Hashtable<Obj, Hashtable<String, Obj>>());
      Hashtable<String, Obj> storage = new Hashtable<String, Obj>();
      Obj o = new Obj() {

        public value.Obj get(String name) {
          return storage.get(name);
        }

        public value.Obj of() {
          return self;
        }

        public value.Obj send(String name, value.Obj... args) {
          return Classifier_send(of(), ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
        }

        public value.Obj set(String name, value.Obj value) {
          storage.put(name, value);
          return this;
        }

        public value.Obj slots() {
          return DBClass_slots(self, null, this);
        }

        public String toString() {
          return storage.toString();
        }
      };
      tables.get(self).put(o, storage);
      for (Obj att : iterate(send(self, "allAttributes")))
        storage.put(get(att, "name").toString(), get(get(att, "type"), "initial"));
      send(self, "initObj", o, args);
      return o;
    });
    set(_new, "isVarArgs", theObjTrue);
    return _new;
  }

  private static Obj DBClass_set(Hashtable<Obj, Hashtable<Obj, Hashtable<String, Obj>>> tables) {
    return Op("set", (self, so, target, name, value) ->
    {
      System.out.println(target + " set " + name + " to " + value);
      tables.get(self).get(target).put(name.toString(), value);
      System.out.println(tables);
      return target;
    });
  }

  public static Obj DBClass_slots(Obj self, Obj so, Obj o) {
    Obj slots = theObjNil;
    for (Obj att : iterate(send(self, "allAttributes"))) {
      String name = get(att, "name").toString();
      slots = cons(Slot(name, get(o, name)), slots);
    }
    return slots;
  }

  public static Obj getGraphics() {
    Obj Graphics = send(Package, "new", Str("Graphics"));
    Obj Display = send(Class, "new", Str("Display"), list(), list(Obj));
    Obj Node = send(Class, "new", Str("Node"), list(send(Attribute, "new", Str("display"), Display)), list(Obj));
    Obj Edge = send(Class, "new", Str("Edge"), list(send(Attribute, "new", Str("source"), Node), send(Attribute, "new", Str("target"), Node)), list(Obj));
    Obj Graph = send(Class, "new", Str("Graph"), list(), list(ContainerOf("node", Node), ContainerOf("edge", Edge)));
    send(Graphics, "addObject", Graph);
    send(Graphics, "addObject", Node);
    send(Graphics, "addObject", Edge);
    send(Graphics, "addObject", Display);
    return Graphics;
  }

  public static Obj getProductLines() {
    Obj ProductLines = send(Package, "new", Str("ProductLines"));
    Obj feature = send(Attribute, "new", Str("feature"), Str);
    Obj PL_Attribute = send(Class, "new", Str("PL_Attribute"), list(feature), list(Attribute));
    send(PL_Attribute, "addConstructor", Op((self, superObj, name, type, f) -> set(set(set(self, "name", name), "type", type), "feature", f), "name", "type", "feature"));

    Obj PL_AttributeNameSpace = NameSpaceOf("attribute", PL_Attribute);
    Obj superAtt = send(send(Class, "allAttributes"), "selectOne", Op((x) -> send(get(x, "name"), "=", Str("attributes"))));
    set(send(PL_AttributeNameSpace, "getAttributeNamed", Str("attributes")), "supers", list(superAtt));
    set(PL_AttributeNameSpace, "supers", list(Class));
    Obj PL_Meta_Class = send(Class, "new", Str("PL_Meta_Class"), list(), list(PL_AttributeNameSpace));

    Obj features = send(Attribute, "new", Str("features"), ListOf(Str));
    Obj metaclass = send(Attribute, "new", Str("metaclass"), PL_Meta_Class);
    Obj PL_Class = send(Class, "new", Str("PL_Class"), list(metaclass, features), list(Class));
    send(PL_Class, "addConstraint", Op("ProductLineInstance", o -> checkProductLineInstance(o)));

    Obj PL_ClassNameSpace = NameSpaceOf("object", PL_Meta_Class);
    superAtt = send(send(Package, "allAttributes"), "selectOne", Op((x) -> send(get(x, "name"), "=", Str("objects"))));
    set(send(PL_ClassNameSpace, "getAttributeNamed", Str("objects")), "supers", list(superAtt));
    set(PL_ClassNameSpace, "supers", list(Package));
    Obj PL_Package = send(Class, "new", Str("PL_Package"), list(), list(PL_ClassNameSpace));

    send(ProductLines, "addObject", PL_Package);
    send(ProductLines, "addObject", PL_Meta_Class);
    send(ProductLines, "addObject", PL_Attribute);
    send(PL_Package, "addOperation", Op("new", productLineNew(PL_Class)));

    Obj PLM = send(PL_Package, "new", Str("PLM"));
    Obj a = send(PL_Attribute, "new", Str("a"), Str, Str("feature_x"));
    Obj b = send(PL_Attribute, "new", Str("b"), Str, Str("feature_y"));
    Obj Catalog = send(PL_Meta_Class, "new", Str("Catalog"), list(a, b), list(Obj));
    send(PLM, "addObject", Catalog);

    // Obj PL_1 = send(PLM, "new", list(Str("feature_x"), Str("feature_y")));
    Obj PL_1 = send(PLM, "new", list(Str("feature_y")));

    Diagrams.generateContentsDiagrams(ProductLines, send(send(send(get(Kernel, "objects"), "remove", Attribute), "remove", Class), "remove", Package));
    Diagrams.generateContentsDiagrams(PLM, get(Kernel, "objects"));
    Diagrams.generateContentsDiagrams(PL_1, Str("PL_1"), get(Kernel, "objects"));

    return ProductLines;
  }

  public static Obj Library_borrow(Obj Borrows) {
    return Op("borrow", (library, so, readerName, bookName) ->
    {
      Obj borrows = send(Borrows, "new", send(library, "getReaderNamed", readerName), send(library, "getBookNamed", bookName));
      send(library, "removeBookNamed", bookName);
      send(library, "removeReaderNamed", readerName);
      send(library, "addBorrow", borrows);
      return library;
    });
  }

  public static void main(String[] args) {

    // This is the entry point for tests. Some of these are very experimental...

    // Create some diagrams that show how Java is organized. These are intended to
    // be used to compare the meta-model of Java with that of JMF...

    JavaDiagram.javaDiagram();
    JavaDiagram.valueDiagram();

    // Let's see how long things take...

    long t0 = System.currentTimeMillis();

    // Must call this before anything else because it creates the reflexive kernel...

    Obj kernel = getKernel();
    // Diagrams.generatePackageDiagrams(kernel);

    // The following are various tests...

    testHashTables();
    testContainers();
    // THIS DOES NOT CURRENTLY WORK! testErrorHandling();

    // testSimpleClass is useful to look at to see how a very simple class
    // is created and used...

    testSimpleClass();
    testFlatten();
    testListOperations();
    testAtomics();
    testVarArgs();
    testContainerOf();
    testSmallFixed();
    testArray();
    testSparse();

    // This example shows how meta-object protocols can be used to change the
    // storage of objects. The language used to create a library model is the
    // same, but the MOP is implemented differently by Class and by DBClass...

    testLibrary(Class);
    testLibrary(DBClass());

    // create a package that represents graphics...

    testGraphics();

    getProductLines();

    System.out.println(send(Class, "getAllAttributes"));
    long t1 = System.currentTimeMillis();
    System.out.println("[ Tests performed in " + (t1 - t0) + " ms ]");

    // Serialization and inflating...

    send(kernel, "save", Str("src/test/kernel.o"));
    // Reading needs to match up the [T] classes with the caches...
    // System.out.println(read("src/test/kernel.o"));

    // Generate 'dot' files for the kernel so that they can be displayed using
    // GraphViz...

    Diagrams.generatePackageDiagrams(kernel);

    System.out.println(send(Kernel, "test"));
    System.out.println(send(Kernel, "getBindingNamed", Str("nil")));
  }

  public static Obj print(Obj o) {
    System.out.println(o);
    return o;
  }

  public static Obj processArgs(Obj pp, Obj names, Obj values) {
    if (length(names) == length(values)) {
      return pp;
    } else throw new Error("illegal number of arguments supplied");
  }

  public static F3 productLineNew(Obj PL_Class) {
    return (self, superObj, features) ->
    {
      Obj p = send(Package, "new", Str(get(self, "name").toString() + features));
      for (Obj mc : iterate(get(self, "objects"))) {
        Obj atts = list();
        for (Obj a : iterate(send(mc, "allAttributes"))) {
          if (isTrue(send(features, "contains", get(a, "feature")))) atts = cons(Attribute(get(a, "name").toString(), get(a, "type")), atts);
        }
        Obj c = send(PL_Class, "new", get(mc, "name"), atts, get(mc, "supers"));
        set(c, "features", features);
        set(c, "metaclass", mc);
        send(p, "addObject", c);
      }
      return p;
    };
  }

  public static Obj SmallFixed_new(Obj c, Obj so, Obj args) {
    Obj atts = send(c, "allAttributes");
    Obj[] values = new Obj[Cons.length(atts)];
    String[] names = new String[Cons.length(atts)];
    for (int i = 0; i < values.length; i++) {
      values[i] = get(get(get(atts, "head"), "type"), "initial");
      names[i] = get(get(atts, "head"), "name").toString();
      atts = get(atts, "tail");
    }
    return new Obj() {

      Obj slots = null;

      public Obj get(String name) {
        for (int i = 0; i < names.length; i++)
          if (names[i].equals(name)) return values[i];
        return this.send("noSlotNamed", Str(name));
      }

      public Obj of() {
        return c;
      }

      public Obj send(String name, value.Obj... args) {
        return Classifier_send(c, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
      }

      public Obj set(String name, value.Obj value) {
        for (int i = 0; i < names.length; i++)
          if (names[i].equals(name)) {
            values[i] = value;
            return this;
          }
        return this.send("noSlotNamed", Str(name));
      }

      public value.Obj slots() {
        if (slots == null) {
          slots = theObjNil;
          for (int i = 0; i < names.length; i++)
            slots = value.Obj.send(slots, "cons", value.Obj.send(Slot, "new", Str(names[i]), values[i]));
        }
        return slots;
      }

      public String toString() {
        return "(" + c + ")" + Arrays.toString(values);
      }

    };
  }

  public static void testArray() {

    // Classes implemented as arrays (see paper)...

    System.out.println("Test array");

    Obj AType = send(Class, "new", Str("AType"), list(Attribute("names", ListOf(Str))), list(Class));
    send(AType, "addConstructor", Op((o, so, n, ns) -> set(set(set(set(o, "name", n), "attributes", theObjNil), "supers", list(Obj)), "names", ns), "name", "names"));
    send(AType, "addOperation", Op("allAttributes", Test::AType_allAttributes));

    Obj Array = send(Class, "new", Str("Array"), list(Attribute("type", Classifier)), list(Class));
    send(AType, "addOperation", Op("new", Test::AType_new));
    set(send(AType, "getOperationNamed", Str("new")), "isVarArgs", theObjTrue);
    send(Array, "addConstructor", Op((o, so, t) -> set(set(set(set(o, "name", Str("Array(" + t + ")")), "attributes", theObjNil), "supers", list(AType)), "type", t), "type"));
    Obj ArrayOfInt = send(Array, "new", Int);
    Obj Point = send(ArrayOfInt, "new", Str("Point"), list(Str("x"), Str("y")));
    Obj p = send(Point, "new");
    System.out.println(p);
    System.out.println(p.of().of());
    System.out.println(get(p, "x"));
    set(p, "x", Int(100));
    System.out.println(get(p, "x"));
    Diagrams.generateTypeDiagramForElements("Array", list(p, Point, ArrayOfInt, Class), send(send(send(get(Kernel, "objects"), "remove", Int), "remove", Class), "remove", Classifier));
  }

  public static void testSparse() {

    // Classes implemented as arrays (see paper)...

    System.out.println("Test sparse");

    Obj Sparse = send(Class, "new", Str("Sparse"), list(), list(Class));
    send(Sparse, "addOperation", Op("new", Test::Sparse_new));
    set(send(Sparse, "getOperationNamed", Str("new")), "isVarArgs", theObjTrue);
    Obj Employee = send(Sparse, "new", Str("Employee"), list(Attribute("name", Str), Attribute("age", Int)), list(Obj));
    Obj p = send(Employee, "new");
    System.out.println(p);
    set(p, "name", Str("Fred"));
    // set(p, "age", Int(100));
    Diagrams.generateTypeDiagramForElements("Sparse", list(p, Employee, Sparse, Class), send(send(send(send(cons(ListOf(Function), get(Kernel, "objects")), "remove", Int), "remove", Class), "remove", Classifier), "remove", Str));
  }

  public static Obj Sparse_new(Obj c, Obj so, Obj args) {
    Hashtable<String, Obj> storage = new Hashtable<String, Obj>();
    return new Obj() {
      Obj slots = theObjNil;

      public Obj get(String n) {
        return storage.get(n);
      }

      public Obj of() {
        return c;
      }

      public Obj send(String name, value.Obj... args) {
        return Classifier_send(c, ConcreteObj.defaultSuperObj, this, Str(name), asList(0, args));
      }

      public Obj set(String name, Obj value) {
        storage.put(name, value);
        return this;
      }

      public Obj slots() {
        if (slots == theObjNil) for (String n : storage.keySet()) {
          Obj s = Slot(n, storage.get(n));
          slots = value.Obj.send(slots, "cons", s);
        }
        return slots;
      }
    };
  }

  public static void testAtomics() {
    Obj Range = send(Atomic, "new", Str("Range"), list(Attribute("low", Int), Attribute("high", Int)), list(Int));
    send(Range, "addOperation", Op("asString", (o, so) -> Str(get(o, "value") + " in [" + get(o, "low") + "," + get(o, "high") + "]")));
    send(Range, "addConstructor", Op((self, superOp, value, low, high) -> set(set(set(self, "value", value), "low", low), "high", high), "value", "low", "high"));
    send(Range, "addConstraint", Op("valueInRange", (o) -> send(send(get(o, "low"), "<", get(o, "value")), "and", send(get(o, "high"), ">", get(o, "value")))));
    Obj r = send(Range, "new", Int(100), Int(50), Int(150));
    System.out.println("range type = " + r.of() + " " + r.of().of());
    System.out.println("range = " + r);
    set(r, "value", Int(99));
    System.out.println("range = " + send(r, "asString"));

    Obj Caps = send(Atomic, "new", Str("Caps"), list(), list(Str));
    send(Caps, "addConstraint", Op("startsWithCap", (s) -> send(head(send(s, "asSeq")), "isCapital")));
    Obj c = send(Caps, "new", Str("Xxx"));
    System.out.println(c);
  }

  public static void testContainerOf() {
    // Obj Test = send(theClassClass, "new", list(Str("Test"), theObjNil, list(send(theClassContainerOf, "new", list(Str("x"), theClassAttribute)))));
    Obj Test = send(Class, "new", Str("Test"), theObjNil, list(ContainerOf("x", Attribute)));
    Obj test = send(Test, "new");
    System.out.println(test);
    Obj a = Attribute("a", Int);
    send(test, "addX", a);
    System.out.println(test);
    System.out.println(send(test, "hasX", a));
    send(test, "removeX", a);
    System.out.println(send(test, "hasX", a));
    System.out.println(test);
  }

  private static void testContainers() {
    System.out.println(send(Kernel, "getObjectNamed", Str("Attribute")));
    System.out.println(send(Class, "allSupers"));
    System.out.println(send(Class, "getSuperNamed", Str("Classifier")));
    System.out.println(send(Class, "hasSuperNamed", Str("Obj")));
    System.out.println(send(Classifier, "getAllOperations"));
  }

  public static void testDB() {
    // Create a new type of class that manages its instances as though they were
    // rows in a database...
    Obj DBClass = send(Class, "new", Str("DBClass"), list(), list(Class));
    Hashtable<Obj, Hashtable<Obj, Hashtable<String, Obj>>> tables = new Hashtable<Obj, Hashtable<Obj, Hashtable<String, Obj>>>();
    send(DBClass, "addOperation", DBClass_new(tables));
    send(DBClass, "addOperation", DBClass_get(tables));
    send(DBClass, "addOperation", DBClass_set(tables));

    Obj Book = send(DBClass, "new", Str("Book"), list(), list(NamedElement));
    Obj Reader = send(DBClass, "new", Str("Reader"), list(), list(NamedElement));
    Obj Borrows = send(DBClass, "new", Str("Borrows"), list(send(Attribute, "new", Str("book"), Book), send(Attribute, "new", Str("reader"), Reader)), list(Obj));
    Obj Library = send(DBClass, "new", Str("Library"), list(), list(NameSpaceOf("book", Book), NameSpaceOf("reader", Reader), ContainerOf("borrow", Borrows)));
    send(Library, "addOperation", Library_borrow(Borrows));
    send(Reader, "addConstructor", Op((self, so, name) -> set(self, "name", name), "name"));
    send(Book, "addConstructor", Op((self, so, name) -> set(self, "name", name), "name"));
    send(Borrows, "addConstructor", Op((self, so, reader, book) -> set(set(self, "book", debug(book)), "reader", debug(reader)), "reader", "book"));

    Obj aLibrary = send(Library, "new");
    for (int i = 0; i < 3; i++) {
      Obj aReader = send(Reader, "new", Str("R" + i));
      Obj aBook = send(Book, "new", Str("B" + i));
      send(aLibrary, "addReader", aReader);
      send(aLibrary, "addBook", aBook);
    }

    System.out.println(tables);
    send(aLibrary, "borrow", Str("R2"), Str("B1"));
    System.out.println(tables);

  }

  public static void testErrorHandling() {
    // Obj SimpleClass = send(theClassClass, "new", list(Str("SimpleClass"), list(Attribute("x", theClassObj)), list(theClassObj)));
    // send(theClassClass, "new", list(Str("IllegalClass"), list(Attribute("x", theClassObj)), list(SimpleClass)));
    // send(theClassClass, "new", list(Str("IllegalParents"), list(Attribute("x", theClassObj)), list()));
    // send(send(theClassClass, "new", list(Str("CheckCnstrs"), list(Attribute("X",theClassInt),Attribute("Y",theClassInt)), list(theClassObj))), "addConstructor",
    // send(theClassConstructor, "new", list(Str("X"), Str("Y"))));
    // set(send(Function, "getOp", Str("invoke")), "type", Int(0));
    set(Class, "attributes", theObjNil);
  }

  public static void testFlatten() {
    // Test flattening inheritance hierarchies....
    Obj A = send(Class, "new", Str("A"), list(), list(Obj));
    Obj B = send(Class, "new", Str("B"), list(), list(Obj));
    Obj C = send(Class, "new", Str("C"), list(), list(Obj));
    Obj D = send(Class, "new", Str("D"), list(), list(A));
    Obj E = send(Class, "new", Str("E"), list(), list(A, B));
    Obj F = send(Class, "new", Str("F"), list(), list(B, C));
    Obj G = send(Class, "new", Str("G"), list(), list(C));
    Obj H = send(Class, "new", Str("H"), list(), list(D, E, F, G));
    System.out.println("FLATTEN: " + send(H, "flatten"));
  }

  public static void testGraphics() {
    Diagrams.generateContentsDiagrams(getGraphics(), get(Kernel, "objects"));

  }

  public static void testHashTables() {
    Obj t = send(TableOf, "new", Str, Int);
    Obj tt = send(t, "new");
    System.out.println(tt.of().send("getAllOperations"));
    set(tt, "X", Int(100));
    System.out.println(get(tt, "X"));
    send(tt, "set", Str("X"), Int(101));
    System.out.println(send(tt, "get", Str("X")));
    System.out.println(send(tt, "asString"));
  }

  public static void testLibrary(Obj metaclass) {
    Obj LibraryPackage = send(Package, "new", Str(metaclass + "Library"));
    Obj Book = send(metaclass, "new", Str("Book"), list(), list(NamedElement));
    Obj Reader = send(metaclass, "new", Str("Reader"), list(), list(NamedElement));
    Obj Borrows = send(metaclass, "new", Str("Borrows"), list(send(Attribute, "new", Str("book"), Book), send(Attribute, "new", Str("reader"), Reader)), list(Obj));
    Obj Library = send(metaclass, "new", Str("Library"), list(), list(NameSpaceOf("book", Book), NameSpaceOf("reader", Reader), ContainerOf("borrow", Borrows)));
    send(Reader, "addConstructor", Op((self, so, name) -> set(self, "name", name), "name"));
    send(Book, "addConstructor", Op((self, so, name) -> set(self, "name", name), "name"));
    send(Borrows, "addConstructor", Op((self, so, reader, book) -> set(set(self, "book", book), "reader", reader), "reader", "book"));
    send(LibraryPackage, "addObjects", list(Reader, Book, Borrows, Library));
    send(Library, "addOperation", Library_borrow(Borrows));
    Diagrams.generateContentsDiagrams(LibraryPackage, send(get(Kernel, "objects"), "remove", NamedElement));
    Obj libSnapshot = send(LibraryPackage, "new");
    Obj aLibrary = send(send(LibraryPackage, "getObjectNamed", Str("Library")), "new");
    send(libSnapshot, "addObject", aLibrary);
    for (int i = 0; i < 3; i++) {
      Obj aReader = send(send(LibraryPackage, "getObjectNamed", Str("Reader")), "new", Str("R" + i));
      Obj aBook = send(send(LibraryPackage, "getObjectNamed", Str("Book")), "new", Str("B" + i));
      send(libSnapshot, "addObject", aReader);
      send(libSnapshot, "addObject", aBook);
      send(aLibrary, "addReader", aReader);
      send(aLibrary, "addBook", aBook);
    }
    Diagrams.generateSnapshotContentsDiagram(libSnapshot, metaclass + "_lib1", get(Kernel, "objects"));
    send(aLibrary, "borrow", Str("R2"), Str("B1"));
    Diagrams.generateSnapshotContentsDiagram(libSnapshot, metaclass + "_lib2", get(Kernel, "objects"));
  }

  public static void testListOperations() {

    System.out.println(send(get(Kernel, "objects"), "forall", Op((c) -> send(Class, "isInstance", print(c)))));
  }

  public static void testPackages() {
    System.out.println(send(Kernel, "new"));
  }

  public static void testSimpleClass() {

    // How to construct a very simple class that implements 2d points...

    // Create the attributes for (x,y)...

    Obj x = send(Attribute, "new", Str("x"), Int);
    Obj y = send(Attribute, "new", Str("y"), Int);

    // Create the class. Note that you create a class by sending Class a "new" message.
    // The arguments in the "new" message are:
    // (1) Name of class as an instance of Str.
    // (2) A list of attributes.
    // (3) A list of the super classes.

    Obj Point = send(Class, "new", Str("Point"), list(x, y), list(Obj));

    // Print out the class using the default "asString" operation...

    System.out.println(send(Class, "allAttributes"));
    System.out.println(Point.slots());

    // Daemons can be added to any object and fire when any slot of the object
    // changes. The daemon is an operation supplied with the object, the name
    // of the slot, the new value and the old value...

    extend(Point, "daemons", Op((o, slot, newValue, oldValue) ->
    {
      // System.out.println("Fire: " + o + "." + slot + " " + oldValue + " => " + newValue);
      return o;
    }));

    // Add some operations to the new class. Note that 'extend' is used to update
    // a list valued slot...

    extend(Point, "operations", Op("reset", (self, so) -> set(set(self, "y", Int(0)), "x", Int(0))));
    extend(Point, "operations", Op("inc", (self, so) -> set(set(self, "y", send(get(self, "y"), "+", Int(1))), "x", send(get(self, "x"), "+", Int(1)))));
    extend(Point, "operations", Op("asString", (self, so) -> Str("(" + get(self, "x") + "," + get(self, "y") + ")")));
    extend(Point, "operations", Op("distance", (self, so, p) ->
    {
      Obj dx = send(get(self, "x"), "-", get(p, "x"));
      Obj dxdx = send(dx, "*", dx);
      Obj dy = send(get(self, "y"), "-", get(p, "y"));
      Obj dydy = send(dy, "*", dy);
      return send(send(dxdx, "+", dydy), "sqrt");
    }));

    // Constructors are functions attached to a class that are used in the same way as Java...

    extend(Point, "constructors", Op((self, superObj, xv, yv) -> set(set(self, "x", xv), "y", yv), "x", "y"));

    // Constraints are attached to a class and run when any instance of the class
    // changes. They are like daemons except that the constraint must be a predicate that
    // always returns true. If it ever returns false then an error is raised...

    extend(Point, "constraints", Op("maxVal", (point) -> send(get(point, "x"), "<", Int(1000))));

    // Create a new instance of the class Point...

    Obj p = send(Point, "new", Int(10), Int(20));
    System.out.println(p);

    // Send the new point a message...

    send(p, "reset");
    System.out.println(p);

    // Send the new point lots of messages...

    for (int i = 0; i < 999; i++)
      send(p, "inc");
    System.out.println(p);

    // Compare two points...
    Obj p1 = send(Point, "new", Int(10), Int(10));
    Obj p2 = send(Point, "new", Int(5), Int(5));
    System.out.println(send(p1, "distance", p2));
  }

  public static void testSmallFixed() {

    // Classes implemented as arrays...

    System.out.println("Test small fixed");

    Obj SmallFixed = send(Class, "new", Str("SmallFixed"), list(), list(Class));
    send(SmallFixed, "addOperation", Op("new", Test::SmallFixed_new));
    Obj Point = send(SmallFixed, "new", Str("Point"), list(send(Attribute, "new", Str("x"), Int), send(Attribute, "new", Str("y"), Int)), list(Obj));
    Obj p = send(Point, "new", list());
    System.out.println(p);
    set(p, "x", Int(100));
    set(p, "y", Int(200));
    System.out.println(p);
  }

  public static void testStandardLibrary() {
    Obj LibraryPackage = send(Package, "new", Str("Library"));
    Obj Book = send(Class, "new", Str("Book"), list(), list(NamedElement));
    Obj Reader = send(Class, "new", Str("Reader"), list(), list(NamedElement));
    Obj Borrows = send(Class, "new", Str("Borrows"), list(send(Attribute, "new", Str("book"), Book), send(Attribute, "new", Str("reader"), Reader)), list(Obj));
    Obj Library = send(Class, "new", Str("Library"), list(), list(NameSpaceOf("book", Book), NameSpaceOf("reader", Reader), ContainerOf("borrow", Borrows)));
    send(Reader, "addConstructor", Op((self, so, name) -> set(self, "name", name), "name"));
    send(Book, "addConstructor", Op((self, so, name) -> set(self, "name", name), "name"));
    send(Borrows, "addConstructor", Op((self, so, reader, book) -> set(set(self, "book", book), "reader", reader), "reader", "book"));
    send(LibraryPackage, "addObject", Reader);
    send(LibraryPackage, "addObject", Book);
    send(LibraryPackage, "addObject", Borrows);
    send(LibraryPackage, "addObject", Library);
    send(Library, "addOperation", Library_borrow(Borrows));
    Diagrams.generateContentsDiagrams(LibraryPackage, send(get(Kernel, "objects"), "remove", NamedElement));
    Obj libSnapshot = send(LibraryPackage, "new");
    Obj aLibrary = send(send(LibraryPackage, "getObjectNamed", Str("Library")), "new");
    send(libSnapshot, "addObject", aLibrary);
    for (int i = 0; i < 3; i++) {
      Obj aReader = send(send(LibraryPackage, "getObjectNamed", Str("Reader")), "new", Str("R" + i));
      Obj aBook = send(send(LibraryPackage, "getObjectNamed", Str("Book")), "new", Str("B" + i));
      send(libSnapshot, "addObject", aReader);
      send(libSnapshot, "addObject", aBook);
      send(aLibrary, "addReader", aReader);
      send(aLibrary, "addBook", aBook);
    }
    Diagrams.generateSnapshotContentsDiagram(libSnapshot, "lib1", get(Kernel, "objects"));
    send(aLibrary, "borrow", Str("R2"), Str("B1"));
    Diagrams.generateSnapshotContentsDiagram(libSnapshot, "lib2", get(Kernel, "objects"));
  }

  public static void testVarArgs() {
    Obj f1 = Op((l) -> send(l, "asString"));
    Obj f2 = Op((l1, l2) -> send(cons(l1, l2), "asString"));
    Obj f3 = Op((l1, l2, l3) -> send(cons(l1, (cons(l2, l3))), "asString"));
    set(f1, "isVarArgs", theObjTrue);
    set(f2, "isVarArgs", theObjTrue);
    set(f3, "isVarArgs", theObjTrue);
    System.out.println("Var args: " + send(f1, "invoke"));
    System.out.println("Var args: " + send(f1, "invoke", Int(1), Int(2)));
    // System.out.println("Var args: " + send(f2, "invoke"));
    System.out.println("Var args: " + send(f2, "invoke", Int(1)));
    System.out.println("Var args: " + send(f2, "invoke", Int(1), Int(2)));
    // System.out.println("Var args: " + send(f3, "invoke", Int(1)));
    System.out.println("Var args: " + send(f3, "invoke", Int(1), Int(2)));
    System.out.println("Var args: " + send(f3, "invoke", Int(1), Int(2), Int(3)));
    Obj c = send(Class, "new", Str("X"), list(), list(Obj));
    send(c, "addOperation", Op("m", (self, superObj, x) -> x));
    send(c, "addOperation", Op("n", (self, superObj, x, y) -> cons(x, y)));
    set(send(c, "getOperationNamed", Str("m")), "isVarArgs", theObjTrue);
    set(send(c, "getOperationNamed", Str("n")), "isVarArgs", theObjTrue);
    Obj o = send(c, "new");
    System.out.println("varArgs: " + send(o, "m"));
    System.out.println("varArgs: " + send(o, "m", Int(1)));
    System.out.println("varArgs: " + send(o, "m", Int(1), Int(2)));
    // System.out.println("varArgs: " + send(o, "n"));
    System.out.println("varArgs: " + send(o, "n", Int(1)));
    System.out.println("varArgs: " + send(o, "n", Int(1), Int(2)));
  }
}
