package cache;

import value.ConcreteFun;
import value.Obj;

public class OpAndSuperObj {

  ConcreteFun operation;
  Obj        superObj;

  public OpAndSuperObj(ConcreteFun operation, Obj superObj) {
    super();
    this.operation = operation;
    this.superObj = superObj;
  }

  public ConcreteFun getOperation() {
    return operation;
  }

  public Obj getSuperObj() {
    return superObj;
  }

  public String toString() {
    return "[operation=" + operation + "]";
  }

}
