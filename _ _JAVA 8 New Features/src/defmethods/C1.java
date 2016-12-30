package defmethods;

public class C1 implements I,J {
	//if the Interfaces are identical we get the error msg: 
	//Duplicate default methods named show with the parameters () and () are inherited from the types J and I
	//so we need to override them here
	//naveen ready correct!
	
	public void show(){
		//super.show(); error:The method show() is undefined for the type Object
		System.out.println("Class C1 - show() called");
	}

}
