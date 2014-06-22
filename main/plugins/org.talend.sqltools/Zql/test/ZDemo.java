import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Vector;

import Zql.ZEval;
import Zql.ZExpression;
import Zql.ZFromItem;
import Zql.ZInsert;
import Zql.ZQuery;
import Zql.ZSelectItem;
import Zql.ZStatement;
import Zql.ZTuple;
import Zql.ZqlParser;

/**
 * <pre>
 * ZDemo is able to send SQL queries to simple CSV (comma-separated values)
 * files; the CSV syntax used here is very simple:
 *  The 1st line contains the column names
 *  Other lines contain column values (tuples)
 *  Values are separated by commas, so they can't contain commas (it's just
 *  for demo purposes).
 * Example:
 * Create a num.db text file that contains the following:
 *  a,b,c,d,e
 *  1,1,1,1,1
 *  2,2,2,2,2
 *  1,2,3,4,5
 *  5,4,3,2,1
 * You can then run ZDemo, and query it; some legal queries follow:
 *  select * from num;
 *  select a, b from num;
 *  select a+b, c from num;
 *  select * from num where a = 1 or e = 1;
 *  select * from num where a = 1 and b = 1 or e = 1;
 *  select d, e from num where a + b + c &lt;= 3;
 *  select * from num where 3 = a + b + c;
 *  select * from num where a = b or e = d - 1;
 *  select * from num where b ** a &lt;= 2;
 * </pre>
 */
public class ZDemo {

    public static void main(String args[]) {
        try {

            ZqlParser p = null;

            if (args.length < 1) {
                System.out.println("Reading SQL from stdin (quit; or exit; to quit)");
                p = new ZqlParser(System.in);
            } else {
                p = new ZqlParser(new DataInputStream(new FileInputStream(args[0])));
            }

            // Read all SQL statements from input
            ZStatement st;
            while ((st = p.readStatement()) != null) {

                System.out.println(st.toString()); // Display the statement

                if (st instanceof ZQuery) { // An SQL query: query the DB
                    queryDB((ZQuery) st);
                } else if (st instanceof ZInsert) { // An SQL insert
                    insertDB((ZInsert) st);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Query the database
     */
    static void queryDB(ZQuery q) throws Exception {

        Vector sel = q.getSelect(); // SELECT part of the query
        Vector from = q.getFrom(); // FROM part of the query
        ZExpression where = (ZExpression) q.getWhere(); // WHERE part of the query

        if (from.size() > 1) {
            throw new SQLException("Joins are not supported");
        }

        // Retrieve the table name in the FROM clause
        ZFromItem table = (ZFromItem) from.elementAt(0);

        // We suppose the data is in a text file called <tableName>.db
        // <tableName> is the table name in the FROM clause
        BufferedReader db = new BufferedReader(new FileReader(table.getTable() + ".db"));

        // Read the column names (the 1st line of the .db file)
        ZTuple tuple = new ZTuple(db.readLine());

        ZEval evaluator = new ZEval();

        // Now, each line in the .db file is a tuple
        String tpl;
        while ((tpl = db.readLine()) != null) {

            tuple.setRow(tpl);

            // Evaluate the WHERE expression for the current tuple
            // Display the tuple if the condition evaluates to true

            if (where == null || evaluator.eval(tuple, where)) {
                DisplayTuple(tuple, sel);
            }

        }

        db.close();
    }

    /**
     * Display a tuple, according to a SELECT map
     */
    static void DisplayTuple(ZTuple tuple, Vector map) throws Exception {

        // If it is a "select *", display the whole tuple
        if (((ZSelectItem) map.elementAt(0)).isWildcard()) {
            System.out.println(tuple.toString());
            return;
        }

        ZEval evaluator = new ZEval();

        // Evaluate the value of each select item
        for (int i = 0; i < map.size(); i++) {

            ZSelectItem item = (ZSelectItem) map.elementAt(i);
            System.out.print(evaluator.evalExpValue(tuple, item.getExpression()).toString());

            if (i == map.size() - 1)
                System.out.println("");
            else
                System.out.print(", ");
        }
    }

    static void insertDB(ZInsert ins) throws Exception {
        System.out.println("Should implement INSERT here");
        System.out.println(ins.toString());
    }

};
