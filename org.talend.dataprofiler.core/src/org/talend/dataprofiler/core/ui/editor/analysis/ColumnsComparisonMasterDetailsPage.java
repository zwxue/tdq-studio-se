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
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnCompareTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.AnalysisHandler;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * This page show the comparisons information of column set.
 */
/**
 * DOC Administrator class global comment. Detailled comment
 */
public class ColumnsComparisonMasterDetailsPage extends
		AbstractAnalysisMetadataPage implements PropertyChangeListener {

	private static Logger log = Logger
			.getLogger(ColumnsComparisonMasterDetailsPage.class);

	// private ColumnAnalysisHandler analysisHandler;

	private RowMatchingIndicator rowMatchingIndicatorA;

	private RowMatchingIndicator rowMatchingIndicatorB;

	private AnalysisColumnCompareTreeViewer anaColumnCompareViewer;
	private ScrolledForm form;

	private Section columnsComparisonSection = null;
	DataFilterComp dataFilterCompA;

	DataFilterComp dataFilterCompB;

	private Section dataFilterSection = null;

	private String stringDataFilterA;

	private String stringDataFilterB;

	public ColumnsComparisonMasterDetailsPage(FormEditor editor, String id,
			String title) {
		super(editor, id, title);

	}

	/**
	 * DOC rli ColumnsComparisonAnalysisResultPage class global comment.
	 * Detailled comment
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
			Indicator[] currentIndicators = new Indicator[] {
					rowMatchingIndicatorA, rowMatchingIndicatorB };
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
			if (!DefinitionHandler.getInstance().setDefaultIndicatorDefinition(
					indicators[i])) {
				log
						.error("Could not set the definition of the given indicator :"
								+ indicators[i].getName());
			}
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		form = managedForm.getForm();
		form
				.setText(DefaultMessagesImpl
						.getString("ColumnsComparisonMasterDetailsPage.columnSetComparisionAnalysis")); //$NON-NLS-1$
		this.metadataSection
				.setText(DefaultMessagesImpl
						.getString("ColumnsComparisonMasterDetailsPage.analysisMetadata")); //$NON-NLS-1$
		this.metadataSection
				.setDescription(DefaultMessagesImpl
						.getString("ColumnsComparisonMasterDetailsPage.setAnalysisProperties")); //$NON-NLS-1$
		// MOD mzhao 2009-06-17 feature 5887.
		anaColumnCompareViewer = new AnalysisColumnCompareTreeViewer(this,
				topComp, (Analysis) this.currentModelElement);
		columnsComparisonSection = anaColumnCompareViewer
				.getColumnsComparisonSection();
		anaColumnCompareViewer.addPropertyChangeListener(this);

		createDataFilterSection(form, topComp);
		
        // MOD mzhao 2009-06-17 feature 5887.
        foldingSections(new Section[] { metadataSection, columnsComparisonSection, dataFilterSection });
        // ~
		
		// MOD 2009-01-10 mzhao, for register sections that would be collapse or
		// expand later.
		currentEditor
				.registerSections(new Section[] { columnsComparisonSection });
	}

	void createDataFilterSection(final ScrolledForm form,
			Composite anasisDataComp) {
		dataFilterSection = createSection(
				form,
				anasisDataComp,
				DefaultMessagesImpl
						.getString("ColumnsComparisonMasterDetailsPage.dataFilter"), false, DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.editDataFilter")); //$NON-NLS-1$ //$NON-NLS-2$
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
				AnalysisHelper.setStringDataFilter(analysis, dataFilterCompA
						.getDataFilterString(), 0);
			}
		});

		Composite rightComp = toolkit.createComposite(sashForm);
		rightComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		rightComp.setLayout(new GridLayout());
		dataFilterCompB = new DataFilterComp(rightComp, stringDataFilterB);
		dataFilterCompB.addPropertyChangeListener(this);
		dataFilterCompB.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				AnalysisHelper.setStringDataFilter(analysis, dataFilterCompB
						.getDataFilterString(), 1);
			}
		});

		dataFilterSection.setClient(sectionClient);
	}

	/**
	 * 
	 * DOC mzhao Open column selection dialog for left column set. this method
	 * is intended to use from cheat sheets.
	 */
	public void openColumnsSetASelectionDialog() {
		anaColumnCompareViewer.openColumnsSetASelectionDialog();
	}

	/**
	 * 
	 * DOC mzhao Open column selection dialog for right column set. this method
	 * is intended to use from cheat sheets.
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
		getAnalysisHandler().clearAnalysis();
		List<ModelElement> analysedElements = new ArrayList<ModelElement>();
		anaColumnCompareViewer.setColumnABForMatchingIndicator(
				rowMatchingIndicatorA, anaColumnCompareViewer.getColumnListA(),
				anaColumnCompareViewer.getColumnListB());
		anaColumnCompareViewer.setColumnABForMatchingIndicator(
				rowMatchingIndicatorB, anaColumnCompareViewer.getColumnListB(),
				anaColumnCompareViewer.getColumnListA());
		for (int i = 0; i < anaColumnCompareViewer.getColumnListA().size(); i++) {
			analysedElements
					.add(anaColumnCompareViewer.getColumnListA().get(i));
		}
		for (int i = 0; i < anaColumnCompareViewer.getColumnListB().size(); i++) {
			analysedElements
					.add(anaColumnCompareViewer.getColumnListB().get(i));
		}
		if (analysedElements.size() > 0) {
			TdDataProvider tdDataProvider = DataProviderHelper
					.getTdDataProvider((Column) analysedElements.get(0));
			analysis.getContext().setConnection(tdDataProvider);
		} else {
			analysis.getContext().setConnection(null);
			analysis.getClientDependency().clear();
		}
		// rowCountIndicator.setAnalyzedElement(value)
		// rowMatchingIndicatorA
		AnalysisBuilder anaBuilder = new AnalysisBuilder();
		anaBuilder.setAnalysis(this.analysis);
		if (anaColumnCompareViewer.getCheckComputeButton().getSelection()) {
			analysis.getParameters().getDeactivatedIndicators().add(
					rowMatchingIndicatorB);
		} else {
			analysis.getParameters().getDeactivatedIndicators().clear();
		}
		anaBuilder
				.addElementsToAnalyze(analysedElements
						.toArray(new ModelElement[analysedElements.size()]),
						new Indicator[] { rowMatchingIndicatorA,
								rowMatchingIndicatorB });
		ReturnCode save = AnaResourceFileHelper.getInstance().save(analysis);
		if (save.isOk()) {
			log
					.info("Success to save connection analysis:" + analysis.getFileName()); //$NON-NLS-1$
		}

	}

	public void fireRuningItemChanged(boolean status) {
		super.fireRuningItemChanged(status);

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	protected ReturnCode canSave() {
		if (anaColumnCompareViewer.getColumnListA().size() != anaColumnCompareViewer
				.getColumnListB().size()) {
			return new ReturnCode(
					DefaultMessagesImpl
							.getString("ColumnsComparisonMasterDetailsPage.columnsSameMessage"), false); //$NON-NLS-1$
		}

		if (anaColumnCompareViewer.getColumnListA().size() > 0) {

			if (!ColumnHelper.isFromSameTable(anaColumnCompareViewer
					.getColumnListA())
					|| !ColumnHelper.isFromSameTable(anaColumnCompareViewer
							.getColumnListB())) {
				return new ReturnCode(
						DefaultMessagesImpl
								.getString("ColumnsComparisonMasterDetailsPage.notSameElementMessage"), false); //$NON-NLS-1$
			}

			for (int i = 0; i < anaColumnCompareViewer.getColumnListA().size(); i++) {
				Column columnA = anaColumnCompareViewer.getColumnListA().get(i);
				Column columnB = anaColumnCompareViewer.getColumnListB().get(i);

				ColumnSet ownerA = ColumnHelper.getColumnSetOwner(columnA);
				ColumnSet ownerB = ColumnHelper.getColumnSetOwner(columnB);

				int typeA = ((TdColumn) columnA).getJavaType();
				int typeB = ((TdColumn) columnB).getJavaType();
				if (!Java2SqlType.isGenericSameType(typeA, typeB)) {
					return new ReturnCode(
							DefaultMessagesImpl
									.getString("ColumnsComparisonMasterDetailsPage.notSameColumnType"), false); //$NON-NLS-1$
				}
				if (!ColumnSetHelper.isFromSamePackage(ownerA, ownerB)) {
					return new ReturnCode(
							DefaultMessagesImpl
									.getString("ColumnsComparisonMasterDetailsPage.schemaSameMessage"), false); //$NON-NLS-1$
				}
			}

			List<Column> allColumns = new ArrayList<Column>();
			allColumns.addAll(anaColumnCompareViewer.getColumnListA());
			allColumns.addAll(anaColumnCompareViewer.getColumnListB());

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
		return new ReturnCode(rowMatchingIndicatorA.getColumnSetA().size() != 0);
	}

	public ScrolledForm getScrolledForm() {
		return form;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
			currentEditor.firePropertyChange(IEditorPart.PROP_DIRTY);
			currentEditor.setRefreshResultPage(true);
		} else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt
				.getPropertyName())) {
			this.setDirty(true);
		}
	}
}
