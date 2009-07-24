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
package org.talend.cwm.compare.ui.editor;

import java.io.IOException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ICompareInputLabelProvider;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.compare.diff.metamodel.ModelInputSnapshot;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorLauncher;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class ModelElementCompareEditorLauncher implements IEditorLauncher {

    private String connectionName = "";

    private Object selectedObject = null;

    public ModelElementCompareEditorLauncher(String connName, Object selObj) {
        connectionName = connName;
        selectedObject = selObj;
    }

    public void open(IPath file) {
        try {
            final EObject snapshot = ModelUtils.load(file.toFile(), new ResourceSetImpl());
            if (snapshot instanceof ModelInputSnapshot) {
                CompareConfiguration comapreConfiguration = new CompareConfiguration();
                comapreConfiguration.setDefaultLabelProvider(new ICompareInputLabelProvider() {

                    public Image getAncestorImage(Object input) {
                        return null;
                    }

                    public String getAncestorLabel(Object input) {
                        return "";
                    }

                    public Image getLeftImage(Object input) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public String getLeftLabel(Object input) {
                        return "Local Structure: \"" + connectionName + "\"";
                    }

                    public Image getRightImage(Object input) {
                        return null;
                    }

                    public String getRightLabel(Object input) {
                        return "Distant Structure";
                    }

                    public Image getImage(Object element) {
                        return null;
                    }

                    public String getText(Object element) {
                        return "Text label";
                    }

                    public void addListener(ILabelProviderListener listener) {

                    }

                    public void dispose() {

                    }

                    public boolean isLabelProperty(Object element, String property) {
                        return false;
                    }

                    public void removeListener(ILabelProviderListener listener) {
                        // TODO Auto-generated method stub

                    }

                });
                ModelElementCompareEditorInput compEditorInput = new ModelElementCompareEditorInput((ModelInputSnapshot) snapshot,
                        comapreConfiguration, selectedObject);
				CompareUI.openCompareEditor(compEditorInput);
             // MOD mzhao feature 8227
				compEditorInput.hookLeftPanelContextMenu();
                
                
            }
        } catch (IOException e) {
            // Fichier non lisible
            assert false;
        }
    }
    
  
}
