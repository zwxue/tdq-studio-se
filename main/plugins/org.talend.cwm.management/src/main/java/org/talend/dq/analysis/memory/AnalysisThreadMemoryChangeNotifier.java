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

import javax.management.NotificationFilter;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.utils.platform.PluginChecker;

/**
 * DOC yyi class global comment. Detailled comment
 */
public final class AnalysisThreadMemoryChangeNotifier extends AbstractMemoryChangeNotifier {

    private static AnalysisThreadMemoryChangeNotifier instance;

    private static final int DEFALUT_MEMORY_THRESHOLD_TOP = 4;// 4MB free by default for TOP

    private static final int DEFALUT_MEMORY_THRESHOLD_TDQ = 16;// 16MB free by default for TOP

    public static final String ANALYSIS_AUTOMATIC_MEMORY_CONTROL = "ANALYSIS_AUTOMATIC_MEMORY_CONTROL";//$NON-NLS-1$

    public static final String ANALYSIS_MEMORY_THRESHOLD = "ANALYSIS_MEMORY_THRESHOLD";//$NON-NLS-1$

    public static synchronized AnalysisThreadMemoryChangeNotifier getInstance() {
        if (null == instance) {
            instance = new AnalysisThreadMemoryChangeNotifier();
        }
        // MOD yyi 2012-06-19 TDQ-4916: reload for each instance to guarantee the threshold has been setted to the jvm.
        instance.reloadPerference();
        return instance;
    }

    private boolean isThresholdControl;

    private int userDefineThreshold;

    private AnalysisThreadMemoryChangeNotifier() {
        super();
    }

    @Override
    protected NotificationFilter getListenerFilter() {
        return null;
    }

    @Override
    protected NotificationFilter getListenerHandBack() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.memory.AbstractMemoryChangeNotifier#initialize()
     */
    @Override
    protected boolean initialize() {
        boolean initialize = super.initialize();

        isThresholdControl = PlatformUI.getPreferenceStore().getBoolean(ANALYSIS_AUTOMATIC_MEMORY_CONTROL);
        userDefineThreshold = PlatformUI.getPreferenceStore().getInt(ANALYSIS_MEMORY_THRESHOLD);
        reloadPerference();

        PlatformUI.getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == ANALYSIS_AUTOMATIC_MEMORY_CONTROL
                        && isThresholdControl != Boolean.valueOf(event.getNewValue().toString())) {
                    isThresholdControl = Boolean.valueOf(event.getNewValue().toString());
                    reloadPerference();
                } else if (event.getProperty() == ANALYSIS_MEMORY_THRESHOLD
                        && userDefineThreshold != Integer.valueOf(event.getNewValue().toString())) {
                    userDefineThreshold = Integer.valueOf(event.getNewValue().toString());
                    reloadPerference();
                }
            }
        });

        return initialize;
    }

    public void reloadPerference() {
        if (isThresholdControl) {
            // The #userDefineThreshold changes to the free memory, if the free memory size less than
            // userDefineThreshold vlaue the analysis shoud be stop.
            setUsageThreshold(megaToByte(userDefineThreshold));
        } else {
            // Clear limit
            setUsageThreshold(0);
        }
    }

    public static long megaToByte(int numMb) {
        return numMb * 1024 * 1024 - 512 * 1024;
    }

    public static int convertToMB(long numByte) {
        return Math.round(numByte / 1024 / 1024);
    }

    public static boolean isAnaMemControl() {
        boolean canControl = PlatformUI.getPreferenceStore().getBoolean(
                AnalysisThreadMemoryChangeNotifier.ANALYSIS_AUTOMATIC_MEMORY_CONTROL);
        return canControl;
    }

    // Get top or d defulat method.
    public static int getDefaultThresholdValue() {
        if (PluginChecker.isOnlyTopLoaded()) {
            return DEFALUT_MEMORY_THRESHOLD_TOP;
        }
        return DEFALUT_MEMORY_THRESHOLD_TDQ;

    }

}
