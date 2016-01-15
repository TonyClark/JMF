package ast;

import static value.Obj.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import commands.Command;
import env.Env;
import machine.Machine;
import value.ConcreteFun;
import value.Obj;
import values.JavaObject;
import values.Value;

public abstract class JMFCommand extends Command {

  public Env<String, Obj> getEnv(Machine machine) {
    Env<String, Value> machineEnv = machine.getEnv();
    Env<String, Obj> env = AST.getTopLvlEnv();
    for (String name : machineEnv.dom()) {
      Value v = machineEnv.lookup(name);
      if (v instanceof JavaObject) {
        JavaObject jo = (JavaObject) v;
        if (jo.getTarget() instanceof Obj) env = env.bind(name, (Obj) jo.getTarget());
      }
    }
    env = env.bind("print", Op(o ->
    {
      System.out.println(o.toString());
      return o;
    }));
    env = env.bind("null", theObjNull);
    env = env.bind("javaObj", Op(JMFCommand::javaObj));
    env = env.bind("Kernel", Kernel);
    return env;
  }

  public static Obj javaObj(Obj path, Obj values) {
    // The path should name a Java class that implements Obj and which
    // has a constructor that expects the supplied list of values...
    try {
      java.lang.Class<?> c = java.lang.Class.forName(path.toString());
      Obj o = null;
      for (Constructor<?> cnstr : c.getConstructors()) {
        java.lang.Class<?>[] types = cnstr.getParameterTypes();
        if (types.length == 1) {
          if (types[0] == Obj.class) o = (Obj) cnstr.newInstance(values);
        }
      }
      if (o == null) o = (Obj) c.newInstance();
      return o;
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      return theObjNull;
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return theObjNull;
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      return theObjNull;
    }
  }

  public Env<String, Obj> getTypeEnv(Machine machine) {
    Env<String, Value> machineEnv = machine.getEnv();
    Env<String, Obj> env = AST.getTypeEnv();
    for (String name : machineEnv.dom()) {
      Value v = machineEnv.lookup(name);
      if (v instanceof JavaObject) {
        JavaObject jo = (JavaObject) v;
        if (jo.getTarget() instanceof Obj) {
          Obj o = (Obj) jo.getTarget();
          if (o instanceof ConcreteFun) {
            ConcreteFun fun = (ConcreteFun) o;
            env = env.bind(name, fun.get("type"));
          } else env = env.bind(name, o.of());
        }
      }
    }
    env = env.bind("null", Null);
    return env;
  }

}
