// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.record;

import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;

/**
 * @author scorreia
 * 
 * Interface of record matcher.
 */
public interface IRecordMatcher {

    /**
     * Method "setRecordSize".
     * 
     * @param numberOfAttributes the number of attributes of a record.
     */
    void setRecordSize(int numberOfAttributes);


    /**
     * Method "setAttributeWeights".
     * 
     * @param weights the weight of each attribute of the records
     * @return false when the weights cannot be applied to the given records.
     */
    boolean setAttributeWeights(double[] weights);

    /**
     * Method "setAttributeGroups".
     * 
     * @param groups the indices of the attributes that can be compared.
     * @return false when the given groups are not applicable to the given records.
     */
    boolean setAttributeGroups(int[][] groups);

    /**
     * Method "setAttributeMatchers".
     * 
     * @param attributeMatchers a matcher for each attribute
     * @return false if the number of matcher is not appropriate (0 or the size of the array does not correspond to the
     * expected number of attributes)
     */
    boolean setAttributeMatchers(IAttributeMatcher[] attributeMatchers);
    
    /**
     * Method "setBlockingAttributeMatchers".
     * 
     * @param attrMatcherIndices the indices of the attribute matcher to be used as blocking variables
     * @return false if a problem.
     */
    boolean setBlockingAttributeMatchers(int[] attrMatcherIndices);

    /**
     * Method "getMatchingWeight".
     * 
     * @param record1
     * @param record2
     * @return the matching weight of the given two records
     */
    double getMatchingWeight(String[] record1, String[] record2);

    /**
     * Method "getCurrentAttributeMatchingWeights".
     * 
     * @return the list of attribute matching weights for the last couple of records given in the
     * {@link #getMatchingWeight(String[], String[])} method
     */
    double[] getCurrentAttributeMatchingWeights();

    /**
     * Method "setblockingThreshold" sets a threshold value. When the attribute matching weight is below this value when
     * comparing blocking attributes, then the records will not match.
     * 
     * @param threshold the value (default value should be 1)
     * @return true if ok
     */
    boolean setblockingThreshold(double threshold);
}
