package org.talend.dataquality.record.linkage.group;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class RecordGroupingImplDefaultTest {

    /**
     * The input data.
     */
    private List<String[]> inputList = null;

    private IRecordGrouping recordGroup = null;

    @Before
    public void setUp() throws Exception {

        // account_num , last name, first name, middle initial, address, city, state, zipcode, blocking key
        // Read input data file.
        InputStream in = this.getClass().getResourceAsStream("incoming_customers.txt");
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, "|");
            inputList.add(fields);
        }


        // set the matching parameters
        // matching parameters for lname
        recordGroup = new RecordGroupingImplDefault();
        Map<String,String> lnameRecords =new HashMap<String,String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, "Jaro-Winkler");
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        recordGroup.add(lnameRecords);

        // matching parameters for state_province
        Map<String, String> accountRecords = new HashMap<String, String>();
        accountRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(6));
        accountRecords.put(IRecordGrouping.MATCHING_TYPE, "Levenshtein");
        accountRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(0.8));

        recordGroup.add(accountRecords);

        recordGroup.initialize();
    }


    @Test
    public void testDoGroup() {
        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
        // loop on all input rows.
        for (String[] inputRow : inputList) {
            recordGroup.doGroup(inputRow);
        }
        recordGroup.end();
        List<String[]> records = recordGroup.getResults();
        // Print the records

        for (String[] rds : records) {
            if (rds[0].equals("26997914900")) {
                    // The group size should be 5 for account 26997914900
                Assert.assertEquals(5, Integer.valueOf(rds[rds.length - 4]).intValue());
                }
            if (rds[0].equals("13700177100")) {
                    // The group size should be 6 for account 13700177100
                Assert.assertEquals(6, Integer.valueOf(rds[rds.length - 4]).intValue());
                }
            if (rds[0].equals("12083684802")) {
                    // The group size should be 4 for account 12083684802
                Assert.assertEquals(4, Integer.valueOf(rds[rds.length - 4]).intValue());
                }
            if (rds[0].equals("13758354187")) {
                    // The group size should be 2 for account 13758354187
                Assert.assertEquals(2, Integer.valueOf(rds[rds.length - 4]).intValue());
                }
            if (rds[0].equals("15114446900")) {
                    // The group size should be 2 for account 15114446900
                Assert.assertEquals(2, Integer.valueOf(rds[rds.length - 4]).intValue());
                }

            for (String rd : rds) {
                System.out.print(rd + ",");
            }
            System.out.println();

        }
    }

}
