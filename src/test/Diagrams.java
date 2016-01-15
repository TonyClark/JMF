package test;

import static value.Cons.isProperList;
import static value.Obj.*;

import java.util.Vector;

import value.Obj;

public class Diagrams {

  static Obj      showOpsInit     = Op((o, a) -> set(o, "showOps", theObjFalse));
  static Obj      showCnstrsInit  = Op((o, a) -> set(o, "showCnstrs", theObjFalse));
  static Obj      showConInit     = Op((o, a) -> set(o, "showCon", theObjFalse));
  static Obj      showDaemonsInit = Op((o, a) -> set(o, "showDaemons", theObjFalse));
  static Obj      TypeWalker      = send(Class, "new", Str("TypeWalker"), list(Attribute("showOps", Bool, showOpsInit), Attribute("showCnstrs", Bool, showCnstrsInit), Attribute("showCon", Bool, showConInit), Attribute("showDaemons", Bool, showDaemonsInit)), list(Walker));
  static Obj      typeWalker      = send(TypeWalker, "new", theObjNil);
  static Obj      objWalker       = send(Walker, "new", theObjNil);
  static Obj      snapshotWalker  = send(Walker, "new", theObjNil);
  static GraphViz diagram;

  static {

    send(snapshotWalker, "addHandler", Op(FunctionOf(list(Obj, Walker), Obj), (o, walker) ->
    {
      String label = "";
      if (isInt(o) || isBool(o) || isStr(o))
        label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\"><tr><td bgcolor=\"violet\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" + o + ":" + get(o.of(), "name") + "</font></td></tr>";
      else label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\"><tr><td bgcolor=\"violet\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\"> :" + get(o.of(), "name") + "</font></td></tr>";
      for (Obj slot : iterate(send(o, "getSlots"))) {
        Obj name = get(slot, "name");
        Obj value = get(slot, "value");
        if (isInt(value) || isBool(value) || isStr(value) || isNil(value) || isNull(value)) {
          label = label + "<tr> <td bgcolor=\"yellowgreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name.toString()) + "</font></td><td bgcolor=\"yellowgreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(value.toString()) + "</font></td></tr>";
        } else if (isProperList(value)) {
          if (isTrue(send(value, "forall", Op((x) -> send(Atom, "isInstance", x))))) {
            // Draw a box containing the elements...
            String listLabel = "<<table BORDER=\"0\" CELLSPACING=\"0\" CELLBORDER=\"1\" CELLPADDING=\"0\">";
            for (Obj element : iterate(value)) {
              listLabel = listLabel + "<tr><td><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(element.toString()) + "</font></td></tr>";
            }
            listLabel = listLabel + "</table>>";
            diagram.addNode(diagram.getId(value), listLabel, "shape=none");
            diagram.addEdge(diagram.getId(o), diagram.getId(value), "label=<" + name + ">");
          } else {
            for (Obj element : iterate(value)) {
              if (isFalse(send(get(walker, "excluded"), "contains", element))) {
                send(walker, "walk", element);
                diagram.addEdge(diagram.getId(o), diagram.getId(element), "label=<" + name + ">");
              }
            }
          }
        } else if (isFalse(send(get(walker, "excluded"), "contains", value))) {
          send(walker, "walk", value);
          diagram.addEdge(diagram.getId(o), diagram.getId(value), "penwidth=\"2\" arrowhead=vee label=" + name);
        }
      }
      label = label + "</table>>";
      diagram.addNode(diagram.getId(o), label, "shape=none");
      return o;
    }));

    send(snapshotWalker, "addHandler", Op(FunctionOf(list(Function, Walker), Obj), (o, walker) ->
    {
      String label = "<<font face=\"INCONSOLATA\" point-size=\"15\">" + GraphViz.encodeHTML(o.toString()) + "</font>>";
      diagram.addNode(diagram.getId(o), label, "shape=none");
      return o;
    }));

    send(objWalker, "addHandler", Op(FunctionOf(list(Obj, Walker), Obj), (o, walker) ->
    {
      diagram.addNode(diagram.getId(o), "", "style=filled,fillcolor=black");
      if (isFalse(send(get(walker, "excluded"), "contains", o.of()))) {
        diagram.addEdge(diagram.getId(o), diagram.getId(o.of()), "penwidth=\"2\" style=dashed arrowhead=vee");
        send(walker, "walk", o.of());
      }
      for (Obj slot : iterate(send(o, "getSlots"))) {
        if (isFalse(send(get(walker, "excluded"), "contains", get(slot, "value")))) {
          send(walker, "walk", get(slot, "value"));
          diagram.addEdge(diagram.getId(o), diagram.getId(get(slot, "value")), "penwidth=\"2\" arrowhead=vee label=" + get(slot, "name"));
        }
      }
      return o;
    }));

    send(objWalker, "addHandler", Op(FunctionOf(list(ListOf(Obj), Walker), Obj), (o, walker) ->
    {
      if (isProperList(o)) {
        diagram.addNode(diagram.getId(o), "\"\"", "shape=square");// "style=filled,fillcolor=\"#D8BFD8\"");
        int i = 1;
        for (Obj element : iterate(o)) {
          send(walker, "walk", element);
          diagram.addEdge(diagram.getId(o), diagram.getId(element), "label=" + (i++));
        }
      } else {
        diagram.addNode(diagram.getId(o), "<cons>", "");
        send(walker, "walk", head(o));
        send(walker, "walk", tail(o));
        diagram.addEdge(diagram.getId(o), diagram.getId(head(o)), "label=head");
        diagram.addEdge(diagram.getId(o), diagram.getId(tail(o)), "label=tail");
      }
      return o;
    }));

    send(snapshotWalker, "addHandler", Op(FunctionOf(list(ListOf(Obj), Walker), Obj), (o, walker) ->
    {
      if (isProperList(o)) {
        diagram.addNode(diagram.getId(o), "<list>", "");
        int i = 1;
        for (Obj element : iterate(o)) {
          send(walker, "walk", element);
          diagram.addEdge(diagram.getId(o), diagram.getId(element), "label=" + (i++));
        }
      } else {
        diagram.addNode(diagram.getId(o), "<cons>", "");
        send(walker, "walk", head(o));
        send(walker, "walk", tail(o));
        diagram.addEdge(diagram.getId(o), diagram.getId(head(o)), "label=head");
        diagram.addEdge(diagram.getId(o), diagram.getId(tail(o)), "label=tail");
      }
      return o;
    }));

    send(objWalker, "addHandler", Op(FunctionOf(list(Int, Walker), Obj), (o, walker) ->
    {
      diagram.addNode(diagram.getId(o), o.toString(), "style=filled fillcolor=\"#87CEEB\"");
      return o;
    }));

    send(objWalker, "addHandler", Op(FunctionOf(list(Str, Walker), Obj), (o, walker) ->
    {
      diagram.addNode(diagram.getId(o), "<<font point-size=\"150\">" + GraphViz.encodeHTML(o.toString()) + "</font>>", "style=filled fillcolor=\"#FFDAB9\"");
      return o;
    }));

    send(objWalker, "addHandler", Op(FunctionOf(list(Bool, Walker), Obj), (o, walker) ->
    {
      diagram.addNode(diagram.getId(o), o.toString(), "style=filled fillcolor=\"#3CB371\"");
      return o;
    }));

    send(typeWalker, "addHandler", Op(FunctionOf(list(Classifier, Obj), Obj), (c, walker) ->
    {
      int id = diagram.getId(c);
      String label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\"><tr><td bgcolor=\"sandybrown\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" + GraphViz.encodeHTML(get(c, "name").toString()) + "</font></td></tr>";
      for (Obj op : iterate(get(c, "operations"))) {
        label = label + "<tr> <td><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(get(op, "name").toString()) + "</font></td><td><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(methodType(get(op, "type")).toString()) + "</font></td></tr>";
      }
      label = label + "</table>>";
      if (isTrue(send(c, "inherits", Classifier)))
        diagram.addNode(id, label, "shape=box");
      else diagram.addNode(id, label, "shape=none");
      if (isFalse(send(get(walker, "excluded"), "contains", c.of()))) {
        send(walker, "walk", c.of());
        if (c.of() != Class) diagram.addEdge(id, diagram.getId(c.of()), "penwidth=\"2\" style=dashed arrowhead=vee");
      }

      for (Obj parent : iterate(get(c, "supers")))
        if (isFalse(send(get(walker, "excluded"), "contains", parent))) {
          processClassParent(walker, diagram, c, id, parent);
        }

      for (Obj slot : iterate(send(c, "getSlots"))) {
        String name = get(slot, "name").toString();
        Obj value = get(slot, "value");
        switch (name) {
          case "name":
          case "operations":
          case "constraints":
          case "daemons":
          case "initial":
          case "supers":
            break;
          default:
            if (isFalse(send(get(walker, "excluded"), "contains", value))) {
              send(walker, "walk", value);
              diagram.addEdge(id, diagram.getId(value), "penwidth=\"2\" arrowhead=vee style=dotted label=" + name);
            }
        }
      }
      return c;
    }));

    send(typeWalker, "addHandler", Op(FunctionOf(list(Class, Obj), Obj), (c, walker) ->
    {
      int id = diagram.getId(c);
      Obj excluded = get(walker, "excluded");
      String cname = GraphViz.encodeHTML(get(c, "name").toString());
      String colour = "pink";
      if (isTrue(get(c, "isAbstract"))) colour = "white";
      String label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"1\"><tr><td bgcolor=\"" + colour + "\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" + cname + "</font></td></tr>";
      if (isTrue(get(walker, "showCnstrs"))) {
        for (Obj cnstr : iterate(get(c, "constructors"))) {
          label = label + "<tr> <td colspan=\"2\" bgcolor=\"LemonChiffon\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(c, "name") + get(cnstr, "args") + "</font></td></tr>";
        }
      }
      if (isTrue(get(walker, "showCon"))) {
        for (Obj f : iterate(get(c, "constraints"))) {
          label = label + "<tr> <td colspan=\"2\" bgcolor=\"Gainsboro\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(f, "name") + "</font></td></tr>";
        }
      }
      if (isTrue(get(walker, "showDaemons"))) {
        for (Obj f : iterate(get(c, "daemons"))) {
          label = label + "<tr> <td colspan=\"2\" bgcolor=\"orange\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(f, "name") + "</font></td></tr>";
        }
      }
      for (Obj a : iterate(get(c, "attributes"))) {
        Obj name = get(a, "name");
        Obj type = get(a, "type");
        if (isFalse(send(excluded, "contains", type))) {
          if (type == Str || type == Int || type == Bool || isTrue(send(type, "inherits", ListOf(Obj))))
            label = label + "<tr> <td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name.toString()) + "</font></td><td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(type.toString()) + "</font></td></tr>";
          else {
            send(walker, "walk", type);
            diagram.addEdge(id, diagram.getId(type), "penwidth=\"2\" arrowhead=vee label=<<font face=\"INCONSOLATA\">" + name + "</font>>");
          }
        }
      }
      if (isTrue(get(walker, "showOps"))) {
        for (Obj op : iterate(get(c, "operations"))) {
          // if (!redefinedOp(op, c)) label = label + "<tr> <td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(get(op,
          // "name").toString()) + "</font></td><td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(methodType(get(op,
          // "type")).toString()) + "</font></td></tr>";
          label = label + "<tr> <td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(get(op, "name").toString()) + "</font></td><td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(methodType(get(op, "type")).toString()) + "</font></td></tr>";
        }
      }
      for (Obj slot : iterate(send(c, "getSlots"))) {
        String name = get(slot, "name").toString();
        Obj value = get(slot, "value");
        switch (name) {
          case "attributes":
          case "name":
          case "constraints":
          case "constructors":
          case "initial":
          case "isAbstract":
          case "daemons":
          case "operations":
          case "supers":
          case "doc":
            break;
          default:
            if (isInt(value) || isBool(value) || isStr(value) || isNil(value) || isNull(value)) {
              label = label + "<tr> <td bgcolor=\"yellowgreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name.toString()) + "</font></td><td bgcolor=\"yellowgreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(value.toString()) + "</font></td></tr>";
            } else if (isProperList(value)) {
              if (isTrue(send(value, "forall", Op((x) -> send(Atom, "isInstance", x))))) {
                label = label + "<tr> <td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name.toString()) + "</font></td><td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(value.toString()) + "</font></td></tr>";

              } else {
                for (Obj element : iterate(value)) {
                  if (isFalse(send(excluded, "contains", element))) {
                    send(walker, "walk", element);
                    diagram.addEdge(diagram.getId(c), diagram.getId(element), "label=<" + name + ">");
                  }
                }
              }
            } else if (isFalse(send(excluded, "contains", value))) {
              send(walker, "walk", value);
              diagram.addEdge(diagram.getId(c), diagram.getId(value), "penwidth=\"2\" arrowhead=vee label=" + name);
            }
        }
      }
      label = label + "</table>>";
      if (isFalse(send(excluded, "contains", c))) {
        if (isTrue(send(c, "inherits", Classifier)))
          diagram.addNode(id, label, "shape=box");
        else diagram.addNode(id, label, "shape=none");
      }

      if (isFalse(send(get(walker, "excluded"), "contains", c.of()))) {
        if (c.of() != Atomic) send(walker, "walk", c.of());
        if ((c.of() != Class && c.of() != Atomic) || c == Class) diagram.addEdge(id, diagram.getId(c.of()), "penwidth=\"2\" style=dashed arrowhead=vee");
      }

      for (Obj parent : iterate(get(c, "supers"))) {
        if (isFalse(send(get(walker, "excluded"), "contains", parent))) {
          processClassParent(walker, diagram, c, id, parent);
        }
      }
      return c;
    }));

    send(typeWalker, "addHandler", Op(FunctionOf(list(ListOf, Obj), Obj), (c, walker) ->
    {
      int id = diagram.getId(c);
      String cname = GraphViz.encodeHTML(get(c, "name").toString());
      String colour = "pink";
      if (isTrue(get(c, "isAbstract"))) colour = "white";
      String label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"1\"><tr><td bgcolor=\"" + colour + "\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" + cname + "</font></td></tr>";
      if (isTrue(get(walker, "showCnstrs"))) {
        for (Obj cnstr : iterate(get(c, "constructors"))) {
          label = label + "<tr> <td colspan=\"2\" bgcolor=\"LemonChiffon\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(c, "name") + get(cnstr, "args") + "</font></td></tr>";
        }
      }
      for (Obj f : iterate(get(c, "constraints"))) {
        label = label + "<tr> <td colspan=\"2\" bgcolor=\"Gainsboro\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(f, "name") + "</font></td></tr>";
      }
      for (Obj f : iterate(get(c, "daemons"))) {
        label = label + "<tr> <td colspan=\"2\" bgcolor=\"orange\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(f, "name") + "</font></td></tr>";
      }
      for (Obj a : iterate(get(c, "attributes"))) {
        Obj name = get(a, "name");
        Obj type = get(a, "type");
        if (type == Str || type == Int || type == Bool || isTrue(send(type, "inherits", ListOf(Obj))))
          label = label + "<tr> <td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name.toString()) + "</font></td><td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(type.toString()) + "</font></td></tr>";
        else {
          send(walker, "walk", type);
          diagram.addEdge(id, diagram.getId(type), "penwidth=\"2\" arrowhead=vee label=<<font face=\"INCONSOLATA\">" + name + "</font>>");
        }
      }
      if (isTrue(get(walker, "showOps"))) {
        for (Obj op : iterate(get(c, "operations"))) {
          if (!redefinedOp(op, c))
            label = label + "<tr> <td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(get(op, "name").toString()) + "</font></td><td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(methodType(get(op, "type")).toString()) + "</font></td></tr>";
        }
      }
      label = label + "</table>>";
      if (isTrue(send(c, "inherits", Classifier)))
        diagram.addNode(id, label, "shape=box");
      else diagram.addNode(id, label, "shape=none");
      send(walker, "walk", c.of());
      if (c.of() != Class) diagram.addEdge(id, diagram.getId(c.of()), "penwidth=\"2\" style=dashed arrowhead=vee");
      for (Obj parent : iterate(get(c, "supers")))
        processClassParent(walker, diagram, c, id, parent);
      for (Obj slot : iterate(send(c, "getSlots"))) {
        String name = get(slot, "name").toString();
        Obj value = get(slot, "value");
        switch (name) {
          case "attributes":
          case "name":
          case "constraints":
          case "constructors":
          case "initial":
          case "isAbstract":
          case "daemons":
          case "operations":
          case "supers":
          case "doc":
          case "elementType":
            break;
          default:
            send(walker, "walk", value);
            diagram.addEdge(id, diagram.getId(value), "penwidth=\"2\" arrowhead=vee style=dotted label=" + name);
        }
      }
      return c;
    }));
    /*
     * send(typeWalker, "addHandler", Op(FunctionOf(list(Atomic, Obj), Obj), (c, walker) -> { int id = diagram.getId(c); String cname = GraphViz.encodeHTML(get(c,
     * "name").toString() + "!"); String colour = "lightblue"; if (isTrue(get(c, "isAbstract"))) colour = "white"; String label =
     * "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"2\"><tr><td bgcolor=\"" + colour + "\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" +
     * cname + "</font></td></tr>"; for (Obj cnstr : iterate(get(c, "constructors"))) { label = label +
     * "<tr> <td colspan=\"2\" bgcolor=\"LemonChiffon\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(c, "name") + get(cnstr, "args") + "</font></td></tr>"; } for (Obj f :
     * iterate(get(c, "constraints"))) { label = label + "<tr> <td colspan=\"2\" bgcolor=\"Gainsboro\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(f, "name") +
     * "</font></td></tr>"; } for (Obj f : iterate(get(c, "daemons"))) { label = label + "<tr> <td colspan=\"2\" bgcolor=\"orange\"><font point-size=\"10\" face=\"INCONSOLATA\">" +
     * get(f, "name") + "</font></td></tr>"; } for (Obj a : iterate(get(c, "attributes"))) { Obj name = get(a, "name"); Obj type = get(a, "type"); if (type == Str || type == Int ||
     * type == Bool || isTrue(send(type, "inherits", ListOf(Obj)))) label = label + "<tr> <td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" +
     * GraphViz.encodeHTML(name.toString()) + "</font></td><td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(type.toString()) +
     * "</font></td></tr>"; else { send(walker, "walk", type); diagram.addEdge(id, diagram.getId(type), "penwidth=\"2\" arrowhead=vee label=<<font face=\"INCONSOLATA\">" + name +
     * "</font>>"); } } for (Obj op : iterate(get(c, "operations"))) { if (!redefinedOp(op, c)) label = label +
     * "<tr> <td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(get(op, "name").toString()) +
     * "</font></td><td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(methodType(get(op, "type")).toString()) +
     * "</font></td></tr>"; } label = label + "</table>>"; diagram.addNode(id, label, "shape=none"); for (Obj parent : iterate(get(c, "supers"))) processClassParent(walker,
     * diagram, c, id, parent); for (Obj slot : iterate(send(c, "getSlots"))) { String name = get(slot, "name").toString(); Obj value = get(slot, "value"); switch (name) { case
     * "attributes": case "name": case "constraints": case "constructors": case "initial": case "isAbstract": case "daemons": case "operations": case "supers": case "doc": break;
     * default: send(walker, "walk", value); diagram.addEdge(id, diagram.getId(value), "penwidth=\"2\" arrowhead=vee style=dotted label=" + name); } } return c; }));
     */
    send(typeWalker, "addHandler", Op(FunctionOf(list(Package, Obj), Obj), (p, walker) ->
    {

      int id = diagram.getId(p);
      String cname = GraphViz.encodeHTML(get(p, "name").toString());
      String colour = "pink";
      if (isTrue(get(p, "isAbstract"))) colour = "white";
      String label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"1\"><tr><td bgcolor=\"" + colour + "\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" + cname + "</font></td></tr>";
      for (Obj cnstr : iterate(get(p, "constructors"))) {
        label = label + "<tr> <td colspan=\"2\" bgcolor=\"LemonChiffon\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(p, "name") + get(cnstr, "args") + "</font></td></tr>";
      }
      for (Obj f : iterate(get(p, "constraints"))) {
        label = label + "<tr> <td colspan=\"2\" bgcolor=\"Gainsboro\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(f, "name") + "</font></td></tr>";
      }
      for (Obj f : iterate(get(p, "daemons"))) {
        label = label + "<tr> <td colspan=\"2\" bgcolor=\"orange\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(f, "name") + "</font></td></tr>";
      }
      for (Obj s : iterate(get(p, "bindings"))) {
        label = label + "<tr> <td bgcolor=\"Beige\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(s, "name") + "</font></td><td bgcolor=\"Beige\"><font point-size=\"10\" face=\"INCONSOLATA\">" + get(s, "value") + "</font></td></tr>";
      }
      for (Obj a : iterate(get(p, "attributes"))) {
        Obj name = get(a, "name");
        Obj type = get(a, "type");
        if (type == Str || type == Int || type == Bool || isTrue(send(type, "inherits", ListOf(Obj))))
          label = label + "<tr> <td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name.toString()) + "</font></td><td bgcolor=\"powderblue\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(type.toString()) + "</font></td></tr>";
        else {
          send(walker, "walk", type);
          diagram.addEdge(id, diagram.getId(type), "penwidth=\"2\" arrowhead=vee label=<<font face=\"INCONSOLATA\">" + name + "</font>>");
        }
      }
      if (isTrue(get(walker, "showOps"))) {
        for (Obj op : iterate(get(p, "operations"))) {
          if (!redefinedOp(op, p))
            label = label + "<tr> <td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(get(op, "name").toString()) + "</font></td><td bgcolor=\"mediumseagreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(methodType(get(op, "type")).toString()) + "</font></td></tr>";
        }
      }
      label = label + "</table>>";
      diagram.addNode(id, label, "shape=tab");
      send(walker, "walk", p.of());
      for (Obj parent : iterate(get(p, "supers")))
        processClassParent(walker, diagram, p, id, parent);
      for (Obj slot : iterate(send(p, "getSlots"))) {
        String name = get(slot, "name").toString();
        Obj value = get(slot, "value");
        switch (name) {
          case "attributes":
          case "name":
          case "constraints":
          case "constructors":
          case "initial":
          case "isAbstract":
          case "daemons":
          case "operations":
          case "supers":
          case "doc":
          case "package":
          case "objects":
          case "superPackages":
          case "bindings":
            break;
          default:
            send(walker, "walk", value);
            diagram.addEdge(id, diagram.getId(value), "penwidth=\"2\" arrowhead=vee style=dotted label=" + name);
        }
      }
      for (Obj o : iterate(get(p, "objects")))
        send(walker, "walk", o);
      send(walker, "walk", get(p, "package"));
      diagram.addEdge(id, diagram.getId(get(p, "package")), "label=package penwidth=\"2\" style=dashed arrowhead=vee");
      return p;
    }));

    send(typeWalker, "addHandler", Op(FunctionOf(list(Obj, Walker), Obj), (o, walker) ->
    {
      String label = "";
      if (isInt(o) || isBool(o) || isStr(o))
        label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\"><tr><td bgcolor=\"violet\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\">" + o + ":" + get(o.of(), "name") + "</font></td></tr>";
      else label = "<<table BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\"><tr><td bgcolor=\"violet\" colspan=\"2\"><font face=\"INCONSOLATA\" point-size=\"15\"> :" + get(o.of(), "name") + "</font></td></tr>";
      for (Obj slot : iterate(send(o, "getSlots"))) {
        Obj name = get(slot, "name");
        Obj value = get(slot, "value");
        if (isInt(value) || isBool(value) || isStr(value) || isNil(value) || isNull(value)) {
          label = label + "<tr> <td bgcolor=\"yellowgreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(name.toString()) + "</font></td><td bgcolor=\"yellowgreen\"><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(value.toString()) + "</font></td></tr>";
        } else if (isProperList(value)) {
          if (isTrue(send(value, "forall", Op((x) -> send(Atom, "isInstance", x))))) {
            // Draw a box containing the elements...
            String listLabel = "<<table BORDER=\"0\" CELLSPACING=\"0\" CELLBORDER=\"1\" CELLPADDING=\"0\">";
            for (Obj element : iterate(value)) {
              listLabel = listLabel + "<tr><td><font point-size=\"10\" face=\"INCONSOLATA\">" + GraphViz.encodeHTML(element.toString()) + "</font></td></tr>";
            }
            listLabel = listLabel + "</table>>";
            diagram.addNode(diagram.getId(value), listLabel, "shape=none");
            diagram.addEdge(diagram.getId(o), diagram.getId(value), "label=<" + name + ">");
          } else {
            for (Obj element : iterate(value)) {
              if (isFalse(send(get(walker, "excluded"), "contains", element))) {
                send(walker, "walk", element);
                diagram.addEdge(diagram.getId(o), diagram.getId(element), "label=<" + name + ">");
              }
            }
          }
        } else if (isFalse(send(get(walker, "excluded"), "contains", value))) {
          send(walker, "walk", value);
          diagram.addEdge(diagram.getId(o), diagram.getId(value), "penwidth=\"2\" arrowhead=vee label=" + name);
        }
      }
      label = label + "</table>>";
      send(walker, "walk", o.of());
      diagram.addEdge(diagram.getId(o), diagram.getId(o.of()), "penwidth=\"2\" style=dashed arrowhead=vee");
      diagram.addNode(diagram.getId(o), label, "shape=none");
      return o;
    }));
  }

