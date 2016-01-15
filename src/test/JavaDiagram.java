package test;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaDiagram {

  public static void javaDiagram2() {
    GraphViz javaDiagram = new GraphViz();
    try {
      Class<?> c = Class.forName("java.lang.Class");
      Set<Object> visited = new HashSet<Object>();
      Set<Class<?>> omit = new HashSet<Class<?>>();
      Set<Package> packages = new HashSet<Package>();
      // omit.add(ClassLoader.class);
      // omit.add(AccessibleObject.class);
      // omit.add(Type.class);
      // omit.add(TypeVariable.class);
      // omit.add(AnnotatedType.class);
      // omit.add(Parameter.class);
      // omit.add(Class.forName("java.lang.ClassValue$ClassValueMap"));
      // omit.add(Class.forName("java.lang.Class$AnnotationData"));
      // omit.add(Class.forName("java.lang.Class$EnclosingMethodInfo"));
      packages.add(Package.getPackage("java.lang"));
      packages.add(Package.getPackage("java.lang.reflect"));
      packages.add(Package.getPackage("java.lang.annotation"));
      packages.add(null);
      walk(c, javaDiagram, visited, omit, packages);
      javaDiagram.graphViz("src/test/java.dot");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static void valueDiagram() {
    GraphViz valueDiagram = new GraphViz();
    addClass(value.Obj.class, valueDiagram, new String[] { "Serializable", "of", "slots", "get", "set", "send" });
    addClass(value.ConcreteObj.class, valueDiagram, new String[] { "Obj", "Serializable", "type" });
    addClass(value.Slot.class, valueDiagram, new String[] { "Obj", "name", "value" });
    addClass(value.Atom.class, valueDiagram, new String[] { "Obj", "type", "slots" });
    addClass(value.Int.class, valueDiagram, new String[] { "Atom", "value" });
    addClass(value.Str.class, valueDiagram, new String[] { "Atom", "value" });
    addClass(value.Bool.class, valueDiagram, new String[] { "Atom", "value" });
    addClass(value.Float.class, valueDiagram, new String[] { "Atom", "value" });
    addClass(value.ConcreteFun.class, valueDiagram, new String[] { "Obj", "Atom", "name", "arity", "args", "f", "isVarArgs" });
    addClass(value.ConcreteFun.F.class, valueDiagram, new String[] { "apply", "Obj", "Serializable" });
    addClass(value.ConcreteFun.F1.class, valueDiagram, new String[] { "apply", "Obj", "F" });
    addClass(Serializable.class, valueDiagram, new String[] {});
    valueDiagram.graphViz("src/test/value.dot");
  }

  public static void javaDiagram() {
    GraphViz javaDiagram = new GraphViz();
    addType(String.class, javaDiagram);
    addType(Number.class, javaDiagram);
    addType(Integer.class, javaDiagram);
    addType(Boolean.class, javaDiagram);
    addType(Executable.class, javaDiagram);
    addType(AccessibleObject.class, javaDiagram);
    addClass(Object.class, javaDiagram, "equals");
    addClass(Constructor.class, javaDiagram, "getName", "newInstance");
    addClass(Package.class, javaDiagram, "getName");
    addClass(Field.class, javaDiagram, "getName", "get", "set", "getType");
    addClass(Method.class, javaDiagram, "getName", "getParameterTypes", "getReturnType", "invoke");
    addClass(Class.class, javaDiagram, "getName", "getDeclaredMethods", "getDeclaredFields", "getPackage", "getSuperclass", "getDeclaredConstructors", "isInstance", "newInstance");
    javaDiagram.graphViz("src/test/java.dot");
  }

  private static void addClass(Class<?> c, GraphViz g, String... names) {
    String label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"2\">";
    label = getClassNameLabel(c, label);
    for (Field f : c.getDeclaredFields()) {
      String name = f.getName();
      if (Arrays.asList(names).contains(name)) {
        Class<?> type = f.getType();
        if (type.isArray()) {
          if (type.getComponentType() == Integer.TYPE || type.getComponentType() == Boolean.TYPE || type.getComponentType() == String.class || type.getComponentType() == Double.TYPE) {
            label = label + "<tr><td colspan=\"2\" bgcolor=\"mediumseagreen\">" + name + ":" + type.getSimpleName() + "</td></tr>";
          } else {
            g.addEdge(g.getId(c), g.getId(type.getComponentType()), "penwidth=\"2\"  arrowhead=vee label=\"*" + name + "\"");
          }
        } else {
          if (type == Integer.TYPE || type == Boolean.TYPE || type == String.class || type == Double.TYPE) {
            label = label + "<tr><td colspan=\"2\" bgcolor=\"mediumseagreen\">" + name + ":" + type.getSimpleName() + "</td></tr>";
          } else {
            g.addEdge(g.getId(c), g.getId(type), "penwidth=\"2\"  arrowhead=vee label=\"" + name + "\"");
          }
        }
      }
    }
    for (Method m : c.getDeclaredMethods()) {
      if (Arrays.asList(names).contains(m.getName())) {
        // if (isAccessor(m))
        // addAccessor(c, m, g);
        // else
        label = addMethod(m, label);
      }
    }
    label = label + "</table>>";
    g.addNode(g.getId(c), label, "shape=none");
    if (c.getSuperclass() != null && c.getSuperclass() != Object.class) g.addEdge(g.getId(c), g.getId(c.getSuperclass()), "penwidth=\"2\" arrowhead=empty");
    for (Class<?> i : c.getInterfaces()) {
      if (Arrays.asList(names).contains(i.getSimpleName())) g.addEdge(g.getId(c), g.getId(i), "penwidth=\"2\" style=dotted arrowhead=vee");
    }
  }

  private static Method getMethod(Class<?> c, String name) {
    for (Method m : c.getDeclaredMethods()) {
      if (m.getName().equals(name)) return m;
    }
    return null;
  }

  private static void addType(Class<?> c, GraphViz g) {
    String label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"2\">";
    label = getClassNameLabel(c, label);
    label = label + "</table>>";
    g.addNode(g.getId(c), label, "shape=none");
    if (c.getSuperclass() != null) g.addEdge(g.getId(c), g.getId(c.getSuperclass()), "penwidth=\"2\" arrowhead=empty");
  }

  public static String getClassNameLabel(Class<?> c, String label) {
    boolean isFinal = Modifier.isFinal(c.getModifiers());
    boolean isInterface = c.isInterface();
    String name = GraphViz.encodeHTML(c.getSimpleName());
    if (isFinal) name = name + "!";
    label =  label + "<tr><td bgcolor=\"tomato\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" + name + "</font></td></tr>";
    if(isInterface) label = label + "<tr><td bgcolor=\"tomato\" colspan=\"2\"> interface </td></tr>";
    return label;
  }

  public static String addMethod(Method m, String label) {
    if (Modifier.isAbstract(m.getModifiers())) label = label + "static ";
    return label + "<tr> <td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(m.getName()) + "</font></td><td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(getMethodTypeDisplay(m)) + "</font></td></tr>";
  }

  public static void addAccessor(Class<?> c, Method m, GraphViz g) {
    Class<?> type = m.getReturnType();
    String name = m.getName();
    if (type.isArray()) {
      type = type.getComponentType();
      name = "*" + name;
    }
    g.addEdge(g.getId(c), g.getId(type), "penwidth=\"2\" arrowhead=vee label=<<font face=\"INCONSOLATA\">" + name + "</font>>");
  }

  private static void walk(Class<?> c, GraphViz javaDiagram, Set<Object> visited, Set<Class<?>> omit, Set<Package> packages) {
    if (!visited.contains(c)) {
      visited.add(c);
      int id = javaDiagram.getId(c);
      String label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"2\"><tr><td bgcolor=\"tomato\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" + GraphViz.encodeHTML(c.getSimpleName()) + "</font></td></tr>";
      for (Field f : c.getDeclaredFields()) {
        String name = f.getName();
        Class<?> type = f.getType();
        if (type.isArray()) {
          type = type.getComponentType();
          name = name + "[]";
        }
        if (!Modifier.isStatic(f.getModifiers()) && !omit.contains(type)) {
          if (type == Integer.TYPE || type == String.class || type == Boolean.TYPE)
            label = label + "<tr> <td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name) + "</font></td><td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(type.getSimpleName()) + "</font></td></tr>";
          else if (packages.contains(type.getPackage())) {
            walk(type, javaDiagram, visited, omit, packages);
            javaDiagram.addEdge(id, javaDiagram.getId(type), "penwidth=\"2\" arrowhead=vee label=<<font face=\"INCONSOLATA\">" + name + "</font>>");
          } else System.out.println("ignoring field " + f);
        } else System.out.println("ignoring static field " + f);
      }
      for (Method m : c.getDeclaredMethods()) {
        String name = m.getName();
        Class<?> type = m.getReturnType();
        if (isAccessor(m)) {
          if (type.isArray()) {
            type = type.getComponentType();
            name = name + "[]";
          }
          if (type == Integer.TYPE || type == String.class || type == Boolean.TYPE)
            label = label + "<tr> <td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name) + "</font></td><td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(type.getSimpleName()) + "</font></td></tr>";
          else if (true) { // packages.contains(type.getPackage()) && !omit.contains(type)) {
            walk(type, javaDiagram, visited, omit, packages);
            javaDiagram.addEdge(id, javaDiagram.getId(type), "penwidth=\"2\" arrowhead=vee label=<<font face=\"INCONSOLATA\">" + name + "</font>>");
          }
        } else label = label + "<tr> <td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(m.getName()) + "</font></td><td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(getMethodTypeDisplay(m)) + "</font></td></tr>";
      }
      label = label + "</table>>";
      javaDiagram.addNode(id, label, "shape=none");
      if (c.getSuperclass() != null) {
        walk(c.getSuperclass(), javaDiagram, visited, omit, packages);
        javaDiagram.addEdge(id, javaDiagram.getId(c.getSuperclass()), "penwidth=\"2\" arrowhead=empty");
      }
    }
  }

  private static String getMethodTypeDisplay(Method m) {
    Class<?>[] argTypes = m.getParameterTypes();
    Class<?> type = m.getReturnType();
    String s = "(";
    for (int i = 0; i < argTypes.length; i++) {
      s = s + argTypes[i].getSimpleName();
      if (i + 1 < argTypes.length) s = s + ",";
    }
    s = s + ")->" + type.getSimpleName();
    return s;
  }

  private static boolean isAccessor(Method m) {
    return m.getParameterTypes().length == 0;
  }

}
