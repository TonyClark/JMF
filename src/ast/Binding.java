package ast;

import env.Env;
import exp.BoaConstructor;
import value.Obj;
import static value.Obj.*;

@BoaConstructor(fields = { "name", "type", "value" })
public class Binding {

	public String	name;
	public Type		type;
	public AST		value;

	public Binding() {
	}

	public String toString() {
		return "Binding(" + name + "," + type + "," + value + ")";
	}

	public Obj typeCheck(Env<String, Obj> tenv,Env<String, Obj> env) {
		Obj valueType = value.typeCheck(null, tenv,env);
		Obj declaredType = type.eval(env);
		if (isTrue(send(valueType, "inherits", declaredType)))
			return declaredType;
		else throw new Error("binding declares " + name + ":" + type + " but value is of type " + valueType);
	}

}
