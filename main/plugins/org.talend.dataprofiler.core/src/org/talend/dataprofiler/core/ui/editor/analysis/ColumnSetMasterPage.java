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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.chart.ChartDecorator;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.composite.AbstractColumnDropTree;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnSetTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.editor.composite.IndicatorsComp;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTypeStatesOperator;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates;
import org.talend.dataprofiler.core.ui.pref.EditorPreferencePage;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.columnset.AllMatchIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.analysis.ColumnSetAnalysisHandler;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.dq.nodes.DFColumnFolderRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.informix.util.stringUtil;

/**
 * @author yyi 2009-12-16
 */
public class ColumnSetMasterPage extends AbstractAnalysisMetadataPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnSetMasterPage.class);

    AnalysisColumnSetTreeViewer treeViewer;

    IndicatorsComp indicatorsViewer;

    DataFilterComp dataFilterComp;

    ColumnSetAnalysisHandler columnSetAnalysisHandler;

    private SimpleStatIndicator simpleStatIndicator;

    private AllMatchIndicator allMatchIndicator;

    private String stringDataFilter;

    private Composite chartComposite;

    private static final int TREE_MAX_LENGTH = 300;

    private static final int INDICATORS_SECTION_HEIGHT = 300;

    protected Composite[] previewChartCompsites;

    private EList<ModelElement> analyzedColumns;

    private Section analysisColSection;

    private Section dataFilterSection;

    private Section previewSection;

    private Section indicatorsSection;

    private ModelElementIndicator[] currentModelElementIndicators;

    // MOD qiongli 2011-1-25.add java engine for columnSet.
    private String execLang;

    private Section analysisParamSection = null;

    private CCombo execCombo;

    private Button drillDownCheck;

    private Text maxNumText;

    private Button storeDataCheck;

    private SashForm sForm;

    private Composite previewComp;

    public ColumnSetMasterPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        currentEditor = (AnalysisEditor) editor;
    }

    @Override
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        recomputeIndicators();

    }

    public void recomputeIndicators() {
        columnSetAnalysisHandler = new ColumnSetAnalysisHandler();
        columnSetAnalysisHandler.setAnalysis((Analysis) this.currentModelElement);
        stringDataFilter = columnSetAnalysisHandler.getStringDataFilter();
        analyzedColumns = columnSetAnalysisHandler.getAnalyzedColumns();
        if (columnSetAnalysisHandler.getSimpleStatIndicator() == null
                || columnSetAnalysisHandler.getSimpleStatIndicator().eIsProxy()) {
            ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
            simpleStatIndicator = columnsetFactory.createSimpleStatIndicator();
            simpleStatIndicator.setRowCountIndicator(IndicatorsFactory.eINSTANCE.createRowCountIndicator());
            simpleStatIndicator.setDistinctCountIndicator(IndicatorsFactory.eINSTANCE.createDistinctCountIndicator());
            simpleStatIndicator.setDuplicateCountIndicator(IndicatorsFactory.eINSTANCE.createDuplicateCountIndicator());
            simpleStatIndicator.setUniqueCountIndicator(IndicatorsFactory.eINSTANCE.createUniqueCountIndicator());
        } else {
            simpleStatIndicator = (SimpleStatIndicator) columnSetAnalysisHandler.getSimpleStatIndicator();
        }
        if (columnSetAnalysisHandler.getAllmatchIndicator() == null) {
            ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
            allMatchIndicator = columnsetFactory.createAllMatchIndicator();
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(allMatchIndicator);
        } else {
            allMatchIndicator = (AllMatchIndicator) columnSetAnalysisHandler.getAllmatchIndicator();
        }

        initializeIndicator(simpleStatIndicator);
        List<ModelElementIndicator> meIndicatorList = new ArrayList<ModelElementIndicator>();
        ModelElementIndicator currentIndicator = null;
        for (ModelElement element : analyzedColumns) {

            // MOD yyi 2011-02-16 17871:delimitefile
            MetadataColumn mdColumn = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(element);
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(element);
            TdXmlElementType xmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(element);

            if (tdColumn == null && mdColumn == null && xmlElement == null) {
                continue;
            }

            if (tdColumn == null && mdColumn != null) {
                currentIndicator = ModelElementIndicatorHelper.createDFColumnIndicator(RepositoryNodeHelper
                        .recursiveFind(mdColumn));
            } else if (tdColumn != null) {
                RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(tdColumn);
                if (recursiveFind == null) {
                    recursiveFind = RepositoryNodeHelper.createRepositoryNode(tdColumn);
                }
                currentIndicator = ModelElementIndicatorHelper.createModelElementIndicator(recursiveFind);
            } else if (xmlElement != null) {
                currentIndicator = ModelElementIndicatorHelper.createXmlElementIndicator(RepositoryNodeHelper
                        .recursiveFind(xmlElement));
            }

            DataminingType dataminingType = MetadataHelper.getDataminingType(element);
            MetadataHelper.setDataminingType(dataminingType == null ? DataminingType.NOMINAL : dataminingType, element);

            Collection<Indicator> indicatorList = columnSetAnalysisHandler.getRegexMathingIndicators(element);
            if (null != currentIndicator) {
                currentIndicator.setIndicators(indicatorList.toArray(new Indicator[indicatorList.size()]));
                meIndicatorList.add(currentIndicator);
            }
        }
        currentModelElementIndicators = meIndicatorList.toArray(new ModelElementIndicator[meIndicatorList.size()]);

    }

    public ModelElementIndicator[] getCurrentModelElementIndicators() {
        return this.currentModelElementIndicators;
    }

    private void initializeIndicator(Indicator indicator) {
        if (indicator.getIndicatorDefinition() == null || indicator.getIndicatorDefinition().eIsProxy()) {
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicator);
        }
        if (indicator instanceof CompositeIndicator) {
            for (Indicator child : ((CompositeIndicator) indicator).getChildIndicators()) {
                initializeIndicator(child); // recurse
            }
        }
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        this.form = managedForm.getForm();
        Composite body = form.getBody();

        body.setLayout(new GridLayout());
        sForm = new SashForm(body, SWT.NULL);
        sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        topComp = toolkit.createComposite(sForm);
        topComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        topComp.setLayout(new GridLayout());
        metadataSection = creatMetadataSection(form, topComp);
        metadataSection.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.analysisMeta")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.setPropOfAnalysis")); //$NON-NLS-1$

        form.setText(DefaultMessagesImpl.getString("ColumnSetMasterPage.title")); //$NON-NLS-1$

        createAnalysisColumnsSection(form, topComp);

        createIndicatorsSection(form, topComp);

        createDataFilterSection(form, topComp);

        createAnalysisParamSection(form, topComp);

        if (!EditorPreferencePage.isHideGraphics()) {
            previewComp = toolkit.createComposite(sForm);
            previewComp.setLayoutData(new GridData(GridData.FILL_BOTH));
            previewComp.setLayout(new GridLayout());
            // add by hcheng for 0007290: Chart cannot auto compute it's size in
            // DQRule analsyis Editor
            previewComp.addControlListener(new ControlAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse .swt.events.ControlEvent)
                 */
                @Override
                public void controlResized(ControlEvent e) {
                    super.controlResized(e);
                    sForm.redraw();
                    form.reflow(true);
                }
            });
            // ~
            createPreviewSection(form, previewComp);
        }
    }

    /**
     * DOC yyi Comment method "createIndicatorsSection".
     * 
     * @param topComp
     * @param form
     */
    private void createIndicatorsSection(ScrolledForm form, Composite topComp) {
        indicatorsSection = createSection(form, topComp, "Indicators", null); //$NON-NLS-1$

        Composite indicatorsComp = toolkit.createComposite(indicatorsSection, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(indicatorsComp);
        indicatorsComp.setLayout(new GridLayout());
        ((GridData) indicatorsComp.getLayoutData()).heightHint = INDICATORS_SECTION_HEIGHT;

        indicatorsViewer = new IndicatorsComp(indicatorsComp, this);
        indicatorsViewer.setDirty(false);
        indicatorsViewer.addPropertyChangeListener(this);
        indicatorsViewer.setInput(simpleStatIndicator, allMatchIndicator);
        indicatorsSection.setClient(indicatorsComp);
    }

    void createAnalysisColumnsSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisColSection = createSection(form, anasisDataComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.analyzeColumn"), null); //$NON-NLS-1$

        Composite topComp = toolkit.createComposite(analysisColSection);
        topComp.setLayout(new GridLayout());
        // ~ MOD mzhao 2009-05-05,Bug 6587.
        createConnBindWidget(topComp);
        // ~
        Hyperlink clmnBtn = toolkit.createHyperlink(topComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.selectColumn"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(clmnBtn);
        clmnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {
                openColumnsSelectionDialog();
            }

        });

        Composite tree = toolkit.createComposite(topComp, SWT.None);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        tree.setLayout(new GridLayout());
        ((GridData) tree.getLayoutData()).heightHint = TREE_MAX_LENGTH;

        treeViewer = new AnalysisColumnSetTreeViewer(tree, this);
        treeViewer.addPropertyChangeListener(this);
        treeViewer.setInput(analyzedColumns.toArray());
        treeViewer.setDirty(false);
        analysisColSection.setClient(topComp);

    }

    public void openColumnsSelectionDialog() {
        List<IRepositoryNode> columnList = treeViewer.getColumnSetMultiValueList();
        if (columnList == null) {
            columnList = new ArrayList<IRepositoryNode>();
        }
        RepositoryNode connNode = (RepositoryNode) getConnCombo().getData(String.valueOf(getConnCombo().getSelectionIndex()));
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(
                this,
                null,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelection"), columnList, connNode, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$ //$NON-NLS-2$
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            treeViewer.setInput(columns);
            indicatorsViewer.setInput(simpleStatIndicator, allMatchIndicator);
            return;
        }
    }

    void createPreviewSection(final ScrolledForm form, Composite parent) {
        previewSection = createSection(
                form,
                parent,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.graphics"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$ //$NON-NLS-2$
        previewSection.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite sectionClient = toolkit.createComposite(previewSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        ImageHyperlink refreshBtn = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
        refreshBtn.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.refreshGraphics")); //$NON-NLS-1$
        refreshBtn.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
        final Label message = toolkit.createLabel(sectionClient,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.spaceWhite")); //$NON-NLS-1$
        message.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        message.setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        chartComposite = toolkit.createComposite(sectionClient);
        chartComposite.setLayout(new GridLayout());
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Analysis analysis = columnSetAnalysisHandler.getAnalysis();

        refreshBtn.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {

                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                boolean analysisStatue = analysis.getResults().getResultMetadata() != null
                        && analysis.getResults().getResultMetadata().getExecutionDate() != null;

                if (!analysisStatue) {
                    boolean returnCode = MessageDialog.openConfirm(null,
                            DefaultMessagesImpl.getString("ColumnMasterDetailsPage.ViewResult"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("ColumnMasterDetailsPage.RunOrSeeSampleData")); //$NON-NLS-1$

                    if (returnCode) {
                        new RunAnalysisAction().run();
                        message.setVisible(false);
                    } else {
                        createPreviewCharts(form, chartComposite, false);
                        message.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.warning")); //$NON-NLS-1$
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

    public void createPreviewCharts(final ScrolledForm form, final Composite parentComp, final boolean isCreate) {
        Section previewSimpleStatSection = createSection(
                form,
                parentComp,
                DefaultMessagesImpl.getString("ColumnSetResultPage.SimpleStatistics"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$ //$NON-NLS-2$
        previewSimpleStatSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite sectionClient = toolkit.createComposite(previewSimpleStatSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite simpleComposite = toolkit.createComposite(sectionClient);
        simpleComposite.setLayout(new GridLayout(1, true));
        simpleComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createSimpleStatistics(form, simpleComposite);
        previewSimpleStatSection.setClient(sectionClient);

        // match
        if (0 < allMatchIndicator.getCompositeRegexMatchingIndicators().size()) {

            Section previewMatchSection = createSection(form, parentComp, "All Match", ""); //$NON-NLS-1$
            previewMatchSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            Composite sectionMatchClient = toolkit.createComposite(previewMatchSection);
            sectionMatchClient.setLayout(new GridLayout());
            sectionMatchClient.setLayoutData(new GridData(GridData.FILL_BOTH));

            Composite matchComposite = toolkit.createComposite(sectionMatchClient);
            matchComposite.setLayout(new GridLayout(1, true));
            matchComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

            createAllMatch(form, matchComposite);
            previewMatchSection.setClient(sectionMatchClient);
        }
    }

    private void createAllMatch(final ScrolledForm form, final Composite composite) {
        List<IndicatorUnit> units = new ArrayList<IndicatorUnit>();
        units.add(new IndicatorUnit(IndicatorEnum.AllMatchIndicatorEnum, allMatchIndicator, null));

        EIndicatorChartType matchingType = EIndicatorChartType.PATTERN_MATCHING;
        IChartTypeStates chartTypeState = ChartTypeStatesOperator.getChartState(matchingType, units);

        JFreeChart chart = chartTypeState.getChart();
        ChartDecorator.decorate(chart, null);
        if (chart != null) {
            ChartComposite cc = new ChartComposite(composite, SWT.NONE, chart, true);

            GridData gd = new GridData();
            gd.widthHint = PluginConstant.CHART_STANDARD_WIDHT;
            gd.heightHint = PluginConstant.CHART_STANDARD_HEIGHT;
            cc.setLayoutData(gd);
        }
    }

    private void createSimpleStatistics(final ScrolledForm form, final Composite composite) {
        List<IndicatorUnit> units = new ArrayList<IndicatorUnit>();
        units.add(new IndicatorUnit(IndicatorEnum.RowCountIndicatorEnum, simpleStatIndicator.getRowCountIndicator(), null));
        units.add(new IndicatorUnit(IndicatorEnum.DistinctCountIndicatorEnum, simpleStatIndicator.getDistinctCountIndicator(),
                null));
        units.add(new IndicatorUnit(IndicatorEnum.DuplicateCountIndicatorEnum, simpleStatIndicator.getDuplicateCountIndicator(),
                null));
        units.add(new IndicatorUnit(IndicatorEnum.UniqueIndicatorEnum, simpleStatIndicator.getUniqueCountIndicator(), null));

        IChartTypeStates chartTypeState = ChartTypeStatesOperator.getChartState(EIndicatorChartType.SIMPLE_STATISTICS, units);

        // create chart
        JFreeChart chart = chartTypeState.getChart();
        ChartDecorator.decorate(chart, null);
        if (chart != null) {
            ChartComposite cc = new ChartComposite(composite, SWT.NONE, chart, true);

            GridData gd = new GridData();
            gd.widthHint = PluginConstant.CHART_STANDARD_WIDHT;
            gd.heightHint = PluginConstant.CHART_STANDARD_HEIGHT;
            cc.setLayoutData(gd);
        }
    }

    @Override
    public void refresh() {
        if (EditorPreferencePage.isHideGraphics()) {
            if (sForm.getChildren().length > 1) {
                if (null != sForm.getChildren()[1] && !sForm.getChildren()[1].isDisposed()) {
                    sForm.getChildren()[1].dispose();
                }
                topComp.getParent().layout();
                topComp.layout();
            }

        } else {
            if (chartComposite != null && !chartComposite.isDisposed()) {
                try {
                    for (Control control : chartComposite.getChildren()) {
                        control.dispose();
                    }
                    createPreviewCharts(form, chartComposite, true);
                    chartComposite.getParent().layout();
                    chartComposite.layout();
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            } else {
                previewComp = toolkit.createComposite(sForm);
                previewComp.setLayoutData(new GridData(GridData.FILL_BOTH));
                previewComp.setLayout(new GridLayout());
                previewComp.addControlListener(new ControlAdapter() {

                    @Override
                    public void controlResized(ControlEvent e) {
                        super.controlResized(e);
                        sForm.redraw();
                        form.reflow(true);
                    }
                });
                createPreviewSection(form, previewComp);
                createPreviewCharts(form, chartComposite, true);
            }
        }
    }

    /**
     * @param form
     * @param toolkit
     * @param anasisDataComp
     */
    void createDataFilterSection(final ScrolledForm form, Composite anasisDataComp) {
        dataFilterSection = createSection(
                form,
                anasisDataComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.dataFilter"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.editDataFilter")); //$NON-NLS-1$ //$NON-NLS-2$

        Composite sectionClient = toolkit.createComposite(dataFilterSection);
        dataFilterComp = new DataFilterComp(sectionClient, stringDataFilter);
        dataFilterComp.addPropertyChangeListener(this);

        // ADD yyi 2011-05-31 16158:add whitespace check for text fields.
        addWhitespaceValidate(dataFilterComp.getDataFilterText());
        dataFilterSection.setClient(sectionClient);
    }

    /**
     * DOC qiongli Comment method "createAnalysisParamSection".
     * 
     * @param form
     * @param anasisDataComp
     */
    void createAnalysisParamSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisParamSection = createSection(form, anasisDataComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.AnalysisParameter"), null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(analysisParamSection);
        sectionClient.setLayout(new GridLayout(1, false));

        Composite comp1 = new Composite(sectionClient, SWT.NONE);
        comp1.setLayout(new GridLayout(1, false));
        this.createAnalysisLimitComposite(comp1);

        Composite comp2 = new Composite(sectionClient, SWT.NONE);
        comp2.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(comp2);
        toolkit.createLabel(comp2, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.ExecutionEngine")); //$NON-NLS-1$
        // MOD zshen:need to use the component with finish indicator Selection.
        execCombo = new CCombo(comp2, SWT.BORDER);
        // ~
        execCombo.setEditable(false);

        for (ExecutionLanguage language : ExecutionLanguage.VALUES) {
            String temp = language.getLiteral();
            execCombo.add(temp);
        }
        ExecutionLanguage executionLanguage = analysis.getParameters().getExecutionLanguage();
        // MOD qiongli 2011-2-28,feature 19192.create 'storeDataSection' only for sql engine.
        final Composite javaEnginSection = createjavaEnginSection(comp2);
        final Composite storeDataSection = createStoreDataCheck(comp2);
        // MOD qiongli 2011-3-15 set DataFilterText disabled except TdColumn.
        if (analyzedColumns != null && !analyzedColumns.isEmpty()) {
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(analyzedColumns.get(0));
            dataFilterComp.getDataFilterText().setEnabled(tdColumn != null);
            if (tdColumn == null) {
                changeExecuteLanguageToJava(true);
            }
        }
        if (ExecutionLanguage.SQL.equals(executionLanguage)) {
            javaEnginSection.setVisible(false);
            GridData data = (GridData) javaEnginSection.getLayoutData();
            data.heightHint = 10;
            javaEnginSection.setLayoutData(data);
            analysisParamSection.setExpanded(true);
        }
        execCombo.setText(executionLanguage.getLiteral());
        execLang = executionLanguage.getLiteral();
        treeViewer.setLanguage(ExecutionLanguage.get(executionLanguage.getLiteral()));
        // ~
        execCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                execLang = execCombo.getText();
                // when have a datePatternFreqIndicator in the "analyzed Columns",ExecutionLanguage only is Java.
                if (ExecutionLanguage.SQL.equals(ExecutionLanguage.get(execLang)) && includeDatePatternFreqIndicator()) {
                    MessageUI.openWarning(DefaultMessagesImpl
                            .getString("ColumnMasterDetailsPage.DatePatternFreqIndicatorWarning")); //$NON-NLS-1$
                    execCombo.setText(ExecutionLanguage.JAVA.getLiteral());
                    execLang = execCombo.getText();
                    return;
                }
                // hidden or display parameter of java engin.
                if (ExecutionLanguage.SQL.equals(ExecutionLanguage.get(execLang))) {
                    javaEnginSection.setVisible(false);
                    GridData data = (GridData) javaEnginSection.getLayoutData();
                    data.heightHint = 10;
                    javaEnginSection.setLayoutData(data);
                    analysisParamSection.setExpanded(true);
                } else {
                    javaEnginSection.setVisible(true);
                    GridData data = (GridData) javaEnginSection.getLayoutData();
                    data.heightHint = 100;
                    javaEnginSection.setLayoutData(data);
                    analysisParamSection.setExpanded(true);
                }
                // 12919~
                setDirty(true);
                treeViewer.setLanguage(ExecutionLanguage.get(execLang));
                // ~
            }

        });

        analysisParamSection.setClient(sectionClient);
    }

    /**
     * DOC qiongli Comment method "createStoreDataCheck".
     * 
     * @param sectionClient
     */
    private Composite createStoreDataCheck(Composite sectionClient) {
        Composite storeDataSection = toolkit.createComposite(sectionClient);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 0;
        storeDataSection.setLayout(gridLayout);
        toolkit.createLabel(storeDataSection, "Store data:").setToolTipText("Storing data in analysis file"); //$NON-NLS-1$ 
        storeDataCheck = new Button(storeDataSection, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        storeDataCheck.setSelection(simpleStatIndicator.isStoreData());

        storeDataCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setDirty(true);
            }
        });

        return storeDataSection;
    }

    /**
     * DOC qiongli Comment method "createjavaEnginSection".
     * 
     * @param sectionClient
     * @return
     */
    protected Composite createjavaEnginSection(Composite sectionClient) {
        AnalysisParameters anaParameters = columnSetAnalysisHandler.getAnalysis().getParameters();
        Composite javaEnginSection = toolkit.createComposite(sectionClient);
        Composite checkSection = toolkit.createComposite(javaEnginSection);
        Composite numberSection = toolkit.createComposite(javaEnginSection);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 0;

        GridDataFactory.fillDefaults().grab(true, false).span(2, 0).align(SWT.FILL, SWT.BEGINNING).applyTo(javaEnginSection);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 0).align(SWT.FILL, SWT.BEGINNING).applyTo(checkSection);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 0).align(SWT.FILL, SWT.BEGINNING).applyTo(numberSection);

        javaEnginSection.setLayout(gridLayout);
        checkSection.setLayout(gridLayout);
        numberSection.setLayout(gridLayout);
        toolkit.createLabel(checkSection, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.allowDrillDownLabel"));
        drillDownCheck = toolkit.createButton(checkSection, "", SWT.CHECK);
        drillDownCheck.setSelection(true);
        drillDownCheck.setSelection(anaParameters.isStoreData());
        drillDownCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                storeDataCheck.setSelection(drillDownCheck.getSelection());
                simpleStatIndicator.setStoreData(drillDownCheck.getSelection());
                columnSetAnalysisHandler.getAnalysis().getParameters().setStoreData(drillDownCheck.getSelection());
                setDirty(true);
            }

        });
        Label maxNumLabel = toolkit.createLabel(numberSection,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.maxNumberLabel"));
        maxNumText = toolkit.createText(numberSection, null, SWT.BORDER);
        maxNumText.setText(String.valueOf(anaParameters.getMaxNumberRows()));
        maxNumText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }

        });
        maxNumText.addVerifyListener(new VerifyListener() {

            public void verifyText(VerifyEvent e) {
                String inputValue = e.text;
                Pattern pattern = Pattern.compile("^[0-9]"); //$NON-NLS-1$
                char[] charArray = inputValue.toCharArray();
                for (char c : charArray) {
                    if (!pattern.matcher(String.valueOf(c)).matches()) {
                        e.doit = false;
                    }
                }
            }
        });
        GridDataFactory.fillDefaults().grab(true, false).applyTo(maxNumText);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(maxNumLabel);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(drillDownCheck);
        return javaEnginSection;
    }

    /**
     * @param outputFolder
     * @throws DataprofilerCoreException
     */

    @Override
    public void saveAnalysis() throws DataprofilerCoreException {
        // ADD gdbu 2011-3-3 bug 19179

        // remove the space from analysis name
        // columnSetAnalysisHandler.setName(columnSetAnalysisHandler.getName().replace(" ", ""));
        for (Domain domain : this.analysis.getParameters().getDataFilter()) {
            domain.setName(this.analysis.getName());
        }
        // ~

        IRepositoryViewObject reposObject = null;
        columnSetAnalysisHandler.clearAnalysis();
        simpleStatIndicator.getAnalyzedColumns().clear();
        allMatchIndicator.getAnalyzedColumns().clear();
        // set execute engine
        Analysis analysis = columnSetAnalysisHandler.getAnalysis();
        analysis.getParameters().setExecutionLanguage(ExecutionLanguage.get(execLang));
        analysis.getParameters().setMaxNumberRows(Integer.valueOf(maxNumText.getText()));
        simpleStatIndicator.setStoreData(storeDataCheck.getSelection());

        // set data filter
        columnSetAnalysisHandler.setStringDataFilter(dataFilterComp.getDataFilterString());

        // save analysis
        List<IRepositoryNode> repositoryNodes = treeViewer.getColumnSetMultiValueList();

        Connection tdProvider = null;
        if (repositoryNodes != null && repositoryNodes.size() != 0) {
            ConnectionItem item = (ConnectionItem) repositoryNodes.get(0).getObject().getProperty().getItem();
            tdProvider = item.getConnection();
            if (tdProvider.eIsProxy()) {
                // Resolve the connection again
                tdProvider = (Connection) EObjectHelper.resolveObject(tdProvider);
            }
            analysis.getContext().setConnection(tdProvider);

            List<ModelElement> columnList = new ArrayList<ModelElement>();
            for (IRepositoryNode rd : repositoryNodes) {
                reposObject = rd.getObject();
                if (rd instanceof MDMXmlElementRepNode) {
                    columnList.add(((MetadataXmlElementTypeRepositoryObject) reposObject).getTdXmlElementType());
                } else {
                    columnList.add(((MetadataColumnRepositoryObject) reposObject).getTdColumn());
                }
            }

            simpleStatIndicator.getAnalyzedColumns().addAll(columnList);
            columnSetAnalysisHandler.addIndicator(columnList, simpleStatIndicator);
            // ~ MOD mzhao feature 13040. 2010-05-21
            allMatchIndicator.getCompositeRegexMatchingIndicators().clear();
            ModelElementIndicator[] modelElementIndicator = treeViewer.getModelElementIndicator();
            if (modelElementIndicator != null) {
                for (ModelElementIndicator modelElementInd : modelElementIndicator) {
                    Indicator[] inds = modelElementInd.getPatternIndicators();
                    for (Indicator ind : inds) {
                        if (ind instanceof RegexpMatchingIndicator) {
                            // MOD yyi 2011-06-15 22419:column set pattern for MDM
                            IRepositoryViewObject obj = modelElementInd.getModelElementRepositoryNode().getObject();
                            ModelElement analyzedElt = obj instanceof MetadataColumnRepositoryObject ? ((MetadataColumnRepositoryObject) obj)
                                    .getTdColumn() : ((MetadataXmlElementTypeRepositoryObject) obj).getModelElement();
                            ind.setAnalyzedElement(analyzedElt);
                            allMatchIndicator.getCompositeRegexMatchingIndicators().add((RegexpMatchingIndicator) ind);
                        }
                    }
                }

            }
            if (allMatchIndicator.getCompositeRegexMatchingIndicators().size() > 0) {
                allMatchIndicator.getAnalyzedColumns().addAll(columnList);
                columnSetAnalysisHandler.addIndicator(columnList, allMatchIndicator);
            }
            // ~
        } else {
            deleteConnectionDependency(analysis);
        }
        // ADD xqliu 2010-07-19 bug 14014
        this.updateAnalysisClientDependency();
        // ~ 14014

        // save the number of connections per analysis
        this.saveNumberOfConnectionsPerAnalysis();

        // 2011.1.12 MOD by zhsne to unify anlysis and connection id when saving.
        ReturnCode saved = new ReturnCode(false);
        IEditorInput editorInput = this.getEditorInput();
        if (editorInput instanceof AnalysisItemEditorInput) {
            AnalysisItemEditorInput analysisInput = (AnalysisItemEditorInput) editorInput;
            TDQAnalysisItem tdqAnalysisItem = analysisInput.getTDQAnalysisItem();

            // ADD gdbu 2011-3-2 bug 19179
            tdqAnalysisItem.getProperty().setDisplayName(columnSetAnalysisHandler.getName());
            tdqAnalysisItem.getProperty().setLabel(WorkspaceUtils.normalize(columnSetAnalysisHandler.getName()));
            this.nameText.setText(columnSetAnalysisHandler.getName());
            // ~
            // TDQ-5581,if has removed emlements(patten),should remove dependency each other before saving.
            HashSet<ModelElement> removedElements = treeViewer.getRemovedElements();
            if (!removedElements.isEmpty()) {
                DependenciesHandler.getInstance().removeDependenciesBetweenModels(analysis,
                        new ArrayList<ModelElement>(removedElements));
            }
            // MOD yyi 2012-02-08 TDQ-4621:Explicitly set true for updating dependencies.
            saved = ElementWriterFactory.getInstance().createAnalysisWrite().save(tdqAnalysisItem, true);
            if (saved.isOk() && !removedElements.isEmpty()) {
                saveRemovedElements();
            }
        }
        // MOD yyi 2012-02-03 TDQ-3602:Avoid to rewriting all analyzes after saving, no reason to update all analyzes
        // which is depended in the referred connection.
        // Extract saving log function.
        // @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#logSaved(ReturnCode)
        logSaved(saved);

        treeViewer.setDirty(false);
        dataFilterComp.setDirty(false);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            currentEditor.firePropertyChange(IEditorPart.PROP_DIRTY);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.columnSetAnalysisHandler.setStringDataFilter((String) evt.getNewValue());
        }

    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || (treeViewer != null && treeViewer.isDirty())
                || (dataFilterComp != null && dataFilterComp.isDirty())
                || (indicatorsViewer != null && indicatorsViewer.isDirty());
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#getTreeViewer()
     */
    @Override
    public AbstractColumnDropTree getTreeViewer() {
        return this.treeViewer;
    }

    public ColumnSetAnalysisHandler getColumnSetAnalysisHandler() {
        return columnSetAnalysisHandler;
    }

    public SimpleStatIndicator getSimpleStatIndicator() {
        return simpleStatIndicator;
    }

    public Composite[] getPreviewChartCompsites() {
        // ADD msjian TDQ-6213 2012-12-18: filter the disposed composite
        if (previewChartCompsites != null && previewChartCompsites.length > 0) {
            List<Composite> withOutDisposed = new ArrayList<Composite>();
            for (Composite com : previewChartCompsites) {
                if (!com.isDisposed()) {
                    withOutDisposed.add(com);
                }
            }
            this.previewChartCompsites = withOutDisposed.toArray(new ExpandableComposite[withOutDisposed.size()]);
        }
        // TDQ-6213~
        return previewChartCompsites;
    }

    @Override
    public Composite getChartComposite() {
        return chartComposite;
    }

    @Override
    public ReturnCode canSave() {
        // MOD by gdbu 2011-3-21 bug 19179
        ReturnCode canModRetCode = super.canSave();
        if (!canModRetCode.isOk()) {
            return canModRetCode;
        }
        // ~19179

        String message = null;

        List<IRepositoryNode> columnSetMultiValueList = this.treeViewer.getColumnSetMultiValueList();

        // MOD yyi 2011-02-16 17871:delimitefile
        List<ModelElement> columnList = new ArrayList<ModelElement>();
        // MOD klliu 2001-03-28 19464 filter column from same table
        Set<EObject> nodeTypeName = new HashSet<EObject>();
        for (IRepositoryNode rd : columnSetMultiValueList) {
            ModelElement modelElementFromRepositoryNode = RepositoryNodeHelper.getModelElementFromRepositoryNode(rd);
            EObject eContainer = modelElementFromRepositoryNode.eContainer();
            nodeTypeName.add(eContainer);
            columnList.add(RepositoryNodeHelper.getModelElementFromRepositoryNode(rd));
        }
        if (nodeTypeName.size() > 1) {
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnSetMasterPage.CannotCreateAnalysis"), false); //$NON-NLS-1$
        }
        if (!columnSetMultiValueList.isEmpty()) {
            // MOD klliu bug 19464,file delimit connection does not need to check from one table.
            if (columnList.get(0) instanceof TdColumn) {
                if (!ModelElementHelper.isFromSameTable(columnList)) {
                    message = DefaultMessagesImpl.getString("ColumnSetMasterPage.CannotCreateAnalysis"); //$NON-NLS-1$
                }
            } else if (columnList.get(0) instanceof MetadataColumn) {
                if (!isFromSameTableByDFColumn(columnSetMultiValueList)) {
                    message = DefaultMessagesImpl.getString("ColumnSetMasterPage.CannotCreateAnalysis"); //$NON-NLS-1$
                }
            }
        }
        if (message == null) {
            resetResultPageData();
            return new ReturnCode(true);
        }

        return new ReturnCode(message, false);
    }

    private boolean isFromSameTableByDFColumn(List<IRepositoryNode> columnSetMultiValueList) {
        Set<String> folderItemIds = new HashSet<String>();
        for (IRepositoryNode node : columnSetMultiValueList) {
            RepositoryNode parent = node.getParent();
            if (parent instanceof DFColumnFolderRepNode) {
                DFColumnFolderRepNode folderNode = (DFColumnFolderRepNode) parent;
                String id = folderNode.getmTable().getId();
                folderItemIds.add(id);
            }
        }
        return folderItemIds.size() == 1;
    }

    @Override
    protected ReturnCode canRun() {
        List<IRepositoryNode> columnSetMultiValueList = this.treeViewer.getColumnSetMultiValueList();
        if (columnSetMultiValueList.isEmpty()) {
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnSetMasterPage.NoColumnsAssigned"), false); //$NON-NLS-1$
        }
        resetResultPageData();
        return new ReturnCode(true);

    }

    public AllMatchIndicator getAllMatchIndicator() {
        return allMatchIndicator;
    }

    private void resetResultPageData() {
        ColumnSetResultPage theResultPage = null;
        if (this.currentEditor.getResultPage() instanceof ColumnSetResultPage) {
            theResultPage = (ColumnSetResultPage) this.currentEditor.getResultPage();
        }
        if (theResultPage.getTableFilterResult() != null) {
            theResultPage.setTableFilterResult(null);
        }
    }

    /**
     * DOC qiongli Comment method "includeDatePatternFreqIndicator".
     * 
     * @return
     */
    private boolean includeDatePatternFreqIndicator() {
        for (ModelElementIndicator modelElementIndicator : this.treeViewer.getModelElementIndicator()) {
            if (modelElementIndicator.contains(IndicatorEnum.DatePatternFreqIndicatorEnum)) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC qiongli Comment method "getDataFilterComp".
     * 
     * @return
     */
    public DataFilterComp getDataFilterComp() {
        return this.dataFilterComp;
    }

    /**
     * DOC qiongli Comment method "chageExecuteLanguageToJava".
     */
    public void changeExecuteLanguageToJava(boolean isDisabled) {
        if (execCombo == null) {
            return;
        }
        if (!(ExecutionLanguage.JAVA.getLiteral().equals(this.execLang))) {
            int i = 0;
            for (ExecutionLanguage language : ExecutionLanguage.VALUES) {
                if (language.compareTo(ExecutionLanguage.JAVA) == 0) {
                    execCombo.select(i);
                } else {
                    i++;
                }
            }
        }
        if (isDisabled) {
            execCombo.setEnabled(false);
        }
    }

    @Override
    public ExecutionLanguage getUIExecuteEngin() {
        return ExecutionLanguage.get(execCombo.getText());
    }

    /**
     * DOC yyin Comment method "removeItem".
     * 
     * @param indicatorUnit
     */
    public void removeItem(IndicatorUnit indicatorUnit) {
        this.treeViewer.removeItemByUnit(indicatorUnit);

    }
}
