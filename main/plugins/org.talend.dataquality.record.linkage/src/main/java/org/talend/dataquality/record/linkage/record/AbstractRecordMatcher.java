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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;

/**
 * DOC scorreia class global comment. Detailled comment
 */
abstract class AbstractRecordMatcher implements IRecordMatcher {

    private static Logger log = Logger.getLogger(AbstractRecordMatcher.class);

    protected int recordSize = 0;

    protected int[][] attributeGroups;

    protected double[] attributeWeights;

    /**
     * Indices of records to be compared.
     */
    protected int[] usedIndices;

    protected IAttributeMatcher[] attributeMatchers;

    /**
     * Indices of the Matchers to be used as blocking variables.
     */
    protected int[] blockedIndices;

    /**
     * Indices of records to be compared (without the blocked indices).
     */
    protected int[] usedIndicesNotblocked = null; // TODO to be reset

    // TODO be able to return the list of attribute weights
    protected double[] attributeMatchingWeights;

    /**
     * Tells whether attribute group are used.
     */
    protected boolean useGroups = false;

    /**
     * Threshold below which variables are blocked. Default value is 1.
     */
    protected double blockingThreshold = 1;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.matching.record.IRecordMatcher#setAttributeGroups(int[][])
     */
    public boolean setAttributeGroups(int[][] groups) {
        if (groups == null) {
            this.useGroups = true;
            return true;
        }
        if (groups.length != recordSize) {
            return false;
        }
        boolean atLeastOneGroup = false;
        for (int[] g : groups) {
            if (g == null || g.length == 0) {
                continue;
            }
            // else
            atLeastOneGroup = true;
            break;
        }
        if (!atLeastOneGroup) {
            return false;
        }
        this.attributeGroups = groups;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.matching.record.IRecordMatcher#setAttributeMatchers(org.talend.dataquality.matching.attribute
     * .IAttributeMatcher[])
     */
    public boolean setAttributeMatchers(IAttributeMatcher[] attrMatchers) {
        if (attrMatchers == null || attrMatchers.length != recordSize) {
            return false;
        }
        // else
        this.attributeMatchers = attrMatchers;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.matching.record.IRecordMatcher#setAttributeWeights(double[])
     */
    public boolean setAttributeWeights(double[] weights) {
        if (weights == null || recordSize != weights.length) {
            return false;
        }
        attributeWeights = normalize(weights);
        return true;
    }

    private double[] normalize(double[] weights) {
        List<Integer> indices = new ArrayList<Integer>();
        double total = 0;
        for (int i = 0; i < recordSize; i++) {
            final double w = weights[i];
            total += w;
            if (w != 0) {
                indices.add(i);
            }
        }

        this.usedIndices = new int[indices.size()];
        int j = 0;
        for (Integer idx : indices) {
            usedIndices[j++] = idx;
        }

        double[] normalized = new double[recordSize];
        for (int i = 0; i < recordSize; i++) {
            final double w = weights[i];
            normalized[i] = w / total;
        }
        return normalized;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.matching.record.IRecordMatcher#setRecordSize(int)
     */
    public void setRecordSize(int numberOfAttributes) {
        this.recordSize = numberOfAttributes;
        // initialize array of attribute matching weights
        this.attributeMatchingWeights = new double[recordSize];
    }

    /**
     * Method "internalScalarProduct" computes the scalar product of elements listed in usedIndices array.
     * 
     * @param v1
     * @param v2
     * @return
     */
    protected double internalScalarProduct(double[] v1, double[] v2) {
        assert usedIndices != null;
        double result = 0;
        for (int idx : usedIndices) {
            result += v1[idx] * v2[idx];
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.record.IRecordMatcher#setBlockingAttributeMatchers(int[])
     */
    public boolean setBlockingAttributeMatchers(int[] attrMatcherIndices) {
        for (int idx : attrMatcherIndices) {
            if (idx < 0 || idx >= recordSize) {
                log.error("the index must be between 0 and the size of the records"); //$NON-NLS-1$
                return false;
            }
        }
        this.blockedIndices = attrMatcherIndices;
        return true;
    }

    protected int[] getUsedIndicesNotblocked() {

        if (usedIndicesNotblocked == null) {
            List<Integer> indices = new ArrayList<Integer>();
            for (int usedIdx : usedIndices) {
                boolean isBlocked = false;
                if (blockedIndices != null) {
                    for (int blockedIdx : blockedIndices) {
                        if (blockedIdx == usedIdx) {
                            isBlocked = true;
                            break;
                        }
                    }
                }
                if (!isBlocked) {
                    indices.add(usedIdx);
                }
            }
            usedIndicesNotblocked = new int[indices.size()];
            for (int i = 0; i < indices.size(); i++) {
                usedIndicesNotblocked[i] = indices.get(i);
            }
        }
        return usedIndicesNotblocked;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.record.IRecordMatcher#setblockingThreshold(double)
     */
    public boolean setblockingThreshold(double threshold) {
        this.blockingThreshold = threshold;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.record.IRecordMatcher#getCurrentAttributeMatchingWeights()
     */
    public double[] getCurrentAttributeMatchingWeights() {
        return this.attributeMatchingWeights;
    }

}
