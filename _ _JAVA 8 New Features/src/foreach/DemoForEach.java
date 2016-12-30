package foreach;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import defmethods.I;

public class DemoForEach {

	public static void main(String[] args) {
		List<Integer> values = Arrays.asList(4,5,6,7,8);

		//method 1
		System.out.println("Printing array using for loop");
		for(int i=0;i < values.size(); i++){
			System.out.println(values.get(i));
		}

		//method 2 enhanced for loop with external loop
		System.out.println("\nPrinting array using enhanced for loop");
		for(Integer current : values){
			System.out.println(current);
		}

		//method 3 - new for java 1.8 - use internal loops -
		//uses lambda expression
		//note, i -> System.out.println(i) is the implementation of the 
		//Consumer interface
		System.out.println("\nPrinting array using enhanced foreach internal loop - lambda expression - Java 1.8");
		values.forEach(current-> System.out.println(current)); //lambda implementing consumer interface

		//derivation of implementation of Consumer interface:
		//step 1
		//		Consumer<Integer> ci = new Consumer<Integer>(){
		//			public void accept(Integer i){
		//				System.out.println(i);
		//			}
		//		};		

		//step 2a - simplify into form: param -> body
		//		Consumer<Integer> ci = (Integer i)->{
		//				System.out.println(i);
		//			}
		//		;

		//step 2b final form
		Consumer<Integer> ci = i->System.out.println(i);
		System.out.println("\nPrinting array using enhanced foreach internal loop  with Consumer interface implementation");
		values.forEach(ci); //lambda implementing consumer interface		
	}
}

//OUTPUT
//Printing array using for loop
//4
//5
//6
//7
//8
//
//Printing array using enhanced for loop
//4
//5
//6
//7
//8
//
//Printing array using enhanced foreach internal loop - lambda expression - Java 1.8
//4
//5
//6
//7
//8
//
//Printing array using enhanced foreach internal loop  with Consumer interface implementation
//4
//5
//6
//7
//8
