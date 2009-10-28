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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.utils.UIPagination;
import org.talend.dq.analysis.AnalysisHandler;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ColumnAnalysisResultPage extends AbstractAnalysisResultPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnAnalysisResultPage.class);

    private Composite resultComp;

    ColumnMasterDetailsPage masterPage;

    private Section resultSection = null;

    /**
     * DOC zqin ColumnAnalysisResultPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ColumnAnalysisResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnMasterDetailsPage) analysisEditor.getMasterPage();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        resultComp = toolkit.createComposite(topComposite);
        resultComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        resultComp.setLayout(new GridLayout());
        createResultSection(resultComp);
        form.reflow(true);
    }

    @Override
    protected AnalysisHandler getAnalysisHandler() {
        return this.masterPage.getAnalysisHandler();
    }

    protected void createResultSection(Composite parent) {
        resultSection = createSection(form, parent,
                DefaultMessagesImpl.getString("ColumnAnalysisResultPage.analysisResult"), null); //$NON-NLS-1$
        final Composite sectionClient = toolkit.createComposite(resultSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        final ColumnIndicator[] columnIndicatores = masterPage.getTreeViewer().getColumnIndicator();

        // ~ MOD mzhao 2009-04-20, Do pagination. Bug 6512.
        UIPagination uiPagination = new UIPagination(toolkit, sectionClient);
        int pageSize = UIPagination.getPageSize();
        int totalPages = columnIndicatores.length / pageSize;
        List<ColumnIndicator> columnIndLs = null;
        for (int index = 0; index < totalPages; index++) {
            columnIndLs = new ArrayList<ColumnIndicator>();
            for (int idx = 0; idx < pageSize; idx++) {
                columnIndLs.add(columnIndicatores[index * pageSize + idx]);
            }
            PaginationInfo pginfo = new ResultPaginationInfo(form, columnIndLs, masterPage, uiPagination);
            uiPagination.addPage(pginfo);

        }
        int left = columnIndicatores.length % pageSize;
        if (left != 0) {
            columnIndLs = new ArrayList<ColumnIndicator>();
            for (int leftIdx = 0; leftIdx < left; leftIdx++) {
                columnIndLs.add(columnIndicatores[totalPages * pageSize + leftIdx]);
            }
            PaginationInfo pginfo = new ResultPaginationInfo(form, columnIndLs, masterPage, uiPagination);
            uiPagination.addPage(pginfo);
            totalPages++;
        }
        uiPagination.init();
        // ~

        resultSection.setClient(sectionClient);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.beans.PropertyChangeListener#propertyChange(java.beans. PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisResultPage#refresh(org.talend.dataprofiler.core
     * .ui.editor.analysis.AbstractAnalysisMetadataPage)
     */
    @Override
    public void refresh(AbstractAnalysisMetadataPage masterPage) {
        this.masterPage = (ColumnMasterDetailsPage) masterPage;

        if (summaryComp != null && !summaryComp.isDisposed()) {
            summaryComp.dispose();
        }

        if (resultComp != null && !resultComp.isDisposed()) {
            resultComp.dispose();
        }

        createFormContent(getManagedForm());
    }
}