  public static void processClassParent(Obj walker, GraphViz diagram, Obj c, int id, Obj parent) {

    // Abstracted here because it might need to be reentrant....

    Obj excluded = get(walker, "excluded");

    if (isFalse(send(excluded, "contains", parent))) {

      if (isTrue(send(NameSpaceOf, "isInstance", parent))) {
        // Suppress the container parent...
        Obj contentName = get(parent, "contentName");
        Obj contentType = get(parent, "contentType");
        Obj att = send(parent, "getAttributeNamed", contentName);
        String supers = isNil(get(att, "supers")) ? "" : "^";
        String inherits = inherits(c, get(att, "name")) ? "$" : "";
        if (isFalse(send(excluded, "contains", contentType))) {
          send(walker, "walk", contentType);
          diagram.addEdge(id, diagram.getId(contentType), "penwidth=\"2\" label=<<font face=\"INCONSOLATA\">::" + contentName + supers + inherits + "</font>>");
        }
        for (Obj nameSpaceSuper : iterate(get(parent, "supers"))) {
          if (nameSpaceSuper != Obj && nameSpaceSuper != Container) processClassParent(walker, diagram, c, id, nameSpaceSuper);
        }
      } else if (isTrue(send(ContainerOf, "isInstance", parent))) {
        // Suppress the container parent...
        Obj contentName = get(parent, "contentName");
        Obj contentType = get(parent, "contentType");
        Obj att = send(parent, "getAttributeNamed", contentName);
        String supers = isNil(get(att, "supers")) ? "" : "^";
        String inherits = inherits(c, get(att, "name")) ? "$" : "";
        if (isFalse(send(excluded, "contains", contentType))) {
          send(walker, "walk", contentType);
          diagram.addEdge(id, diagram.getId(contentType), "penwidth=\"2\" label=<<font face=\"INCONSOLATA\">*" + contentName + supers + inherits + "</font>>");
        }
      } else if (isFalse(send(Inherits, "isInstance", parent))) {
        send(walker, "walk", parent);
        // if (parent != Obj && parent != Class) diagram.addEdge(id, diagram.getId(parent), "penwidth=\"2\" arrowhead=empty");
        if (parent != Obj) diagram.addEdge(id, diagram.getId(parent), "penwidth=\"2\" arrowhead=empty");
      }
    }
  }

