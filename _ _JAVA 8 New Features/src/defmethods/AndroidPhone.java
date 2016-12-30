package defmethods;

interface Phone {
	void call();
	default void message(){
		System.out.println("text message sent");
	}
}

public class AndroidPhone implements Phone{
	public void call(){
		System.out.println("In AndroidPhone class - Android phone calling!");
	}

	public static void main(String[] args) {
		//first way of using interface via AndroidPhone - code bloat way - needs AndroidPhone class
		Phone ap = new AndroidPhone();
		ap.call();
		ap.message();
		
		//second way of using interface is with anon inner class and implement abstract method
		Phone ap2 = new Phone(){
			public void call(){
				System.out.println("\r\nsecond way - using anon innerclass - not using AndroidPhone class");
			}
		};
		ap2.call();
		ap2.message();
		
		//third way - lambda expression - remove boiler plate code
		Phone p = ()->System.out.println("\r\nthird way - using lambda with Phone ref");
		p.call();
		p.message();
		
	}
}

//output:
//In AndroidPhone class - Android phone calling!
//text message sent
//
//second way - using anon innerclass - not using AndroidPhone class
//text message sent
//
//third way - using lambda with Phone ref
//text message sent

