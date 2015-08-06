// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.indicator;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.DefaultSaveAction;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.editor.TdEditorToolBar;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorEditor extends CommonFormEditor {

    private DefaultSaveAction saveAction;

    private IndicatorDefinitionMaterPage masterPage;

    protected IndicatorsDefinitions indicatorDefinitions;

    public IndicatorEditor() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInputWithNotify(input);
        setPartName(getName(input));
    }

    /**
     * DOC xqliu Comment method "getName".
     * 
     * @param input
     * @return
     */
    private String getName(IEditorInput input) {
        if (input instanceof IndicatorEditorInput) {
            return input.getName();
        } else if (input instanceof FileEditorInput) {
            return IndicatorResourceFileHelper.getInstance().findIndDefinition(((FileEditorInput) input).getFile()).getName();
        } else if (input instanceof IndicatorDefinitionItemEditorInput) {
            return input.getName();
        }
        return DefaultMessagesImpl.getString("IndicatorEditor.IndicatorEditor"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.CommonFormEditor#addPages()
     */
    @Override
    protected void addPages() {

        masterPage = new IndicatorDefinitionMaterPage(this, "Master Page", "Indicator Definition");//$NON-NLS-1$//$NON-NLS-2$
        try {
            addPage(masterPage);
        } catch (PartInitException e) {
            ExceptionHandler.process(e, Level.ERROR);
        }

        TdEditorToolBar toolbar = getToolBar();
        if (toolbar != null && masterPage != null) {
            saveAction = new DefaultSaveAction(this);
            toolbar.addActions(saveAction);
            setSaveActionButtonState(false);
        }
        // super.addPages();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#firePropertyChange(int)
     */
    @Override
    protected void firePropertyChange(int propertyId) {
        setSaveActionButtonState(isDirty());

        super.firePropertyChange(propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.CommonFormEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        masterPage.doSave(monitor);
        setPartName(masterPage.getIntactElemenetName());
        setEditorObject(masterPage.getIndicatorDefinitionRepNode());
        super.doSave(monitor);
    }

    /**
     * Sets the indicatorDefinitions.
     * 
     * @param indicatorDefinitions the indicatorDefinitions to set
     */
    public void setIndicatorDefinitions(IndicatorsDefinitions indicatorDefinitions) {
        this.indicatorDefinitions = indicatorDefinitions;
    }

    /**
     * Getter for indicatorDefinitions.
     * 
     * @return the indicatorDefinitions
     */
    public IndicatorsDefinitions getIndicatorDefinitions() {
        return indicatorDefinitions;
    }

    /**
     * DOC xqliu 2009-07-02 bug 7687.
     * 
     * @param state
     */
    public void setSaveActionButtonState(boolean state) {
        if (saveAction != null) {
            saveAction.setEnabled(state);
        }
    }

    @Override
    public void setFocus() {
        super.setFocus();
        // don't invoke this method here, invoke it in IPartListener.partBroughtToTop()
        // WorkbenchUtils.autoChange2DataProfilerPerspective();
    }
}
