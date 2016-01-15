package value;

import java.util.Arrays;

public class Debug {

  boolean traceSend = false;
  boolean traceGet  = false;
  boolean traceSet  = false;
  boolean nested    = false;
  int     indent;

  public void get(Obj target, String name) {
    if (traceGet && !nested) {
      spaces();
      nested = true;
      System.out.println("GET: " + target + " " + name);
      nested = false;
      indent = indent + 2;
    }
  }

  public Obj getResult(Obj o) {
    if (traceGet && !nested) {
      indent = indent - 2;
      spaces();
      nested = true;
      System.out.println(o.toString());
      nested = false;
    }
    return o;
  }

  public boolean isNested() {
    return nested;
  }

  public void send(Obj target, Obj message, Obj... args) {
    if (traceSend && !nested) {
      spaces();
      nested = true;
      System.out.println("SEND: " + target + " " + message + Arrays.toString(args));
      nested = false;
      indent = indent + 2;
    }
  }

  public void send(Obj target, String message, Obj... args) {
    if (traceSend && !nested) {
      spaces();
      nested = true;
      System.out.println("SEND: " + target + " " + message + Arrays.toString(args));
      nested = false;
      indent = indent + 2;
    }
  }

  public Obj sendResult(Obj o) {
    if (traceSend && !nested) {
      indent = indent - 2;
      spaces();
      nested = true;
      System.out.println(o.toString());
      nested = false;
    }
    return o;
  }

  public void set(Obj target, String name, Obj value) {
    if (traceSet && !nested) {
      spaces();
      nested = true;
      System.out.println("SET: " + target + " " + name + " " + value);
      nested = false;
      indent = indent + 2;
    }
  }

  public void setNested(boolean nested) {
    this.nested = nested;
  }

  public Obj setResult(Obj o) {
    if (traceSet && !nested) {
      indent = indent - 2;
      spaces();
      nested = true;
      System.out.println(o.toString());
      nested = false;
    }
    return o;
  }

  public void spaces() {
    for (int i = 0; i < indent; i = i + 2)
      System.out.print("| ");
  }
}