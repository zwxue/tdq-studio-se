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
package org.talend.dq.analysis.category;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFUtil;
import org.talend.dataquality.analysis.category.AnalysisCategories;
import org.talend.dataquality.analysis.category.util.CategorySwitch;

/**
 * @author scorreia
 * 
 * This class is a handler for the categories of analysis. A tree structure of categories is loaded from a configuration
 * file.
 */
public final class CategoryHandler {

    private static Logger log = Logger.getLogger(CategoryHandler.class);

    private static AnalysisCategories analysisCategories;

    private CategoryHandler() {
    }

    private static AnalysisCategories loadFromFile() {
        EMFUtil util = EMFUtil.getInstance();
        String pathName = "/org.talend.cwm.management/My.category";
        URI uri = URI.createPlatformPluginURI(pathName, false);
        Resource catFile = null;
        try {
            catFile = util.getResourceSet().getResource(uri, true);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
        if (catFile == null) {
            // try to load from a local file
            catFile = util.getResourceSet().getResource(URI.createFileURI(".." + File.separator + pathName), true);
        }
        if (catFile == null) {
            log.error("No resource found at " + pathName + " URI= " + uri);
            return null;
        }
        EList<EObject> contents = catFile.getContents();
        if (contents == null) {
            log.error("No category found in given resource: " + uri);
            return null;
        }
        CategorySwitch<AnalysisCategories> catSwitch = new CategorySwitch<AnalysisCategories>() {

            @Override
            public AnalysisCategories caseAnalysisCategories(AnalysisCategories object) {
                return object;
            }
        };

        return catSwitch.doSwitch(contents.get(0));
    }

    /**
     * Method "getAnalysisCategories".
     * 
     * @return the singleton analysis categories (or null if a problem occured)
     */
    public static AnalysisCategories getAnalysisCategories() {
        if (analysisCategories == null) {
            analysisCategories = loadFromFile();
        }

        return analysisCategories;
    }
}
