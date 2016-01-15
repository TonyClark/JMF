package ast;

import exp.BoaConstructor;

@BoaConstructor(fields = { "name", "value" })
public class Slot {

	public String	name;
	public AST		value;

	public Slot() {
	}

	public String toString() {
		return "Slot(" + name + "," + value + ")";
	}

}
