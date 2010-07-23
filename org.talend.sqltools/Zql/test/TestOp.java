
import java.io.ByteArrayInputStream;

import Zql.ParseException;
import Zql.ZConstant;
import Zql.ZExpression;
import Zql.ZQuery;
import Zql.ZqlParser;

public class TestOp {

  public static void main(String args[]) {
    try {

      ZqlParser p = new ZqlParser();

      p.initParser(new ByteArrayInputStream(args[0].getBytes()));
      ZQuery st = (ZQuery) p.readStatement();
      System.out.println(st.toString()); // Display the statement

        ZExpression where = (ZExpression) st.getWhere();
        ZExpression prjNums = new ZExpression("OR");
        for (int i = 1; i < 4; i++) {
            prjNums.addOperand(
                new ZExpression(
                    "=",
                    new ZConstant("ID", ZConstant.COLUMNNAME),
                    new ZConstant("" + i, ZConstant.NUMBER)));
        }
 
        if (where != null) {
            //where.addOperand(new ZExpression("AND", prjNums));
            ZExpression w = new ZExpression("AND");
            w.addOperand(where);
            w.addOperand(prjNums);
            where = w;
        } else {
            where = prjNums;
        }
        st.addWhere(where);
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
