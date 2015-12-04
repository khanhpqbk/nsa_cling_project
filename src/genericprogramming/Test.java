package genericprogramming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Test {
	public static <T> T countOccurences(Collection<T> col, T itemToCount) {
		int count = 0;
		for (T item : col) {
			if (itemToCount.equals(item))
				count++;
		}
		System.out.println(itemToCount.getClass());
		return itemToCount;
	}

	public static <T> boolean compare(T t1, T t2) {
        return t1.equals(t2);
    }

	public static <T> T test() {
		Object obj = new Object();
		return (T) obj;
	}

	public static void main(String[] args) {
		Object integer = new Integer(1);
		// classCastException is of type run time exception
		String newInt = (String) Test.countOccurences(new ArrayList<>(), integer);
		Object o = Test.<Integer>test();
//		System.out.println(o);

		 System.out.println(Test.<String>compare("a", "b"));
//	        System.out.println(Test.<String>compare(new String(""), new Long(1)));
	        System.out.println(Test.compare(new String(""), new Long(1)));

//	        HashMap<, V>
	}
}

class Foo<E>
{
    public static <E> Foo<E> createFoo()
    {
        // ...
    	return (Foo<E>) new Object();
    }
}

class Bar<E>
{
    private Foo<E> member;

    public Bar()
    {
        member = Foo.<E>createFoo();
        // hello
    }
}
