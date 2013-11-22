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
package org.talend.dq.helper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.utils.CustomAttributeMatcherClassNameConvert;
import org.talend.dataquality.record.linkage.utils.CustomMatcherLoaderHandler;
import org.talend.resource.ResourceManager;

/**
 * created by zshen on Nov 14, 2013 Detailled comment
 * 
 */

public class CustomAttributeMatcherHelper {
    public static final String FILEPROTOCOL = "File"; //$NON-NLS-1$

    public static final String SEPARATOR = "||"; //$NON-NLS-1$

    private static final Map<String, IAttributeMatcher> customMatcherMAP = new HashMap<String, IAttributeMatcher>();

    private static Logger log = Logger.getLogger(CustomAttributeMatcherHelper.class);

    /**
     * 
     * Create new instance for special custom matcher class
     * 
     * @param classPathParameter
     * @return
     */
    public static IAttributeMatcher getCustomMatcher(String classPathParameter) {
        if (customMatcherMAP.get(classPathParameter) != null) {
            return customMatcherMAP.get(classPathParameter);
        }
        return loadClass(classPathParameter);
    }

    public static void updateCustomMatcherMap(String classPathParameter) {
        loadClass(classPathParameter);
    }

    /**
     * DOC zshen Comment method "getClassURLList".
     * 
     * @param classPathParameter
     * @return
     */
    private static List<URL> getClassURLList(String classPathParameter) {

        String[] allElements = classPathParameter.split(CustomAttributeMatcherClassNameConvert.REGEXKEY);
        List<URL> jarURLs = new ArrayList<URL>();
        for (int index = 0; index < allElements.length - 1; index++) {
            try {
                IFile jarFile = ResourceManager.getUDIJarFolder().getFile(allElements[index]);
                // jarURLs.add(new URL(CustomAttributeMatcherHelper.FILEPROTOCOL, StringUtils.EMPTY,
                // allElements[index]));
                jarURLs.add(new URL(CustomAttributeMatcherHelper.FILEPROTOCOL, StringUtils.EMPTY, jarFile.getLocation()
                        .toOSString()));
            } catch (MalformedURLException e) {
                log.error(e, e);
            }
        }
        return jarURLs;
    }

    /**
     * DOC zshen Comment method "loadClass".
     * 
     * @param classPathParameter
     * @param listURL
     */
    private static IAttributeMatcher loadClass(String classPathParameter) {
        URLClassLoader urlClassLoader = getUrlClassLoader(classPathParameter);
        IAttributeMatcher newInstance = null;
        try {
            Class<?> loadClass = urlClassLoader.loadClass(getClassName(classPathParameter));
            newInstance = (IAttributeMatcher) loadClass.newInstance();
            customMatcherMAP.put(classPathParameter, newInstance);
        } catch (ClassNotFoundException e) {
            log.error(e, e);
        } catch (InstantiationException e) {
            log.error(e, e);
        } catch (IllegalAccessException e) {
            log.error(e, e);
        }
        return newInstance;
    }

    /**
     * DOC zshen Comment method "getClassName".
     * 
     * @param classPathParameter
     * @return
     */
    public static String getClassName(String classPathParameter) {
        return CustomAttributeMatcherClassNameConvert.getClassName(classPathParameter);
    }

    public static String[] splitJarPath(String classPathParameter) {
        String[] allElements = classPathParameter.split(CustomAttributeMatcherClassNameConvert.REGEXKEY);
        String[] jarPathElements = new String[allElements.length - 1];
        for (int index = 0; index < allElements.length - 1; index++) {
            jarPathElements[index] = new File(allElements[index]).getName();
        }
        return jarPathElements;
    }

    /**
     * 
     * create classLoader
     * 
     * @param key
     * @return
     */
    public static URLClassLoader createClassLoader(String key) {
        URLClassLoader urlClassLoader = initClassLoader(key);
        CustomMatcherLoaderHandler.getCustommatcherClassLoaderMap().put(key, urlClassLoader);
        return urlClassLoader;
    }

    /**
     * Getter for urlClassLoader.
     * 
     * @return the urlClassLoader
     */
    public static URLClassLoader getUrlClassLoader(String key) {
        URLClassLoader urlClassLoader = CustomMatcherLoaderHandler.getCustommatcherClassLoaderMap().get(key);
        if (urlClassLoader == null) {
            urlClassLoader = createClassLoader(key);
        }
        return urlClassLoader;
    }

    /**
     * DOC zshen Comment method "initClassLoader".
     */
    private static URLClassLoader initClassLoader(String key) {
        List<URL> listURL = getClassURLList(key);
        URLClassLoader urlClassLoader = new URLClassLoader(listURL.toArray(new URL[0]),
                CustomAttributeMatcherHelper.class.getClassLoader());
        return urlClassLoader;

    }
}
