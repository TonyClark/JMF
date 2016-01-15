package ast;

import env.Env;
import exp.BoaConstructor;
import value.Cons;
import value.Obj;

import static value.Obj.*;

import java.util.Arrays;

@BoaConstructor(fields = { "object", "name", "args" })
public class Send extends AST {

  public AST    object;
  public String name;
  public AST[]  args;

  public Send() {
  }

  public Send(AST object, String name, AST[] args) {
    super();
    this.object = object;
    this.name = name;
    this.args = args;
  }

  public String toString() {
    return "Send(" + object + "," + name + Arrays.toString(args) + ")";
  }

  public Obj eval(Obj self, Env<String, Obj> env) {
    Obj o = object.eval(self, env);
    Obj[] values = new Obj[args.length];
    for (int i = 0; i < args.length; i++)
      values[i] = args[i].eval(self, env);
    return send(o, name, values);
  }

  public Obj typeCheck(Obj tself, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj t = object.typeCheck(tself, tenv, env);
    if (t.of() == Class)
      return standardMOPTypeCheck(tself, t, tenv, env);
    else {
      Obj type = t.send("typeCheckSend", Str(name), argTypes(tenv, env));
      if (type == theObjNull)
        throw new Error("type error in " + this);
      else return type;
    }
  }

  private Obj argTypes(Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj types = theObjNil;
    for (int i = args.length - 1; i >= 0; i--) {
      types = cons(args[i].typeCheck(null, tenv, env), types);
    }
    return types;
  }

  public Obj standardMOPTypeCheck(Obj tself, Obj type, Env<String, Obj> tenv, Env<String, Obj> env) {
    Obj classes = cons(type, send(type, "allSupers"));
    Obj op = null;
    Obj opType = null;
    for (Obj c : iterate(classes)) {
      if (isTrue(send(c, "hasOperationNamed", Str(name)))) {
        op = send(c, "getOperationNamed", Str(name));
        Obj staticSelf = staticSelf(env);
        opType = send(get(op, "type"), "subst", type, Self);
        if (staticSelf != null)
          opType = send(opType, "subst", staticSelf, StaticSelf);
        else opType = send(opType, "subst", type, StaticSelf);
        break;
      }
    }
    if (op == null)
      throw new Error("cannot find an operation named " + name + " in " + classes);
    else {
      boolean isVarArgs = isTrue(get(op, "isVarArgs"));
      if ((isVarArgs && args.length >= asInt(get(op, "arity")) - 3) || args.length == asInt(get(op, "arity")) - 2) {
        // drop self and super...
        Obj domain = tail(tail(get(opType, "domain")));
        int arity = Cons.length(domain);
        Obj declaredType = null;
        for (int i = 0; i < args.length; i++) {
          if (!isVarArgs || i < arity - 2) {
            declaredType = head(domain);
            domain = tail(domain);
          }
          if (isVarArgs && i == arity - 1) declaredType = head(domain).get("elementType");
          Obj argType = args[i].typeCheck(tself, tenv, env);
          if (!isTrue(send(argType, "inherits", declaredType))) throw new Error("arg " + args[i] + ":" + argType + " is not of type " + declaredType + " in " + name + ":" + opType);
        }
        return get(opType, "type");
      } else throw new Error(op + ": supplied " + args.length + " args, but expects " + (asInt(get(op, "arity")) - 2));
    }
  }

  private Obj staticSelf(Env<String, Obj> env) {
    // If the type checker can calculate the value of the target then
    // the target might be a type that can be referenced in the operation
    // type. This supports new that needs to reference the target type in
    // order to calculate the type of the result...
    if (object instanceof Var) {
      Var var = (Var) object;
      if (env.binds(var.name))
        return var.eval(null, env);
      else return null;
    } else return null;
  }

}
