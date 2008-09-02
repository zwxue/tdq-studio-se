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
package org.talend.dataprofiler.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This factory for getting the <code>ModelElement</code> from <code>IFile</code>.
 */
public final class ModelElementFileFactory {

    private ModelElementFileFactory() {

    }

    private static Logger log = Logger.getLogger(ModelElementFileFactory.class);

    public static ModelElement getModelElement(IFile file) {
        ModelElement modelElement = null;
        if (file.getName().endsWith(PluginConstant.PRV_SUFFIX)) {
            TypedReturnCode<TdDataProvider> returnValue = PrvResourceFileHelper.getInstance().getTdProvider(file);
            modelElement = returnValue.getObject();
        } else if (file.getName().endsWith(PluginConstant.ANA_SUFFIX)) {
            modelElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
        } else if (file.getName().endsWith(PluginConstant.REP_SUFFIX)) {
            modelElement = RepResourceFileHelper.getInstance().findReport(file);
        } else {
            log.error("The file \"" + file.getFullPath() + "\" has no corresponding ModelElement!");
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
