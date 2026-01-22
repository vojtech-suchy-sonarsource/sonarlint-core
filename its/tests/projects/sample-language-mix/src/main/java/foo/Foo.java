package foo;

public class Foo {
  public void call_echo() {
    echo(3);
  }
  
  public void echo(int i) {
    should_be_static();
  }
  
  //ACR-8f8b52924b7d4f07b7931a22d8ae2684
  private void should_be_static() {
    System.out.println("Foo");
  }
  
}
