package defmethods;

public interface J {
	default void show(){
		System.out.println("public interface J - show() called");
	}


}
