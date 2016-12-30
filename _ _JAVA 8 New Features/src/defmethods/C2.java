package defmethods;
 	
public class C2 extends A implements I {
	public void show(){
		super.show();
		System.out.println("Class C2 - show() called");
	}

}