  private static boolean inherits(Obj c, Obj name) {
    for (Obj parent : iterate(get(c, "supers"))) {
      if (isTrue(send(Inherits, "isInstance", parent))) {
        if (isTrue(send(name, "=", get(parent, "inheritedName")))) return true;
      }
    }
    return false;
  }

  public static void generatePackageDiagrams(Obj _package) {
    generateTypeDiagram(_package);
    generateSnapshotDiagram(_package, get(_package, "name").toString(), theObjNil);
    generatediagram(_package, get(_package, "name").toString());
  }

  public static void generatediagram(Obj obj, String name) {
    GraphViz objDiagram = new GraphViz();
    Vector<Obj> limited = new Vector<Obj>();
    send(objWalker, "reset");
    set(objWalker, "limit", Int(2));
    set(snapshotWalker, "limitHandler", Op((o) ->
    {
      limited.add(o);
      return o;
    }));
    diagram = objDiagram;
    send(objWalker, "walk", obj);
    for (Obj o : limited)
      if (!objDiagram.hasNode(o)) objDiagram.removeEdgesTo(o);
    objDiagram.graphViz("src/test/" + name + "obj.dot");
    System.out.println("Object Diagram Generated");
  }

  public static void generateSnapshotContentsDiagram(Obj obj, String name, Obj excluded) {
    GraphViz snapshotDiagram = new GraphViz();
    Vector<Obj> limited = new Vector<Obj>();
    send(snapshotWalker, "reset");
    set(snapshotWalker, "excluded", excluded);
    set(snapshotWalker, "limit", Int(10));
    set(snapshotWalker, "limitHandler", Op((o) ->
    {
      limited.add(o);
      return o;
    }));
    diagram = snapshotDiagram;
    for (Obj o : iterate(get(obj, "objects")))
      send(snapshotWalker, "walk", o);
    for (Obj o : limited)
      if (!snapshotDiagram.hasNode(o)) snapshotDiagram.removeEdgesTo(o);
    snapshotDiagram.graphViz("src/test/" + name + "snap.dot");
    System.out.println("Snapshot Diagram Generated");
  }

