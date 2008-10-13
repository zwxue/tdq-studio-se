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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.AnalysisWriter;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * AbstractAnalysisWizard can creat a empty analysis file.
 */
public abstract class AbstractAnalysisWizard extends AbstractWizard {

    static Logger log = Logger.getLogger(AbstractAnalysisWizard.class);

    protected String analysisName;

    protected AnalysisType analysisType;

    private AnalysisParameter parameter;

    /**
     * The folder where to save the analysis.
     */
    protected IFolder folderResource;

    public AbstractAnalysisWizard(AnalysisParameter parameter) {
        this.parameter = parameter;
    }

    @Override
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

    protected abstract void fillAnalysisEditorParam();

    private boolean checkAnalysisEditorParam() {
        if (analysisType == null) {
            log.error("The field 'analysisType' haven't assigned a value.");
            return false;
        } else if (analysisName == null) {
            log.error("The field 'analysisName' haven't assigned a value.");
            return false;
        } else if (folderResource == null) {
            log.error("The field 'fileURI' haven't assigned a value.");
            return false;
        }
        return true;
    }

    protected IFile createEmptyAnalysisFile() throws DataprofilerCoreException {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder();
        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, analysisType);
        if (!analysisInitialized) {
            throw new DataprofilerCoreException(analysisName + " failed to initialize!");
        }
        Analysis analysis = analysisBuilder.getAnalysis();
        fillAnalysisBuilder(analysisBuilder);
        AnalysisWriter writer = new AnalysisWriter();

        TypedReturnCode<IFile> saved = writer.createAnalysisFile(analysis, folderResource);
        IFile file;
        if (saved.isOk()) {
            log.info("Saved in  " + folderResource.getFullPath().toString());
            file = saved.getObject();
            Resource anaResource = analysis.eResource();
            AnaResourceFileHelper.getInstance().register(file, anaResource);
            AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true);
        } else {
            throw new DataprofilerCoreException("Problem saving file: " + folderResource.getFullPath().toString() + ": "
                    + saved.getMessage());
        }

        CorePlugin.getDefault().refreshWorkSpace();
        ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();

        return file;

    }

    protected void fillAnalysisBuilder(AnalysisBuilder analysisBuilder) {

        String analysisStatue = parameter.getAnalysisStatus();
        String analysisAuthor = parameter.getAnalysisAuthor();
        String analysisPurpse = parameter.getAnalysisPurpose();
        String analysisDescription = parameter.getAnalysisDescription();

        Analysis analysis = analysisBuilder.getAnalysis();
        TaggedValueHelper.setDevStatus(analysis, DevelopmentStatus.get(analysisStatue));
        TaggedValueHelper.setAuthor(analysis, analysisAuthor);
        TaggedValueHelper.setPurpose(analysisPurpse, analysis);
        TaggedValueHelper.setDescription(analysisDescription, analysis);
    }

    @Override
    protected ConnectionParameter getConnectionParameter() {

        return this.parameter;
    }
}
