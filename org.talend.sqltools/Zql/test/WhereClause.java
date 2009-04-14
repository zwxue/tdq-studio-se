/**
 * WARNING: This code uses INTERNAL APIs ZqlJJParser should not be used directly by Zql users
 */

import java.io.ByteArrayInputStream;

import Zql.ZExp;
import Zql.ZqlJJParser;

public class WhereClause {

    public static void main(String args[]) {
        try {

            ZqlJJParser p = new ZqlJJParser(new ByteArrayInputStream(args[0].getBytes()));

            ZExp e = p.SQLExpression();
            System.out.println(e.toString());

        } catch (Exception e) {
            System.out.println(e);
        }
    }

};
