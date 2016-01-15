package cache;

import java.util.Hashtable;

import value.ConcreteFun;
import value.Obj;

public class MessageCache extends Hashtable<Obj, OperationCache> {

  private static final long serialVersionUID = 4344120925350864768L;

  public void cache(Obj type, String message, int arity, ConcreteFun operation, Obj superObj) {
    OperationCache cache = virtualGet(type);
    cache.cache(message, arity, operation, superObj);
  }

  private OperationCache virtualGet(Obj type) {
    if (containsKey(type))
      return get(type);
    else {
      put(type, new OperationCache());
      return get(type);
    }
  }
}
