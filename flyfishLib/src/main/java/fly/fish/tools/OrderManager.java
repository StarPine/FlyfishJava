package fly.fish.tools;

import java.text.SimpleDateFormat;
import java.util.Random;

public class OrderManager {
	public static String getorder(){
	 	  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		  String ly_time = sdf.format(new java.util.Date());		
		  Random rnd = new Random();
          for (int i = 0; i <5; i++) {
        	  ly_time+=  rnd.nextInt(5)+"";
		}
		return ly_time;
	}
}
