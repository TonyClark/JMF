package serialize;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import value.Obj;

public class Serializer extends ObjectOutputStream {

  protected Serializer() throws IOException, SecurityException {
    super();
  }

  public Serializer(OutputStream out) throws IOException {
    super(out);
    this.enableReplaceObject(true);
  }

  public Object replaceObject(Object o) {
    if (o == Obj.Atom) return Inflater.Kernel.Atom;
    if (o == Obj.Atomic) return Inflater.Kernel.Atomic;
    if (o == Obj.Attribute) return Inflater.Kernel.Attribute;
    if (o == Obj.Bool) return Inflater.Kernel.Bool;
    if (o == Obj.Class) return Inflater.Kernel.Class;
    if (o == Obj.Classifier) return Inflater.Kernel.Classifier;
    if (o == Obj.ConcreteFun) return Inflater.Kernel.ConcreteFun;
    if (o == Obj.ConsOf) return Inflater.Kernel.ConsOf;
    if (o == Obj.Container) return Inflater.Kernel.Container;
    if (o == Obj.ContainerOf) return Inflater.Kernel.ContainerOf;
    if (o == Obj.DocumentedElement) return Inflater.Kernel.DocumentedElement;
    if (o == Obj.Float) return Inflater.Kernel.Float;
    if (o == Obj.Function) return Inflater.Kernel.Function;
    if (o == Obj.FunctionOf) return Inflater.Kernel.FunctionOf;
    if (o == Obj.Inherits) return Inflater.Kernel.Inherits;
    if (o == Obj.Int) return Inflater.Kernel.Int;
    if (o == Obj.ListOf) return Inflater.Kernel.ListOf;
    if (o == Obj.NameSpaceOf) return Inflater.Kernel.NameSpaceOf;
    if (o == Obj.NamedElement) return Inflater.Kernel.NamedElement;
    if (o == Obj.NilOf) return Inflater.Kernel.NilOf;
    if (o == Obj.Null) return Inflater.Kernel.Null;
    if (o == Obj.Number) return Inflater.Kernel.Number;
    if (o == Obj.Obj) return Inflater.Kernel.Obj;
    if (o == Obj.Package) return Inflater.Kernel.Package;
    if (o == Obj.PackageController) return Inflater.Kernel.PackageController;
    if (o == Obj.Snapshot) return Inflater.Kernel.Snapshot;
    if (o == Obj.TypedElement) return Inflater.Kernel.TypedElement;
    if (o == Obj.Str) return Inflater.Kernel.Str;
    if (o == Obj.Symbol) return Inflater.Kernel.Symbol;
    if (o == Obj.Slot) return Inflater.Kernel.Slot;
    if (o == Obj.Table) return Inflater.Kernel.Table;
    if (o == Obj.TableOf) return Inflater.Kernel.TableOf;
    if (o == Obj.Walker) return Inflater.Kernel.Walker;
    
    if (o == Obj.theObjFalse) return Inflater.Kernel.theObjFalse;
    if (o == Obj.theObjNil) return Inflater.Kernel.theObjNil;
    if (o == Obj.theObjNull) return Inflater.Kernel.theObjNull;
    if (o == Obj.theObjTrue) return Inflater.Kernel.theObjTrue;
    if (o == Obj.Kernel) return Inflater.Kernel.Kernel;
    return o;
  }

}
