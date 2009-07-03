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
package org.talend.dataprofiler.core.ui.editor.indicator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.talend.dataprofiler.core.ui.action.actions.DefaultSaveAction;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.editor.TdEditorToolBar;
import org.talend.dataquality.exception.ExceptionHandler;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorEditor extends CommonFormEditor {

    private static Logger log = Logger.getLogger(IndicatorEditor.class);

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
        setPartName(input.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.CommonFormEditor#addPages()
     */
    @Override
    protected void addPages() {

        masterPage = new IndicatorDefinitionMaterPage(this, "Master Page", getPartName());
        try {
            addPage(masterPage);
        } catch (PartInitException e) {
            ExceptionHandler.process(e, Level.ERROR);
        }

        TdEditorToolBar toolbar = getToolBar();
        if (toolbar != null && masterPage != null) {
            saveAction = new DefaultSaveAction(this);
            toolbar.addActions(saveAction);
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
}
