// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.utils;

import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;

/**
 * created by zshen on Nov 22, 2013 Detailled comment
 * 
 */
public class CustomMatcherLoaderHandler {

    private static final Map<String, URLClassLoader> customMatcherClassLoaderMAP = new HashMap<String, URLClassLoader>();

    /**
     * Getter for custommatchermap.
     * 
     * @return the custommatchermap
     */
    public static Map<String, URLClassLoader> getCustommatcherClassLoaderMap() {
        return customMatcherClassLoaderMAP;
    }

    /**
     * 
     * customMatcherClassLoaderMAP has been init by CustomAttributeMatcherHelper
     * 
     * @param className
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static IAttributeMatcher createInstance(String className) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        URLClassLoader classLoader = customMatcherClassLoaderMAP.get(className);
        if (classLoader != null) {

            return (IAttributeMatcher) classLoader.loadClass(CustomAttributeMatcherClassNameConvert.getClassName(className))
                    .newInstance();
        } else {
            // for the case which custom matcher need to be use on the tmatchGroup component
            return (IAttributeMatcher) Class.forName(className).newInstance();
        }
    }

}
