// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.resource.xml;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import org.xml.sax.SAXException;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public final class TdqPropertieManager {
    
    

    private static TdqPropertieManager instance = null;
    
    private static Logger log = Logger.getLogger(TdqPropertieManager.class);

    private List<TdqProperties> propetiesCache = null;
    
    private Map<String, TdqFolderProperties> folderMapProperty = new HashMap<String, TdqFolderProperties>();
    private TdqPropertieManager() {
        propetiesCache = retrieve();
    }
    
    
    
    public static TdqPropertieManager getInstance() {
        if (instance == null) {
            instance = new TdqPropertieManager();
        }
        return instance;
    }

    public static void reload() {
        instance = null;
    }
    
    /**
     * 
     * DOC mzhao TdqPropertieManager class global comment. Detailled comment
     */
    public static enum TdqPropertiesType {
        FOLDER_PROPS(),
        DATABASE_PROPS();
        TdqPropertiesType() {
        }
    }
    
    private TdqFolderProperties getFolderProperties(IResource resource) {
        TdqFolderProperties tfp = folderMapProperty.get(resource.getProjectRelativePath().toString());
        if (tfp == null) {
            tfp = new TdqFolderProperties();
            folderMapProperty.put(resource.getProjectRelativePath().toString(), tfp);
        }
        return tfp;
    }
    
    public void addFolderProperties(IFolder folder, String key, String value) {
        TdqFolderProperties props = getFolderProperties(folder);
        props.setFolder(folder.getProjectRelativePath().toString());
        props.setProperties(key, value);
        add(props);
    }
    public void addFolderProperties(IResource res, QualifiedName key, String value) {
        TdqFolderProperties props = getFolderProperties(res);
        props.setFolder(res.getProjectRelativePath().toString());
        props.setProperties(key.getQualifier() + key.getLocalName(), value);
        add(props);
    }    
    public void addFolderProperties(IFolder folder, QualifiedName key, String value) {
        addFolderProperties(folder, key.getQualifier() + key.getLocalName(), value);
    }
    
    
    public void addDatabaseProperties(String key, String value) {
        TdqDatabaseProperties props = new TdqDatabaseProperties();
        props.setProperties(key, value);
        add(props);
    }
    
    // Add a property to cache
    private void add(TdqProperties property) {
        if (!propetiesCache.contains(property)) {
            propetiesCache.add(property);
        }
        persist();
    }
    
    // Remove a property from cache
    // public void remove(TdqProperties property) {
    // propetiesCache.remove(property);
    // }

    //Get database value for the specified key.
    public Object getDatabasePropertyValue(String key) {
        for(TdqProperties prop:propetiesCache){
            if (prop instanceof TdqDatabaseProperties) {
                return prop.getProperties(key);
            }
        }
        return null;
    }
    // Get folder value for the specified key.
    public Object getFolderPropertyValue(String folderName, String key) {
        for (TdqProperties prop : propetiesCache) {
            if (prop instanceof TdqFolderProperties) {
                if (((TdqFolderProperties) prop).getFolder().trim().equals(folderName)) {
                    return prop.getProperties(key);
                }
            }
        }
        return null;
    }
    public Object getFolderPropertyValue(IFolder folder, String key) {
        return getFolderPropertyValue(folder.getProjectRelativePath().toString(), key);
    }
    public Object getFolderPropertyValue(IFolder folder, QualifiedName key) {
        return getFolderPropertyValue(folder.getProjectRelativePath().toString(), key.getQualifier() + key.getLocalName());
    }
    public Object getFolderPropertyValue(IResource resource, QualifiedName key) {
        return getFolderPropertyValue(resource.getProjectRelativePath().toString(), key.getQualifier() + key.getLocalName());
    }
    public Object getFolderPropertyValue(String folderName, QualifiedName key) {
        return getFolderPropertyValue(folderName, key.getQualifier() + key.getLocalName());
    } 
    public ReturnCode persist() {
        ReturnCode rc = new ReturnCode();
        try {
            write(propetiesCache);
        } catch (IOException e) {
            log.error(e, e);
            rc.setReturnCode(e.getMessage(), Boolean.FALSE);
        } catch (SAXException e) {
            log.error(e, e);
            rc.setReturnCode(e.getMessage(), Boolean.FALSE);
        } catch (IntrospectionException e) {
            log.error(e, e);
            rc.setReturnCode(e.getMessage(), Boolean.FALSE);
        }
        return rc;
    }

    private List<TdqProperties> retrieve() {
        List<TdqProperties> list = new ArrayList<TdqProperties>();
        try {
            parse(list);
        } catch (Exception e) {
            log.error(e, e);
            return null;
        }
        return list;
    }

    private void write(List<TdqProperties> properties) throws IOException, SAXException, IntrospectionException {
        // Start by preparing the writer
        // We'll write to a string

        File file = getPropertiesFile(PROPERTIES_FILE);
        FileWriter outputWriter = new FileWriter(file);

        
        // Betwixt just writes out the bean as a fragment
        // So if we want well-formed xml, we need to add the prolog
        outputWriter.write("<?xml version='1.0' encoding='UTF-8'?>\n");//$NON-NLS-1$

        // Create a BeanWriter which writes to our prepared stream
        BeanWriter beanWriter = new BeanWriter(outputWriter);

        // Configure betwixt
        // For more details see java docs or later in the main documentation
        beanWriter.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
        beanWriter.getBindingConfiguration().setMapIDs(false);
        beanWriter.enablePrettyPrint();

        // If the base element is not passed in, Betwixt will guess
        // But let's write example bean as base element 'person'
        beanWriter.write("TdqProperties", properties);//$NON-NLS-1$
        // Write to System.out
        // (We could have used the empty constructor for BeanWriter
        // but this way is more instructive)

        // Betwixt writes fragments not documents so does not automatically close
        // writers or streams.
        // This example will do no more writing so close the writer now.
        outputWriter.close();

    }

    private void parse(Object parseObj) throws Exception {
        Digester d = null;
        try {
            URL ruleUrl = getPropertiesURL(PROPERTIES_RULE_FILE);
            d = DigesterLoader.createDigester(ruleUrl);
            d.setClassLoader(this.getClass().getClassLoader());
            // Prime the digester stack with an object for rules to
            // operate on. Note that it is quite common for "this"
            // to be the object pushed.
            d.push(parseObj);
            // Process the input file.
            URL parseXmlUrl = getPropertiesURL(PROPERTIES_FILE);
            d.parse(parseXmlUrl.openStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private File getPropertiesFile(String propFileStr) {
        IProject rootProject = ResourceManager.getRootProject();
        IFile file = rootProject.getFile(propFileStr);
       
        return WorkspaceUtils.ifileToFile(file);
    }
    
    private URL getPropertiesURL(String propFileStr) throws MalformedURLException {
        IProject rootProject = ResourceManager.getRootProject();
        IFile file = rootProject.getFile(propFileStr);
        return file.getLocationURI().toURL();
    }

    public static final String PROPERTIES_FILE = "TdqProperties.xml";

    public static final String PROPERTIES_RULE_FILE = "TdqProperties_rule.xml";
}
