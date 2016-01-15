package test.db;

import java.util.Vector;

import value.Obj;

public class Table {

  String        name;
  String[]      columns;
  Vector<Obj[]> rows = new Vector<Obj[]>();

  public Table(String name, String... columns) {
    super();
    this.name = name;
    this.columns = columns;
  }

  public void add(Obj... row) {
    rows.add(row);
  }

}
