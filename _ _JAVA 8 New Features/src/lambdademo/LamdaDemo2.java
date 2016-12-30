package lambdademo;

interface I2{//interface is public abstract by default
	void show(); //method is public abstract by default
	void show2();
}

class C2 implements I2{//a waste of class only used once
	@Override
	public void show(){
		System.out.println("in class C - hello!");
	}
	public void show2(){
		System.out.println("in class C - hello!");
	}
}

public class LamdaDemo2 {

	public static void main(String[] args) {
		//method 1 - conventional way
		I2 obj = new C2();
		obj.show();
		
		//method 2 - anon inner class way
		I2 obj2 = new I2(){
			@Override
			public void show(){
				System.out.println("show() anon inner class instantiation - hello!");
			}
			public void show2(){
				System.out.println("show2() anon inner class instantiation - hello!");
			}
		};
		obj2.show();
		
		//method 3 - anon inner class way - second way
		(new I2(){
			@Override
			public void show(){
				System.out.println("show() anon inner class instantiation - 2nd way - hello!");
			}
			@Override
			public void show2(){
				System.out.println("show2()anon inner class instantiation - 2nd way - hello!");
			}
		}).show2();
		


	}

}
