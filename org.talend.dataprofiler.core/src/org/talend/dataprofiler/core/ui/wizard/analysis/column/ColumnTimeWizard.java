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
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.WizardPage;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.AnalysisWriter;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * @author zqin
 * 
 */
public class ColumnTimeWizard extends ColumnWizard {

    private AnalysisParameter parameter;

    private WizardPage[] extenalPages;

    static Logger log = Logger.getLogger(ColumnTimeWizard.class);

    public WizardPage[] getExtenalPages() {
        if (extenalPages == null) {
            return new WizardPage[0];
        }
        return extenalPages;
    }

    public void setExtenalPages(WizardPage[] extenalPages) {
        this.extenalPages = extenalPages;
    }

    public ColumnTimeWizard(AnalysisParameter parameter) {
        super(parameter);
        this.parameter = parameter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());

        for (WizardPage page : getExtenalPages()) {
            addPage(page);
        }
    }

    @Override
    protected void fillAnalysisEditorParam() {
        this.analysisName = parameter.getAnalysisName();
        this.analysisType = parameter.getAnalysisType();
        this.folderResource = parameter.getFolderProvider().getFolderResource();
    }

    public boolean performFinish() {
        this.fillAnalysisEditorParam();
        if (!checkAnalysisEditorParam()) {
            return false;
        }

        try {
            IFile file = createEmptyAnalysisFile();
            if (file == null) {
                return false;
            }
            CorePlugin.getDefault().openEditor(file, AnalysisEditor.class.getName());
        } catch (Exception e) {
            ExceptionHandler.process(e, Level.ERROR);
        }
        return true;
    }

    protected boolean checkAnalysisEditorParam() {
        if (analysisType == null) {
            log.error(DefaultMessagesImpl.getString("AbstractAnalysisWizard.analysisType")); //$NON-NLS-1$
            return false;
        } else if (analysisName == null) {
            log.error(DefaultMessagesImpl.getString("AbstractAnalysisWizard.analysisName")); //$NON-NLS-1$
            return false;
        } else if (folderResource == null) {
            log.error(DefaultMessagesImpl.getString("AbstractAnalysisWizard.fileURI")); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    protected IFile createEmptyAnalysisFile() throws DataprofilerCoreException {

        ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
        ColumnSetMultiValueIndicator minMaxDateIndicator = columnsetFactory.createMinMaxDateIndicator();
        AnalysisBuilder analysisBuilder = new AnalysisBuilder();
        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, analysisType);
        if (!analysisInitialized) {
            throw new DataprofilerCoreException(analysisName
                    + DefaultMessagesImpl.getString("AbstractAnalysisWizard.failedToInitialize")); //$NON-NLS-1$
        }
        Analysis analysis = analysisBuilder.getAnalysis();
        fillAnalysisBuilder(analysisBuilder);
        AnalysisWriter writer = new AnalysisWriter();
        analysis.getResults().getIndicators().add(minMaxDateIndicator);
        TypedReturnCode<IFile> saved = writer.createAnalysisFile(analysis, folderResource);

        IFile file;
        if (saved.isOk()) {
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + folderResource.getFullPath().toString()); //$NON-NLS-1$
            }
            file = saved.getObject();
            Resource anaResource = analysis.eResource();
            AnaResourceFileHelper.getInstance().register(file, anaResource);
            AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true);
        } else {
            throw new DataprofilerCoreException(DefaultMessagesImpl.getString("AbstractAnalysisWizard.saving", folderResource
                    .getFullPath().toString(), saved.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        CorePlugin.getDefault().refreshWorkSpace();
        ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();

        return file;

    }
}
