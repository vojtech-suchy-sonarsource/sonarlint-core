package foo;

public class Foo {
  public void call_echo() {
    echo(3);
  }
  
  public void echo(int i) {
    should_be_static();
  }
  
  //ACR-ecdf8caca8d74636aebe4dcbda165ffc
  private void should_be_static() {
    System.out.println("Foo");
  }
  
}
