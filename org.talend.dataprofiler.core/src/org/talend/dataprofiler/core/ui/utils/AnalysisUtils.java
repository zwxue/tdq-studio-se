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
package org.talend.dataprofiler.core.ui.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * the Analysis's utility class which associated with UI.
 */
public class AnalysisUtils {

    /**
     * used for table analysis-- select dq rules, add filter for match rule folder TDQ-8001
     * 
     * @return
     */
    public static ViewerFilter createRuleFilter() {
        return new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (FactoriesUtil.DQRULE.equals(file.getFileExtension())) {
                        return true;
                    }
                } else if (element instanceof IFolder) {
                    IFolder folder = (IFolder) element;
                    // filter the match rule folder in table analysis
                    if (EResourceConstant.RULES_MATCHER.getName().equals(folder.getName())) {
                        return false;
                    }// ~
                    return ResourceService.isSubFolder(ResourceManager.getRulesFolder(), folder);
                }
                return false;
            }
        };
    }
}
