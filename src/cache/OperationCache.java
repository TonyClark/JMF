package cache;

import java.util.Hashtable;
import java.util.Vector;

import value.ConcreteFun;
import value.Obj;

public class OperationCache extends Hashtable<String, Vector<OpAndSuperObj>> {

  private static final long serialVersionUID = -7556839748740118308L;

  public void cache(String message, int arity, ConcreteFun operation, Obj superObj) {
    Vector<OpAndSuperObj> pairs = virtualGet(message);
    pairs.setSize(arity + 1);
    pairs.set(arity, new OpAndSuperObj(operation, superObj));
  }

  private Vector<OpAndSuperObj> virtualGet(String message) {
    if (containsKey(message))
      return get(message);
    else {
      put(message, new Vector<OpAndSuperObj>());
      return get(message);
    }
  }

}
