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
package org.talend.dataprofiler.service.semantic;

/**
 * created by zhao on 2015-4-27 Semantic category bean which can be a bridge from UI to semantic API.
 *
 */
public class SemanticCategory {

    public SemanticCategory(String categoryString, long count, double freq) {
        this.semanticCategory = categoryString;
        this.count = count;
        this.frequencies = freq;
    }

    String semanticCategory = null;

    long count = 0l;

    double frequencies = 0d;

    /**
     * Getter for semanticCategory.
     * 
     * @return the semanticCategory
     */
    public String getSemanticCategory() {
        return this.semanticCategory;
    }

    /**
     * Getter for count.
     * 
     * @return the count
     */
    public long getCount() {
        return this.count;
    }

    /**
     * Getter for frequencies.
     * 
     * @return the frequencies
     */
    public double getFrequencies() {
        return this.frequencies;
    }

}
