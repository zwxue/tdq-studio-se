// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.group;

import java.util.List;
import java.util.Map;

/**
 * 
 * feature TDQ-1707, record comparison algorithm with blocking key for component tMatchGroupHadoop.
 */
public interface IRecordGrouping {


    /**
     * Key for retrieving the matching type.
     */
    public final static String MATCHING_TYPE = "MATCHING_TYPE";

    /**
     * Key used for retrieving the custom matching class when the user implements his own matching algorithm.
     */
    public final static String CUSTOMER_MATCH_CLASS = "CUSTOMER_MATCH_CLASS";

    /**
     * Key for retrieving the confidence weight.
     */
    public final static String CONFIDENCE_WEIGHT = "CONFIDENCE_WEIGHT";

    // zero based
    public final static String COLUMN_IDX = "COLUMN_IDX";

    /**
     * 
     * Prepare the parameters (key definition) of the matching algorithm.
     * 
     * @param matchingSettings <br>
     * Input matching attributes with the algorithms. e.g<br>
     * Map<String,String> recordMap = new HashMap<String,String>(); <br>
     * recordMap.put(INPUT_KEY_ATTRIBUTE,"name");<br>
     * recordMap.put(MATCHING_TYPE,"Exact");<br>
     * recordMap.put(CONFIDENCE_WEIGHT,"1.0");<br>
     * attributes.add(recordMap); <br>
     * ......<br>
     * attributes.add(recordMap1);
     * 
     * When all matching settings are passed to this class, call the {@link #initialize()} method.
     * 
     */
    public void add(Map<String, String> matchingSettings);

    /**
     * Method "initialize" must be called once in the initialization step.
     */
    public void initialize();

    /**
     * 
     * Groups similar records together according to the matching definitions.
     * 
     * @param inputRow
     */
    public void doGroup(String[] inputRow);

    /**
     * 
     * Clear the intermediate data generated during the process the matching.
     */
    public void end();

    /**
     * Get the match results in a block <br>
     * output row: all columns of input AND following fixed columns:<BR>
     * GID,GRP_SIZE,MASTER,SCORE
     * 
     */
    public List<String[]> getResults();

    /**
     * 
     * Set acceptable threshold.
     * 
     * @param acceptableThreshold
     */
    public void setAcceptableThreshold(float acceptableThreshold);

    /**
     * 
     * Set output distance details.
     * 
     * @param isOutputDistDetails
     */
    public void setIsOutputDistDetails(boolean isOutputDistDetails);
}
