package ast;

import context.Context;
import exp.Apply;
import exp.BoaConstructor;
import exp.Str;
import grammar.Call;
import grammar.Grammar;
import machine.Machine;
import values.Value;
import xpl.XPL;

@BoaConstructor(fields = { "paths" })
public class TplvlImport extends JMFCommand {

  public String[] paths;

  public boolean perform(Machine machine) {
    for (String path : paths) {
      values.List defs = getFile(path);
      for (Value def : defs.values) {
        if (def instanceof TplvlBind) {
          TplvlBind b = (TplvlBind) def;
          b.perform(machine);
        } else throw new Error("expecting a TplvlBind: " + def);
      }
    }
    return false;
  }

  public static values.List getFile(String name) {
    Machine machine = new Machine(null, Value.builtinEnv, Context.readFile(name), 0, null, 0);
    Grammar grammar = (Grammar) XPL.XPL;
    machine.setGrammar(grammar);
    machine.pushInstr(new Call("file", new Apply("exp.BindingNameLiteral", new Str(name))));
    long start = System.currentTimeMillis();
    System.out.print("[" + name);
    Value value = machine.run();
    System.out.println(" " + (System.currentTimeMillis() - start) + " ms," + machine.getMaxFailDepth() + "]");
    if (machine.isOk()) {
      if (value instanceof values.List) {
        return (values.List) value;
      } else throw new Error("expecting a list of bindings: " + value);
    } else throw new Error(machine.getError());
  }

}
