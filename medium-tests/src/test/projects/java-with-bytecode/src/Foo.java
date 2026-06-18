public class Foo {

	public static void main(String[] args) {
		System.out.println("Foo7"); //NOSONAR
		System.out.println("Foo7");
	}
	
	private void foo() {
		// intentionally empty, used as test fixture for bytecode analysis
	}
}
