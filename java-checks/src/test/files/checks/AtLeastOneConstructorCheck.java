import java.lang.Object;

class A { // Noncompliant {{Add a constructor to the class.}}
  private int field;
}

class B {
  private int field;

  B() {
    field = 0;
  }
}

class C {
  public int field;
  private static int field;
  void foo() {
    Object o = new Object() {
      private int field;
    };
  }
}

enum Enum { // Noncompliant {{Add a constructor to the enum.}}
  A;
  private int field;
}

abstract class D {
  private int field;
}

class E {
  private int field = 5;
}
