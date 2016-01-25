// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * AbstractAnalysisWizard can creat a empty analysis file.
 */
public abstract class AbstractAnalysisWizard extends AbstractWizard {

    static Logger log = Logger.getLogger(AbstractAnalysisWizard.class);

    protected AnalysisType analysisType;

    private AnalysisParameter parameter;

    private AnalysisBuilder analysisBuilder;

    public AbstractAnalysisWizard(AnalysisParameter parameter) {
        this.parameter = parameter;
    }

    public ModelElement initCWMResourceBuilder() {
        analysisBuilder = new AnalysisBuilder();
        boolean analysisInitialized = analysisBuilder.initializeAnalysis(parameter.getName(), parameter.getAnalysisType());
        if (analysisInitialized) {
            return analysisBuilder.getAnalysis();
        }
        return null;
    }

    public TypedReturnCode<Object> createAndSaveCWMFile(ModelElement cwmElement) {
        Analysis analysis = (Analysis) cwmElement;
        IFolder folder = parameter.getFolderProvider().getFolderResource();
        AnalysisWriter analysisWriter = ElementWriterFactory.getInstance().createAnalysisWrite();
        return analysisWriter.create(analysis, folder);
    }

    @Override
    protected String getEditorName() {
        return AnalysisEditor.class.getName();
    }

    @Override
    protected ResourceFileMap getResourceFileMap() {
        return AnaResourceFileHelper.getInstance();
    }

    @Override
    protected ConnectionParameter getParameter() {
        return this.parameter;
    }

    public AnalysisBuilder getAnalysisBuilder() {
        return analysisBuilder;
    }

    @Override
    public void openEditor(Item item) {
        AnalysisItemEditorInput analysisEditorInput = new AnalysisItemEditorInput(item);
        IRepositoryNode connectionRepNode = getParameter().getConnectionRepNode();
        analysisEditorInput.setConnectionNode(connectionRepNode);
        CorePlugin.getDefault().openEditor(analysisEditorInput, AnalysisEditor.class.getName());
    }
}
