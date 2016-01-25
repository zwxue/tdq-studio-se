// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.commons.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * DOC mzhao bug 11128, try to load by current thread loader.
 */
public class TalendURLClassLoader extends URLClassLoader {

    private Map<String, Class<?>> classesMap = new HashMap<String, Class<?>>();

    /**
     * DOC zhao mzhao 11128, try to load by current thread loader.
     * 
     * @param urls
     */
    public TalendURLClassLoader(URL[] urls) {
        super(urls, TalendURLClassLoader.class.getClassLoader());
    }

    /**
     *ADD mzhao 11128, try to load by current thread loader.
     */
    @Override
    public Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> cls = (Class<?>) classesMap.get(className);
        if (cls == null) {
            try {
                cls = super.findClass(className);
            } catch (ClassNotFoundException cne) {
                // MOD mzhao 11128, try to load by current thread loader.e.g: when a class has a super class that needs
                // to load by current loader other than url loader.
                cls = getClass().getClassLoader().loadClass(className);
            }
            classesMap.put(className, cls);
        }
        return cls;
    }
}