  public static void generateSnapshotDiagram(Obj obj, String name, Obj excluded) {
    GraphViz snapshotDiagram = new GraphViz();
    Vector<Obj> limited = new Vector<Obj>();
    send(snapshotWalker, "reset");
    set(snapshotWalker, "excluded", excluded);
    set(snapshotWalker, "limit", Int(2));
    set(snapshotWalker, "limitHandler", Op((o) ->
    {
      limited.add(o);
      return o;
    }));
    diagram = snapshotDiagram;
    send(snapshotWalker, "walk", obj);
    for (Obj o : limited)
      if (!snapshotDiagram.hasNode(o)) snapshotDiagram.removeEdgesTo(o);
    snapshotDiagram.graphViz("src/test/" + name + "snap.dot");
    System.out.println("Snapshot Diagram Generated");
  }

  public static void generateTypeDiagram(Obj _package) {
    GraphViz typeDiagram = new GraphViz();
    send(typeWalker, "reset");
    set(typeWalker, "limit", Int(9999));
    typeDiagram.reset();
    diagram = typeDiagram;
    send(typeWalker, "walk", _package);
    typeDiagram.graphViz("src/test/" + get(_package, "name") + "class.dot");
    System.out.println("Type Diagram Generated " + _package);
  }

  public static void generateTypeDiagramForElements(String name, Obj elements, Obj excluded) {
    GraphViz typeDiagram = new GraphViz();
    send(typeWalker, "reset");
    set(typeWalker, "limit", Int(9999));
    set(typeWalker, "excluded", excluded);
    set(typeWalker, "showOps", theObjFalse);
    typeDiagram.reset();
    diagram = typeDiagram;
    for (Obj o : iterate(elements))
      send(typeWalker, "walk", o);
    typeDiagram.graphViz("src/test/" + name + "class.dot");
    System.out.println("Type Diagram Generated " + name);
  }

