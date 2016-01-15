package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import value.Obj;

public class GraphViz implements Serializable {

  private static final long serialVersionUID = -5266682506292957968L;

  class Node implements Serializable {

    private static final long serialVersionUID = 8000948366547733106L;

    int    id;
    String label = "";
    String style = "";

    public Node(int id) {
      this.id = id;
    }

    public Node(int id, String label, String style) {
      this(id);
      this.label = label;
      this.style = style;
    }

    public void graphViz(PrintStream out) {
      out.print(id);
      if (!style.equals("") || !label.equals("")) {
        out.print("[");
        if (!label.equals("")) out.print("label=" + label);
        if (!style.equals("")) {
          if (!label.equals("")) out.print(",");
          out.print(style);
        }
        out.print("]");
      }
      out.println(";");
    }
  }

  class Edge implements Serializable {

    private static final long serialVersionUID = 235966248067372481L;

    int    sourceLabel;
    int    targetLabel;
    String style = "";

    public Edge(int sourceLabel, int targetLabel) {
      super();
      this.sourceLabel = sourceLabel;
      this.targetLabel = targetLabel;
    }

    public Edge(int sourceLabel, int targetLabel, String style) {
      this(sourceLabel, targetLabel);
      this.style = style;
    }

    public void graphViz(PrintStream out) {
      out.print(sourceLabel + " -> " + targetLabel);
      if (!style.equals(""))
        out.println("[" + style + "];");
      else out.println(";");
    }
  }

  Vector<Node>               nodes   = new Vector<Node>();
  Vector<Edge>               edges   = new Vector<Edge>();
  Hashtable<Object, Integer> ids     = new Hashtable<Object, Integer>();
  int                        counter = 0;

  public GraphViz() {
  }

  public void reset() {
    nodes.clear();
    edges.clear();
    ids.clear();
    counter = 0;
  }

  public int getId(Object o) {
    if (ids.containsKey(o))
      return ids.get(o);
    else {
      int id = counter++;
      ids.put(o, id);
      return id;
    }
  }

  public String toString() {
    return "GraphViz(" + nodes + "," + edges + ")";
  }

  public void graphViz(PrintStream out) {
    out.println("digraph {");
    out.println("  rankdir=\"LR\"");
    for (Node node : nodes)
      node.graphViz(out);
    for (Edge edge : edges)
      edge.graphViz(out);
    out.println("}");
  }

  public void addNode(int id) {
    nodes.add(new Node(id));
  }

  public void addNode(int id, String label, String style) {
    nodes.add(new Node(id, label, style));
  }

  public void addEdge(int sourceLabel, int targetLabel) {
    edges.add(new Edge(sourceLabel, targetLabel));
  }

  public void addEdge(int sourceLabel, int targetLabel, String style) {
    edges.add(new Edge(sourceLabel, targetLabel, style));
  }

  public void graphViz(String path) {
    try {
      PrintStream out = new PrintStream(new File(path));
      graphViz(out);
      out.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static String encodeHTML(String str) {
    if (str == null) return null;

    int len = str.length();
    if (len == 0) return "\"" + str + "\"";

    StringBuffer encoded = new StringBuffer();
    for (int i = 0; i < len; i++) {
      char c = str.charAt(i);
      char cc = (i + 1) < (len - 1) ? str.charAt(i + 1) : 0;
      if (c == '<')
        encoded.append("&lt;");
      else if (c == '\n') {
        encoded.append("&#13;");
        if (cc == '\r') i++;
      } else if (c == '\r') {
        encoded.append("&#10;");
        if (cc == '\n') i++;
      } else if (c == '\"')
        encoded.append("&quot;");
      else if (c == '>')
        encoded.append("&gt;");
      else if (c == '\'')
        encoded.append("&apos;");
      else if (c == '&')
        encoded.append("&amp;");
      else encoded.append(c);
    }

    return encoded.toString();
  }

  public void removeEdgesTo(Object o) {
    if (ids.containsKey(o)) {
      int id = ids.get(o);
      Vector<Edge> E = new Vector<Edge>();
      for (Edge edge : edges) {
        if (edge.targetLabel == id) E.add(edge);
      }
      edges.removeAll(E);
    }
  }

  public boolean hasNode(Object o) {
    if (ids.containsKey(o)) {
      int id = ids.get(o);
      for (Node node : nodes)
        if (node.id == id) return true;
    }
    return false;
  }

}
