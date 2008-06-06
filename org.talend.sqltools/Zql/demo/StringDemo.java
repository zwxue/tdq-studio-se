
import java.io.*;
import Zql.*;

public class StringDemo {

  public static void main(String args[]) {
    try {

      ZqlParser p = new ZqlParser();

      p.initParser(new ByteArrayInputStream(args[0].getBytes()));
      ZStatement st = p.readStatement();
      System.out.println(st.toString()); // Display the statement

    } catch(ParseException e) {
      System.err.println("PARSE EXCEPTION:");
      e.printStackTrace(System.err);
    } catch(Error e) {
      System.err.println("ERROR");
    } catch(Exception e) {
      System.err.println("CLASS" + e.getClass());
      e.printStackTrace(System.err);
    }
  }

};
