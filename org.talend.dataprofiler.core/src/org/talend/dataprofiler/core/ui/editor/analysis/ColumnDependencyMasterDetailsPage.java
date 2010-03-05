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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnCompareTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnDependencyIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.AnalysisHandler;
import org.talend.dq.analysis.ColumnDependencyAnalysisHandler;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * This is a Master Detail Page for <a href="http://www.talendforge.org/bugs/view.php?id=8134">Functional Dependency
 * Analysis</a>.
 * 
 * @author jet
 * 
 */
public class ColumnDependencyMasterDetailsPage extends AbstractAnalysisMetadataPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnDependencyMasterDetailsPage.class);

    private ScrolledForm form;

    private Section columnsComparisonSection = null;

    List<Column> columnListA = null;

    List<Column> columnListB = null;

    DataFilterComp dataFilterComp;

    private Section dataFilterSection = null;

    private String stringDataFilter;

    private AnalysisColumnCompareTreeViewer anaColumnCompareViewer;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor
     * )
     */
    @Override
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        stringDataFilter = AnalysisHelper.getStringDataFilter(analysis);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        form = managedForm.getForm();
        form.setText(DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.ColumnDependencyAnalysis")); //$NON-NLS-1$

        this.metadataSection.setText(DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.analysisMeta")); //$NON-NLS-1$

        this.metadataSection.setDescription(DefaultMessagesImpl
                .getString("ColumnsComparisonMasterDetailsPage.setAnalysisProperties")); //$NON-NLS-1$

        anaColumnCompareViewer = new AnalysisColumnCompareTreeViewer((AbstractAnalysisMetadataPage) this, topComp,
                getColumnLeftSet(), getColumnRightSet(), DefaultMessagesImpl
                        .getString("FunctionalDependencyMasterDetailsPage.Title"), DefaultMessagesImpl //$NON-NLS-1$
                        .getString(DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.1")), false, true); //$NON-NLS-1$

        anaColumnCompareViewer.addPropertyChangeListener(this);

        createDataFilterSection(form, topComp);

        columnListA = anaColumnCompareViewer.getColumnListA();
        columnListB = anaColumnCompareViewer.getColumnListB();

        columnsComparisonSection = anaColumnCompareViewer.getColumnsComparisonSection();
        anaColumnCompareViewer.addPropertyChangeListener(this);
    }

    /**
     * DOC bZhou Comment method "createDataFilterSection".
     * 
     * @param form
     * @param anasisDataComp
     */
    void createDataFilterSection(final ScrolledForm form, Composite anasisDataComp) {
        dataFilterSection = createSection(
                form,
                anasisDataComp,
                DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.DataFilter"), DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.EditorFilter")); //$NON-NLS-1$ //$NON-NLS-2$
        Composite sectionClient = toolkit.createComposite(dataFilterSection);
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));
        sectionClient.setLayout(new GridLayout());

        dataFilterComp = new DataFilterComp(sectionClient, stringDataFilter);
        dataFilterComp.addPropertyChangeListener(this);
        dataFilterComp.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                AnalysisHelper.setStringDataFilter(analysis, dataFilterComp.getDataFilterString());
            }
        });

        dataFilterSection.setClient(sectionClient);
    }

    public ScrolledForm getScrolledForm() {
        return form;
    }

    /**
     * @param editor
     * @param id
     * @param title
     */
    public ColumnDependencyMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#canRun()
     */
    @Override
    protected ReturnCode canRun() {

        return new ReturnCode(columnListA.size() > 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#canSave()
     */
    @Override
    protected ReturnCode canSave() {
        // do some validator.
        return validator(columnListA, columnListB);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#refresh()
     */
    @Override
    public void refresh() {
        switchToResultPage();
    }

    public AnalysisHandler getAnalysisHandler() {
        ColumnDependencyAnalysisHandler analysisHandler = new ColumnDependencyAnalysisHandler();
        analysisHandler.setAnalysis(this.analysis);
        return analysisHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#saveAnalysis()
     */
    @Override
    protected void saveAnalysis() throws DataprofilerCoreException {
        getAnalysisHandler().clearAnalysis();

        List<ModelElement> analysedElements = new ArrayList<ModelElement>();
        List<Column> columnListA = anaColumnCompareViewer.getColumnListA();
        List<Column> columnListB = anaColumnCompareViewer.getColumnListB();

        AnalysisBuilder anaBuilder = new AnalysisBuilder();
        anaBuilder.setAnalysis(this.analysis);

        for (int i = 0; i < columnListA.size(); i++) {
            if (columnListB.size() > i) {
                ColumnDependencyIndicator indicator = ColumnsetFactory.eINSTANCE.createColumnDependencyIndicator();
                indicator.setColumnA(columnListA.get(i));
                indicator.setColumnB(columnListB.get(i));
                indicator.setIndicatorDefinition(DefinitionHandler.getInstance().getFDRuleDefaultIndicatorDefinition());
                analysis.getResults().getIndicators().add(indicator);
                anaBuilder.addElementToAnalyze(columnListA.get(i), indicator);
            }
        }

        if (columnListA.size() > 0) {
            TdDataProvider tdDataProvider = DataProviderHelper.getTdDataProvider(columnListA.get(0));
            analysis.getContext().setConnection(tdDataProvider);
        } else {
            analysis.getContext().setConnection(null);
            analysis.getClientDependency().clear();
        }

        ReturnCode save = AnaResourceFileHelper.getInstance().save(analysis);
        if (save.isOk()) {
            log.info("Success to save connection analysis:" + analysis.getFileName()); //$NON-NLS-1$
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {

        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            currentEditor.firePropertyChange(IEditorPart.PROP_DIRTY);
            currentEditor.setRefreshResultPage(true);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.setDirty(true);
        }
    }

    private List<Column> getColumnLeftSet() {
        return getColumnSet(ColumnsetPackage.Literals.COLUMN_DEPENDENCY_INDICATOR__COLUMN_A);
    }

    private List<Column> getColumnRightSet() {
        return getColumnSet(ColumnsetPackage.Literals.COLUMN_DEPENDENCY_INDICATOR__COLUMN_B);
    }

    private List<Column> getColumnSet(EReference reference) {
        List<Column> columns = new ArrayList<Column>();
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            columns.add((Column) indicator.eGet(reference));
        }
        return columns;
    }

    private ReturnCode validator(List<Column> columnASet, List<Column> columnBSet) {

        if (columnASet.size() == 0 || columnBSet.size() == 0) {
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.columnsBlankMessag"), false); //$NON-NLS-1$
        }
        if (columnASet.size() != columnBSet.size()) {
            // two set must have equal number.
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.columnsSameMessage"), false); //$NON-NLS-1$
        }

        for (int i = 0; i < columnASet.size(); i++) {
            Column columnA = columnASet.get(i);
            Column columnB = columnBSet.get(i);

            ColumnSet ownerA = ColumnHelper.getColumnSetOwner(columnA);
            ColumnSet ownerB = ColumnHelper.getColumnSetOwner(columnB);

            int typeA = ((TdColumn) columnA).getJavaType();
            int typeB = ((TdColumn) columnB).getJavaType();

            if (ownerA != ownerB) {
                // must come from one table
                return new ReturnCode(DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.tableMessage"), false); //$NON-NLS-1$
            }
        }

        return new ReturnCode(true);
    }

}
