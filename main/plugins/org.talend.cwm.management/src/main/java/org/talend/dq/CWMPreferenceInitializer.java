package org.talend.dq;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.dq.analysis.memory.AnalysisThreadMemoryChangeNotifier;

public class CWMPreferenceInitializer extends AbstractPreferenceInitializer {

    public CWMPreferenceInitializer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences prefs = new DefaultScope().getNode(CWMPlugin.getDefault().getBundle().getSymbolicName());
        prefs.putBoolean(PluginConstant.CONNECTION_TIMEOUT, false);
        prefs.putBoolean(PluginConstant.FILTER_TABLE_VIEW_COLUMN, true);
        PlatformUI.getPreferenceStore().setDefault(AnalysisThreadMemoryChangeNotifier.ANALYSIS_AUTOMATIC_MEMORY_CONTROL, false);

    }

}
