
import java.sql.SQLException;
import java.util.Vector;
import java.io.*;

import Zql.*;

/**
 * This class shows how you can customize Zql so it supports custom functions
 * In this example, the following functions are defined :
 * - whynot (1 parameter)
 * - nvl (2 parameters)
 * - concat (any number of parameters)
 * For example, the following request will be accepted:
 *  select nvl(a, 0), concat(b, 'abc', d, 123), whynot(c) from mytable;
 */
public class CustomFct {

  public static void main(String args[]) {
    try {

      ZqlParser p = null;

      if(args.length < 1) {
        System.out.println("Reading SQL from stdin (quit; or exit; to quit)");
        p = new ZqlParser(System.in);
      } else {
        p = new ZqlParser(new DataInputStream(new FileInputStream(args[0])));
      }

      p.addCustomFunction("whynot", 1);
      p.addCustomFunction("nvl", 2);
      p.addCustomFunction("concat", ZUtils.VARIABLE_PLIST);

      // Read all SQL statements from input
      ZStatement st;
      while((st = p.readStatement()) != null) {

        System.out.println(st.toString()); // Display the statement

      }

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

};

