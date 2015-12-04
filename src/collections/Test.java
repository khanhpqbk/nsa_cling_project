package collections;

import java.util.*;

public class Test {

   public static void main(String args[]) {
      // create a hash set
      HashSet<String> hs = new HashSet<>();
      // add elements to the hash set
      hs.add("B");
      hs.add("A");
      hs.add("D");
      hs.add("E");
      hs.add("C");
      hs.add("D");
//      System.out.println(hs);
      Iterator<String> i = hs.iterator();
      while (i.hasNext()) {
    	  String str = i.next();
//    	  System.out.println(str);
    	  if(str.equals("A"))
    		  i.remove();
      }

      for (String s : hs) {
    	  if (s.equals("B"))
    		  hs.remove("B");
      }

      System.out.println(hs);
   }
}