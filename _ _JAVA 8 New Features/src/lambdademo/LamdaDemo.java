package lambdademo;

interface I{
	void show(int i); 
}

//class C implements I{//a waste of class only used once
//	@Override
//	public void show(){
//		System.out.println("in class C - hello!");
//	}
//}

public class LamdaDemo {

	public static void main(String[] args) {
//		//method 1 - conventional way
//		I obj = new C();
//		obj.show();
//
//		//method 2 - anon inner class way
//		I obj2 = new I(){
//			@Override
//			public void show(){
//				System.out.println("show() anon inner class instantiation - hello!");
//			}
//		};
//		obj2.show();
//
//		//method 3 - anon inner class way - second way
//		(new I(){
//			@Override
//			public void show(){
//				System.out.println("show() anon inner class instantiation - 2nd way - hello!");
//			}
//		}).show();

		//method 4 - lambda expressions - note that no inner class .class file was created
		I obj4;
		obj4 = (i)->{
			System.out.println("lamda in da house!" + i);
		};
		obj4.show(5);
		//or
		//method 5 - lambda expressions - note that no inner class was created
		I obj5;
		obj5 = i->{System.out.println("lamda in da house!" + i);
		};
		obj5.show(95);
	}

}
//output
//lamda in da house!5
//lamda in da house!95
