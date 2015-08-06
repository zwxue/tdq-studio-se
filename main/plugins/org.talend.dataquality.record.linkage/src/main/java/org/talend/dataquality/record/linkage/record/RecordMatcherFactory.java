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
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;

/**
 * @author scorreia
 * 
 * Factory to create the record matchers.
 */
public final class RecordMatcherFactory {

    private static Logger log = Logger.getLogger(RecordMatcherFactory.class);

    private static List<String> labels = new ArrayList<String>();

    /**
     * private RecordMatcherFactory constructor.
     */
    private RecordMatcherFactory() {
    }

    public static IRecordMatcher createMatcher(String matcherLabel) {
        for (RecordMatcherType type : RecordMatcherType.values()) {
            if (type.getLabel().equalsIgnoreCase(matcherLabel)) {
                return createMatcher(type);
            }
        }
        log.warn("matcher not found: [matcherLabel=" + matcherLabel + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }

    /**
     * Method "createMatcher".
     * 
     * @param name the name of the matcher
     * @return a matcher given the name or null
     */
    public static IRecordMatcher createMatcher(RecordMatcherType type) {
        if (type != null) {

            switch (type) {
            case simpleVSRMatcher:
                return new SimpleVSRRecordMatcher();
            default:
                break;
            }
        }
        // else
        return null;
    }

    /**
     * Method "getMatcherLabels".
     * 
     * @return the labels of all matchers
     */
    public static List<String> getMatcherLabels() {
        if (!labels.isEmpty()) {
            return labels;
        }
        // else fill in the labels
        for (RecordMatcherType type : RecordMatcherType.values()) {
            labels.add(type.getLabel());
        }
        return labels;
    }
}
