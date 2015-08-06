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
package org.talend.dq.analysis.memory;

import org.talend.commons.utils.platform.PluginChecker;

/**
 * Singleton class to manage memory when running analyses.
 */
public final class AnalysisThreadMemoryChangeNotifier extends AbstractMemoryChangeNotifier {

    private static AnalysisThreadMemoryChangeNotifier instance;

    private static final int DEFALUT_MEMORY_THRESHOLD_TOP = 4;// 4MB free by default for TOP

    private static final int DEFALUT_MEMORY_THRESHOLD_TDQ = 16;// 16MB free by default for TOP

    /**
     * Method "getInstance".
     * 
     * @return the singleton instance.
     */
    public static synchronized AnalysisThreadMemoryChangeNotifier getInstance() {
        if (null == instance) {
            instance = new AnalysisThreadMemoryChangeNotifier();
        }
        // MOD yyi 2012-06-19 TDQ-4916: reload for each instance to guarantee the threshold has been setted to the jvm.
        // MOD TDQ-7674 scorreia 2013-09-10 avoid setting these values at each call (calls happen for every analyzed
        // data row)
        // instance.reloadPerference();

        return instance;
    }

    private AnalysisThreadMemoryChangeNotifier() {
        super();
    }

    /**
     * Method "convertToMB" convert bytes to MB.
     * 
     * @param numByte the number of bytes.
     * @return the number of MB.
     */
    public static int convertToMB(long numByte) {
        return Math.round(numByte / 1024 / 1024);
    }

    // Get top or d defulat method.
    /**
     * FIXME remove this method in 6.0
     * 
     * @deprecated this method will be removed. Do not use it.
     */
    @Deprecated
    public static int getDefaultThresholdValue() {
        if (PluginChecker.isOnlyTopLoaded()) {
            return DEFALUT_MEMORY_THRESHOLD_TOP;
        }
        return DEFALUT_MEMORY_THRESHOLD_TDQ;

    }

}
