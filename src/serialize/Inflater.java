package serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import value.Obj;

public class Inflater extends ObjectInputStream {

  public enum Kernel {
    Atom, Atomic, Attribute, Bool, Class, Classifier, ConcreteFun, ConsOf, Container, ContainerOf, DocumentedElement, Float, Function, FunctionOf, Inherits, Int, ListOf, NameSpaceOf, NamedElement, NilOf, Null, Number, Obj, Package, PackageController, Snapshot, TypedElement, Str, Slot, Symbol, Table, TableOf, Walker, theObjFalse, theObjNil, theObjNull, theObjTrue, Kernel
  };

  protected Inflater() throws IOException, SecurityException {
    super();
  }

  public Inflater(InputStream in) throws IOException {
    super(in);
    this.enableResolveObject(true);
  }

  public Object resolveObject(Object o) {
    if (o == Kernel.Atom) return Obj.Atom;
    if (o == Kernel.Atomic) return Obj.Atomic;
    if (o == Kernel.Attribute) return Obj.Attribute;
    if (o == Kernel.Bool) return Obj.Bool;
    if (o == Kernel.Class) return Obj.Class;
    if (o == Kernel.Classifier) return Obj.Classifier;
    if (o == Kernel.ConcreteFun) return Obj.ConcreteFun;
    if (o == Kernel.ConsOf) return Obj.ConsOf;
    if (o == Kernel.Container) return Obj.Container;
    if (o == Kernel.ContainerOf) return Obj.ContainerOf;
    if (o == Kernel.DocumentedElement) return Obj.DocumentedElement;
    if (o == Kernel.Float) return Obj.Float;
    if (o == Kernel.Function) return Obj.Function;
    if (o == Kernel.FunctionOf) return Obj.FunctionOf;
    if (o == Kernel.Inherits) return Obj.Inherits;
    if (o == Kernel.Int) return Obj.Int;
    if (o == Kernel.ListOf) return Obj.ListOf;
    if (o == Kernel.NameSpaceOf) return Obj.NameSpaceOf;
    if (o == Kernel.NamedElement) return Obj.NamedElement;
    if (o == Kernel.NilOf) return Obj.NilOf;
    if (o == Kernel.Null) return Obj.Null;
    if (o == Kernel.Number) return Obj.Number;
    if (o == Kernel.Obj) return Obj.Obj;
    if (o == Kernel.Package) return Obj.Package;
    if (o == Kernel.PackageController) return Obj.PackageController;
    if (o == Kernel.Snapshot) return Obj.Snapshot;
    if (o == Kernel.TypedElement) return Obj.TypedElement;
    if (o == Kernel.Str) return Obj.Str;
    if (o == Kernel.Symbol) return Obj.Symbol;
    if (o == Kernel.Slot) return Obj.Slot;
    if (o == Kernel.Table) return Obj.Table;
    if (o == Kernel.TableOf) return Obj.TableOf;
    if (o == Kernel.Walker) return Obj.Walker;

    if (o == Kernel.theObjFalse) return Obj.theObjFalse;
    if (o == Kernel.theObjNil) return Obj.theObjNil;
    if (o == Kernel.theObjNull) return Obj.theObjNull;
    if (o == Kernel.theObjTrue) return Obj.theObjTrue;
    if (o == Kernel.Kernel) return Obj.Kernel;
    return o;
  }
}
