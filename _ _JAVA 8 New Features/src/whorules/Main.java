package whorules;

class First {
	public String name() {
		return "First";
	}
}

class Second extends First {
	public void whoRules() {
		System.out.print(super.name() + " rules");
		System.out.println(" but " + name() + " is even better");
	}
	public String name() {
		return "Second";
	}
}

class Third extends Second {
	public String name() {
		return "Third";
	}
}

public class Main {

	public static void main(String[] args) {
		Second varSecond = new Second();
		varSecond.whoRules();
		
		Third varThird = new Third();
		varThird.whoRules();
	}

}
//OUTPUT:
//First rules but Second is even better
//First rules but Third is even better


//Q&A:
//Why wouldn't it be: ... "Second rules but third is even better"
//Because super.name() in class Second refers to the superclass of Second, not the superclass of the instance.
//
//From the Java Language Specification: The form super.Identifier refers to the field named Identifier of the current object, but with the current object viewed as an instance of the superclass of the current class.
//
//Can a subclass be a superclass for another class?
//Yes.
//
//Or can there only be one(superclass)?
//Each class can have at most one direct superclass, which in turn may have its superclass, etc.
//
//(Using example code above) I understand that First is a superclass for Second, but is Second a superclass for Third? Or is First the superclass for Third?
//Both First and Second are superclasses for Third.
//
//Second is the direct superclass for Third.