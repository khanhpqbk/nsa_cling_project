package genericprogramming;

import java.util.HashMap;
import java.util.Map;

public class Animal {
    private Map<String,Animal> friends = new HashMap<String,Animal>();

    public void addFriend(String name, Animal animal){
        friends.put(name,animal);
    }

    public<T extends Animal> T callFriend(String name){
        return (T)friends.get(name);
    }

}

class Mouse extends Animal {
	public void cheet() {
		System.out.println("cheet");
	}
}

class Dog extends Animal {
	public void bark() {
		System.out.println("bark");
	}
}

class Duck extends Animal {
	public void quack() {
		System.out.println("quack");
	}
}


class Main2 {
	public static void main(String[] args) {
		Mouse jerry = new Mouse();
		jerry.addFriend("spike", new Dog());
		jerry.addFriend("quacker", new Duck());

		jerry.<Dog>callFriend("spike").bark();
	}
}
