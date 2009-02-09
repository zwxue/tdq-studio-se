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
package org.talend.dq.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dq.PluginConstant;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This factory for getting the <code>ModelElement</code> from <code>IFile</code>.
 */
public final class ModelElementFileFactory {

    private ModelElementFileFactory() {

    }

    // private static Logger log = Logger.getLogger(ModelElementFileFactory.class);

    public static ModelElement getModelElement(IFile file) {
        ModelElement modelElement = null;
        if (file.getName().endsWith(PluginConstant.PRV_SUFFIX)) {
            TypedReturnCode<TdDataProvider> returnValue = PrvResourceFileHelper.getInstance().findProvider(file);
            modelElement = returnValue.getObject();
        } else if (file.getName().endsWith(PluginConstant.ANA_SUFFIX)) {
            modelElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
        } else if (file.getName().endsWith(PluginConstant.REP_SUFFIX)) {
            modelElement = RepResourceFileHelper.getInstance().findReport(file);
        } else {
            return modelElement;
            // log.info("The file \"" + file.getFullPath() + "\" has no corresponding ModelElement!");
        }
        return modelElement;
    }

    public static ResourceFileMap getResourceFileMap(IFile file) {
        ResourceFileMap modelElement = null;
        if (file.getName().endsWith(PluginConstant.PRV_SUFFIX)) {
            modelElement = PrvResourceFileHelper.getInstance();
        } else if (file.getName().endsWith(PluginConstant.ANA_SUFFIX)) {
            modelElement = AnaResourceFileHelper.getInstance();
        } else if (file.getName().endsWith(PluginConstant.REP_SUFFIX)) {
            modelElement = RepResourceFileHelper.getInstance();
        }
        return modelElement;
    }

    public static ModelElement[] getModelElements(IFile[] files) {
        List<ModelElement> modelElements = new ArrayList<ModelElement>();
        ModelElement element;
        for (IFile file : files) {
            element = getModelElement(file);
            if (element != null) {
                modelElements.add(element);
            }
        }
        return modelElements.toArray(new ModelElement[modelElements.size()]);
    }
}