  public static void explode(String name, int typeLevel, int snapLevel, Obj obj, Obj excluded) {
    GraphViz typeDiagram = new GraphViz();
    GraphViz objDiagram = new GraphViz();
    GraphViz snapshotDiagram = new GraphViz();
    Vector<Obj> typeLimited = new Vector<Obj>();
    Vector<Obj> snapLimited = new Vector<Obj>();
    Vector<Obj> objLimited = new Vector<Obj>();

    send(typeWalker, "reset");
    send(objWalker, "reset");
    send(snapshotWalker, "reset");
    set(typeWalker, "excluded", excluded);
    set(snapshotWalker, "excluded", excluded);
    set(objWalker, "excluded", excluded);
    set(typeWalker, "limit", Int(typeLevel));
    set(snapshotWalker, "limit", Int(snapLevel));
    set(objWalker, "limit", Int(1));
    set(typeWalker, "limitHandler", Op((o) ->
    {
      typeLimited.add(o);
      return o;
    }));
    set(snapshotWalker, "limitHandler", Op((o) ->
    {
      snapLimited.add(o);
      return o;
    }));
    set(objWalker, "limitHandler", Op((o) ->
    {
      objLimited.add(o);
      return o;
    }));

    diagram = typeDiagram;
    send(typeWalker, "walk", obj);
    diagram = objDiagram;
    send(objWalker, "walk", obj);
    diagram = snapshotDiagram;
    send(snapshotWalker, "walk", obj);
    for (Obj o : typeLimited)
      if (!typeDiagram.hasNode(o)) typeDiagram.removeEdgesTo(o);
    for (Obj o : snapLimited)
      if (!snapshotDiagram.hasNode(o)) snapshotDiagram.removeEdgesTo(o);
    for (Obj o : objLimited)
      if (!objDiagram.hasNode(o)) objDiagram.removeEdgesTo(o);

    typeDiagram.graphViz("src/test/explode/" + name + "class.dot");
    objDiagram.graphViz("src/test/explode/" + name + "obj.dot");
    snapshotDiagram.graphViz("src/test/explode/" + name + "snap.dot");

  }

