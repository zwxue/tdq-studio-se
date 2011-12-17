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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnCompareTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.AnalysisHandler;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * This page show the comparisons information of column set.
 */
/**
 * DOC Administrator class global comment. Detailled comment
 */
public class ColumnsComparisonMasterDetailsPage extends AbstractAnalysisMetadataPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnsComparisonMasterDetailsPage.class);

    // private ColumnAnalysisHandler analysisHandler;

    private RowMatchingIndicator rowMatchingIndicatorA;

    private RowMatchingIndicator rowMatchingIndicatorB;

    private AnalysisColumnCompareTreeViewer anaColumnCompareViewer;

    private ScrolledForm form;

    private Section columnsComparisonSection = null;

    public Section getColumnsComparisonSection() {
        return this.columnsComparisonSection;
    }

    public void setColumnsComparisonSection(Section columnsComparisonSection) {
        this.columnsComparisonSection = columnsComparisonSection;
    }

    DataFilterComp dataFilterCompA;

    DataFilterComp dataFilterCompB;

    private Section dataFilterSection = null;

    private String stringDataFilterA;

    private String stringDataFilterB;

    public ColumnsComparisonMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);

    }

    /**
     * DOC rli ColumnsComparisonAnalysisResultPage class global comment. Detailled comment
     */
    // class ColumnComparisonAnalysisHandler extends ColumnAnalysisHandler {
    //
    // public boolean addIndicator(TdColumn column, Indicator... indicators) {
    // if (!analysis.getContext().getAnalysedElements().contains(column)) {
    // analysis.getContext().getAnalysedElements().add(column);
    // }
    //
    // for (Indicator indicator : indicators) {
    // // store first level of indicators in result.
    // analysis.getResults().getIndicators().add(indicator);
    // // initializeIndicator(indicator, column);
    // }
    // DataManager connection = analysis.getContext().getConnection();
    // if (connection == null) {
    // // try to get one
    // log.error("Connection has not been set in analysis Context");
    // connection = DataProviderHelper.getTdDataProvider(column);
    // analysis.getContext().setConnection(connection);
    // }
    // return true;
    // }
    //
    // }
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        Analysis analysis = (Analysis) this.currentModelElement;
        // MOD xqliu 2009-06-10 bug7334
        stringDataFilterA = AnalysisHelper.getStringDataFilter(analysis, 0);
        stringDataFilterB = AnalysisHelper.getStringDataFilter(analysis, 1);
        // ~
        if (analysis.getResults().getIndicators().size() == 0) {
            ColumnsetFactory factory = ColumnsetFactory.eINSTANCE;
            rowMatchingIndicatorA = factory.createRowMatchingIndicator();
            rowMatchingIndicatorB = factory.createRowMatchingIndicator();
            Indicator[] currentIndicators = new Indicator[] { rowMatchingIndicatorA, rowMatchingIndicatorB };
            setDefaultIndDef(currentIndicators);
        } else {
            EList<Indicator> indicators = analysis.getResults().getIndicators();
            rowMatchingIndicatorA = (RowMatchingIndicator) indicators.get(0);
            rowMatchingIndicatorB = (RowMatchingIndicator) indicators.get(1);
        }
    }

    /**
     * DOC rli Comment method "setDefaultIndDef".
     * 
     * @param indicator
     */
    private void setDefaultIndDef(Indicator[] indicators) {
        for (int i = 0; i < indicators.length; i++) {
            if (!DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicators[i])) {
                log.error(DefaultMessagesImpl.getString("TableIndicator.couldnotSetDef") + indicators[i].getName());//$NON-NLS-1$
            }
        }
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        form = managedForm.getForm();
        form.setText(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.columnSetComparisionAnalysis")); //$NON-NLS-1$
        this.metadataSection.setText(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.analysisMetadata")); //$NON-NLS-1$
        this.metadataSection.setDescription(DefaultMessagesImpl
                .getString("ColumnsComparisonMasterDetailsPage.setAnalysisProperties")); //$NON-NLS-1$
        // MOD mzhao 2009-06-17 feature 5887.
        anaColumnCompareViewer = new AnalysisColumnCompareTreeViewer(this, topComp, (Analysis) this.currentModelElement, false);
        columnsComparisonSection = anaColumnCompareViewer.getColumnsComparisonSection();

        anaColumnCompareViewer.refreash();
        anaColumnCompareViewer.addPropertyChangeListener(this);

        createDataFilterSection(form, topComp);
    }

    void createDataFilterSection(final ScrolledForm form, Composite anasisDataComp) {
        dataFilterSection = createSection(
                form,
                anasisDataComp,
                DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.dataFilter"), DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.editDataFilter")); //$NON-NLS-1$ //$NON-NLS-2$
        Composite sectionClient = toolkit.createComposite(dataFilterSection);
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));
        sectionClient.setLayout(new GridLayout());

        SashForm sashForm = new SashForm(sectionClient, SWT.NULL);
        sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite leftComp = toolkit.createComposite(sashForm);
        leftComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        leftComp.setLayout(new GridLayout());
        dataFilterCompA = new DataFilterComp(leftComp, stringDataFilterA);
        dataFilterCompA.addPropertyChangeListener(this);
        dataFilterCompA.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                AnalysisHelper.setStringDataFilter(analysis, dataFilterCompA.getDataFilterString(), 0);
            }
        });

        Composite rightComp = toolkit.createComposite(sashForm);
        rightComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        rightComp.setLayout(new GridLayout());
        dataFilterCompB = new DataFilterComp(rightComp, stringDataFilterB);
        dataFilterCompB.addPropertyChangeListener(this);
        dataFilterCompB.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                AnalysisHelper.setStringDataFilter(analysis, dataFilterCompB.getDataFilterString(), 1);
            }
        });

        // ADD yyi 2011-05-31 16158:add whitespace check for text fields.
        addWhitespaceValidate(dataFilterCompA.getDataFilterText(), dataFilterCompB.getDataFilterText());
        dataFilterSection.setClient(sectionClient);
    }

    /**
     * 
     * DOC mzhao Open column selection dialog for left column set. this method is intended to use from cheat sheets.
     */
    public void openColumnsSetASelectionDialog() {
        anaColumnCompareViewer.openColumnsSetASelectionDialog();
    }

    /**
     * 
     * DOC mzhao Open column selection dialog for right column set. this method is intended to use from cheat sheets.
     */
    public void openColumnsSetBSelectionDialog() {
        anaColumnCompareViewer.openColumnsSetBSelectionDialog();
    }

    public AnalysisHandler getAnalysisHandler() {
        AnalysisHandler analysisHandler = new AnalysisHandler();
        analysisHandler.setAnalysis(this.analysis);
        return analysisHandler;
    }

    @Override
    protected void saveAnalysis() throws DataprofilerCoreException {
        // ADD gdbu 2011-3-3 bug 19179

        // remove the space from analysis name
        this.analysis.setName(this.analysis.getName().replace(" ", ""));//$NON-NLS-1$//$NON-NLS-2$
        // change 'ana' field's 'dataquality' tag content
        for (Domain domain : this.analysis.getParameters().getDataFilter()) {
            domain.setName(this.analysis.getName());
        }
        // ~
        IRepositoryViewObject reposObject = null;
        getAnalysisHandler().clearAnalysis();
        List<ModelElement> analysedElements = new ArrayList<ModelElement>();
        anaColumnCompareViewer.setColumnABForMatchingIndicator(rowMatchingIndicatorA, anaColumnCompareViewer.getColumnListA(),
                anaColumnCompareViewer.getColumnListB());
        anaColumnCompareViewer.setColumnABForMatchingIndicator(rowMatchingIndicatorB, anaColumnCompareViewer.getColumnListB(),
                anaColumnCompareViewer.getColumnListA());
        Connection tdDataProvider = null;
        for (int i = 0; i < anaColumnCompareViewer.getColumnListA().size(); i++) {
            reposObject = anaColumnCompareViewer.getColumnListA().get(i).getObject();
            analysedElements.add(((MetadataColumnRepositoryObject) reposObject)
                    .getTdColumn());
        }
        for (int i = 0; i < anaColumnCompareViewer.getColumnListB().size(); i++) {
            reposObject = anaColumnCompareViewer.getColumnListB().get(i).getObject();
            analysedElements.add(((MetadataColumnRepositoryObject) reposObject)
                    .getTdColumn());
        }
        if (analysedElements.size() > 0) {
            tdDataProvider = ConnectionHelper.getTdDataProvider((TdColumn) analysedElements.get(0));
            analysis.getContext().setConnection(tdDataProvider);
            // MOD qiongli bug 14437:Add dependency
            analysis.getContext().setConnection(tdDataProvider);
            TypedReturnCode<Dependency> rc = DependenciesHandler.getInstance().setDependencyOn(analysis, tdDataProvider);
            if (!rc.isOk()) {
                log.info("fail to save dependency analysis:" + analysis.getFileName());//$NON-NLS-1$
            }
        } else {
            tdDataProvider = (Connection) analysis.getContext().getConnection();
            if (tdDataProvider != null) {
            tdDataProvider.getSupplierDependency().get(0).getClient().remove(analysis);
            analysis.getContext().setConnection(null);
            analysis.getClientDependency().clear();
            }
        }
        // rowCountIndicator.setAnalyzedElement(value)
        // rowMatchingIndicatorA
        AnalysisBuilder anaBuilder = new AnalysisBuilder();
        anaBuilder.setAnalysis(this.analysis);
        if (anaColumnCompareViewer.getCheckComputeButton().getSelection()) {
            analysis.getParameters().getDeactivatedIndicators().add(rowMatchingIndicatorB);
        } else {
            analysis.getParameters().getDeactivatedIndicators().clear();
        }
        anaBuilder.addElementsToAnalyze(analysedElements.toArray(new ModelElement[analysedElements.size()]), new Indicator[] {
                rowMatchingIndicatorA, rowMatchingIndicatorB });
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
            // MOD qiongli bug 14437:Add dependency
            RepositoryNode node = RepositoryNodeHelper.recursiveFind(tdDataProvider);
            if (node != null) {
                // ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(Boolean.TRUE, Boolean.TRUE);
                ElementWriterFactory.getInstance().createDataProviderWriter().save(node.getObject().getProperty().getItem());
            }
            log.info("Success to save connection analysis:" + analysis.getFileName()); //$NON-NLS-1$
        }

    }

    public void fireRuningItemChanged(boolean status) {
        super.fireRuningItemChanged(status);

    }

    @Override
    public void refresh() {
        switchToResultPage();
    }

    @Override
    public ReturnCode canSave() {

        // MOD by gdbu 2011-3-21 bug 19179
        ReturnCode canModRetCode = canModifyAnalysisName();
        if (!canModRetCode.isOk()) {
            return canModRetCode;
        }
        // ~19179

        if (anaColumnCompareViewer.getColumnListA().size() != anaColumnCompareViewer.getColumnListB().size()) {
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.columnsSameMessage"), false); //$NON-NLS-1$
        }

        List<TdColumn> columnAList = new ArrayList<TdColumn>();
        List<TdColumn> columnBList = new ArrayList<TdColumn>();
        for (RepositoryNode rd : anaColumnCompareViewer.getColumnListA()) {
            columnAList.add((TdColumn) ((MetadataColumnRepositoryObject) rd.getObject()).getTdColumn());
        }
        for (RepositoryNode rd : anaColumnCompareViewer.getColumnListB()) {
            columnBList.add((TdColumn) ((MetadataColumnRepositoryObject) rd.getObject()).getTdColumn());
        }
        if (anaColumnCompareViewer.getColumnListA().size() > 0) {
            if (!ColumnHelper.isFromSameTable(columnAList) || !ColumnHelper.isFromSameTable(columnBList)) {
                return new ReturnCode(
                        DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.notSameElementMessage"), false); //$NON-NLS-1$
            }

            for (int i = 0; i < anaColumnCompareViewer.getColumnListA().size(); i++) {
                TdColumn columnA = columnAList.get(i);
                TdColumn columnB = columnBList.get(i);

                ColumnSet ownerA = ColumnHelper.getColumnOwnerAsColumnSet(columnA);
                ColumnSet ownerB = ColumnHelper.getColumnOwnerAsColumnSet(columnB);

                int typeA = ((TdColumn) columnA).getSqlDataType().getJavaDataType();
                int typeB = ((TdColumn) columnB).getSqlDataType().getJavaDataType();
                if (!Java2SqlType.isGenericSameType(typeA, typeB)) {
                    return new ReturnCode(
                            DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.notSameColumnType"), false); //$NON-NLS-1$
                }
                if (!ColumnSetHelper.isFromSamePackage(ownerA, ownerB)) {
                    return new ReturnCode(
                            DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.schemaSameMessage"), false); //$NON-NLS-1$
                }
            }

            List<TdColumn> allColumns = new ArrayList<TdColumn>();
            allColumns.addAll(columnAList);
            allColumns.addAll(columnBList);

            // MOD scorreia 2009-05-25 allow to compare elements from the same
            // table
            // if (ColumnHelper.isFromSameTable(allColumns)) {
            //                return new ReturnCode(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.TwoSideColumns"), false); //$NON-NLS-1$
            // }
        }

        return new ReturnCode(true);
    }

    @Override
    protected ReturnCode canRun() {

        // return new ReturnCode(rowMatchingIndicatorA.getColumnSetA().size() != 0);

        return 0 == rowMatchingIndicatorA.getColumnSetA().size() ? new ReturnCode(DefaultMessagesImpl
                .getString("ColumnsComparisonMasterDetailsPage.columnsBlankRunMessage"), false) : new ReturnCode(true); //$NON-NLS-1$

    }

    public ScrolledForm getScrolledForm() {
        return form;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            currentEditor.firePropertyChange(IEditorPart.PROP_DIRTY);
            currentEditor.setRefreshResultPage(true);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.setDirty(true);
        }
    }
}
