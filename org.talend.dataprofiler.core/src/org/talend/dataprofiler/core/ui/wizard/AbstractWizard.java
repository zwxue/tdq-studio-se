// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.wizard.Wizard;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.UIMessages;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.helper.DQConnectionReposViewObjDelegator;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC zqin class global comment. Detailled comment
 */
public abstract class AbstractWizard extends Wizard implements ICWMResouceAdapter {

    @Override
    public boolean performFinish() {
        ReturnCode checkResult = checkMetadata();
        if (checkResult.isOk()) {
            ModelElement cwnElement = initCWMResourceBuilder();
            if (cwnElement != null) {
                fillMetadataToCWMResource(cwnElement);
                TypedReturnCode<Object> csResult = createAndSaveCWMFile(cwnElement);
                if (csResult.isOk()) {
                    Object savedObj = csResult.getObject();
                    if (savedObj instanceof IRepositoryViewObject) {
                        ConnectionItemEditorInput connItemEditorInput = new ConnectionItemEditorInput(
                                (IRepositoryViewObject) savedObj);
                        CorePlugin.getDefault().openEditor(connItemEditorInput, ConnectionEditor.class.getName());
                    } else if (savedObj instanceof IFile) {
                        IFile file = (IFile) csResult.getObject();
                        CorePlugin.getDefault().openEditor(file, getEditorName());
                    }
                    CorePlugin.getDefault().refreshWorkSpace();
                    CorePlugin.getDefault().refreshDQView();

                    return true;
                }
            }
        } else {
            MessageUI.openError(checkResult.getMessage());
        }

        return false;
    }

    public ReturnCode checkMetadata() {
        String elementName = getParameter().getName();
        IFolder folderResource = getParameter().getFolderProvider().getFolderResource();
        if (elementName == null || folderResource == null) {
            return new ReturnCode("", false); //$NON-NLS-1$
        } else {

            Collection<ModelElement> modelElements = new ArrayList<ModelElement>();

            switch (getParameter().getParamType()) {
            case ANALYSIS:
                modelElements.addAll(AnaResourceFileHelper.getInstance().getAllAnalysis(folderResource));
                break;
            case REPORT:
                modelElements.addAll(RepResourceFileHelper.getInstance().getAllReports(folderResource));
                break;
            case PATTERN:
                modelElements.addAll(PatternResourceFileHelper.getInstance().getAllPatternes(folderResource));
                break;
            case DBCONNECTON:
                modelElements.addAll(DQConnectionReposViewObjDelegator.getInstance().getAllElements());
                break;
            case DQRULE:
                modelElements.addAll(DQRuleResourceFileHelper.getInstance().getAllDQRules(folderResource));
                break;
            case UDINDICATOR:
                modelElements.addAll(IndicatorResourceFileHelper.getInstance().getAllIndicators(folderResource));
                break;
            default:
                break;
            }

            if (!modelElements.isEmpty()) {
                for (ModelElement element : modelElements) {
                    if (element.getName().equals(elementName)) {
                        if (!MessageUI.openConfirm(UIMessages.MSG_ANALYSIS_SAME_NAME)) {
                            getParameter().setName(elementName + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")); //$NON-NLS-1$
                        }

                        break;
                    }
                }
            }
        }
        return new ReturnCode(true);
    }

    public void fillMetadataToCWMResource(ModelElement cwmElement) {
        MetadataHelper.setDevStatus(cwmElement, getParameter().getStatus());
        MetadataHelper.setAuthor(cwmElement, getParameter().getAuthor());
        MetadataHelper.setPurpose(getParameter().getPurpose(), cwmElement);
        MetadataHelper.setDescription(getParameter().getDescription(), cwmElement);
        MetadataHelper.setVersion(getParameter().getVersion(), cwmElement);
    }

    protected abstract ResourceFileMap getResourceFileMap();

    protected abstract ConnectionParameter getParameter();

    protected abstract String getEditorName();

}
