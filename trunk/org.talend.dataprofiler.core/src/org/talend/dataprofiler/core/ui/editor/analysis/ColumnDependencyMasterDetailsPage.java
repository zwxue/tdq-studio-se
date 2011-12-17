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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnCompareTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnDependencyIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.AnalysisHandler;
import org.talend.dq.analysis.ColumnDependencyAnalysisHandler;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
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

    public Section getColumnsComparisonSection() {
        return this.columnsComparisonSection;
    }

    public void setColumnsComparisonSection(Section columnsComparisonSection) {
        this.columnsComparisonSection = columnsComparisonSection;
    }

    List<RepositoryNode> columnListA = null;

    List<RepositoryNode> columnListB = null;

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
                getColumnLeftSet(), getColumnRightSet(),
                DefaultMessagesImpl.getString("FunctionalDependencyMasterDetailsPage.Title"), DefaultMessagesImpl //$NON-NLS-1$
                        .getString("FunctionalDependencyMasterDetailsPage.Description"), false, true); //$NON-NLS-1$

        anaColumnCompareViewer.addPropertyChangeListener(this);

        createDataFilterSection(form, topComp);

        columnListA = anaColumnCompareViewer.getColumnListA();
        columnListB = anaColumnCompareViewer.getColumnListB();

        columnsComparisonSection = anaColumnCompareViewer.getColumnsComparisonSection();
        anaColumnCompareViewer.addPropertyChangeListener(this);

        form.reflow(true);
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

        // ADD yyi 2011-05-31 16158:add whitespace check for text fields.
        addWhitespaceValidate(dataFilterComp.getDataFilterText());
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

        return columnListA.size() > 0 ? new ReturnCode(true) : new ReturnCode(
                DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.columnsBlankRunMessage"), false); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#canSave()
     */
    @Override
    public ReturnCode canSave() {
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

        // ADD gdbu 2011-3-3 bug 19179
        // remove the space from analysis name
        analysis.setName(analysis.getName().replace(" ", ""));//$NON-NLS-1$ //$NON-NLS-2$
        for (Domain domain : this.analysis.getParameters().getDataFilter()) {
            domain.setName(this.analysis.getName());
        }
        // ~

        IRepositoryViewObject reposObject = null;
        getAnalysisHandler().clearAnalysis();

        List<RepositoryNode> columnListAANode = anaColumnCompareViewer.getColumnListA();
        List<RepositoryNode> columnListBBNode = anaColumnCompareViewer.getColumnListB();

        AnalysisBuilder anaBuilder = new AnalysisBuilder();
        anaBuilder.setAnalysis(this.analysis);
        Connection tdDataProvider = null;

        for (int i = 0; i < columnListAANode.size(); i++) {
            if (columnListBBNode.size() > i) {
                ColumnDependencyIndicator indicator = ColumnsetFactory.eINSTANCE.createColumnDependencyIndicator();
                TdColumn columnA = (TdColumn) ((MetadataColumnRepositoryObject) columnListAANode.get(i).getObject())
                        .getTdColumn();
                TdColumn columnB = (TdColumn) ((MetadataColumnRepositoryObject) columnListBBNode.get(i).getObject())
                        .getTdColumn();
                indicator.setColumnA(columnA);
                indicator.setColumnB(columnB);
                indicator.setIndicatorDefinition(DefinitionHandler.getInstance().getFDRuleDefaultIndicatorDefinition());
                analysis.getResults().getIndicators().add(indicator);
                anaBuilder.addElementToAnalyze(columnA, indicator);
                // ADD this line qiongli 2010-6-8
                anaBuilder.addElementToAnalyze(columnB, indicator);
            }
        }

        if (columnListAANode.size() > 0) {
            reposObject = columnListAANode.get(0).getObject();
            tdDataProvider = ((ConnectionItem) reposObject.getProperty().getItem()).getConnection();
            // MOD qiongli bug 14437:Add dependency
            analysis.getContext().setConnection(tdDataProvider);
            TypedReturnCode<Dependency> rc = DependenciesHandler.getInstance().setDependencyOn(analysis, tdDataProvider);
            if (!rc.isOk()) {
                log.info("fail to save dependency analysis:" + analysis.getFileName());//$NON-NLS-1$
            }
        } else {
            tdDataProvider = (Connection) analysis.getContext().getConnection();
            if (tdDataProvider != null && tdDataProvider.getSupplierDependency().size() > 0) {
            tdDataProvider.getSupplierDependency().get(0).getClient().remove(analysis);
            analysis.getContext().setConnection(null);
            analysis.getClientDependency().clear();
            }
        }
        // ADD xqliu 2010-07-19 bug 14014
        this.updateAnalysisClientDependency();
        // ~ 14014
        // 2011.1.12 MOD by zhsne to unify anlysis and connection id when saving.
        ReturnCode saved = new ReturnCode(false);
        IEditorInput editorInput = this.getEditorInput();
        if (editorInput instanceof AnalysisItemEditorInput) {
            AnalysisItemEditorInput analysisInput = (AnalysisItemEditorInput) editorInput;
            TDQAnalysisItem tdqAnalysisItem = analysisInput.getTDQAnalysisItem();

            // ADD gdbu 2011-3-3 bug 19179
            tdqAnalysisItem.getProperty().setLabel(analysis.getName());
            this.nameText.setText(analysis.getName());
            // ~

            saved = ElementWriterFactory.getInstance().createAnalysisWrite().save(tdqAnalysisItem);
        }
        if (saved.isOk()) {
            // MOD qiongli bug 14437: save dependency
            RepositoryNode node = RepositoryNodeHelper.recursiveFind(tdDataProvider);
            if (node != null) {
                // ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(Boolean.TRUE, Boolean.TRUE);
                ElementWriterFactory.getInstance().createDataProviderWriter().save(node.getObject().getProperty().getItem());
            }
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

    private List<RepositoryNode> getColumnLeftSet() {
        return getColumnSet(ColumnsetPackage.Literals.COLUMN_DEPENDENCY_INDICATOR__COLUMN_A);
    }

    private List<RepositoryNode> getColumnRightSet() {
        return getColumnSet(ColumnsetPackage.Literals.COLUMN_DEPENDENCY_INDICATOR__COLUMN_B);
    }

    private List<RepositoryNode> getColumnSet(EReference reference) {
        List<RepositoryNode> columns = new ArrayList<RepositoryNode>();
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            columns.add(RepositoryNodeHelper.recursiveFind((TdColumn) indicator.eGet(reference)));
        }
        return columns;
    }

    private ReturnCode validator(List<RepositoryNode> columnASet, List<RepositoryNode> columnBSet) {

        // MOD by gdbu 2011-3-21 bug 19179
        ReturnCode canModRetCode = canModifyAnalysisName();
        if (!canModRetCode.isOk()) {
            return canModRetCode;
        }
        // ~19179

        if (columnASet.size() == 0 || columnBSet.size() == 0) {
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.columnsBlankMessag"), false); //$NON-NLS-1$
        }
        if (columnASet.size() != columnBSet.size()) {
            // two set must have equal number.
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.columnsSameMessage"), false); //$NON-NLS-1$
        }

        for (int i = 0; i < columnASet.size(); i++) {
            RepositoryNode columnANode = columnASet.get(i);
            RepositoryNode columnBNode = columnBSet.get(i);
            TdColumn tdColumnA = ((DBColumnRepNode) columnANode).getTdColumn();
            TdColumn tdColumnB = ((DBColumnRepNode) columnBNode).getTdColumn();
            ColumnSet ownerA = ColumnHelper.getColumnOwnerAsColumnSet(tdColumnA);
            ColumnSet ownerB = ColumnHelper.getColumnOwnerAsColumnSet(tdColumnB);
            String uuidA = ResourceHelper.getUUID(ownerA);
            String uuidB = ResourceHelper.getUUID(ownerB);
            if (!uuidA.equals(uuidB)) {
                // must come from one table
                return new ReturnCode(DefaultMessagesImpl.getString("ColumnDependencyMasterDetailsPage.tableMessage"), false); //$NON-NLS-1$
            }
        }

        return new ReturnCode(true);
    }

    /**
     * 
     * DOC zshen DOC mzhao Open column selection dialog for left column set. this method is intended to use from cheat
     * sheets.
     */
    public void openColumnsSetASelectionDialog() {
        anaColumnCompareViewer.openColumnsSetASelectionDialog();
    }

    /**
     * 
     * DOC zshen DOC mzhao Open column selection dialog for right column set. this method is intended to use from cheat
     * sheets.
     */
    public void openColumnsSetBSelectionDialog() {
        anaColumnCompareViewer.openColumnsSetBSelectionDialog();
    }
}
