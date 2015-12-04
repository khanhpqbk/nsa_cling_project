package genericprogramming;

import java.util.Collection;
import java.util.List;

public class Shape {
	public void draw() {

	}

	public static void drawAll(Collection<? extends Shape> shapes) {
		for ( Shape s : shapes )
			s.draw();
	}

	public static void main(String[] args) {
		List<Rect> rects = null;
		drawAll(rects);

		Collection<String> coll1 = null;
		Collection<Integer> coll2Collection = null;
		coll2Collection.containsAll(coll1);
	}
}

class Oval extends Shape {

}

class Rect extends Shape {

}