  public static void generateContentsDiagrams(Obj _package, Obj excluded) {
    generateContentsDiagrams(_package, get(_package, "name"), excluded);
  }

  public static void generateContentsDiagrams(Obj _package, Obj name, Obj excluded) {

    GraphViz typeDiagram = new GraphViz();
    GraphViz objDiagram = new GraphViz();
    GraphViz snapshotDiagram = new GraphViz();

    send(typeWalker, "reset");
    send(objWalker, "reset");
    send(snapshotWalker, "reset");
    set(typeWalker, "excluded", excluded);
    set(typeWalker, "limit", Int(999));

    for (Obj o : iterate(get(_package, "objects"))) {
      diagram = typeDiagram;
      send(typeWalker, "walk", o);
      diagram = objDiagram;
      send(objWalker, "walk", o);
      diagram = snapshotDiagram;
      send(snapshotWalker, "walk", o);
    }

    typeDiagram.graphViz("src/test/" + name + "class.dot");
    objDiagram.graphViz("src/test/" + name + "obj.dot");
    snapshotDiagram.graphViz("src/test/" + name + "snap.dot");

    System.out.println("Diagrams Generated");
  }

  private static boolean redefinedOp(Obj op, Obj c) {
    for (Obj o : iterate(send(c, "getAllOperations"))) {
      if (o != op && sameOpSignature(o, op)) return true;
    }
    return false;
  }

  private static boolean sameOpSignature(Obj o1, Obj o2) {
    return isTrue(send(get(o1, "name"), "=", get(o2, "name")));
  }

}
