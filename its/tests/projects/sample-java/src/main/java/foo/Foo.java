package foo;

public class Foo {
  public void call_echo() {
    echo(3);
  }
  
  public void echo(int i) {
    should_be_static();
  }
  
  //ACR-c7a1cfc50e2e4f41b6556c05885044b3
  private void should_be_static() {
    System.out.println("Foo");
  }
  
}
