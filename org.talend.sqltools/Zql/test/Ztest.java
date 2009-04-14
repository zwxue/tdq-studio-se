import java.io.ByteArrayInputStream;
import java.util.Vector;

import Zql.ZQuery;
import Zql.ZSelectItem;
import Zql.ZStatement;
import Zql.ZqlParser;

public class Ztest {

    public static void main(String args[]) {
        try {

            ZqlParser p = new ZqlParser();

            p.initParser(new ByteArrayInputStream(args[0].getBytes()));
            ZStatement st = p.readStatement();
            System.out.println(st.toString()); // Display the statement

            ZQuery q = (ZQuery) st;
            Vector v = q.getSelect();
            for (int i = 0; i < v.size(); i++) {
                ZSelectItem it = (ZSelectItem) v.elementAt(i);
                System.out.println("col=" + it.getColumn() + ",agg=" + it.getAggregate());
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

};
