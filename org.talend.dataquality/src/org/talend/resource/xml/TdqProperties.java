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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class TdqProperties {

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public TdqProperties() {
        properties = new HashMap<String, Object>();
    }

    public Object getProperties(String key) {
        return properties.get(key);
    }

    public void setProperties(String key, Object value) {
        this.properties.put(key, value);
    }
    
    
    protected Map<String, Object> properties = null;

    public Map<String, Object> getProperties() {
        return properties;
    }
}
