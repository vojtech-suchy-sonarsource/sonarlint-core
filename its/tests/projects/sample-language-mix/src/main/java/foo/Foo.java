package foo;

public class Foo {
  public void call_echo() {
    echo(3);
  }
  
  public void echo(int i) {
    should_be_static();
  }
  
  //ACR-44c059facd8f4d5a97d6fde91d3495b4
  private void should_be_static() {
    System.out.println("Foo");
  }
  
}
