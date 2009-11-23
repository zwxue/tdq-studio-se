package org.talend.dataprofiler.ecos;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.talend.dataprofiler.ecos.pref.PreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class EcosPlugin extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.dataprofiler.ecos"; //$NON-NLS-1$

    // The shared instance
    private static EcosPlugin plugin;

    /**
     * The constructor
     */
    public EcosPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        initEcosTimeout();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * DOC bZhou Comment method "initEcosTimeout".
     */
    private void initEcosTimeout() {
        getPluginPreferences().setDefault(PreferenceConstants.ECOS_TIME_OUT_VALUE, EcosConstants.DEFAULT_TIME_OUT_VALUE);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static EcosPlugin getDefault() {
        return plugin;
    }

}
