package defmethods;

public interface I {
	default void show(){
		System.out.println("public interface I - show() called");
	}

}
