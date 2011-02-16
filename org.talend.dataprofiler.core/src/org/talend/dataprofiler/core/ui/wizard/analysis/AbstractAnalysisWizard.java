// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
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
        TDQAnalysisItem anaItem = (TDQAnalysisItem) item;
        String folderPath = anaItem.getState().getPath();
        Path path = new Path(folderPath);
        Path append = (Path) path.append(new Path(anaItem.getFilename()));
        IPath removeLastSegments = append.removeFirstSegments(1);
        IFile fileEditorInput = ResourceManager.getRootProject().getFile(removeLastSegments);
        try {
            IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileEditorInput, true);
        } catch (PartInitException e) {
            log.error(e, e);
        }
        // AnalysisItemEditorInput analysisEditorInput = new AnalysisItemEditorInput(item);
        // DBConnectionRepNode connectionRepNode = getParameter().getConnectionRepNode();
        // analysisEditorInput.setConnectionNode(connectionRepNode);
        //
        // CorePlugin.getDefault().openEditor(analysisEditorInput, AnalysisEditor.class.getName());
    }
}
