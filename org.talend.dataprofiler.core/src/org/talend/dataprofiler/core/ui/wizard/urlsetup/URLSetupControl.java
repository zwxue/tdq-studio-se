// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.urlsetup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.dataprofiler.core.PluginConstant;


/**
 * 
 */
public abstract class URLSetupControl extends Composite {
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
//    private String urlPattern;
//    private Map properties = Collections.synchronizedMap(new HashMap());
//    private final JDBCDriver driver;
    private String connectionURL;
    
    protected SupportDBUrlType dbType;

    /**
     * @param parent
     * @param style
     */
    public URLSetupControl(Composite parent, SupportDBUrlType dbType) {
        super(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        setLayout(layout);
//        this.urlPattern = AdapterFactory.getInstance().getURLPattern(driver.getClassName());
        
//        setPropertyDefaults();
        this.dbType = dbType;
        setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType));
    }

    /**
     * 
     */
    protected void createPart() {
        Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
        group.setText("DB URL"); 
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        
        createPart(group);
    }

    /**
     * Gets the possible default values for the properties (for example the port has usually a default)
     * and uses them in the text boxes.
     */
//    private void setPropertyDefaults() {
//        DatabaseAdapter adapter = 
//            AdapterFactory.getInstance().getAdapter(getDriver().getType());
//        Map properties = (adapter == null) ? new HashMap() : adapter.getDefaultConnectionParameters();
//        for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
//            String key = (String) i.next();
//            putProperty(key, (String) properties.get(key));
//            
//        }
//    }

    /**
     * This function will be redefined in subclasses, with a different format for
     * each defined set of parameters.
     * 
     * TODO: Make something more generic, where simply adding an URL with a set of parameters will generate the appropiate text boxes
     *
     * @param parent
     */
    protected abstract void createPart(Composite parent);
    
//    private Map getProperties() {
//        return this.properties;
//    }

//    protected String getProperty(String name) {
//        String value = (String) this.properties.get(name);
//        return value == null ? "" : value;
//    }

//    protected void putProperty(String name, String value) {
//        this.properties.put(name, value);
//        setConnectionURL(DBUrlStore.getInstance().getDBUrl(dbType, host, port, dbName));
//    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
//    protected JDBCDriver getDriver() {
//        return this.driver;
//    }
    public String getConnectionURL() {
        return this.connectionURL == null ? "" : this.connectionURL; //$NON-NLS-1$
    }
    public void setConnectionURL(String connectionURL) {
        if (connectionURL != null && !connectionURL.equals(this.connectionURL)) {
            String original = this.connectionURL;
            this.connectionURL = connectionURL;
            firePropertyChange(PluginConstant.CONNECTION_URL_PROPERTY, original, connectionURL);
        }
    }
}
