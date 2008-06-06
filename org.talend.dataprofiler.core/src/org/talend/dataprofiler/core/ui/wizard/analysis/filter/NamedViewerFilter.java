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
package org.talend.dataprofiler.core.ui.wizard.analysis.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.talend.dataprofiler.core.model.nodes.analysis.AnalysisTypeNode;


/**
 * @author huangssssx
 *
 */
public class NamedViewerFilter extends ViewerFilter {

    private String typeName;
    /**
     * 
     */
    public NamedViewerFilter() {
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        // TODO Auto-generated method stub
        if (element instanceof AnalysisTypeNode) {
            AnalysisTypeNode sonNode = (AnalysisTypeNode) element;
            if (parentElement != null && parentElement instanceof AnalysisTypeNode) {
                AnalysisTypeNode parentNode = (AnalysisTypeNode) parentElement;
                return parentNode.getName().toLowerCase().startsWith(typeName.toLowerCase());
            }
            
            return sonNode.getName().toLowerCase().startsWith(this.typeName.toLowerCase());
        } else {
            return false;
        }
    }
    
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
