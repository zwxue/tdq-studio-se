import java.io.ByteArrayInputStream;
import java.util.Vector;

import Zql.ZInsert;
import Zql.ZStatement;
import Zql.ZqlParser;

public class InsertTest {

    public static void main(String args[]) {
        try {

            ZqlParser p = new ZqlParser();

            p.initParser(new ByteArrayInputStream(args[0].getBytes()));
            ZStatement st = p.readStatement();

            if (st instanceof ZInsert) {
                ZInsert ins = (ZInsert) st;
                Vector columns = ins.getColumns();
                Vector values = ins.getValues();
                System.out.println("Insert: Table=" + ins.getTable());
                for (int i = 0; i < columns.size(); i++) {
                    System.out.println("  " + columns.elementAt(i) + "=" + values.elementAt(i));
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

};
