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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnNominalIntervalTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.editor.preview.HideSeriesChartComposite;
import org.talend.dataprofiler.core.ui.utils.JungGraphGenerator;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dq.analysis.ColumnCorrelationAnalysisHandler;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.indicators.graph.GraphBuilder;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * @author xzhao
 * 
 */
public class ColumnCorrelationNominalAndIntervalMasterPage extends
		AbstractAnalysisMetadataPage implements PropertyChangeListener {

	private static Logger log = Logger
			.getLogger(ColumnCorrelationNominalAndIntervalMasterPage.class);

	AnalysisColumnNominalIntervalTreeViewer treeViewer;

	DataFilterComp dataFilterComp;

	ColumnCorrelationAnalysisHandler correlationAnalysisHandler;

	private ColumnSetMultiValueIndicator columnSetMultiIndicator;

	protected String execLang;

	private String stringDataFilter;

	private Composite chartComposite;

	private ScrolledForm form;

	private static final int TREE_MAX_LENGTH = 400;

	protected Composite[] previewChartCompsites;

	private EList<ModelElement> analyzedColumns;

	private Section analysisColSection;

	private Section dataFilterSection;

	private Section previewSection;

	private Section analysisParamSection;

	public ColumnCorrelationNominalAndIntervalMasterPage(FormEditor editor,
			String id, String title) {
		super(editor, id, title);
	}

	public void initialize(FormEditor editor) {
		super.initialize(editor);

		correlationAnalysisHandler = new ColumnCorrelationAnalysisHandler();
		correlationAnalysisHandler
				.setAnalysis((Analysis) this.currentModelElement);
		stringDataFilter = correlationAnalysisHandler.getStringDataFilter();
		analyzedColumns = correlationAnalysisHandler.getAnalyzedColumns();
		CountAvgNullIndicator currentCountAvgNullIndicator;
		if (correlationAnalysisHandler.getIndicator() == null) {
			ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
			currentCountAvgNullIndicator = columnsetFactory
					.createCountAvgNullIndicator();
			columnSetMultiIndicator = currentCountAvgNullIndicator;
		} else {
			columnSetMultiIndicator = (ColumnSetMultiValueIndicator) correlationAnalysisHandler
					.getIndicator();
		}
		for (ModelElement element : analyzedColumns) {
			TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(element);
			if (tdColumn == null) {
				continue;
			}
			// currentColumnIndicator = new ColumnIndicator(tdColumn);
			DataminingType dataminingType = correlationAnalysisHandler
					.getDatamingType(tdColumn);
			MetadataHelper.setDataminingType(
					dataminingType == null ? DataminingType.NOMINAL
							: dataminingType, tdColumn);
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		this.form = managedForm.getForm();
		Composite body = form.getBody();

		body.setLayout(new GridLayout());
		SashForm sForm = new SashForm(body, SWT.NULL);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		topComp = toolkit.createComposite(sForm);
		topComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		topComp.setLayout(new GridLayout());
		metadataSection = creatMetadataSection(form, topComp);
		metadataSection.setText(DefaultMessagesImpl
				.getString("ColumnMasterDetailsPage.analysisMeta")); //$NON-NLS-1$
		metadataSection.setDescription(DefaultMessagesImpl
				.getString("ColumnMasterDetailsPage.setPropOfAnalysis")); //$NON-NLS-1$

		// set title of form.
		if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator() == columnSetMultiIndicator
				.eClass()) {
			form
					.setText(DefaultMessagesImpl
							.getString("ColumnCorrelationNominalAndIntervalMasterPage.CorrelationAnalysisInterval")); //$NON-NLS-1$
		}

		if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator() == columnSetMultiIndicator
				.eClass()) {
			form
					.setText(DefaultMessagesImpl
							.getString("ColumnCorrelationNominalAndIntervalMasterPage.CorrelationAnalysisDate")); //$NON-NLS-1$
		}

		if (ColumnsetPackage.eINSTANCE.getWeakCorrelationIndicator() == columnSetMultiIndicator
				.eClass()) {
			form.setText("Nominal Columns Correlation");
		}

		createAnalysisColumnsSection(form, topComp);

		createDataFilterSection(form, topComp);

		// createAnalysisParamSection(form, topComp);

		Composite previewComp = toolkit.createComposite(sForm);
		previewComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		previewComp.setLayout(new GridLayout());

		createPreviewSection(form, previewComp);

		// MOD 2009-01-12 mzhao, for register sections that would be collapse or
		// expand later.
		currentEditor.registerSections(new Section[] { analysisColSection,
				metadataSection, dataFilterSection, analysisParamSection,
				previewSection });
	}

	void createAnalysisColumnsSection(final ScrolledForm form,
			Composite anasisDataComp) {
		analysisColSection = createSection(
				form,
				anasisDataComp,
				DefaultMessagesImpl
						.getString("ColumnMasterDetailsPage.analyzeColumn"), false, null); //$NON-NLS-1$

		Composite topComp = toolkit.createComposite(analysisColSection);
		topComp.setLayout(new GridLayout());
		// ~ MOD mzhao 2009-05-05,Bug 6587.
		createConnBindWidget(topComp);
		// ~
		Hyperlink clmnBtn = toolkit
				.createHyperlink(
						topComp,
						DefaultMessagesImpl
								.getString("ColumnMasterDetailsPage.selectColumn"), SWT.NONE); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP)
				.applyTo(clmnBtn);
		clmnBtn.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				openColumnsSelectionDialog();
			}

		});

		Composite tree = toolkit.createComposite(topComp, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(tree);
		tree.setLayout(new GridLayout());
		((GridData) tree.getLayoutData()).heightHint = TREE_MAX_LENGTH;

		treeViewer = new AnalysisColumnNominalIntervalTreeViewer(tree, this);
		treeViewer.addPropertyChangeListener(this);
		treeViewer.setInput(analyzedColumns.toArray());
		treeViewer.setDirty(false);
		analysisColSection.setClient(topComp);

	}

	/**
     * 
     */
	public void openColumnsSelectionDialog() {
		List<Column> columnList = treeViewer.getColumnSetMultiValueList();
		if (columnList == null) {
			columnList = new ArrayList<Column>();
		}
		ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(
				this,
				null,
				DefaultMessagesImpl
						.getString("ColumnMasterDetailsPage.columnSelection"), columnList, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$ //$NON-NLS-2$
		if (dialog.open() == Window.OK) {
			Object[] columns = dialog.getResult();
			treeViewer.setInput(columns);
			return;
		}
	}

	void createPreviewSection(final ScrolledForm form, Composite parent) {

		previewSection = createSection(
				form,
				parent,
				DefaultMessagesImpl
						.getString("ColumnMasterDetailsPage.graphics"), true, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$ //$NON-NLS-2$
		previewSection.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite sectionClient = toolkit.createComposite(previewSection);
		sectionClient.setLayout(new GridLayout());
		sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

		ImageHyperlink refreshBtn = toolkit.createImageHyperlink(sectionClient,
				SWT.NONE);
		refreshBtn.setText(DefaultMessagesImpl
				.getString("ColumnMasterDetailsPage.refreshGraphics")); //$NON-NLS-1$
		refreshBtn.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
		final Label message = toolkit.createLabel(sectionClient,
				DefaultMessagesImpl
						.getString("ColumnMasterDetailsPage.spaceWhite")); //$NON-NLS-1$
		message.setForeground(Display.getDefault()
				.getSystemColor(SWT.COLOR_RED));
		message.setVisible(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(
				sectionClient);

		chartComposite = toolkit.createComposite(sectionClient);
		chartComposite.setLayout(new GridLayout());
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Analysis analysis = correlationAnalysisHandler.getAnalysis();

		refreshBtn.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {

				for (Control control : chartComposite.getChildren()) {
					control.dispose();
				}

				boolean analysisStatue = analysis.getResults()
						.getResultMetadata() != null
						&& analysis.getResults().getResultMetadata()
								.getExecutionDate() != null;

				if (!analysisStatue) {
					boolean returnCode = MessageDialog
							.openConfirm(
									null,
									DefaultMessagesImpl
											.getString("ColumnMasterDetailsPage.ViewResult"), //$NON-NLS-1$
									DefaultMessagesImpl
											.getString("ColumnMasterDetailsPage.RunOrSeeSampleData")); //$NON-NLS-1$

					if (returnCode) {
						new RunAnalysisAction(
								ColumnCorrelationNominalAndIntervalMasterPage.this)
								.run();
						message.setVisible(false);
					} else {
						createPreviewCharts(form, chartComposite, false);
						message.setText(DefaultMessagesImpl
								.getString("ColumnMasterDetailsPage.warning")); //$NON-NLS-1$
						message.setVisible(true);
					}
				} else {
					createPreviewCharts(form, chartComposite, true);
				}

				chartComposite.layout();
				form.reflow(true);
			}

		});

		previewSection.setClient(sectionClient);
	}

	public void createPreviewCharts(final ScrolledForm form,
			final Composite composite, final boolean isCreate) {

		List<Composite> previewChartList = new ArrayList<Composite>();

		if (ColumnsetPackage.eINSTANCE.getWeakCorrelationIndicator() == columnSetMultiIndicator
				.eClass()) {
			GraphBuilder gBuilder = new GraphBuilder();
			gBuilder.setTotalWeight(columnSetMultiIndicator.getCount());
			List<Object[]> listRows = columnSetMultiIndicator.getListRows();
			JungGraphGenerator generator = new JungGraphGenerator(gBuilder,
					listRows);
			generator.generate(composite, false);

		} else {

			List<Column> numericOrDateList = new ArrayList<Column>();
			if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator() == columnSetMultiIndicator
					.eClass()) {
				numericOrDateList = columnSetMultiIndicator.getNumericColumns();
			}
			if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator() == columnSetMultiIndicator
					.eClass()) {
				numericOrDateList = columnSetMultiIndicator.getDateColumns();
			}

			for (Column column : numericOrDateList) {
				final TdColumn tdColumn = (TdColumn) column;

				ExpandableComposite exComp = toolkit.createExpandableComposite(
						composite, ExpandableComposite.TREE_NODE
								| ExpandableComposite.CLIENT_INDENT);
				exComp.setText(DefaultMessagesImpl.getString(
						"ColumnMasterDetailsPage.column", tdColumn.getName())); //$NON-NLS-1$
				exComp.setLayout(new GridLayout());
				exComp.setData(columnSetMultiIndicator);
				previewChartList.add(exComp);

				final Composite comp = toolkit.createComposite(exComp);
				comp.setLayout(new GridLayout());
				comp.setLayoutData(new GridData(GridData.FILL_BOTH));

				if (tdColumn != null) {
					IRunnableWithProgress rwp = new IRunnableWithProgress() {

						public void run(final IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask(DefaultMessagesImpl.getString(
									"ColumnMasterDetailsPage.createPreview", //$NON-NLS-1$
									tdColumn.getName()),
									IProgressMonitor.UNKNOWN); //$NON-NLS-1$
							Display.getDefault().asyncExec(new Runnable() {

								public void run() {
									// carete chart
									GridData gd = new GridData();
									gd.heightHint = 230;
									gd.widthHint = 460;

									HideSeriesChartComposite hcc = new HideSeriesChartComposite(
											comp, analysis,
											columnSetMultiIndicator, tdColumn,
											false);
									hcc.setLayoutData(gd);
								}

							});

							monitor.done();
						}

					};

					try {
						new ProgressMonitorDialog(getSite().getShell()).run(
								true, false, rwp);
					} catch (Exception ex) {
						log.error(ex, ex);
					}
				}

				exComp.addExpansionListener(new ExpansionAdapter() {

					@Override
					public void expansionStateChanged(ExpansionEvent e) {
						getChartComposite().layout();
						form.reflow(true);
					}

				});

				exComp.setExpanded(true);

				exComp.setClient(comp);
			}
		}

		if (!previewChartList.isEmpty()) {
			this.previewChartCompsites = previewChartList
					.toArray(new Composite[previewChartList.size()]);
		}
	}

	@Override
	public void refresh() {
		if (chartComposite != null) {
			try {
				for (Control control : chartComposite.getChildren()) {
					control.dispose();
				}

				createPreviewCharts(form, chartComposite, true);
				chartComposite.layout();
				getForm().reflow(true);
			} catch (Exception ex) {
				log.error(ex, ex);
			}

		}
	}

	/**
	 * @param form
	 * @param toolkit
	 * @param anasisDataComp
	 */
	void createDataFilterSection(final ScrolledForm form,
			Composite anasisDataComp) {
		dataFilterSection = createSection(
				form,
				anasisDataComp,
				DefaultMessagesImpl
						.getString("ColumnMasterDetailsPage.dataFilter"), false, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.editDataFilter")); //$NON-NLS-1$ //$NON-NLS-2$

		Composite sectionClient = toolkit.createComposite(dataFilterSection);
		dataFilterComp = new DataFilterComp(sectionClient, stringDataFilter);
		dataFilterComp.addPropertyChangeListener(this);
		dataFilterSection.setClient(sectionClient);
	}

	/**
	 * DOC hcheng Comment method "createAnalysisParamSection".
	 * 
	 * @param form
	 * @param anasisDataComp
	 */
	void createAnalysisParamSection(final ScrolledForm form,
			Composite anasisDataComp) {
		analysisParamSection = createSection(
				form,
				anasisDataComp,
				DefaultMessagesImpl
						.getString("ColumnMasterDetailsPage.AnalysisParameter"), false, null); //$NON-NLS-1$
		Composite sectionClient = toolkit.createComposite(analysisParamSection);
		sectionClient.setLayout(new GridLayout(2, false));
		toolkit.createLabel(sectionClient, DefaultMessagesImpl
				.getString("ColumnMasterDetailsPage.ExecutionEngine")); //$NON-NLS-1$
		final CCombo execCombo = new CCombo(sectionClient, SWT.BORDER);
		execCombo.setEditable(false);
		for (ExecutionLanguage language : ExecutionLanguage.VALUES) {
			String temp = language.getLiteral();
			execCombo.add(temp);
		}
		// ExecutionLanguage executionLanguage =
		// analysis.getParameters().getExecutionLanguage();
		execCombo.setText(ExecutionLanguage.SQL.getLiteral());
		execCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
				execLang = execCombo.getText();
			}

		});
		analysisParamSection.setClient(sectionClient);
	}

	/**
	 * @param outputFolder
	 * @throws DataprofilerCoreException
	 */

	public void saveAnalysis() throws DataprofilerCoreException {
		correlationAnalysisHandler.clearAnalysis();
		columnSetMultiIndicator.getAnalyzedColumns().clear();

		// set execute engine
		Analysis analysis = correlationAnalysisHandler.getAnalysis();
		analysis.getParameters().setExecutionLanguage(
				ExecutionLanguage.get(execLang));

		// set data filter
		correlationAnalysisHandler.setStringDataFilter(dataFilterComp
				.getDataFilterString());

		// save analysis
		List<Column> columnList = treeViewer.getColumnSetMultiValueList();

		TdDataProvider tdProvider = null;
		if (columnList != null && columnList.size() != 0) {
			tdProvider = DataProviderHelper
					.getTdDataProvider(SwitchHelpers.COLUMN_SWITCH
							.doSwitch(columnList.get(0)));
			analysis.getContext().setConnection(tdProvider);
			columnSetMultiIndicator.getAnalyzedColumns().addAll(columnList);
			correlationAnalysisHandler.addIndicator(columnList,
					columnSetMultiIndicator);
		} else {
			analysis.getContext().setConnection(null);
			analysis.getClientDependency().clear();
		}

		String urlString = analysis.eResource() != null ? analysis.eResource()
				.getURI().toFileString() : PluginConstant.EMPTY_STRING;

		ReturnCode saved = AnaResourceFileHelper.getInstance().save(analysis);
		if (saved.isOk()) {
			if (tdProvider != null) {
				PrvResourceFileHelper.getInstance().save(tdProvider);
			}
			//AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true
			// );
			if (log.isDebugEnabled()) {
				log.debug("Saved in  " + urlString + " successful");
			}
		} else {
			throw new DataprofilerCoreException(
					DefaultMessagesImpl
							.getString(
									"ColumnMasterDetailsPage.problem", analysis.getName(), urlString, saved.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		treeViewer.setDirty(false);
		dataFilterComp.setDirty(false);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
			((AnalysisEditor) this.getEditor())
					.firePropertyChange(IEditorPart.PROP_DIRTY);
		} else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt
				.getPropertyName())) {
			this.correlationAnalysisHandler.setStringDataFilter((String) evt
					.getNewValue());
		}

	}

	@Override
	public boolean isDirty() {
		return super.isDirty() || (treeViewer != null && treeViewer.isDirty())
				|| (dataFilterComp != null && dataFilterComp.isDirty());
	}

	@Override
	public void dispose() {
		super.dispose();
		if (this.treeViewer != null) {
			this.treeViewer.removePropertyChangeListener(this);
		}
		if (dataFilterComp != null) {
			this.dataFilterComp.removePropertyChangeListener(this);
		}
	}

	/**
	 * Getter for treeViewer.
	 * 
	 * @return the treeViewer
	 */
	public AnalysisColumnNominalIntervalTreeViewer getTreeViewer() {
		return this.treeViewer;
	}

	public ScrolledForm getForm() {
		return form;
	}

	public void setForm(ScrolledForm form) {
		this.form = form;
	}

	public ColumnCorrelationAnalysisHandler getColumnCorrelationAnalysisHandler() {
		return correlationAnalysisHandler;
	}

	public ColumnSetMultiValueIndicator getColumnSetMultiValueIndicator() {
		return columnSetMultiIndicator;
	}

	public Composite[] getPreviewChartCompsites() {
		return previewChartCompsites;
	}

	public Composite getChartComposite() {
		return chartComposite;
	}

	@Override
	protected ReturnCode canSave() {
		String message = null;
		List<Column> columnSetMultiValueList = getTreeViewer()
				.getColumnSetMultiValueList();

		if (!columnSetMultiValueList.isEmpty()) {
			if (!ColumnHelper.isFromSameTable(columnSetMultiValueList)) {
				message = DefaultMessagesImpl
						.getString("ColumnCorrelationNominalAndIntervalMasterPage.CannotCreateAnalysis");
			}

			List<Column> columns = treeViewer.getColumnSetMultiValueList();

			if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator() == columnSetMultiIndicator
					.eClass()) {
				for (int i = 0; i < columns.size(); i++) {
					TdColumn tdColumn = (TdColumn) columns.get(i);
					if (Java2SqlType.isDateInSQL(tdColumn.getJavaType())) {
						message = DefaultMessagesImpl
								.getString("ColumnCorrelationNominalAndIntervalMasterPage.ColumnNotUsed");
						break;
					}
				}
			}

			if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator() == columnSetMultiIndicator
					.eClass()) {
				for (int i = 0; i < columns.size(); i++) {
					TdColumn tdColumn = (TdColumn) columns.get(i);
					if (Java2SqlType.isNumbericInSQL(tdColumn.getJavaType())) {
						message = DefaultMessagesImpl
								.getString("ColumnCorrelationNominalAndIntervalMasterPage.NumbericColumn");
						break;
					}
				}
			}

			if (ColumnsetPackage.eINSTANCE.getWeakCorrelationIndicator() == columnSetMultiIndicator
					.eClass()) {
				for (int i = 0; i < columns.size(); i++) {
					TdColumn tdColumn = (TdColumn) columns.get(i);

					if (correlationAnalysisHandler.getDatamingType(tdColumn) != DataminingType.NOMINAL) {
						message = "Not all columnes are NOMINAL.";
						break;
					}
				}
			}
		}

		if (message == null) {
			return new ReturnCode(true);
		}

		return new ReturnCode(message, false);
	}

	@Override
	protected ReturnCode canRun() {
		List<Column> columnSetMultiValueList = getTreeViewer()
				.getColumnSetMultiValueList();
		if (columnSetMultiValueList.isEmpty()) {
			return new ReturnCode(
					DefaultMessagesImpl
							.getString("ColumnCorrelationNominalAndIntervalMasterPage.NoColumnsAssigned"), false); //$NON-NLS-1$
		}

		return new ReturnCode(true);

	}

}
