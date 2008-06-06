
import java.util.Vector;
import java.io.*;
import Zql.*;

public class MergeQueries {

  public static void main(String args[]) {
    try {

      String q1 = "select a.id, a.descr from acce.producto a;";
      String q2 = "select b.id, b.price from info.ventas b;";

      ZqlParser p = new ZqlParser();

      // Parse query 1
      p.initParser(new ByteArrayInputStream(q1.getBytes()));
      ZQuery st1 = (ZQuery)p.readStatement();
      System.out.println(st1.toString()); // Display the query

      // Parse query 2
      p.initParser(new ByteArrayInputStream(q2.getBytes()));
      ZQuery st2 = (ZQuery)p.readStatement();
      System.out.println(st2.toString()); // Display the query

      // Extract "select" and "from" parts of query 1
      Vector cols = st1.getSelect();
      Vector from = st1.getFrom();

      // Extract "select" and "from" parts of query 2
      Vector cols2 = st2.getSelect();
      Vector f2 = st2.getFrom();

      // Append the 2 "select" parts
      for(int i=0; i<cols2.size(); i++) cols.addElement(cols2.elementAt(i));

      // Append the 2 "from" parts
      for(int i=0; i<f2.size(); i++) from.addElement(f2.elementAt(i));

      // Build the new query (adding a new "where" clause)
      ZQuery q = new ZQuery();
      q.addSelect(cols);
      q.addFrom(from);
      ZExpression where = new ZExpression("=",
       new ZConstant("a.id", ZConstant.COLUMNNAME),
       new ZConstant("b.id", ZConstant.COLUMNNAME));
      q.addWhere(where);

      // Display the new query
      System.out.println(q);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

};

