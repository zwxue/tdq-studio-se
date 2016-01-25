// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.jfree.util.Log;
import org.talend.commons.utils.TalendURLClassLoader;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.InternationalizationUtil;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.PatternLanguageType;
import org.talend.dataprofiler.core.ui.dialog.ExpressionEditDialog;
import org.talend.dataprofiler.core.ui.dialog.JavaUdiJarSelectDialog;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.definition.CharactersMapping;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.IndicatorDefinitionParameter;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorDefinitionMaterPage extends AbstractMetadataFormPage {

    private static final String ADDITIONAL_FUNCTIONS_SPLIT = PluginConstant.SEMICOLON_STRING;

    private Section definitionSection;

    private Composite definitionComp;

    private Composite categoryComp;

    private List<String> allDBTypeList;

    private Map<CCombo, TdExpression> tempExpressionMap;

    private Map<CCombo, TdExpression> tempViewRowsExpressionMap;

    private Map<CCombo, TdExpression> tempViewValidRowsExpressionMap;

    private Map<CCombo, TdExpression> tempViewInvalidRowsExpressionMap;

    private Map<CCombo, TdExpression> tempViewValidValuesExpressionMap;

    private Map<CCombo, TdExpression> tempViewInvalidValuesExpressionMap;

    private Map<CCombo, Composite> widgetMap;

    private Composite expressionComp;

    private Combo comboCategory;

    private Label labelDetail;

    private IndicatorDefinition definition;

    private TDQIndicatorDefinitionItem definitionItem;

    protected SysIndicatorDefinitionRepNode indicatorDefinitionRepNode;

    public SysIndicatorDefinitionRepNode getIndicatorDefinitionRepNode() {
        return this.indicatorDefinitionRepNode;
    }

    private void initIndicatorDefinitionRepNode(IndicatorDefinition indicatorDefinition) {
        // RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(indicatorDefinition);
        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind2(definition);
        if (recursiveFind != null && recursiveFind instanceof SysIndicatorDefinitionRepNode) {
            this.indicatorDefinitionRepNode = (SysIndicatorDefinitionRepNode) recursiveFind;
        }
    }

    private IndicatorCategory category;

    private Section additionalFunctionsSection;

    private Composite additionalFunctionsComp;

    private Composite afExpressionComp;

    private Map<String, AggregateDateExpression> afExpressionMap, afExpressionMapTemp;

    private List<String> remainDBTypeListAF;

    private boolean hasAggregateExpression, hasDateExpression, hasCharactersMapping;

    private boolean systemIndicator;

    // used to when change the category changed, whether need the confirm dialog
    private boolean needConfirm = true;

    // ADD xqliu 2010-03-23 feature 11201
    private List<TdExpression> tempExpressionList;

    // ADD klliu 2010-06-03 bug 13451
    private String classNameForSave;

    private String jarPathForSave;

    // ADD klliu 2011-08-08 bug 0022994
    private Composite dataBaseComp;

    private Composite javaLanguageComp;

    private Composite dataBaseTitleComp;

    private Composite javaTitleComp;

    // ~
    // MOD klliu 2010-06-03 bug 13451
    private String getClassNameForSave() {
        return this.classNameForSave;
    }

    private void setClassNameForSave(String classNameForSave) {
        this.classNameForSave = classNameForSave;
    }

    private String getJarPathForSave() {
        return this.jarPathForSave;
    }

    private void setJarPathForSave(String jarPathForSave) {
        this.jarPathForSave = jarPathForSave;
    }

    public boolean isSystemIndicator() {
        return systemIndicator;
    }

    // Add klliu figure 13429
    private List<IndicatorDefinitionParameter> tempParameters;

    private IndicatorDefinitionParameter element = null;

    private Section parametersSection;

    private Composite parametersComp;

    private TableViewer parView;

    // End klliu figure 13429

    private static final String BODY_AGGREGATE = "AVG({0});COUNT({0});SUM(CASE WHEN {0} IS NULL THEN 1 ELSE 0 END)"; //$NON-NLS-1$

    private static final String BODY_DATE = "MIN({0});MAX({0});COUNT({0});SUM(CASE WHEN {0} IS NULL THEN 1 ELSE 0 END)"; //$NON-NLS-1$

    private Section charactersMappingSection;

    private Section categorySection;

    private Composite charactersMappingComp;

    private Composite charactersMappingLineComp;

    private Map<String, CharactersMapping> charactersMappingMap, charactersMappingMapTemp;

    private List<String> remainDBTypeListCM;

    private static final String BODY_CHARACTERS_TO_REPLACE = "abcdefghijklmnopqrstuvwxyzçâêîôûéèùïöüABCDE"//$NON-NLS-1$
            + "FGHIJKLMNOPQRSTUVWXYZÇÂÊÎÔÛÉÈÙÏÖÜ0123456789"; //$NON-NLS-1$

    private static final String BODY_REPLACEMENT_CHARACTERS = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAA"//$NON-NLS-1$
            + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999"; //$NON-NLS-1$

    /**
     * DOC bZhou IndicatorDefinitionMaterPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public IndicatorDefinitionMaterPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#getIntactElemenetName()
     */
    @Override
    public String getIntactElemenetName() {
        // The title of indicatorDefinition should display an internationalzation String
        String intactElemenetName = super.getIntactElemenetName();
        String internationalizationLabel = StringUtils.EMPTY;
        Property property = getProperty();
        if (property != null && isSystemIndicator()) {
            // system indicatorDefinition need to be internationalization
            internationalizationLabel = InternationalizationUtil.getDefinitionInternationalizationLabel(property
                    .getLabel());
            if (StringUtils.EMPTY.equals(internationalizationLabel)) {
                return intactElemenetName;
            }
        } else {
            return intactElemenetName;
        }
        return internationalizationLabel;
    }

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
        String[] supportTypes = PatternLanguageType.getAllLanguageTypes();
        // initialize user defined indicator category
        definition = (IndicatorDefinition) getCurrentModelElement(getEditor());

        allDBTypeList = new ArrayList<String>();
        allDBTypeList.addAll(Arrays.asList(supportTypes));
        // MOD klliu 13104: Do not allow the user to add a java language in the system indicators
        URI uri = null;
        if (definition.eIsProxy()) {
            uri = ((InternalEObject) definition).eProxyURI();

        } else {
            uri = definition.eResource().getURI();
        }
        systemIndicator = definition != null && uri.toString().contains(EResourceConstant.SYSTEM_INDICATORS.getName());
        if (systemIndicator) {
            allDBTypeList.remove(PatternLanguageType.JAVA.getLiteral());

        }
        // MOD xqliu 2010-03-23 feature 11201
        // remainDBTypeList = new ArrayList<String>();
        // remainDBTypeList.addAll(allDBTypeList);

        remainDBTypeListAF = new ArrayList<String>();
        remainDBTypeListAF.addAll(allDBTypeList);

        remainDBTypeListCM = new ArrayList<String>();
        remainDBTypeListCM.addAll(allDBTypeList);

        // MOD xqliu 2010-03-23 feature 11201
        initTempMaps();

        if (widgetMap == null) {
            widgetMap = new HashMap<CCombo, Composite>();
        } else {
            widgetMap.clear();
        }

        if (definition != null && definition.getCategories().size() > 0) {
            category = definition.getCategories().get(0);
        } else {
            category = DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory();
        }

        if (definition != null) {
            hasAggregateExpression = definition.getAggregate1argFunctions().size() > 0;
            hasDateExpression = definition.getDate1argFunctions().size() > 0;
            hasCharactersMapping = definition.getCharactersMapping().size() > 0;
        }
        afExpressionMap = new HashMap<String, AggregateDateExpression>();
        afExpressionMapTemp = new HashMap<String, AggregateDateExpression>();

        charactersMappingMap = new HashMap<String, CharactersMapping>();
        charactersMappingMapTemp = new HashMap<String, CharactersMapping>();

        // ADD xqliu 2010-03-23 feature 11201
        initTempExpressionList(definition);

        // ADD klliu 2010-07-14 feature 13429
        initTempIndicatorDefinitionParameter(definition);

        initIndicatorDefinitionRepNode(definition);

        if (this.indicatorDefinitionRepNode != null) {
            this.definitionItem = (TDQIndicatorDefinitionItem) this.indicatorDefinitionRepNode.getObject().getProperty()
                    .getItem();
        }
    }

    /**
     * init Temp Maps.
     */
    public void initTempMaps() {
        tempExpressionMap = initTempMap(tempExpressionMap);
        tempViewValidRowsExpressionMap = initTempMap(tempViewValidRowsExpressionMap);
        tempViewInvalidRowsExpressionMap = initTempMap(tempViewInvalidRowsExpressionMap);
        tempViewValidValuesExpressionMap = initTempMap(tempViewValidValuesExpressionMap);
        tempViewInvalidValuesExpressionMap = initTempMap(tempViewInvalidValuesExpressionMap);
        tempViewRowsExpressionMap = initTempMap(tempViewRowsExpressionMap);
    }

    /**
     * when change the indicator catogroy, remove all except the old java type.
     */
    public void removeFromTempMapsExceptJava() {
        Iterator<CCombo> it = tempExpressionMap.keySet().iterator();
        while (it.hasNext()) {
            CCombo cb = it.next();
            if (!cb.getText().equals(PatternLanguageType.JAVA.getName())) {
                it.remove();
            }
        }

        tempViewValidRowsExpressionMap = initTempMap(tempViewValidRowsExpressionMap);
        tempViewInvalidRowsExpressionMap = initTempMap(tempViewInvalidRowsExpressionMap);
        tempViewValidValuesExpressionMap = initTempMap(tempViewValidValuesExpressionMap);
        tempViewInvalidValuesExpressionMap = initTempMap(tempViewInvalidValuesExpressionMap);
        tempViewRowsExpressionMap = initTempMap(tempViewRowsExpressionMap);
    }

    /**
     * DOC klliu Comment method "initTempIndicatorDefinitionParameter". ADD klliu 2010-07-12 bug 13429.
     * 
     * @param definition2
     */
    private void initTempIndicatorDefinitionParameter(IndicatorDefinition definition2) {
        if (definition != null) {
            tempParameters = cloneIndicatorDefParameter(definition.getIndicatorDefinitionParameter());
        } else {
            tempParameters.clear();
        }
    }

    /**
     * DOC klliu Comment method "cloneIndicatorDefParameter".ADD klliu 2010-07-12 bug 13429.
     * 
     * @param indicatorDefParameter
     * @return
     */
    private List<IndicatorDefinitionParameter> cloneIndicatorDefParameter(
            EList<IndicatorDefinitionParameter> indicatorDefParameter) {
        List<IndicatorDefinitionParameter> result = new ArrayList<IndicatorDefinitionParameter>();
        for (IndicatorDefinitionParameter param : indicatorDefParameter) {
            result.add(param.clone());
        }
        return result;
    }

    /**
     * DOC klliu 2Comment method "checkJavaUDIBeforeOpen".ADD klliu 2010-06-02 bug 13451.
     * 
     * @return
     */
    private boolean checkJavaUDIBeforeOpen() {
        EList<TaggedValue> tvs = definition.getTaggedValue();
        for (TaggedValue tv : tvs) {
            if (tv.getTag().equals(TaggedValueHelper.CLASS_NAME_TEXT) || tv.getTag().equals(TaggedValueHelper.JAR_FILE_PATH)) {
                return true;
            }
        }
        return false;
    }

    /**
     * init Temp Map.
     */
    private Map<CCombo, TdExpression> initTempMap(Map<CCombo, TdExpression> tmp) {
        Map<CCombo, TdExpression> temp = tmp;
        if (temp == null) {
            temp = new HashMap<CCombo, TdExpression>();
        } else {
            temp.clear();
        }
        return temp;
    }

    /**
     * init TempExpressionList.
     * 
     * @param def
     */
    private void initTempExpressionList(IndicatorDefinition def) {
        if (tempExpressionList == null) {
            tempExpressionList = new ArrayList<TdExpression>();
        } else {
            tempExpressionList.clear();
        }
        if (def != null) {
            EList<TdExpression> expressions = def.getSqlGenericExpression();
            for (TdExpression exp : expressions) {
                tempExpressionList.add(cloneExpression(exp));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#createFormContent(org.eclipse.ui.forms.IManagedForm
     * )
     */
    @Override
    protected void createFormContent(IManagedForm managedForm) {
        setFormTitle(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.formTitle")); //$NON-NLS-1$

        setMetadataTitle(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.formMedata")); //$NON-NLS-1$

        super.createFormContent(managedForm);

        metadataSection.setDescription(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.formDescript")); //$NON-NLS-1$
        // MOD by zshen move CategorySection before of DefinitionSection on UDI Editor
        if (isSystemIndicator()) {
            createDefinitionSection(topComp);
            if (IndicatorCategoryHelper.isCorrelation(category)) {
                createAdditionalFunctionsSection(topComp);
            }
            if (this.hasCharactersMapping) {
                createCharactersMappingSection(topComp);
            }
        } else {
            createCategorySection(topComp);
            createDefinitionSection(topComp);
            createDefinitionParametersSection(topComp);

        }

        form.reflow(true);
    }

    /**
     * DOC klliu Comment method "createDefinitionParametersSection". ADD klliu figure 13429 2010-07-12
     * 
     * @param topComp
     */
    private void createDefinitionParametersSection(Composite topComp) {
        parametersSection = createSection(form, topComp,
                DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.parameters"), null); //$NON-NLS-1$

        Label label = new Label(parametersSection, SWT.WRAP);
        label.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.parametersDecription")); //$NON-NLS-1$
        parametersSection.setDescriptionControl(label);

        parametersComp = createDefinitionParametersComp(parametersSection);

        parametersSection.setClient(parametersComp);
    }

    /**
     * DOC klliu Comment method "createDefinitionParametersComp" ADD klliu figure 13429 2010-07-12.
     * 
     * @param definitionSection2
     * @return
     */
    private Composite createDefinitionParametersComp(Section parametersSection) {
        Composite composite = toolkit.createComposite(parametersSection);
        GridData parData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(parData);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);

        parView = new TableViewer(composite);
        createDefiniationParameterColumns(parView);
        IndicatorParametersContentProvider provider = new IndicatorParametersContentProvider();
        parView.setContentProvider(provider);
        parView.setLabelProvider(new IndicatorParametersLabelProvider());
        parView.setInput(tempParameters);
        createDefinitionParametersButton(composite, parView);
        return composite;

    }

    /**
     * DOC klliu Comment method "createDefinitionParametersButton".ADD klliu figure 13429 2010-07-12.
     * 
     * @param composite
     * @param parView
     */
    private void createDefinitionParametersButton(Composite comp, final TableViewer parView) {
        Composite composite = toolkit.createComposite(comp);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.CENTER;
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(gd);
        final Button addButton = new Button(composite, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setToolTipText(DefaultMessagesImpl.getString("PatternMasterDetailsPage.add")); //$NON-NLS-1$
        GridData labelGd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        labelGd.horizontalAlignment = SWT.RIGHT;
        labelGd.widthHint = 65;
        addButton.setLayoutData(labelGd);
        addButton.addListener(SWT.MouseDown, new Listener() {

            public void handleEvent(Event event) {
                IndicatorDefinitionParameter ip = DefinitionFactory.eINSTANCE.createIndicatorDefinitionParameter();
                ip.setKey("paraKey");//$NON-NLS-1$
                ip.setValue("paraValue");//$NON-NLS-1$
                tempParameters.add(ip);
                if (parView != null) {
                    parView.refresh(tempParameters);
                    setDirty(true);
                }
            }
        });
        final Button romveButton = new Button(composite, SWT.NONE);
        romveButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        romveButton.setToolTipText(DefaultMessagesImpl.getString("PatternMasterDetailsPage.del")); //$NON-NLS-1$
        GridData reGd = new GridData();
        reGd.horizontalAlignment = SWT.LEFT;
        reGd.widthHint = 65;
        romveButton.setLayoutData(reGd);
        romveButton.addListener(SWT.MouseDown, new Listener() {

            public void handleEvent(Event event) {
                IStructuredSelection selection = (IStructuredSelection) parView.getSelection();
                Object o = selection.getFirstElement();
                if (o instanceof IndicatorDefinitionParameter) {
                    element = (IndicatorDefinitionParameter) o;
                    tempParameters.remove(element);
                    parView.refresh(tempParameters);
                    setDirty(true);
                }
            }
        });
    }

    /**
     * DOC klliu Comment method "createDefiniationParameterColumns". ADD klliu figure 13429 2010-07-12
     * 
     * @param viewer
     */
    private void createDefiniationParameterColumns(TableViewer viewer) {

        String[] titles = { "Parameters Key", "Parameters Value" };//$NON-NLS-1$ //$NON-NLS-2$
        int[] bounds = { 200, 200 };
        for (int i = 0; i < titles.length; i++) {
            TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
            column.getColumn().setText(titles[i]);
            column.getColumn().setWidth(bounds[i]);
            column.getColumn().setResizable(false);
            column.getColumn().setMoveable(true);
        }
        Table table = viewer.getTable();
        table.setLayout(new FillLayout(SWT.VERTICAL | SWT.V_SCROLL));
        GridData tableData = new GridData(GridData.FILL_VERTICAL);
        tableData.horizontalSpan = 2;
        tableData.heightHint = 150;
        table.setLayoutData(tableData);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        TableLayout tl = new TableLayout();
        tl.addColumnData(new ColumnWeightData(60));
        tl.addColumnData(new ColumnWeightData(60));
        table.setLayout(tl);
        attachDefiniationParameterCellEditors(viewer, table, titles);
    }

    /**
     * DOC klliu Comment method "attachDefiniationParameterCellEditors". ADD klliu figure 13429 2010-07-12
     * 
     * @param viewer
     * @param table
     * @param titles
     */
    private void attachDefiniationParameterCellEditors(final TableViewer viewer, Composite table, String[] titles) {
        viewer.setCellModifier(new ICellModifier() {

            public boolean canModify(Object element, String property) {
                return true;
            }

            public Object getValue(Object element, String property) {
                if ("Parameters Key".equals(property)) {//$NON-NLS-1$
                    return ((IndicatorDefinitionParameter) element).getKey();
                } else if ("Parameters Value".equals(property)) {//$NON-NLS-1$
                    return ((IndicatorDefinitionParameter) element).getValue();
                }
                return null;
            }

            public void modify(Object element, String property, Object value) {
                TableItem tableItem = (TableItem) element;
                IndicatorDefinitionParameter data = (IndicatorDefinitionParameter) tableItem.getData();
                if ("Parameters Key".equals(property)) {//$NON-NLS-1$
                    if (!data.getKey().equals(value.toString())) {
                        data.setKey(value.toString());
                        viewer.refresh(data);
                        setDirty(true);
                    }
                } else if ("Parameters Value".equals(property)) {//$NON-NLS-1$
                    if (!data.getValue().equals(value)) {
                        data.setValue((String) value);
                        viewer.refresh(data);
                        setDirty(true);
                    }
                }
            }
        });
        viewer.setColumnProperties(titles);

        viewer.setCellEditors(new CellEditor[] { new TextCellEditor(table), new TextCellEditor(table) });

    }

    /**
     * DOC xqliu Comment method "createCharactersMappingSection".
     * 
     * @param topComp
     */
    private void createCharactersMappingSection(Composite topComp) {
        charactersMappingSection = createSection(form, topComp,
                DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.CharactersMappingSectionTitle"), null);//$NON-NLS-1$

        charactersMappingComp = createCharactersMappingComp(charactersMappingSection);

        charactersMappingSection.setClient(charactersMappingComp);
    }

    /**
     * DOC xqliu Comment method "createCharactersMappingComp".
     * 
     * @param charactersMappingSection
     * @return
     */
    private Composite createCharactersMappingComp(Section charactersMappingSection) {
        Composite composite = toolkit.createComposite(charactersMappingSection);
        composite.setLayout(new GridLayout());

        charactersMappingLineComp = new Composite(composite, SWT.NONE);
        charactersMappingLineComp.setLayout(new GridLayout());
        charactersMappingLineComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (definition != null) {
            EList<CharactersMapping> charactersMappings = definition.getCharactersMapping();
            // private Map<String, CharactersMapping> charactersMappingMap, charactersMappingMapTemp;

            if (charactersMappings != null && charactersMappings.size() > 0) {
                for (CharactersMapping charactersMapping : charactersMappings) {
                    recordCharactersMapping(charactersMappingMap, charactersMapping);
                }
            }

            charactersMappingMapTemp.clear();
            for (String key : charactersMappingMap.keySet()) {
                charactersMappingMapTemp.put(key, cloneCharactersMapping(charactersMappingMap.get(key)));
            }

            for (String language : charactersMappingMapTemp.keySet()) {
                createNewCharactersMappingLine(language, charactersMappingMapTemp.get(language));
            }
        }

        createCMAddButton(composite);

        return composite;
    }

    /**
     * DOC xqliu Comment method "createCMAddButton".
     * 
     * @param composite
     */
    private void createCMAddButton(Composite composite) {
        final Button addButton = new Button(composite, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setToolTipText(DefaultMessagesImpl.getString("PatternMasterDetailsPage.add")); //$NON-NLS-1$
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.CENTER;
        labelGd.widthHint = 65;
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                remainDBTypeListCM.clear();
                remainDBTypeListCM.addAll(allDBTypeList);
                for (CharactersMapping cm : charactersMappingMapTemp.values()) {
                    String language = cm.getLanguage();
                    String languageName = PatternLanguageType.findNameByLanguage(language);
                    remainDBTypeListCM.remove(languageName);
                }
                if (remainDBTypeListCM.size() == 0) {
                    MessageDialog.openWarning(
                            null,
                            DefaultMessagesImpl.getString("PatternMasterDetailsPage.warning"), DefaultMessagesImpl.getString("PatternMasterDetailsPage.patternExpression")); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                String language = PatternLanguageType.findLanguageByName(remainDBTypeListCM.get(0));
                CharactersMapping cm = DefinitionFactory.eINSTANCE.createCharactersMapping();
                cm.setLanguage(language);
                cm.setCharactersToReplace(BODY_CHARACTERS_TO_REPLACE);
                cm.setReplacementCharacters(BODY_REPLACEMENT_CHARACTERS);
                createNewCharactersMappingLine(language, cm);
                charactersMappingMapTemp.put(language, cm);
                charactersMappingSection.setExpanded(true);
                setDirty(true);
            }
        });

    }

    /**
     * DOC xqliu Comment method "createNewCharactersMappingLine".
     * 
     * @param language
     * @param charactersMapping
     */
    private void createNewCharactersMappingLine(String language, final CharactersMapping charactersMapping) {
        final Composite cmComp = new Composite(charactersMappingLineComp, SWT.NONE);
        cmComp.setLayout(new GridLayout(2, false));

        final Composite cmLanguageComp = new Composite(cmComp, SWT.NONE);
        cmLanguageComp.setLayout(new GridLayout());
        cmLanguageComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        final CCombo combo = new CCombo(cmLanguageComp, SWT.BORDER);
        GridData comboGridData = new GridData();
        comboGridData.widthHint = 150;
        comboGridData.verticalAlignment = SWT.TOP;
        combo.setLayoutData(comboGridData);
        combo.setEditable(false);
        combo.setItems(remainDBTypeListCM.toArray(new String[remainDBTypeListCM.size()]));
        if (language == null) {
            combo.setText(remainDBTypeListCM.get(0));
        } else {
            combo.setText(PatternLanguageType.findNameByLanguage(language));
        }
        combo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String lang = combo.getText();
                charactersMapping.setLanguage(PatternLanguageType.findLanguageByName(lang));
                setDirty(true);
            }
        });

        final Composite cmBodyComp = new Composite(cmComp, SWT.NONE);
        cmBodyComp.setLayout(new GridLayout(1, false));
        cmBodyComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
        String[] headers = { "Characters to replace", "Replacement characters" };//$NON-NLS-1$//$NON-NLS-2$
        int[] widths = { 300, 300 };
        buildCharactersMappingLineComp(cmBodyComp, charactersMapping, style, headers, widths);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(cmComp);
    }

    /**
     * DOC xqliu Comment method "buildCharactersMappingLineComp".
     * 
     * @param cmBodyComp
     * @param charactersMapping
     * @param style
     * @param headers
     * @param widths
     */
    private void buildCharactersMappingLineComp(Composite cmBodyComp, CharactersMapping charactersMapping, int style,
            String[] headers, int[] widths) {
        Table table = new Table(cmBodyComp, style);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        for (int i = 0; i < headers.length; ++i) {
            TableColumn tableColumn = new TableColumn(table, SWT.LEFT, i);
            tableColumn.setText(headers[i]);
            tableColumn.setWidth(widths[i]);
        }

        TableViewer tableViewer = new TableViewer(table);

        tableViewer.setUseHashlookup(true);
        tableViewer.setColumnProperties(headers);

        CellEditor[] editors = new CellEditor[headers.length];
        for (int i = 0; i < editors.length; ++i) {
            editors[i] = new TextCellEditor(table);
        }
        tableViewer.setCellEditors(editors);
        tableViewer.setCellModifier(new CharactersMappingCellModifier(headers, tableViewer));
        tableViewer.setContentProvider(new CommonContentProvider());
        tableViewer.setLabelProvider(new CharactersMappingLabelProvider());
        tableViewer.setInput(charactersMapping);
    }

    /**
     * DOC xqliu Comment method "cloneCharactersMapping".
     * 
     * @param charactersMapping
     * @return
     */
    private CharactersMapping cloneCharactersMapping(CharactersMapping charactersMapping) {
        if (charactersMapping != null) {
            CharactersMapping newCharactersMapping = DefinitionFactory.eINSTANCE.createCharactersMapping();
            newCharactersMapping.setLanguage(charactersMapping.getLanguage());
            newCharactersMapping.setCharactersToReplace(charactersMapping.getCharactersToReplace());
            newCharactersMapping.setReplacementCharacters(charactersMapping.getReplacementCharacters());
            return newCharactersMapping;
        }
        return null;
    }

    /**
     * DOC xqliu Comment method "recordCharactersMapping".
     * 
     * @param charactersMappingMap
     * @param charactersMapping
     */
    private void recordCharactersMapping(Map<String, CharactersMapping> charactersMappingMap, CharactersMapping charactersMapping) {
        String language = null;
        if (charactersMapping != null) {
            language = charactersMapping.getLanguage();
        }
        if (language != null) {
            charactersMappingMap.put(language, charactersMapping);
        }
    }

    /**
     * DOC xqliu Comment method "createAdditionalFunctionsSection".
     * 
     * @param topComp
     */
    private void createAdditionalFunctionsSection(Composite topComp) {
        additionalFunctionsSection = createSection(form, topComp,
                DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.AdditionalFunctionsSectionTitle"), null);//$NON-NLS-1$

        additionalFunctionsComp = createAdditionalFunctionsComp(additionalFunctionsSection);

        additionalFunctionsSection.setClient(additionalFunctionsComp);
    }

    /**
     * DOC xqliu Comment method "createAdditionalFunctionsComp".
     * 
     * @param additionalFunctionsSection
     * @return
     */
    private Composite createAdditionalFunctionsComp(Section additionalFunctionsSection) {
        Composite composite = toolkit.createComposite(additionalFunctionsSection);
        composite.setLayout(new GridLayout());

        afExpressionComp = new Composite(composite, SWT.NONE);
        afExpressionComp.setLayout(new GridLayout());
        afExpressionComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (definition != null) {
            EList<TdExpression> aggregate1argFunctions = definition.getAggregate1argFunctions();
            EList<TdExpression> date1argFunctions = definition.getDate1argFunctions();

            if (aggregate1argFunctions != null && aggregate1argFunctions.size() > 0) {
                for (TdExpression expression : aggregate1argFunctions) {
                    recordAFExpression(afExpressionMap, expression, null);
                }
            }

            if (date1argFunctions != null && date1argFunctions.size() > 0) {
                for (TdExpression expression : date1argFunctions) {
                    recordAFExpression(afExpressionMap, null, expression);
                }
            }

            afExpressionMapTemp.clear();
            for (String key : afExpressionMap.keySet()) {
                afExpressionMapTemp.put(key, afExpressionMap.get(key).clone());
            }

            for (String language : afExpressionMapTemp.keySet()) {
                createNewAFExpressLine(language, afExpressionMapTemp.get(language));
            }
        }

        createAFAddButton(composite);

        return composite;
    }

    /**
     * DOC xqliu Comment method "createNewAFExpressLine".
     * 
     * @param language
     * @param aggregateDateExpression
     */
    private void createNewAFExpressLine(String language, final AggregateDateExpression aggregateDateExpression) {
        final Composite expressComp = new Composite(afExpressionComp, SWT.NONE);
        expressComp.setLayout(new GridLayout(2, false));

        final Composite expressionLanguageComp = new Composite(expressComp, SWT.NONE);
        expressionLanguageComp.setLayout(new GridLayout());
        expressionLanguageComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        new Label(expressionLanguageComp, SWT.NONE);
        final CCombo combo = new CCombo(expressionLanguageComp, SWT.BORDER);
        GridData comboGridData = new GridData();
        comboGridData.widthHint = 150;
        comboGridData.verticalAlignment = SWT.TOP;
        combo.setLayoutData(comboGridData);
        combo.setEditable(false);
        combo.setItems(remainDBTypeListAF.toArray(new String[remainDBTypeListAF.size()]));
        if (language == null) {
            combo.setText(remainDBTypeListAF.get(0));
        } else {
            combo.setText(PatternLanguageType.findNameByLanguage(language));
        }
        combo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String lang = combo.getText();
                aggregateDateExpression.setLanguage(PatternLanguageType.findLanguageByName(lang));
                aggregateDateExpression.setModificationDate(UDIUtils.getCurrentDateTime());
                setDirty(true);
            }
        });

        final Composite expressionBodyComp = new Composite(expressComp, SWT.NONE);
        expressionBodyComp.setLayout(new GridLayout(1, false));
        expressionBodyComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

        if (aggregateDateExpression.haveAggregateExpression()) {
            String title = "bubble chart functions";//$NON-NLS-1$
            String[] headers = { "horizontal axis", "vertical axis", "bubble size" };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            int[] widths = { 120, 120, 240 };
            buildAggregateDateComp(expressionBodyComp, aggregateDateExpression, style, title, headers, widths);
        }

        if (aggregateDateExpression.haveDateExpression()) {
            String title = "gantt chart functions";//$NON-NLS-1$
            String[] headers = { "lower value", "upper value", "total", "highlighted values" };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
            int[] widths = { 120, 120, 120, 240 };
            buildAggregateDateComp(expressionBodyComp, aggregateDateExpression, style, title, headers, widths);
        }

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(expressComp);
    }

    /**
     * DOC xqliu Comment method "buildAggregateDateComp".
     * 
     * @param expressionBodyComp
     * @param aggregateDateExpression
     * @param style
     * @param title
     * @param headers
     * @param widths
     */
    private void buildAggregateDateComp(final Composite expressionBodyComp,
            final AggregateDateExpression aggregateDateExpression, int style, String title, String[] headers, int[] widths) {
        Label label = new Label(expressionBodyComp, SWT.LEFT);
        label.setText(title);

        Table table = new Table(expressionBodyComp, style);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        for (int i = 0; i < headers.length; ++i) {
            TableColumn tableColumn = new TableColumn(table, SWT.LEFT, i);
            tableColumn.setText(headers[i]);
            tableColumn.setWidth(widths[i]);
        }

        TableViewer tableViewer = new TableViewer(table);

        tableViewer.setUseHashlookup(true);
        tableViewer.setColumnProperties(headers);

        CellEditor[] editors = new CellEditor[headers.length];
        for (int i = 0; i < editors.length; ++i) {
            editors[i] = new TextCellEditor(table);
            ((Text) editors[i].getControl()).addVerifyListener(

            new VerifyListener() {

                public void verifyText(VerifyEvent e) {
                    e.doit = !ADDITIONAL_FUNCTIONS_SPLIT.equals(e.text);
                }
            });
        }
        tableViewer.setCellEditors(editors);

        if (aggregateDateExpression.haveAggregateExpression()) {
            tableViewer.setCellModifier(new AggregateCellModifier(headers, tableViewer));
            tableViewer.setContentProvider(new CommonContentProvider());
            tableViewer.setLabelProvider(new AggregateLabelProvider());
            tableViewer.setInput(new AggregateVO(aggregateDateExpression.getAggregateExpression()));
        }

        if (aggregateDateExpression.haveDateExpression()) {
            tableViewer.setCellModifier(new DateCellModifier(headers, tableViewer));
            tableViewer.setContentProvider(new CommonContentProvider());
            tableViewer.setLabelProvider(new DateLabelProvider());
            tableViewer.setInput(new DateVO(aggregateDateExpression.getDateExpression()));
        }
    }

    /**
     * DOC xqliu Comment method "recordAFExpression".
     * 
     * @param expressionMap
     * @param aggregateExpression
     * @param dateExpression
     */
    private void recordAFExpression(Map<String, AggregateDateExpression> expressionMap, TdExpression aggregateExpression,
            TdExpression dateExpression) {
        String language = null;
        if (aggregateExpression != null) {
            language = aggregateExpression.getLanguage();
        } else if (dateExpression != null) {
            language = dateExpression.getLanguage();
        }
        if (language != null) {
            AggregateDateExpression aggregateDateExpression = expressionMap.get(language);
            if (aggregateDateExpression == null) {
                AggregateDateExpression expression = new AggregateDateExpression();
                expression.setAggregateExpression(aggregateExpression);
                expression.setDateExpression(dateExpression);
                expressionMap.put(language, expression);
            } else {
                if (aggregateExpression != null) {
                    aggregateDateExpression.setAggregateExpression(aggregateExpression);
                } else if (dateExpression != null) {
                    aggregateDateExpression.setDateExpression(dateExpression);
                }
            }
        }
    }

    /**
     * DOC xqliu Comment method "createAFAddButton".
     * 
     * @param composite
     */
    private void createAFAddButton(Composite composite) {
        final Button addButton = new Button(composite, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setToolTipText(DefaultMessagesImpl.getString("PatternMasterDetailsPage.add")); //$NON-NLS-1$
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.CENTER;
        labelGd.widthHint = 65;
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                remainDBTypeListAF.clear();
                remainDBTypeListAF.addAll(allDBTypeList);
                for (AggregateDateExpression ade : afExpressionMapTemp.values()) {
                    String language = ade.getLanguage();
                    String languageName = PatternLanguageType.findNameByLanguage(language);
                    remainDBTypeListAF.remove(languageName);
                }
                if (remainDBTypeListAF.size() == 0) {
                    MessageDialog.openWarning(
                            null,
                            DefaultMessagesImpl.getString("PatternMasterDetailsPage.warning"), DefaultMessagesImpl.getString("PatternMasterDetailsPage.patternExpression")); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                String language = PatternLanguageType.findLanguageByName(remainDBTypeListAF.get(0));
                TdExpression expression = BooleanExpressionHelper.createTdExpression(language, PluginConstant.EMPTY_STRING);
                expression.setModificationDate(UDIUtils.getCurrentDateTime());
                AggregateDateExpression ade = new AggregateDateExpression();
                if (hasAggregateExpression) {
                    expression.setBody(BODY_AGGREGATE);
                    ade.setAggregateExpression(expression);
                }
                if (hasDateExpression) {
                    expression.setBody(BODY_DATE);
                    ade.setDateExpression(expression);
                }
                createNewAFExpressLine(language, ade);
                afExpressionMapTemp.put(language, ade);
                additionalFunctionsSection.setExpanded(true);
                setDirty(true);
            }
        });

    }

    /**
     * DOC xqliu Comment method "createCategorySection".
     * 
     * @param topComp
     */
    private void createCategorySection(Composite topComp) {
        categorySection = createSection(form, topComp, DefaultMessagesImpl.getString("UDIMasterPage.CategorySectionTitle"), null);//$NON-NLS-1$

        Label label = new Label(categorySection, SWT.WRAP);
        label.setText(DefaultMessagesImpl.getString("UDIMasterPage.CategorySectionDescription"));//$NON-NLS-1$
        categorySection.setDescriptionControl(label);

        categoryComp = createCategoryComp(categorySection);

        categorySection.setClient(categoryComp);
    }

    /**
     * DOC xqliu Comment method "createCategoryComp".
     * 
     * @param categorySection
     * @return
     */
    private Composite createCategoryComp(Section categorySection) {
        Composite composite = toolkit.createComposite(categorySection);
        composite.setLayout(new GridLayout(2, false));

        Collection<String> categories = DefinitionHandler.getInstance().getUserDefinedIndicatorCategoryLabels();
        comboCategory = new Combo(composite, SWT.READ_ONLY);
        GridData data = new GridData();
        data.verticalAlignment = GridData.BEGINNING;
        comboCategory.setLayoutData(data);
        comboCategory.setItems(categories.toArray(new String[categories.size()]));
        if (categories.size() > 0 && category == null) {
            category = DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory();
        }
        comboCategory.setText(InternationalizationUtil.getCategoryInternationalizationLabel(category.getLabel()));
        comboCategory.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (needConfirm) {
                    boolean openQuestion = MessageDialogWithToggle.openQuestion(Display.getCurrent().getActiveShell(),
                            DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.changeCategoryTitle"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.changeCategory")); //$NON-NLS-1$

                    if (openQuestion) {
                        setDirty(true);
                        category = DefinitionHandler.getInstance().getIndicatorCategoryByLabel(comboCategory.getText());
                        updateDetailList();
                        // clear all the temp maps except java type
                        removeFromTempMapsExceptJava();

                        if (dataBaseComp != null) {
                            Control[] children = dataBaseComp.getChildren();
                            for (Control con : children) {
                                con.dispose();
                            }

                        }
                        definitionSection.setExpanded(false);
                        definitionSection.setExpanded(true);
                    } else {
                        needConfirm = false;
                        comboCategory.setText(InternationalizationUtil.getCategoryInternationalizationLabel(category.getLabel()));
                        needConfirm = true;
                    }
                }
            }

        });

        // ADD yyi 2009-09-23 Feature 9059
        createDetailList(composite);
        updateDetailList();
        return composite;
    }

    /**
     * DOC yyi 2009-09-23 Feature 9059
     * 
     * @param composite
     */
    protected void updateDetailList() {
        String categoryLabel = comboCategory.getText();
        if (StringUtils.isNotBlank(categoryLabel)) {
            IndicatorCategory ic = DefinitionHandler.getInstance().getIndicatorCategoryByLabel(categoryLabel);
            if (ic != null) {
                String purposeString = PluginConstant.EMPTY_STRING;
                String descriptionString = PluginConstant.EMPTY_STRING;
                for (TaggedValue value : ic.getTaggedValue()) {
                    if ("Purpose".equals(value.getTag())) {//$NON-NLS-1$
                        purposeString = DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.Purpose") + value.getValue();//$NON-NLS-1$
                    } else if ("Description".equals(value.getTag())) { //$NON-NLS-1$
                        descriptionString = DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.Description")//$NON-NLS-1$
                                + value.getValue();
                    }
                }
                labelDetail.setText(purposeString + System.getProperty("line.separator") + System.getProperty("line.separator")//$NON-NLS-1$//$NON-NLS-2$
                        + descriptionString);
            }
        }
    }

    /**
     * DOC yyi 2009-09-23 Feature 9059
     * 
     * @param composite
     */
    private void createDetailList(Composite composite) {
        Composite compoDetail = new Composite(composite, SWT.NONE);
        compoDetail.setLayout(new GridLayout(1, false));
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 150;
        data.widthHint = 300;
        compoDetail.setLayoutData(data);

        labelDetail = new Label(compoDetail, SWT.WRAP);
        labelDetail.setLayoutData(data);
    }

    private void createDefinitionSection(Composite topCmp) {
        definitionSection = createSection(form, topCmp,
                DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.definition"), null); //$NON-NLS-1$

        Label label = new Label(definitionSection, SWT.WRAP);
        label.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.definitionDecription")); //$NON-NLS-1$
        definitionSection.setDescriptionControl(label);

        definitionComp = createDefinitionComp(definitionSection);

        definitionSection.setClient(definitionComp);
    }

    /**
     * DOC bZhou Comment method "createPatternDefinitionComp". MOD mzhao feature 11128 Be able to add Java UDI,
     * 2010-01-27
     * 
     * @param definitionSection
     * 
     * @return
     */
    private Composite createDefinitionComp(Composite definitionSection) {
        Composite composite = toolkit.createComposite(definitionSection);
        composite.setLayout(new GridLayout());

        expressionComp = new Composite(composite, SWT.NONE);
        expressionComp.setLayout(new GridLayout());
        expressionComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        // base sql composite
        dataBaseComp = new Composite(expressionComp, SWT.NONE);
        dataBaseComp.setLayout(new GridLayout());
        dataBaseComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        // ADD xqliu 2010-02-26 bug 11201
        if (tempExpressionList.size() > 0 || !checkJavaUDIBeforeOpen()) {
            addTitleComp(dataBaseComp);
        }
        // ADD klliu 2010-06-02 bug 13451: Class name of Java User Define Indicator must be validated
        // MOD backport klliu2010-06-10
        // if (!checkJavaUDIBeforeOpen()) {
        if (tempExpressionMap.size() == 0) {
            if (definition != null) {
                // MOD xqliu 2010-03-23 feature 11201
                for (TdExpression expression : tempExpressionList) {
                    createNewLineWithExpression(expression);
                }
                createNewLineWithJavaUDI();
            }
        }
        createAddButton(composite);

        return composite;
    }

    /**
     * DOC xqliu Comment method "addTitleComp". ADD xqliu 2010-02-26 bug 11201
     * 
     * @param expressionComp
     * @return
     */
    private Composite addTitleComp(Composite parentComp) {
        dataBaseTitleComp = new Composite(parentComp, SWT.NONE);
        dataBaseTitleComp.setLayout(new GridLayout(3, false));
        Label databaseLabel = new Label(dataBaseTitleComp, SWT.NONE);
        databaseLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.database")); //$NON-NLS-1$
        databaseLabel.setLayoutData(new GridData());
        ((GridData) databaseLabel.getLayoutData()).widthHint = 150;
        Label dbversionLabel = new Label(dataBaseTitleComp, SWT.NONE);
        dbversionLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.dbVersion")); //$NON-NLS-1$
        dbversionLabel.setLayoutData(new GridData(GridData.BEGINNING));
        ((GridData) dbversionLabel.getLayoutData()).widthHint = 60;
        Label sqlTemplateLabel = new Label(dataBaseTitleComp, SWT.NONE);
        sqlTemplateLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.sqlTemplate")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(dataBaseTitleComp);
        return dataBaseTitleComp;
    }

    private void createNewLineWithJavaUDI() {
        // MOD klliu 2011-08-08 bug 22994: Headers are wrong for Java option in indicator editor.
        if (javaLanguageComp == null && checkJavaUDIBeforeOpen()) {
            addJavaTitleComp(expressionComp);
        }
        // ~
        EList<TaggedValue> tvs = definition.getTaggedValue();
        String classNameStr = null;
        String jarPathStr = PluginConstant.EMPTY_STRING;
        for (TaggedValue tv : tvs) {
            if (tv.getTag().equals(TaggedValueHelper.CLASS_NAME_TEXT)) {
                classNameStr = tv.getValue();
                continue;
            }
            if (tv.getTag().equals(TaggedValueHelper.JAR_FILE_PATH)) {
                jarPathStr = tv.getValue();
            }
        }
        if (classNameStr == null) {
            return;
        }

        final Composite lineComp = new Composite(javaLanguageComp, SWT.NONE);
        lineComp.setLayout(new GridLayout(2, false));
        final CCombo javaCombo = new CCombo(lineComp, SWT.BORDER);
        javaCombo.setLayoutData(new GridData());
        ((GridData) javaCombo.getLayoutData()).widthHint = 150;
        javaCombo.setEditable(false);
        // MOD klliu 2010-06-10 bug 13104
        // combo.setEnabled(false);
        // MOD xqliu 2010-03-23 feature 11201
        // combo.setItems(remainDBTypeList.toArray(new String[remainDBTypeList.size()]));
        javaCombo.setItems(allDBTypeList.toArray(new String[allDBTypeList.size()]));
        // ~11201
        javaCombo.setText(PatternLanguageType.JAVA.getName());
        javaCombo.addSelectionListener(new LangCombSelectionListener());
        putTdExpressToTempMap(javaCombo,
                BooleanExpressionHelper.createTdExpression(PatternLanguageType.findLanguageByName(javaCombo.getText()), null));
        final Composite detailComp = new Composite(javaCombo.getParent(), SWT.NONE);
        widgetMap.put(javaCombo, detailComp);
        detailComp.setLayout(new GridLayout(4, false));
        final Text classNameText = new Text(detailComp, SWT.BORDER);
        classNameText.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridData) classNameText.getLayoutData()).widthHint = 250;
        classNameText.setText(classNameStr);
        classNameText.addModifyListener(new NeedToSetDirtyListener());
        final Text jarPathText = new Text(detailComp, SWT.BORDER);
        jarPathText.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridData) jarPathText.getLayoutData()).widthHint = 350;
        jarPathText.setText(jarPathStr);
        jarPathText.addModifyListener(new NeedToSetDirtyListener());
        Button button = new Button(detailComp, SWT.PUSH);
        button.setLayoutData(new GridData(GridData.FILL_BOTH));
        button.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        button.setToolTipText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        button.addSelectionListener(new SelectionAdapter() {

            // MOD by zshen for bug 18724 2011.02.23
            @Override
            public void widgetSelected(SelectionEvent e) {
                // MOD msjian 2011-8-9 TDQ-3199 fixed: define a new method use the exsit sourse, because there are two
                // places used the same sourse
                openJarSelectDialog(jarPathText, classNameText);
            }
        });
        // MOD klliu 2010-05-31 13451: Class name of Java User Define Indicator must be validated

        classNameText.addListener(SWT.Modify, new Listener() {

            public void handleEvent(Event event) {
                setClassNameForSave(classNameText.getText().toString());
                setJarPathForSave(jarPathText.getText().toString());
            }

        });
        javaCombo.setData(TaggedValueHelper.CLASS_NAME_TEXT, classNameText);
        javaCombo.setData(TaggedValueHelper.JAR_FILE_PATH, jarPathText);

        createExpressionDelButton(detailComp, javaCombo);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(detailComp);
    }

    /**
     * DOC klliu Comment method "addJavaTitleComp".
     * 
     * @param expressionComp
     */
    private Composite addJavaTitleComp(Composite expressionComp) {
        // MOD klliu 2011-07-09 bug 22994: Headers are wrong for Java option in indicator editor.
        javaLanguageComp = new Composite(expressionComp, SWT.NONE);
        javaLanguageComp.setLayout(new GridLayout());
        javaLanguageComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        javaTitleComp = new Composite(javaLanguageComp, SWT.NONE);
        javaTitleComp.setLayout(new GridLayout(3, false));
        Label languageLabel = new Label(javaTitleComp, SWT.NONE);
        languageLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.language")); //$NON-NLS-1$
        languageLabel.setLayoutData(new GridData());
        ((GridData) languageLabel.getLayoutData()).widthHint = 160;
        Label classLabel = new Label(javaTitleComp, SWT.NONE);
        classLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.javaClass")); //$NON-NLS-1$
        classLabel.setLayoutData(new GridData(GridData.BEGINNING));
        ((GridData) classLabel.getLayoutData()).widthHint = 255;
        Label jarLabel = new Label(javaTitleComp, SWT.NONE);
        jarLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.jars")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(javaTitleComp);
        return javaTitleComp;
    }

    // MOD klliu 2010-05-31 13451: Class name of Java User Define Indicator must be validated
    /**
     * DOC klliu Comment method "alidateJavaUDI". ADD klliu 2010-05-31 bug 13451
     * 
     * @param detailComp
     * @param classNameText
     * @param jarPathText
     */
    private boolean validateJavaUDI(Text classNameText, Text jarPathText) {
        if (!isSystemIndicator()) {
            String className = classNameText.getText().toString();
            String jarPath = jarPathText.getText().toString();
            this.setClassNameForSave(className);
            this.setJarPathForSave(jarPath);
        }
        return false;
    }

    /**
     * 
     * DOC mzhao IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private class NeedToSetDirtyListener implements ModifyListener {

        public void modifyText(ModifyEvent e) {
            setDirty(true);
        }
    }

    private void createNewLineWithExpression(final TdExpression expression) {
        final Composite lineComp = new Composite(dataBaseComp, SWT.NONE);
        lineComp.setLayout(new GridLayout(3, false));
        final CCombo combo = new CCombo(lineComp, SWT.BORDER);

        putTdExpressToTempMap(combo, expression);

        combo.setLayoutData(new GridData());
        ((GridData) combo.getLayoutData()).widthHint = 150;
        combo.setEditable(false);
        // MOD xqliu 2010-02-25 feature 11201
        combo.setItems(allDBTypeList.toArray(new String[allDBTypeList.size()]));
        // ~
        String language = expression.getLanguage();
        if (language == null) {
            // MOD xqliu 2010-02-25 feature 11201
            combo.setText(allDBTypeList.get(0));
            // ~
        } else {
            combo.setText(PatternLanguageType.findNameByLanguage(language));
        }
        combo.addSelectionListener(new LangCombSelectionListener());
        Composite detailComp = new Composite(lineComp, SWT.NONE);
        detailComp.setLayout(new GridLayout(4, false));

        createDataBaseLineComponent(combo, expression, detailComp);

        widgetMap.put(combo, detailComp);
        updateOtherCombos(combo);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(detailComp);
    }

    /**
     * DOC msjian Comment method "putTdExpressToTempMap".
     * 
     * @param expression
     * @param combo
     */
    public void putTdExpressToTempMap(final CCombo combo, final TdExpression expression) {
        tempExpressionMap.put(combo, expression);
        if (definition instanceof UDIndicatorDefinition) {
            UDIndicatorDefinition definition2 = (UDIndicatorDefinition) definition;
            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                tempViewValidRowsExpressionMap = setToTempMap(expression, combo, definition2.getViewValidRowsExpression(),
                        tempViewValidRowsExpressionMap);

                tempViewInvalidRowsExpressionMap = setToTempMap(expression, combo, definition2.getViewInvalidRowsExpression(),
                        tempViewInvalidRowsExpressionMap);

                tempViewValidValuesExpressionMap = setToTempMap(expression, combo, definition2.getViewValidValuesExpression(),
                        tempViewValidValuesExpressionMap);

                tempViewInvalidValuesExpressionMap = setToTempMap(expression, combo,
                        definition2.getViewInvalidValuesExpression(), tempViewInvalidValuesExpressionMap);
            } else {
                // get view rows tdExpress list, and set currect tdexpress to temp map
                tempViewRowsExpressionMap = setToTempMap(expression, combo, definition2.getViewRowsExpression(),
                        tempViewRowsExpressionMap);
            }
        }
    }

    public void removeFromTempMap(final CCombo combo) {
        tempExpressionMap.remove(combo);
        if (definition instanceof UDIndicatorDefinition) {

            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                tempViewValidRowsExpressionMap.remove(combo);
                tempViewInvalidRowsExpressionMap.remove(combo);
                tempViewValidValuesExpressionMap.remove(combo);
                tempViewInvalidValuesExpressionMap.remove(combo);

            } else {
                tempViewRowsExpressionMap.remove(combo);
            }
        }
    }

    /**
     * set To Temp Map.
     * 
     * @param expression
     * @param combo
     * @param list
     * @param map
     * @return
     */
    public Map<CCombo, TdExpression> setToTempMap(final TdExpression expression, final CCombo combo, EList<TdExpression> list,
            Map<CCombo, TdExpression> map) {
        Map<CCombo, TdExpression> temp = map;
        String language = expression.getLanguage();
        String version = expression.getVersion();
        if (list != null) {
            for (TdExpression tdExp : list) {
                if (tdExp.getLanguage().equals(language)
                        && ((tdExp.getVersion() == null && version == null) || (tdExp.getVersion() != null && tdExp.getVersion()
                                .equals(version)))) {
                    temp.put(combo, tdExp);
                    break;
                }
            }
        }
        return temp;
    }

    /**
     * DOC bZhou Comment method "creatNewLine". MOD mzhao feature 11128 Be able to add Java UDI, 2010-01-27
     * 
     * @param expression
     */
    private void createNewLine() {
        if (dataBaseTitleComp == null || dataBaseTitleComp.isDisposed()) {
            addTitleComp(dataBaseComp);
        }
        final Composite lineComp = new Composite(dataBaseComp, SWT.NONE);
        lineComp.setLayout(new GridLayout(5, false));
        final CCombo combo = new CCombo(lineComp, SWT.BORDER);
        combo.setLayoutData(new GridData());
        ((GridData) combo.getLayoutData()).widthHint = 150;

        combo.setEditable(false);
        // MOD xqliu 2010-02-25 feature 11201
        // combo.setItems(remainDBTypeList.toArray(new String[remainDBTypeList.size()]));
        combo.setItems(allDBTypeList.toArray(new String[allDBTypeList.size()]));
        // ~
        combo.select(0);

        combo.addSelectionListener(new LangCombSelectionListener());

        // MOD TDQ-6824 msjian 2013-2-8: when create expression, we should set correct language
        TdExpression expression = BooleanExpressionHelper.createTdExpression(
                PatternLanguageType.findLanguageByName(combo.getText()), null);
        // TDQ-6824~
        String oldLanguage = expression.getLanguage();
        expression.setModificationDate(UDIUtils.getCurrentDateTime());
        checkCComboIsDisposed();

        putTdExpressToTempMap(combo, expression);

        updateLineAndOtherCombos(combo, expression, oldLanguage);
    }

    /**
     * DOC msjian Comment method "updateLineAndOtherCombos".
     * 
     * @param combo
     * @param expression
     * @param oldLanguage
     */
    public void updateLineAndOtherCombos(final CCombo combo, TdExpression expression, String oldLanguage) {
        if (combo.getText().equals(PatternLanguageType.JAVA.getName())) {
            updateLineForJava(combo);
        } else {
            // MOD xqliu 2010-03-23 feature 11201
            updateLineForExpression(combo, expression, oldLanguage);
            // ~11201
        }
        updateOtherCombos(combo);
    }

    /**
     * DOC xqliu Comment method "createDbVersionText". ADD xqliu 2010-02-25 feature 11201
     * 
     * @param combo
     * @param composite
     * @param value
     * @param width
     */
    private void createDbVersionText(final CCombo combo, final Composite composite, String value, int width) {
        final Text dbVersionText = new Text(composite, SWT.BORDER);
        dbVersionText.setText(value == null ? PluginConstant.EMPTY_STRING : value);
        dbVersionText.setLayoutData(new GridData(GridData.BEGINNING));
        ((GridData) dbVersionText.getLayoutData()).widthHint = width;
        dbVersionText.addModifyListener(new DbVersionTextModListener(combo));
    }

    /**
     * 
     * DOC mzhao IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private class ExpressTextModListener implements ModifyListener {

        private CCombo combo;

        public ExpressTextModListener(CCombo combo) {
            this.combo = combo;
        }

        public void modifyText(ModifyEvent e) {
            Text patternText = (Text) e.getSource();
            TdExpression expression = tempExpressionMap.get(combo);
            expression.setBody(patternText.getText());
            expression.setModificationDate(UDIUtils.getCurrentDateTime());
            setDirty(true);
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private class DbVersionTextModListener implements ModifyListener {

        private CCombo combo;

        public DbVersionTextModListener(CCombo combo) {
            this.combo = combo;
        }

        public void modifyText(ModifyEvent e) {
            // MOD xqliu 2010-04-01 bug 11892
            setDirty(true);

            Text dbVersionText = (Text) e.getSource();
            List<TdExpression> modifyList = new ArrayList<TdExpression>();
            modifyList.add(tempExpressionMap.get(this.combo));
            // update other Temp Maps
            if (definition instanceof UDIndicatorDefinition) {
                if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                    modifyList.add(tempViewValidRowsExpressionMap.get(this.combo));
                    modifyList.add(tempViewInvalidRowsExpressionMap.get(this.combo));
                    modifyList.add(tempViewValidValuesExpressionMap.get(this.combo));
                    modifyList.add(tempViewInvalidValuesExpressionMap.get(this.combo));

                } else {
                    modifyList.add(tempViewRowsExpressionMap.get(this.combo));
                }
            }

            for (TdExpression exp : modifyList) {
                if (exp != null) {
                    exp.setVersion(dbVersionText.getText().trim());
                    exp.setModificationDate(UDIUtils.getCurrentDateTime());
                }
            }
            // ~11892
        }

    }

    /**
     * 
     * DOC mzhao IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private class LangCombSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e) {
            CCombo combo = (CCombo) e.getSource();
            String name = combo.getText();
            List<TdExpression> modifyList = new ArrayList<TdExpression>();
            TdExpression expression = tempExpressionMap.get(combo);
            String oldLanguage = expression.getLanguage();
            String oldSQLTemplate = expression.getBody();
            modifyList.add(expression);

            // check whether the java type exist first
            if (isJavaExist(combo)) {
                MessageUI.openError(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.isRepeated", combo.getText())); //$NON-NLS-1$
                combo.setText(PatternLanguageType.findNameByLanguage(oldLanguage));
                return;
            }

            if (oldLanguage.equals(PatternLanguageType.JAVA.getName())) {
                doDeleteOnlyForJava();
            }
            // update other Temp Maps
            if (definition instanceof UDIndicatorDefinition) {
                if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                    modifyList.add(tempViewValidRowsExpressionMap.get(combo));
                    modifyList.add(tempViewInvalidRowsExpressionMap.get(combo));
                    modifyList.add(tempViewValidValuesExpressionMap.get(combo));
                    modifyList.add(tempViewInvalidValuesExpressionMap.get(combo));

                } else {
                    modifyList.add(tempViewRowsExpressionMap.get(combo));
                }
            }

            for (TdExpression exp : modifyList) {
                if (exp == null) {
                    // MOD TDQ-6824 msjian 2013-2-8: when create expression, we should set correct language
                    exp = BooleanExpressionHelper.createTdExpression(PatternLanguageType.findLanguageByName(name), null);
                    exp.setBody(oldSQLTemplate);
                    // TDQ-6824~
                    putTdExpressToTempMap(combo, exp);
                } else {
                    exp.setLanguage(PatternLanguageType.findLanguageByName(name));
                }
                exp.setModificationDate(UDIUtils.getCurrentDateTime());
            }
            updateLineAndOtherCombos(combo, expression, oldLanguage);

            checkCComboIsDisposed();

            setDirty(true);
        }
    }

    /**
     * DOC xqliu Comment method "updateLineForExpression". // MOD xqliu 2010-03-23 feature 11201
     * 
     * @param combo
     * @param expression
     */
    private void updateLineForExpression(final CCombo combo, TdExpression expression, String oldLanguage) {
        // MOD klliu 2011-07-09 bug 22994: Headers are wrong for Java option in indicator editor.
        if (dataBaseTitleComp == null || dataBaseTitleComp.isDisposed()) {
            addTitleComp(dataBaseComp);
        }
        Composite detailComp = widgetMap.get(combo);
        if (detailComp != null) {
            detailComp.dispose();
        }
        if (!oldLanguage.equals(PatternLanguageType.JAVA.getName())) {
            detailComp = new Composite(combo.getParent(), SWT.NONE);
            widgetMap.put(combo, detailComp);
            detailComp.setLayout(new GridLayout(4, false));
            // ADD xqliu 2010-04-02 feature 11201
            createDataBaseLineComponent(combo, expression, detailComp);
            // ~
        } else {
            final Composite lineComp = new Composite(dataBaseComp, SWT.NONE);
            lineComp.setLayout(new GridLayout(3, false));
            final CCombo dataBaseCombo = new CCombo(lineComp, SWT.BORDER);
            // add listener
            dataBaseCombo.setLayoutData(new GridData());
            ((GridData) dataBaseCombo.getLayoutData()).widthHint = 150;
            dataBaseCombo.setEditable(false);
            dataBaseCombo.setItems(allDBTypeList.toArray(new String[allDBTypeList.size()]));
            dataBaseCombo.addSelectionListener(new LangCombSelectionListener());
            dataBaseCombo.select(combo.getSelectionIndex());
            putTdExpressToTempMap(dataBaseCombo, expression);
            detailComp = new Composite(lineComp, SWT.NONE);
            detailComp.setLayout(new GridLayout(4, false));
            widgetMap.put(dataBaseCombo, detailComp);
            createDataBaseLineComponent(dataBaseCombo, expression, detailComp);
            // remove the combo in the widgetMap then add new javacombo
            widgetMap.remove(combo);

            removeFromTempMap(combo);
            // line comp dispose
            combo.getParent().dispose();
            // java title dispose
            if (javaLanguageComp.getChildren().length == 1) {
                Control[] children = javaLanguageComp.getChildren();
                children[0].dispose();
            }
        }
        definitionSection.setExpanded(false);
        definitionSection.setExpanded(true);

    }

    private void createDataBaseLineComponent(final CCombo combo, TdExpression expression, Composite detailComp) {
        createDbVersionText(combo, detailComp, expression.getVersion(), 30);
        final Text patternText = new Text(detailComp, SWT.BORDER);
        patternText.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridData) patternText.getLayoutData()).widthHint = 600;
        // MOD xqliu 2010-03-23 feature 11201
        patternText.setText(expression.getBody() == null ? PluginConstant.EMPTY_STRING : expression.getBody());
        // ~11201
        patternText.addModifyListener(new ExpressTextModListener(combo));
        // ADD msjian TDQ-6841: set the pattern text can not input when the indicator is UDI
        if (!isSystemIndicator()) {
            patternText.setEditable(false);
        }
        // TDQ-6841~
        createExpressionEditButton(detailComp, patternText, combo, expression.getVersion());
        createExpressionDelButton(detailComp, combo);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(detailComp);
        detailComp.getParent().layout();
    }

    /**
     * DOC mzhao Comment method "updateLineForJava".
     * 
     * @param combo
     */
    private void updateLineForJava(final CCombo combo) {
        if (javaTitleComp == null || javaTitleComp.isDisposed()) {
            addJavaTitleComp(expressionComp);
        }
        final Composite lineComp = new Composite(javaLanguageComp, SWT.NONE);
        lineComp.setLayout(new GridLayout(2, false));
        // create a new java combo
        final CCombo javaCombo = new CCombo(lineComp, SWT.BORDER);
        javaCombo.setLayoutData(new GridData());
        ((GridData) javaCombo.getLayoutData()).widthHint = 150;
        javaCombo.setEditable(false);
        javaCombo.addSelectionListener(new LangCombSelectionListener());
        javaCombo.setItems(allDBTypeList.toArray(new String[allDBTypeList.size()]));
        javaCombo.select(combo.getSelectionIndex());
        // ~~~~
        Composite detailComp = new Composite(lineComp, SWT.NONE);
        detailComp.setLayout(new GridLayout(5, false));
        final Text classNameText = new Text(detailComp, SWT.BORDER);
        classNameText.setLayoutData(new GridData(GridData.FILL_BOTH));
        classNameText.addModifyListener(new NeedToSetDirtyListener());
        ((GridData) classNameText.getLayoutData()).widthHint = 250;
        classNameText.addModifyListener(new ExpressTextModListener(javaCombo));
        final Text jarPathText = new Text(detailComp, SWT.BORDER);
        jarPathText.setLayoutData(new GridData(GridData.FILL_BOTH));
        jarPathText.addModifyListener(new NeedToSetDirtyListener());
        GridData layoutData = (GridData) jarPathText.getLayoutData();
        layoutData.horizontalSpan = 2;
        layoutData.widthHint = 380;
        Button button = new Button(detailComp, SWT.PUSH);
        button.setLayoutData(new GridData(GridData.FILL_BOTH));
        button.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        button.setToolTipText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        // ((GridData) button.getLayoutData()).widthHint = 100;
        button.addSelectionListener(new SelectionAdapter() {

            // MOD by zshen for bug 18724 2011.02.23
            @Override
            public void widgetSelected(SelectionEvent e) {
                // MOD msjian 2011-8-9 TDQ-3199 fixed: define a new method use the exsit sourse, because there are two
                // places used the same sourse
                openJarSelectDialog(jarPathText, classNameText);
            }
        });
        // MOD klliu 2010-05-31 13451: Class name of Java User Define Indicator must be validated
        classNameText.addListener(SWT.Modify, new Listener() {

            public void handleEvent(Event event) {
                setClassNameForSave(classNameText.getText().toString());
                setJarPathForSave(jarPathText.getText().toString());

            }
        });
        javaCombo.setData(TaggedValueHelper.CLASS_NAME_TEXT, classNameText);
        javaCombo.setData(TaggedValueHelper.JAR_FILE_PATH, jarPathText);
        createExpressionDelButton(detailComp, javaCombo);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(detailComp);
        detailComp.getParent().layout();
        // remove the data base combo in the widgetMap then add new javacombo
        widgetMap.put(javaCombo, detailComp);
        widgetMap.remove(combo);
        removeFromTempMap(combo);
        TdExpression expression = tempExpressionMap.get(combo);
        if (expression == null) {
            expression = BooleanExpressionHelper.createTdExpression(PatternLanguageType.findLanguageByName(javaCombo.getText()),
                    null);
            putTdExpressToTempMap(javaCombo, expression);
        }
        combo.getParent().dispose();

        if (dataBaseComp.getChildren().length == 1) {
            Control[] children = dataBaseComp.getChildren();
            children[0].dispose();
        }
        definitionSection.setExpanded(false);
        definitionSection.setExpanded(true);
    }

    // ADD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used already.
    /**
     * DOC msjian Comment method "openJarSelectDialog".
     * 
     * @param jarPathText
     * @param classNameText
     */
    private void openJarSelectDialog(Text jarPathText, Text classNameText) {
        String jarpathStr = jarPathText.getText();
        JavaUdiJarSelectDialog selectDialog = UDIUtils.createUdiJarCheckedTreeSelectionDialog(definition,
                ResourceManager.getUDIJarFolder(), jarpathStr.split("\\|\\|"));//$NON-NLS-1$
        selectDialog.setControl(jarPathText);
        selectDialog.open();

        // MOD klliu 2010-05-31 13451: Class name of Java User Define Indicator must be validated
        validateJavaUDI(classNameText, jarPathText);
        ProxyRepositoryManager.getInstance().save();

    }

    /**
     * DOC xqliu Comment method "updateOtherCombos". MOD xqliu 2010-03-23 feature 11201
     * 
     * @param combo
     */
    private void updateOtherCombos(CCombo combo) {
        // MOD xqliu 2010-03-23 feature 11201
        // rebuildRemainDBTypeList();
        // ~11201
        Collection<CCombo> allCombos = tempExpressionMap.keySet();
        for (CCombo cb : allCombos) {
            if (!combo.isDisposed() && combo != cb) {
                String tx = cb.getText();
                // MOD xqliu 2010-03-23 feature 11201
                cb.setItems(allDBTypeList.toArray(new String[allDBTypeList.size()]));
                // ~11201
                cb.setText(tx);
            }
        }

    }

    /**
     * DOC xqliu Comment method "createExpressionDelButton".
     * 
     * @param expressComp
     * @param expression
     */
    private void createExpressionDelButton(final Composite expressComp, final CCombo languageCombo) {
        Button delButton = new Button(expressComp, SWT.PUSH);
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delButton.setToolTipText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.deleteExpression"));//$NON-NLS-1$
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;
        delButton.setLayoutData(labelGd);
        delButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                doDelete(expressComp, languageCombo.getText());
            }
        });

    }

    /**
     * the delete action for delete button.
     * 
     * @param expressComp
     * @param language
     */
    private void doDelete(final Composite expressComp, final String language) {
        // remove title line comp and combo line
        Composite lineComp = expressComp.getParent();
        Composite parent = lineComp.getParent();
        lineComp.dispose();
        if (parent.getChildren().length == 1) {
            Control[] children = parent.getChildren();
            children[0].dispose();
        }
        int languageLength = javaLanguageComp == null ? 0 : javaLanguageComp.getChildren().length;
        int dataBaseLength = dataBaseComp == null ? 0 : dataBaseComp.getChildren().length;
        if (languageLength == 0 && dataBaseLength == 0) {
            addTitleComp(dataBaseComp);
        }
        if (languageLength == 1) {
            Control[] children = javaLanguageComp.getChildren();
            children[0].dispose();
        }
        // ~
        checkCComboIsDisposed();
        if (language.equals(PatternLanguageType.JAVA.getName())) {
            doDeleteOnlyForJava();
        }

        definitionSection.setExpanded(false);
        definitionSection.setExpanded(true);
        setDirty(true);
    }

    /**
     * do Delete Only For java type.
     */
    protected void doDeleteOnlyForJava() {
        classNameForSave = ""; //$NON-NLS-1$
        jarPathForSave = ""; //$NON-NLS-1$
    }

    /**
     * DOC yyi 2009-09-11 Feature:9030.
     * 
     * @param expressComp
     * @param patternText
     * 
     * @return
     */
    private void createExpressionEditButton(Composite expressComp, final Text patternText, final CCombo combo,
            final String version) {
        Button editButton = new Button(expressComp, SWT.PUSH);
        editButton.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        editButton.setToolTipText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        editButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                definition = (IndicatorDefinition) getCurrentModelElement(getEditor());

                // if it is dirty, get value from tempMap, else from definition
                TdExpression tdExpression = getCurrentLanguageExp(definition.getSqlGenericExpression(),
                        PatternLanguageType.findLanguageByName(combo.getText()), version);
                if (isDirty()) {
                    tdExpression = tempExpressionMap.get(combo);
                }

                String language = tdExpression.getLanguage();

                boolean isUDIndicatorDefinition = definition instanceof UDIndicatorDefinition;
                final ExpressionEditDialog editDialog = new ExpressionEditDialog(null, patternText.getText(),
                        isUDIndicatorDefinition, cloneExpression(tdExpression));
                editDialog.setVersion(version);
                editDialog.setLanguage(language);
                editDialog.setCategory(category);

                if (isUDIndicatorDefinition) {
                    if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                        EList<TdExpression> viewValidRowsExpression = ((UDIndicatorDefinition) definition)
                                .getViewValidRowsExpression();
                        TdExpression viewValidRows = getCurrentLanguageExp(viewValidRowsExpression, language, version);
                        if (isDirty()) {
                            viewValidRows = tempViewValidRowsExpressionMap.get(combo);
                        }
                        editDialog.setTempViewValidRowsExp(cloneExpression(viewValidRows));

                        EList<TdExpression> viewInvalidRowsExpression = ((UDIndicatorDefinition) definition)
                                .getViewInvalidRowsExpression();
                        TdExpression viewInvalidRows = getCurrentLanguageExp(viewInvalidRowsExpression, language, version);
                        if (isDirty()) {
                            viewInvalidRows = tempViewInvalidRowsExpressionMap.get(combo);
                        }
                        editDialog.setTempViewInvalidRowsExp(cloneExpression(viewInvalidRows));

                        EList<TdExpression> viewValidValuesExpression = ((UDIndicatorDefinition) definition)
                                .getViewValidValuesExpression();
                        TdExpression viewValidValues = getCurrentLanguageExp(viewValidValuesExpression, language, version);
                        if (isDirty()) {
                            viewValidValues = tempViewValidValuesExpressionMap.get(combo);
                        }
                        editDialog.setTempViewValidValuesExp(cloneExpression(viewValidValues));

                        EList<TdExpression> viewInvalidValuesExpression = ((UDIndicatorDefinition) definition)
                                .getViewInvalidValuesExpression();
                        TdExpression viewInvalidValues = getCurrentLanguageExp(viewInvalidValuesExpression, language, version);
                        if (isDirty()) {
                            viewInvalidValues = tempViewInvalidValuesExpressionMap.get(combo);
                        }
                        editDialog.setTempViewInvalidValuesExp(cloneExpression(viewInvalidValues));

                    } else {
                        EList<TdExpression> viewRowsExpression = ((UDIndicatorDefinition) definition).getViewRowsExpression();
                        TdExpression viewRows = getCurrentLanguageExp(viewRowsExpression, language, version);
                        if (isDirty()) {
                            viewRows = tempViewRowsExpressionMap.get(combo);
                        }
                        editDialog.setTempViewRowsExp(cloneExpression(viewRows));
                    }
                }

                if (Dialog.OK == editDialog.open()) {
                    patternText.setText(editDialog.getTempExpression().getBody());
                    tempExpressionMap.put(combo, editDialog.getTempExpression());

                    if (isUDIndicatorDefinition) {
                        if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                            tempViewValidRowsExpressionMap.put(combo, editDialog.getTempViewValidRowsExp());
                            tempViewInvalidRowsExpressionMap.put(combo, editDialog.getTempViewInvalidRowsExp());
                            tempViewValidValuesExpressionMap.put(combo, editDialog.getTempViewValidValuesExp());
                            tempViewInvalidValuesExpressionMap.put(combo, editDialog.getTempViewInvalidValuesExp());

                        } else {
                            // get view rows tdExpress list, and set currect tdexpress to temp map
                            tempViewRowsExpressionMap.put(combo, editDialog.getTempViewRowsExp());
                        }
                    }

                    setDirty(true);
                }
            }
        });

    }

    /**
     * get Current Language Expression.
     * 
     * @param tempViewValidRowsExpList
     * @param language
     * @param version
     */
    public TdExpression getCurrentLanguageExp(List<TdExpression> tempViewValidRowsExpList, String language, String version) {
        if (tempViewValidRowsExpList != null) {
            for (TdExpression tdExp : tempViewValidRowsExpList) {
                if (UDIUtils.isCurrentLanguageAndVersion(tdExp, language, version)) {
                    return tdExp;
                }
            }
        }
        TdExpression createTdExpression = BooleanExpressionHelper.createTdExpression(language, PluginConstant.EMPTY_STRING,
                version);
        createTdExpression.setModificationDate(UDIUtils.getCurrentDateTime());
        return createTdExpression;
    }

    private void createAddButton(final Composite parent) {
        final Button addButton = new Button(parent, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setToolTipText(DefaultMessagesImpl.getString("PatternMasterDetailsPage.add")); //$NON-NLS-1$
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.CENTER;
        labelGd.widthHint = 65;
        addButton.setLayoutData(labelGd);
        // MOD qiongli 2011-8-15 feature TDQ-1894:disable for phone nuber indicator
        if (definition != null && category != null && IndicatorCategoryHelper.isPhoneNumberCategory(category)) {
            addButton.setEnabled(false);
        }
        addButton.addSelectionListener(new SelectionAdapter() {

            // MOD mzhao feature 11128 Be able to add Java UDI, 2010-01-27
            @Override
            public void widgetSelected(SelectionEvent e) {
                createNewLine();
                definitionSection.setExpanded(true);
                setDirty(true);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#getCurrentModelElement(org.eclipse.ui.forms.editor
     * .FormEditor)
     */
    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        IEditorInput editorInputt = editor.getEditorInput();
        IndicatorDefinition ind = null;
        // MOD klliu 2010-12-10
        if (editorInputt instanceof IndicatorEditorInput) {
            IndicatorEditorInput indicatorEditorInput = (IndicatorEditorInput) editorInputt;
            ind = indicatorEditorInput.getIndicatorDefinition();
        } else if (editorInputt instanceof FileEditorInput) {
            FileEditorInput fileEditorInput = (FileEditorInput) editorInputt;
            ind = IndicatorResourceFileHelper.getInstance().findIndDefinition(fileEditorInput.getFile());
        } else if (editorInputt instanceof IndicatorDefinitionItemEditorInput) {
            IndicatorDefinitionItemEditorInput indicatorDefEditorInput = (IndicatorDefinitionItemEditorInput) editorInputt;
            TDQIndicatorDefinitionItem tdqIndicatorDefinitionItem = indicatorDefEditorInput.getTDQIndicatorDefinitionItem();
            ind = tdqIndicatorDefinitionItem.getIndicatorDefinition();
        }
        return ind;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((IndicatorEditor) getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        ReturnCode rc = canSave();
        if (!rc.isOk()) {
            return;
        }
        super.doSave(monitor);
        boolean needReloadJUDIJar = false;
        // ADD klliu 2010-06-01 bug 13451: Class name of Java User Define Indicator must be validated
        if (!checkJavaUDIBeforeSave()) {
            ((IndicatorEditor) this.getEditor()).setSaveActionButtonState(false);
            String message = DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.classPathError");//$NON-NLS-1$
            MessageUI.openWarning(message);
            return;
        }

        // ADD yyi 2011-05-31 16158:add whitespace check for text fields.
        if (!checkWhithspace()) {
            MessageUI.openError(DefaultMessagesImpl.getString("AbstractMetadataFormPage.whitespace")); //$NON-NLS-1$
            return;
        }
        // ADD xqliu 2010-02-25 feature 11201
        rc = checkBeforeSave();
        if (rc.isOk()) {

            if (definition instanceof UDIndicatorDefinition) {
                UDIndicatorDefinition def = (UDIndicatorDefinition) definition;
                EList<TdExpression> viewValidRowsExpression = def.getViewValidRowsExpression();
                viewValidRowsExpression.clear();
                viewValidRowsExpression = saveFromTempMapToDefinition(viewValidRowsExpression, tempViewValidRowsExpressionMap);

                EList<TdExpression> viewInvalidRowsExpression = def.getViewInvalidRowsExpression();
                viewInvalidRowsExpression.clear();
                viewInvalidRowsExpression = saveFromTempMapToDefinition(viewInvalidRowsExpression,
                        tempViewInvalidRowsExpressionMap);

                EList<TdExpression> viewValidValuesExpression = def.getViewValidValuesExpression();
                viewValidValuesExpression.clear();
                viewValidValuesExpression = saveFromTempMapToDefinition(viewValidValuesExpression,
                        tempViewValidValuesExpressionMap);

                EList<TdExpression> viewInvalidValuesExpression = def.getViewInvalidValuesExpression();
                viewInvalidValuesExpression.clear();
                viewInvalidValuesExpression = saveFromTempMapToDefinition(viewInvalidValuesExpression,
                        tempViewInvalidValuesExpressionMap);

                EList<TdExpression> viewRowsExpression = def.getViewRowsExpression();
                viewRowsExpression.clear();
                viewRowsExpression = saveFromTempMapToDefinition(viewRowsExpression, tempViewRowsExpressionMap);

                if (category != null) {
                    UDIHelper.setUDICategory(definition, category);
                }
            }

            EList<TdExpression> expressions = definition.getSqlGenericExpression();
            expressions.clear();
            Iterator<CCombo> it = tempExpressionMap.keySet().iterator();

            CCombo javaUIDCombo = null;
            while (it.hasNext()) {
                CCombo cb = it.next();
                // MOD MOD mzhao feature 11128 Be able to add Java UDI, 2010-01-28
                if (cb.getText().equals(PatternLanguageType.JAVA.getName())) {
                    javaUIDCombo = cb;
                } else {
                    TdExpression exp = tempExpressionMap.get(cb);
                    if (exp.getBody() != null && !PluginConstant.EMPTY_STRING.equals(exp.getBody())) {
                        // MOD xqliu 2010-04-01 bug 11892
                        TdExpression cloneExpression = cloneExpression(exp);
                        expressions.add(cloneExpression);
                        // ~11892
                    }
                }
            }

            // Save Java UID
            EList<TaggedValue> tvs = definition.getTaggedValue();
            if (javaUIDCombo != null) {
                boolean isNewTaggedValue = true;
                for (TaggedValue tv : tvs) {
                    if (tv.getTag().equals(TaggedValueHelper.CLASS_NAME_TEXT)) {
                        String newTagValue = ((Text) javaUIDCombo.getData(TaggedValueHelper.CLASS_NAME_TEXT)).getText();
                        needReloadJUDIJar |= !StringUtils.equals(tv.getValue(), newTagValue);
                        tv.setValue(newTagValue);
                        isNewTaggedValue = false;
                        continue;
                    }
                    if (tv.getTag().equals(TaggedValueHelper.JAR_FILE_PATH)) {
                        String newTagValue = ((Text) javaUIDCombo.getData(TaggedValueHelper.JAR_FILE_PATH)).getText();
                        needReloadJUDIJar |= !StringUtils.equals(tv.getValue(), newTagValue);
                        tv.setValue(newTagValue);
                    }
                }
                if (isNewTaggedValue) {
                    TaggedValue classNameTV = TaggedValueHelper.createTaggedValue(TaggedValueHelper.CLASS_NAME_TEXT,
                            ((Text) javaUIDCombo.getData(TaggedValueHelper.CLASS_NAME_TEXT)).getText());
                    TaggedValue jarPathTV = TaggedValueHelper.createTaggedValue(TaggedValueHelper.JAR_FILE_PATH,
                            ((Text) javaUIDCombo.getData(TaggedValueHelper.JAR_FILE_PATH)).getText());
                    definition.getTaggedValue().add(classNameTV);
                    definition.getTaggedValue().add(jarPathTV);
                }
            } else {
                // Remove Java UDI tagged values if there have.
                TaggedValue tvCN = null;
                TaggedValue tvJARP = null;
                for (TaggedValue tv : tvs) {
                    if (tv.getTag().equals(TaggedValueHelper.CLASS_NAME_TEXT)) {
                        tvCN = tv;
                        continue;
                    }
                    if (tv.getTag().equals(TaggedValueHelper.JAR_FILE_PATH)) {
                        tvJARP = tv;
                    }
                }
                if (tvCN != null) {
                    tvs.remove(tvCN);
                }
                if (tvJARP != null) {
                    tvs.remove(tvJARP);
                }

            }
            if (hasAggregateExpression) {
                EList<TdExpression> aggregate1argFunctions = definition.getAggregate1argFunctions();
                aggregate1argFunctions.clear();
                for (AggregateDateExpression ade : afExpressionMapTemp.values()) {
                    TdExpression expression = ade.getAggregateExpression();
                    if (expression.getBody() != null && !PluginConstant.EMPTY_STRING.equals(expression.getBody())) {
                        aggregate1argFunctions.add(expression);
                    }
                }
            }

            if (hasDateExpression) {
                EList<TdExpression> date1argFunctions = definition.getDate1argFunctions();
                date1argFunctions.clear();
                for (AggregateDateExpression ade : afExpressionMapTemp.values()) {
                    TdExpression expression = ade.getDateExpression();
                    if (expression.getBody() != null && !PluginConstant.EMPTY_STRING.equals(expression.getBody())) {
                        date1argFunctions.add(expression);
                    }
                }
            }

            if (hasCharactersMapping) {
                EList<CharactersMapping> charactersMappings = definition.getCharactersMapping();
                charactersMappings.clear();
                for (CharactersMapping cm : charactersMappingMapTemp.values()) {
                    String c = cm.getCharactersToReplace();
                    String r = cm.getReplacementCharacters();
                    if (checkMappingString(c, r)) {
                        charactersMappings.add(cm);
                    } else {
                        MessageUI.openError("[" + cm.getLanguage()//$NON-NLS-1$
                                + "] INPUT ERROR:\nThe length of two inputed strings are not equal.");//$NON-NLS-1$
                        return;
                    }
                }
            }

            // Save difinition UDI Parameters
            saveDefinitionParameters(definition);

            rc = UDIHelper.validate(definition);
        }

        // MOD by zshen for bug 18724 2011.02.23
        if (this.definitionItem != null) {
            this.definitionItem.setIndicatorDefinition(definition);
        }
        if (rc.isOk()) {
            this.isDirty = false;
            // Mod TDQ-7474, only when rc is ok, should save the definition
            // MOD yyi 2012-02-08 TDQ-4621:Explicitly set true for updating dependencies.
            ElementWriterFactory.getInstance().createIndicatorDefinitionWriter().save(definitionItem, true);

            if (UDIHelper.isJUDIValid(definition) && needReloadJUDIJar) {
                UDIHelper.clearJAVAUDIMAPByIndicatorDefinition(definition);
            }
        } else {
            MessageUI.openError(rc.getMessage());
        }
    }

    /**
     * check whether the java type is exist.
     * 
     * @param combo
     * @return boolean
     */
    protected boolean isJavaExist(CCombo combo) {
        if (combo.getText().equals(PatternLanguageType.JAVA.getName())) {
            if (javaLanguageComp != null && checkContainJavaInTempExpressionMap()) {
                return true;
            }
        }
        return false;
    }

    /**
     * check the tempExpressionMap contains java expression.
     * 
     * @return boolean
     */
    protected boolean checkContainJavaInTempExpressionMap() {
        for (TdExpression value : tempExpressionMap.values()) {
            if (value.getLanguage().equals(PatternLanguageType.JAVA.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC msjian Comment method "saveFromTempMapToDefinition".
     * 
     * @param expressions
     * @return
     */
    public EList<TdExpression> saveFromTempMapToDefinition(EList<TdExpression> expressions, Map<CCombo, TdExpression> map) {
        EList<TdExpression> temp = expressions;
        temp.clear();
        Iterator<CCombo> it = map.keySet().iterator();
        while (it.hasNext()) {
            CCombo cb = it.next();
            TdExpression exp = map.get(cb);
            if (exp.getBody() != null && !PluginConstant.EMPTY_STRING.equals(exp.getBody())) {
                TdExpression cloneExpression = cloneExpression(exp);
                temp.add(cloneExpression);
            }
        }
        return temp;
    }

    /**
     * DDOC klliu Comment method "saveDefinitionParameters". ADD klliu figure 13429 2010-07-12
     * 
     * @param definitionUDI
     */
    private void saveDefinitionParameters(IndicatorDefinition definitionUDI) {
        EList<IndicatorDefinitionParameter> params = definitionUDI.getIndicatorDefinitionParameter();
        if (params != null) {
            params.clear();
            params.addAll(tempParameters);
        }
    }

    /**
     * DOC klliu Comment method "checkJavaUDIBeforeSave".
     * 
     * @return
     */
    private boolean checkJavaUDIBeforeSave() {
        if (!systemIndicator) {

            EList<TaggedValue> tvs = definition.getTaggedValue();
            boolean isHaveJavaComb = checkIsHaveJavaComb();
            boolean isHaveJavaTag = checkContainsJavaUDI(definition);
            boolean isHaveSqlExpr = checkIsHaveSqlExcepretion();
            String className = this.getClassNameForSave();
            String jarPath = this.getJarPathForSave();
            volidateNameAndPath(className, jarPath, tvs);
            boolean cj2e = checkJavaIndicatorIsEixt();
            if (isHaveJavaComb == true && isHaveSqlExpr == true && isHaveJavaTag == false) {
                return cj2e;
            } else if (isHaveJavaComb == false && isHaveSqlExpr == true) {
                return true;
            } else if (isHaveJavaComb == true && isHaveSqlExpr == false) {
                return cj2e;
            } else if (isHaveJavaComb == true && isHaveSqlExpr == true) {
                return cj2e;
            }
        }
        return true;

    }

    /**
     * DOC klliu Comment method "checkContainsJavaUDI".
     * 
     * @param definition2
     * @return
     */
    private boolean checkContainsJavaUDI(IndicatorDefinition definition2) {
        EList<TaggedValue> tvs = definition.getTaggedValue();
        for (TaggedValue tv : tvs) {
            if (tv.getTag().equals(TaggedValueHelper.CLASS_NAME_TEXT)) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC klliu Comment method "checkJavaIndicatorIsEixt".
     * 
     * @return
     */
    private boolean checkJavaIndicatorIsEixt() {
        String className = this.getClassNameForSave();
        String jarPath = this.getJarPathForSave();

        if (className != null && jarPath != null && !className.trim().equals(PluginConstant.EMPTY_STRING)
                && !jarPath.trim().equals(PluginConstant.EMPTY_STRING)) {
            // MOD by zshen for bug 18724 2011.02.23
            for (IFile file : UDIUtils.getContainJarFile(jarPath)) {
                TalendURLClassLoader cl;
                try {
                    cl = new TalendURLClassLoader(new URL[] { file.getLocation().toFile().toURI().toURL() });
                    Class<?> theClass = cl.findClass(className);
                    if (theClass != null) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    Log.error(e1.getStackTrace());
                } catch (ClassNotFoundException e1) {
                    Log.error(e1.getStackTrace());
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * DOC klliu Comment method "checkIsHaveSqlExcepretion".
     * 
     * @return
     */
    private boolean checkIsHaveSqlExcepretion() {
        EList<TdExpression> expression = definition.getSqlGenericExpression();
        if (!expression.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * DOC klliu Comment method "checkIsHavaJavaComb".
     * 
     * @return
     */
    private boolean checkIsHaveJavaComb() {
        Iterator<CCombo> it = tempExpressionMap.keySet().iterator();
        while (it.hasNext()) {
            CCombo cb = it.next();
            // MOD MOD mzhao feature 11128 Be able to add Java UDI, 2010-01-28
            if (cb.getText().equals(PatternLanguageType.JAVA.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC klliu Comment method "volidateNameAndPath".
     * 
     * @param className
     * @param jarPath
     */
    private void volidateNameAndPath(String className, String jarPath, EList<TaggedValue> tvs) {
        if (className == null || jarPath == null) {
            for (TaggedValue tv : tvs) {
                if (tv.getTag().equals(TaggedValueHelper.CLASS_NAME_TEXT)) {
                    this.setClassNameForSave(tv.getValue());
                    continue;
                }
                if (tv.getTag().equals(TaggedValueHelper.JAR_FILE_PATH)) {
                    this.setJarPathForSave(tv.getValue());
                }
            }
        }

    }

    /**
     * DOC xqliu Comment method "checkBeforeSave". ADD xqliu 2010-02-25 feature 11201
     * 
     * @return
     */
    private ReturnCode checkBeforeSave() {
        ReturnCode rc = new ReturnCode();

        // Added TDQ-7474 20131213 yyin
        // check the size of the tempExpressionMap, if size=0(means no expression), return false
        if (tempExpressionMap.size() == 0) {
            rc.setOk(false);
            rc.setMessage(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.validateNoExpression"));//$NON-NLS-1$
            return rc;
        }// ~

        Map<String, Integer> languageVersionCountMap = new HashMap<String, Integer>();
        Iterator<CCombo> it = tempExpressionMap.keySet().iterator();
        while (it.hasNext()) {
            CCombo cb = it.next();
            TdExpression expression = tempExpressionMap.get(cb);
            String language = expression.getLanguage();
            String version = expression.getVersion();
            if (!language.equals(PatternLanguageType.JAVA.getLiteral())) {
                String body = expression.getBody();
                if (null == body || body.length() + 1 < UDIHelper.MIN_EXPRESSION_LENGTH) {
                    rc.setOk(false);
                    rc.setMessage(DefaultMessagesImpl.getString("IndicatorDefinition.validateTooShort"));//$NON-NLS-1$
                    return rc;
                }
            }
            if (version != null && !PluginConstant.EMPTY_STRING.equals(version)) {
                language = language + " V" + expression.getVersion();//$NON-NLS-1$
            }
            Integer integer = languageVersionCountMap.get(language);
            if (integer == null) {
                languageVersionCountMap.put(language, 1);
            } else {
                languageVersionCountMap.put(language, integer + 1);
            }
        }

        Iterator<String> iterator = languageVersionCountMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Integer integer = languageVersionCountMap.get(key);
            if (integer > 1) {
                rc.setOk(false);
                rc.setMessage(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.isRepeated", key));//$NON-NLS-1$
                return rc;
            }
        }

        if (tempParameters != null) {
            // detecting the IndicatorDefinitionParameter whether include duplicate keywords
            Map<String, Integer> paraMap = new HashMap<String, Integer>();
            for (IndicatorDefinitionParameter para : tempParameters) {
                String key = para.getKey();
                Integer keyCount = paraMap.get(key);
                if (keyCount == null) {
                    paraMap.put(key, Integer.valueOf(1));
                } else {
                    paraMap.put(key, Integer.valueOf(keyCount.intValue() + 1));
                }
            }

            if (paraMap.size() != tempParameters.size()) {
                StringBuffer duplicateKeywords = new StringBuffer();
                for (String key : paraMap.keySet()) {
                    Integer value = paraMap.get(key);
                    if (value.intValue() > 1) {
                        duplicateKeywords.append("\n" + key); //$NON-NLS-1$
                    }
                }
                rc.setOk(false);
                rc.setMessage(DefaultMessagesImpl.getString(
                        "IndicatorDefinitionMaterPage.includeDuplicateKeywords", duplicateKeywords.toString()));//$NON-NLS-1$
                return rc;
            }

            // detecting the IndicatorDefinitionParameter whether include special characters
            for (IndicatorDefinitionParameter para : tempParameters) {
                String key = para.getKey();
                String value = para.getValue();

                if (!StringUtils.isBlank(key)
                        && (key.indexOf(UDIHelper.PARA_SEPARATE_1) > -1 || key.indexOf(UDIHelper.PARA_SEPARATE_2) > -1)) {
                    rc.setOk(false);
                    rc.setMessage(DefaultMessagesImpl
                            .getString(
                                    "IndicatorDefinitionMaterPage.includeSpecialCharacter", "\n" + UDIHelper.PARA_SEPARATE_1 + "\n" + UDIHelper.PARA_SEPARATE_2));//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    return rc;
                }

                if (!StringUtils.isBlank(value)
                        && (value.indexOf(UDIHelper.PARA_SEPARATE_1) > -1 || value.indexOf(UDIHelper.PARA_SEPARATE_2) > -1)) {
                    rc.setOk(false);
                    rc.setMessage(DefaultMessagesImpl
                            .getString(
                                    "IndicatorDefinitionMaterPage.includeSpecialCharacter", "\n" + UDIHelper.PARA_SEPARATE_1 + "\n" + UDIHelper.PARA_SEPARATE_2));//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    return rc;
                }
            }
        }

        return rc;
    }

    /**
     * 
     * DOC klliu Comment method "checkCComboIsDisposed". if CCombo is disposed,remove it from tempExpressionMap.
     */
    private void checkCComboIsDisposed() {
        Iterator<CCombo> it = tempExpressionMap.keySet().iterator();
        List<CCombo> disposedKey = new ArrayList<CCombo>();
        while (it.hasNext()) {
            CCombo cb = it.next();
            if (cb.isDisposed()) {
                disposedKey.add(cb);
            }
        }
        for (CCombo combo : disposedKey) {
            removeFromTempMap(combo);
        }
    }

    /**
     * DOC bZhou Comment method "checkMappingString".
     * 
     * @param c
     * @param r
     * @return
     */
    private boolean checkMappingString(String c, String r) {
        return !PluginConstant.EMPTY_STRING.equals(c) && !PluginConstant.EMPTY_STRING.equals(r) && c.length() == r.length();
    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    private static final class AggregateDateExpression implements Cloneable {

        private TdExpression aggregateExpression;

        private TdExpression dateExpression;

        public TdExpression getAggregateExpression() {
            return aggregateExpression;
        }

        public void setAggregateExpression(TdExpression aggregateExpression) {
            this.aggregateExpression = aggregateExpression;
        }

        public TdExpression getDateExpression() {
            return dateExpression;
        }

        public void setDateExpression(TdExpression dateExpression) {
            this.dateExpression = dateExpression;
        }

        public AggregateDateExpression() {
        }

        public void setLanguage(String language) {
            if (aggregateExpression != null) {
                aggregateExpression.setLanguage(language);
            }
            if (dateExpression != null) {
                dateExpression.setLanguage(language);
            }
        }

        public String getLanguage() {
            if (aggregateExpression != null) {
                return aggregateExpression.getLanguage();
            }
            if (dateExpression != null) {
                return dateExpression.getLanguage();
            }
            return PluginConstant.EMPTY_STRING;
        }

        public void setAggregateBody(String body) {
            if (aggregateExpression != null) {
                aggregateExpression.setBody(body);
            }
        }

        public String getAggregateBody() {
            if (aggregateExpression != null) {
                return aggregateExpression.getBody();
            }
            return PluginConstant.EMPTY_STRING;
        }

        public void setDateBody(String body) {
            if (dateExpression != null) {
                dateExpression.setBody(body);
            }
        }

        public String getDateBody() {
            if (dateExpression != null) {
                return dateExpression.getBody();
            }
            return PluginConstant.EMPTY_STRING;
        }

        public boolean haveAggregateExpression() {
            return aggregateExpression != null;
        }

        public boolean haveDateExpression() {
            return dateExpression != null;
        }

        @Override
        protected AggregateDateExpression clone() {
            AggregateDateExpression ade = null;
            try {
                ade = (AggregateDateExpression) super.clone();
                if (getAggregateExpression() != null) {
                    ade.setAggregateExpression(BooleanExpressionHelper.createTdExpression(getAggregateExpression().getLanguage(),
                            getAggregateExpression().getBody()));
                }
                if (getDateExpression() != null) {
                    ade.setDateExpression(BooleanExpressionHelper.createTdExpression(getDateExpression().getLanguage(),
                            getDateExpression().getBody()));
                }
            } catch (CloneNotSupportedException e) {
                Log.error(e.getMessage());
            }
            return ade;
        }

        public void setModificationDate(String dateValue) {
            if (aggregateExpression != null) {
                aggregateExpression.setModificationDate(dateValue);
            }
            if (dateExpression != null) {
                dateExpression.setModificationDate(dateValue);
            }
        }

        public String getModificationDate() {
            if (aggregateExpression != null) {
                return aggregateExpression.getModificationDate();
            }
            if (dateExpression != null) {
                return dateExpression.getModificationDate();
            }
            return null;
        }
    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    private final class AggregateVO {

        String horizontalAxis, verticalAxis, bubbleSize;

        TdExpression aggreagetExpression;

        public String getHorizontalAxis() {
            return horizontalAxis;
        }

        public void setHorizontalAxis(String horizontalAxis) {
            this.horizontalAxis = horizontalAxis;
            updateAggregateExpressionBody();
        }

        public String getVerticalAxis() {
            return verticalAxis;
        }

        public void setVerticalAxis(String verticalAxis) {
            this.verticalAxis = verticalAxis;
            updateAggregateExpressionBody();
        }

        public String getBubbleSize() {
            return bubbleSize;
        }

        public void setBubbleSize(String bubbleSize) {
            this.bubbleSize = bubbleSize;
            updateAggregateExpressionBody();
        }

        private void updateAggregateExpressionBody() {
            if (this.aggreagetExpression != null) {
                if (PluginConstant.EMPTY_STRING.equals(this.getHorizontalAxis())
                        && PluginConstant.EMPTY_STRING.equals(this.getVerticalAxis())
                        && PluginConstant.EMPTY_STRING.equals(this.getBubbleSize())) {
                    this.aggreagetExpression.setBody(PluginConstant.EMPTY_STRING);
                } else {
                    this.aggreagetExpression.setBody(this.getHorizontalAxis() + ADDITIONAL_FUNCTIONS_SPLIT
                            + this.getVerticalAxis() + ADDITIONAL_FUNCTIONS_SPLIT + this.getBubbleSize());
                }
            }
        }

        public AggregateVO(TdExpression aggregateExpression) {
            this.aggreagetExpression = aggregateExpression;
            if (this.aggreagetExpression != null) {
                String body = this.aggreagetExpression.getBody();
                if (body != null) {
                    String[] values = body.split(ADDITIONAL_FUNCTIONS_SPLIT);
                    if (values.length > 2) {
                        this.setHorizontalAxis(values[0]);
                        this.setVerticalAxis(values[1]);
                        this.setBubbleSize(values[2]);
                    } else {
                        this.setHorizontalAxis(PluginConstant.EMPTY_STRING);
                        this.setVerticalAxis(PluginConstant.EMPTY_STRING);
                        this.setBubbleSize(PluginConstant.EMPTY_STRING);
                    }
                }
            }
        }

        public String getBody() {
            return this.aggreagetExpression == null ? PluginConstant.EMPTY_STRING
                    : this.aggreagetExpression.getBody() == null ? PluginConstant.EMPTY_STRING : this.aggreagetExpression
                            .getBody();
        }

        public String getLanguage() {
            return this.aggreagetExpression == null ? PluginConstant.EMPTY_STRING
                    : this.aggreagetExpression.getLanguage() == null ? PluginConstant.EMPTY_STRING : this.aggreagetExpression
                            .getLanguage();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof AggregateVO) {
                AggregateVO vo = (AggregateVO) obj;
                return this.getBody().equals(vo.getBody()) && this.getLanguage().equals(vo.getLanguage());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.getLanguage().concat(this.getBody()).hashCode();
        }

        public String getModificationDate() {
            return aggreagetExpression == null ? null : aggreagetExpression.getModificationDate();
        }

        public void setModificationDate(String dateValue) {
            if (aggreagetExpression != null) {
                aggreagetExpression.setModificationDate(dateValue);
            }
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private final class AggregateCellModifier implements ICellModifier {

        private List<String> columeNames;

        private TableViewer tableViewer;

        public AggregateCellModifier(String[] columeNames, TableViewer tableViewer) {
            super();
            this.columeNames = new ArrayList<String>();
            for (String columnName : columeNames) {
                this.columeNames.add(columnName);
            }
            this.tableViewer = tableViewer;
        }

        public boolean canModify(Object element, String property) {
            return true;
        }

        public Object getValue(Object element, String property) {
            int columnIndex = columeNames.indexOf(property);

            Object result = null;
            AggregateVO vo = (AggregateVO) element;

            switch (columnIndex) {
            case 0: // Horizontal Axis
                result = vo.getHorizontalAxis();
                break;
            case 1: // Vertical Axis
                result = vo.getVerticalAxis();
                break;
            case 2: // Bubble Size
                result = vo.getBubbleSize();
                break;
            default:
                result = PluginConstant.EMPTY_STRING;
            }
            return result;
        }

        public void modify(Object element, String property, Object value) {
            int columnIndex = this.columeNames.indexOf(property);

            Table table = (Table) element;
            AggregateVO vo = (AggregateVO) table.getItem(0).getData();
            String valueString = ((String) value).trim();

            switch (columnIndex) {
            case 0: // Horizontal Axis
                vo.setHorizontalAxis(valueString);
                break;
            case 1: // Vertical Axis
                vo.setVerticalAxis(valueString);
                break;
            case 2: // Bubble Size
                vo.setBubbleSize(valueString);
                break;
            default:
            }
            tableViewer.setInput(vo);
            IndicatorDefinitionMaterPage.this.setDirty(true);
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    private final class CommonContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            Object[] results = new Object[1];
            results[0] = inputElement;
            return results;
        }

        public void dispose() {
            // until now. do nothing
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // until now. do nothing
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    private class AggregateLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String result = PluginConstant.EMPTY_STRING;
            AggregateVO vo = (AggregateVO) element;
            switch (columnIndex) {
            case 0:
                result = vo.getHorizontalAxis();
                break;
            case 1:
                result = vo.getVerticalAxis();
                break;
            case 2:
                result = vo.getBubbleSize();
                break;
            default:
                break;
            }
            return result;
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    private final class DateVO {

        String lowerValue, upperValue, total, highlightedValues;

        TdExpression dateExpression;

        public String getLowerValue() {
            return lowerValue;
        }

        public void setLowerValue(String lowerValue) {
            this.lowerValue = lowerValue;
            updateDateExpressionBody();
        }

        public String getUpperValue() {
            return upperValue;
        }

        public void setUpperValue(String upperValue) {
            this.upperValue = upperValue;
            updateDateExpressionBody();
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
            updateDateExpressionBody();
        }

        public String getHighlightedValues() {
            return highlightedValues;
        }

        public void setHighlightedValues(String highlightedValues) {
            this.highlightedValues = highlightedValues;
            updateDateExpressionBody();
        }

        private void updateDateExpressionBody() {
            if (this.dateExpression != null) {
                if (PluginConstant.EMPTY_STRING.equals(this.getLowerValue())
                        && PluginConstant.EMPTY_STRING.equals(this.getUpperValue())
                        && PluginConstant.EMPTY_STRING.equals(this.getTotal())
                        && PluginConstant.EMPTY_STRING.equals(this.getHighlightedValues())) {
                    this.dateExpression.setBody(PluginConstant.EMPTY_STRING);
                } else {
                    this.dateExpression.setBody(this.getLowerValue() + ADDITIONAL_FUNCTIONS_SPLIT + this.getUpperValue()
                            + ADDITIONAL_FUNCTIONS_SPLIT + this.getTotal() + ADDITIONAL_FUNCTIONS_SPLIT
                            + this.getHighlightedValues());
                }
            }
        }

        public DateVO(TdExpression dateExpression) {
            this.dateExpression = dateExpression;
            if (this.dateExpression != null) {
                String body = this.dateExpression.getBody();
                if (body != null) {
                    String[] values = body.split(ADDITIONAL_FUNCTIONS_SPLIT);
                    if (values.length > 3) {
                        this.setLowerValue(values[0]);
                        this.setUpperValue(values[1]);
                        this.setTotal(values[2]);
                        this.setHighlightedValues(values[3]);
                    } else {
                        this.setLowerValue(PluginConstant.EMPTY_STRING);
                        this.setUpperValue(PluginConstant.EMPTY_STRING);
                        this.setTotal(PluginConstant.EMPTY_STRING);
                        this.setHighlightedValues(PluginConstant.EMPTY_STRING);
                    }
                }
            }
        }

        public String getBody() {
            return this.dateExpression == null ? PluginConstant.EMPTY_STRING
                    : this.dateExpression.getBody() == null ? PluginConstant.EMPTY_STRING : this.dateExpression.getBody();
        }

        public String getLanguage() {
            return this.dateExpression == null ? PluginConstant.EMPTY_STRING
                    : this.dateExpression.getLanguage() == null ? PluginConstant.EMPTY_STRING : this.dateExpression.getLanguage();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof DateVO) {
                DateVO vo = (DateVO) obj;
                return this.getBody().equals(vo.getBody()) && this.getLanguage().equals(vo.getLanguage());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.getLanguage().concat(this.getBody()).hashCode();
        }

        public void setModificationDate(String dateValue) {
            if (dateExpression != null) {
                dateExpression.setModificationDate(dateValue);
            }
        }

        public String getModificationDate() {
            return dateExpression == null ? null : dateExpression.getModificationDate();
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private final class DateCellModifier implements ICellModifier {

        private List<String> columeNames;

        private TableViewer tableViewer;

        public DateCellModifier(String[] columeNames, TableViewer tableViewer) {
            super();
            this.columeNames = new ArrayList<String>();
            for (String columnName : columeNames) {
                this.columeNames.add(columnName);
            }
            this.tableViewer = tableViewer;
        }

        public boolean canModify(Object element, String property) {
            return true;
        }

        public Object getValue(Object element, String property) {
            int columnIndex = columeNames.indexOf(property);

            Object result = null;
            DateVO vo = (DateVO) element;

            switch (columnIndex) {
            case 0: // Lower Value
                result = vo.getLowerValue();
                break;
            case 1: // Upper Value
                result = vo.getUpperValue();
                break;
            case 2: // Total
                result = vo.getTotal();
                break;
            case 3: // Highlighted Values
                result = vo.getHighlightedValues();
                break;
            default:
                result = PluginConstant.EMPTY_STRING;
            }
            return result;
        }

        public void modify(Object element, String property, Object value) {
            int columnIndex = this.columeNames.indexOf(property);

            Table table = (Table) element;
            DateVO vo = (DateVO) table.getItem(0).getData();
            String valueString = ((String) value).trim();

            switch (columnIndex) {
            case 0: // Lower Value
                vo.setLowerValue(valueString);
                break;
            case 1: // Upper Value
                vo.setUpperValue(valueString);
                break;
            case 2: // Total
                vo.setTotal(valueString);
                break;
            case 3: // Highlighted Values
                vo.setHighlightedValues(valueString);
                break;
            default:
            }
            tableViewer.setInput(vo);
            IndicatorDefinitionMaterPage.this.setDirty(true);
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    private class DateLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String result = PluginConstant.EMPTY_STRING;
            DateVO vo = (DateVO) element;
            switch (columnIndex) {
            case 0:
                result = vo.getLowerValue();
                break;
            case 1:
                result = vo.getUpperValue();
                break;
            case 2:
                result = vo.getTotal();
                break;
            case 3:
                result = vo.getHighlightedValues();
                break;
            default:
                break;
            }
            return result;
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private final class CharactersMappingCellModifier implements ICellModifier {

        private List<String> columeNames;

        private TableViewer tableViewer;

        public CharactersMappingCellModifier(String[] columeNames, TableViewer tableViewer) {
            super();
            this.columeNames = new ArrayList<String>();
            for (String columnName : columeNames) {
                this.columeNames.add(columnName);
            }
            this.tableViewer = tableViewer;
        }

        public boolean canModify(Object element, String property) {
            return true;
        }

        public Object getValue(Object element, String property) {
            int columnIndex = columeNames.indexOf(property);

            Object result = null;
            CharactersMapping cm = (CharactersMapping) element;

            switch (columnIndex) {
            case 0:
                result = cm.getCharactersToReplace();
                break;
            case 1:
                result = cm.getReplacementCharacters();
                break;
            default:
                result = PluginConstant.EMPTY_STRING;
            }
            return result;
        }

        public void modify(Object element, String property, Object value) {
            int columnIndex = this.columeNames.indexOf(property);

            Table table = (Table) element;
            CharactersMapping cm = (CharactersMapping) table.getItem(0).getData();
            String valueString = ((String) value).trim();

            switch (columnIndex) {
            case 0:
                cm.setCharactersToReplace(valueString);
                break;
            case 1:
                cm.setReplacementCharacters(valueString);
                break;
            default:
            }
            tableViewer.setInput(cm);
            IndicatorDefinitionMaterPage.this.setDirty(true);
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    private class CharactersMappingLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String result = PluginConstant.EMPTY_STRING;
            CharactersMapping cm = (CharactersMapping) element;
            switch (columnIndex) {
            case 0:
                result = cm.getCharactersToReplace();
                break;
            case 1:
                result = cm.getReplacementCharacters();
                break;
            default:
                break;
            }
            return result;
        }

    }

    /**
     * clone Expression.
     * 
     * @param exp
     * @return
     */
    public static final TdExpression cloneExpression(TdExpression exp) {
        if (exp == null) {
            return null;
        }
        TdExpression copy = EcoreUtil.copy(exp);
        HashMap<String, String> expressionVariableMap = exp.getExpressionVariableMap();
        if (expressionVariableMap != null) {
            copy.setExpressionVariableMap((HashMap<String, String>) expressionVariableMap.clone());
        }
        return copy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#canSave()
     */
    @Override
    public ReturnCode canSave() {
        ReturnCode rc = canModifyName(ERepositoryObjectType.TDQ_INDICATOR_ELEMENT);
        if (!rc.isOk()) {
            MessageDialogWithToggle.openError(null,
                    DefaultMessagesImpl.getString("AbstractMetadataFormPage.saveFailed"), rc.getMessage()); //$NON-NLS-1$
        }
        return rc;
    }
}
