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
package org.talend.dataprofiler.core.ui.editor.indicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.PatternLanguageType;
import org.talend.dataprofiler.core.ui.dialog.ExpressionEditDialog;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.definition.CharactersMapping;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.PluginConstant;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorDefinitionMaterPage extends AbstractMetadataFormPage {

    private static final String ADDITIONAL_FUNCTIONS_SPLIT = ";"; //$NON-NLS-1$

    private Section definitionSection;

    private Composite definitionComp;

    private Composite categoryComp;

    private List<String> allDBTypeList;

    // MOD xqliu 2010-03-23 feature 11201
    // private List<String> remainDBTypeList;

    private Map<CCombo, Expression> tempExpressionMap;

    private Map<CCombo, Composite> widgetMap;

    private Composite expressionComp;

    private Combo comboCategory;

    private Label labelDetail;

    private IndicatorDefinition definition;

    private IndicatorCategory category;

    private Section additionalFunctionsSection;

    private Composite additionalFunctionsComp;

    private Composite afExpressionComp;

    private Map<String, AggregateDateExpression> afExpressionMap, afExpressionMapTemp;

    private List<String> remainDBTypeListAF;

    private boolean hasAggregateExpression, hasDateExpression, hasCharactersMapping;

    private boolean systemIndicator;

    // ADD xqliu 2010-03-23 feature 11201
    private List<Expression> tempExpressionList;

    public boolean isSystemIndicator() {
        return systemIndicator;
    }

    public void setSystemIndicator(boolean systemIndicator) {
        this.systemIndicator = systemIndicator;
    }

    private static final String BODY_AGGREGATE = "AVG({0});COUNT({0});SUM(CASE WHEN {0} IS NULL THEN 1 ELSE 0 END)"; //$NON-NLS-1$

    private static final String BODY_DATE = "MIN({0});MAX({0});COUNT({0});SUM(CASE WHEN {0} IS NULL THEN 1 ELSE 0 END)"; //$NON-NLS-1$

    private Section charactersMappingSection;

    private Section categorySection;

    private Composite charactersMappingComp;

    private Composite charactersMappingLineComp;

    private Map<String, CharactersMapping> charactersMappingMap, charactersMappingMapTemp;

    private List<String> remainDBTypeListCM;

    private static final String BODY_CHARACTERS_TO_REPLACE = "abcdefghijklmnopqrstuvwxyzçâêîôûéèùïöüABCDE"
            + "FGHIJKLMNOPQRSTUVWXYZÇÂÊÎÔÛÉÈÙÏÖÜ0123456789"; //$NON-NLS-1$

    private static final String BODY_REPLACEMENT_CHARACTERS = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAA"
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
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor
     * )
     */
    @Override
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        String[] supportTypes = PatternLanguageType.getAllLanguageTypes();
        allDBTypeList = new ArrayList<String>();
        allDBTypeList.addAll(Arrays.asList(supportTypes));

        // MOD xqliu 2010-03-23 feature 11201
        // remainDBTypeList = new ArrayList<String>();
        // remainDBTypeList.addAll(allDBTypeList);

        remainDBTypeListAF = new ArrayList<String>();
        remainDBTypeListAF.addAll(allDBTypeList);

        remainDBTypeListCM = new ArrayList<String>();
        remainDBTypeListCM.addAll(allDBTypeList);

        // MOD xqliu 2010-03-23 feature 11201
        initTempExpressionMap();

        if (widgetMap == null) {
            widgetMap = new HashMap<CCombo, Composite>();
        } else {
            widgetMap.clear();
        }

        // initialize user defined indicator category
        definition = (IndicatorDefinition) getCurrentModelElement(getEditor());
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

        // initialize indicator type
        systemIndicator = this.getEditor().getEditorInput() instanceof IndicatorEditorInput;

        afExpressionMap = new HashMap<String, AggregateDateExpression>();
        afExpressionMapTemp = new HashMap<String, AggregateDateExpression>();

        charactersMappingMap = new HashMap<String, CharactersMapping>();
        charactersMappingMapTemp = new HashMap<String, CharactersMapping>();

        // ADD xqliu 2010-03-23 feature 11201
        initTempExpressionList(definition);
    }

    /**
     * DOC xqliu Comment method "initTempExpressionMap". ADD xqliu 2010-03-23 feature 11201
     */
    private void initTempExpressionMap() {
        if (tempExpressionMap == null) {
            tempExpressionMap = new HashMap<CCombo, Expression>();
        } else {
            tempExpressionMap.clear();
        }
    }

    /**
     * DOC xqliu Comment method "initTempExpressionList". ADD xqliu 2010-03-23 feature 11201
     */
    private void initTempExpressionList(IndicatorDefinition definition) {
        if (tempExpressionList == null) {
            tempExpressionList = new ArrayList<Expression>();
        } else {
            tempExpressionList.clear();
        }
        if (definition != null) {
            EList<Expression> expressions = definition.getSqlGenericExpression();
            for (Expression exp : expressions) {
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

        createDefinitionSection(topComp);

        if (isSystemIndicator()) {
            if (IndicatorCategoryHelper.isCorrelation(category)) {
                createAdditionalFunctionsSection(topComp);
            }
            if (this.hasCharactersMapping) {
                createCharactersMappingSection(topComp);
            }
        } else {
            createCategorySection(topComp);
        }
    }

    /**
     * DOC xqliu Comment method "createCharactersMappingSection".
     * 
     * @param topComp
     */
    private void createCharactersMappingSection(Composite topComp) {
        charactersMappingSection = createSection(form, topComp, "Character mapping", null);

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

            public void widgetSelected(SelectionEvent e) {
                remainDBTypeListCM.clear();
                remainDBTypeListCM.addAll(allDBTypeList);
                for (CharactersMapping cm : charactersMappingMapTemp.values()) {
                    String language = cm.getLanguage();
                    String languageName = PatternLanguageType.findNameByLanguage(language);
                    remainDBTypeListCM.remove(languageName);
                }
                if (remainDBTypeListCM.size() == 0) {
                    MessageDialog
                            .openWarning(
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
        String[] headers = { "Characters to replace", "Replacement characters" };
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
        additionalFunctionsSection = createSection(form, topComp, "Additional Functions", null);

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
            EList<Expression> aggregate1argFunctions = definition.getAggregate1argFunctions();
            EList<Expression> date1argFunctions = definition.getDate1argFunctions();

            if (aggregate1argFunctions != null && aggregate1argFunctions.size() > 0) {
                for (Expression expression : aggregate1argFunctions) {
                    recordAFExpression(afExpressionMap, expression, null);
                }
            }

            if (date1argFunctions != null && date1argFunctions.size() > 0) {
                for (Expression expression : date1argFunctions) {
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

            public void widgetSelected(SelectionEvent e) {
                String lang = combo.getText();
                aggregateDateExpression.setLanguage(PatternLanguageType.findLanguageByName(lang));
                setDirty(true);
            }
        });

        final Composite expressionBodyComp = new Composite(expressComp, SWT.NONE);
        expressionBodyComp.setLayout(new GridLayout(1, false));
        expressionBodyComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

        if (aggregateDateExpression.haveAggregateExpression()) {
            String title = "bubble chart functions";
            String[] headers = { "horizontal axis", "vertical axis", "bubble size" };
            int[] widths = { 120, 120, 240 };
            buildAggregateDateComp(expressionBodyComp, aggregateDateExpression, style, title, headers, widths);
        }

        if (aggregateDateExpression.haveDateExpression()) {
            String title = "gantt chart functions";
            String[] headers = { "lower value", "upper value", "total", "highlighted values" };
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
    private void recordAFExpression(Map<String, AggregateDateExpression> expressionMap, Expression aggregateExpression,
            Expression dateExpression) {
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

            public void widgetSelected(SelectionEvent e) {
                remainDBTypeListAF.clear();
                remainDBTypeListAF.addAll(allDBTypeList);
                for (AggregateDateExpression ade : afExpressionMapTemp.values()) {
                    String language = ade.getLanguage();
                    String languageName = PatternLanguageType.findNameByLanguage(language);
                    remainDBTypeListAF.remove(languageName);
                }
                if (remainDBTypeListAF.size() == 0) {
                    MessageDialog
                            .openWarning(
                                    null,
                                    DefaultMessagesImpl.getString("PatternMasterDetailsPage.warning"), DefaultMessagesImpl.getString("PatternMasterDetailsPage.patternExpression")); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                String language = PatternLanguageType.findLanguageByName(remainDBTypeListAF.get(0));
                Expression expression = BooleanExpressionHelper.createExpression(language, ""); //$NON-NLS-1$
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
        categorySection = createSection(form, topComp, "Indicator Category", null);

        Label label = new Label(categorySection, SWT.WRAP);
        label.setText("This section is for indicator category.");
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
        comboCategory.setText(category.getLabel());
        comboCategory.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                UDIHelper.setUDICategory(definition, comboCategory.getText());
                updateDetailList();
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
        if (!"".equals(comboCategory.getText())) { //$NON-NLS-1$
            IndicatorCategory ic = UDIHelper.getUDICategory(definition);
            String purposeText = "";
            String descriptionText = "";
            for (TaggedValue value : ic.getTaggedValue()) {
                if ("Purpose".equals(value.getTag())) {
                    purposeText = DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.Purpose") + value.getValue();
                } else if (DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.Descript").equals(value.getTag())) { //$NON-NLS-1$
                    descriptionText = DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.Description")
                            + value.getValue();
                }
            }
            labelDetail.setText(purposeText + System.getProperty("line.separator") + System.getProperty("line.separator")
                    + descriptionText);
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
        data.heightHint = 300;
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

        // ADD xqliu 2010-02-26 bug 11201
        addTitleComp(expressionComp);
        // ~11201

        if (tempExpressionMap.size() == 0) {
            if (definition != null) {
                // MOD xqliu 2010-03-23 feature 11201
                // EList<Expression> expList = definition.getSqlGenericExpression();
                // for (Expression expression : expList) {
                for (Expression expression : tempExpressionList) {
                    createNewLineWithExpression(expression);
                }
                // ~11201
                // Whether Java UDI exists.
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
    private Composite addTitleComp(Composite expressionComp) {
        final Composite titleComp = new Composite(expressionComp, SWT.NONE);
        titleComp.setLayout(new GridLayout(3, false));
        Label databaseLabel = new Label(titleComp, SWT.NONE);
        databaseLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.database"));
        databaseLabel.setLayoutData(new GridData());
        ((GridData) databaseLabel.getLayoutData()).widthHint = 150;
        Label dbversionLabel = new Label(titleComp, SWT.NONE);
        dbversionLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.dbVersion"));
        dbversionLabel.setLayoutData(new GridData(GridData.BEGINNING));
        ((GridData) dbversionLabel.getLayoutData()).widthHint = 60;
        Label sqlTemplateLabel = new Label(titleComp, SWT.NONE);
        sqlTemplateLabel.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.sqlTemplate"));
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(titleComp);
        return titleComp;
    }

    private void createNewLineWithJavaUDI() {
        EList<TaggedValue> tvs = definition.getTaggedValue();
        String classNameStr = null;
        String jarPathStr = "";
        for (TaggedValue tv : tvs) {
            if (tv.getTag().equals(PluginConstant.CLASS_NAME_TEXT)) {
                classNameStr = tv.getValue();
                continue;
            }
            if (tv.getTag().equals(PluginConstant.JAR_FILE_PATH)) {
                jarPathStr = tv.getValue();
            }
        }
        if (classNameStr == null) {
            return;
        }

        final Composite lineComp = new Composite(expressionComp, SWT.NONE);
        lineComp.setLayout(new GridLayout(2, false));
        final CCombo combo = new CCombo(lineComp, SWT.BORDER);
        combo.setLayoutData(new GridData());
        ((GridData) combo.getLayoutData()).widthHint = 150;
        combo.setEditable(false);
        // MOD xqliu 2010-03-23 feature 11201
        // combo.setItems(remainDBTypeList.toArray(new String[remainDBTypeList.size()]));
        combo.setItems(allDBTypeList.toArray(new String[allDBTypeList.size()]));
        // ~11201
        combo.setText(PatternLanguageType.JAVA.getName());
        combo.addSelectionListener(new LangCombSelectionListener());
        tempExpressionMap.put(combo, BooleanExpressionHelper.createExpression(combo.getText(), null));

        Composite detailComp = new Composite(combo.getParent(), SWT.NONE);
        widgetMap.put(combo, detailComp);
        detailComp.setLayout(new GridLayout(4, false));
        Text classNameText = new Text(detailComp, SWT.BORDER);
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
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(combo.getParent().getShell(), SWT.NONE);
                dialog.setFilterExtensions(new String[] { "*.jar" }); //$NON-NLS-1$
                String path = dialog.open();
                if (path != null) {
                    jarPathText.setText(path);
                }
            }
        });
        combo.setData(PluginConstant.CLASS_NAME_TEXT, classNameText);
        combo.setData(PluginConstant.JAR_FILE_PATH, jarPathText);

        createExpressionDelButton(detailComp, combo);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(detailComp);
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

    private void createNewLineWithExpression(final Expression expression) {
        final Composite lineComp = new Composite(expressionComp, SWT.NONE);
        lineComp.setLayout(new GridLayout(3, false));
        final CCombo combo = new CCombo(lineComp, SWT.BORDER);
        tempExpressionMap.put(combo, expression);
        combo.setLayoutData(new GridData());
        ((GridData) combo.getLayoutData()).widthHint = 150;
        combo.setEditable(false);
        // MOD xqliu 2010-02-25 feature 11201
        // combo.setItems(remainDBTypeList.toArray(new String[remainDBTypeList.size()]));
        combo.setItems(allDBTypeList.toArray(new String[allDBTypeList.size()]));
        // ~
        String language = expression.getLanguage();
        String body = expression.getBody();
        // ADD xqliu 2010-02-25 feature 11201
        String version = expression.getVersion();
        // ~

        if (language == null) {
            // MOD xqliu 2010-02-25 feature 11201
            // combo.setText(remainDBTypeList.get(0));
            combo.setText(allDBTypeList.get(0));
            // ~
        } else {
            combo.setText(PatternLanguageType.findNameByLanguage(language));
        }
        combo.addSelectionListener(new LangCombSelectionListener());
        // ADD xqliu 2010-02-25 feature 11201
        createDbVersionText(combo, lineComp, version, 30);
        // ~
        Composite detailComp = new Composite(lineComp, SWT.NONE);
        detailComp.setLayout(new GridLayout(3, false));
        final Text patternText = new Text(detailComp, SWT.BORDER);
        patternText.setText(body == null ? PluginConstant.EMPTY_STRING : body);
        patternText.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridData) patternText.getLayoutData()).widthHint = 600;
        patternText.addModifyListener(new ExpressTextModListener(combo));
        createExpressionEditButton(detailComp, patternText);
        createExpressionDelButton(detailComp, combo);
        widgetMap.put(combo, detailComp);
        updateOtherCombos(combo);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(detailComp);
    }

    /**
     * DOC bZhou Comment method "creatNewLine". MOD mzhao feature 11128 Be able to add Java UDI, 2010-01-27
     * 
     * @param expression
     */
    private void createNewLine() {
        final Composite lineComp = new Composite(expressionComp, SWT.NONE);
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
        // String language = expression.getLanguage();
        // String body = expression.getBody();

        // if (language == null) {
        // combo.setText(remainDBTypeList.get(0));
        // } else {
        // combo.setText(PatternLanguageType.findNameByLanguage(language));
        // }
        combo.addSelectionListener(new LangCombSelectionListener());
        // combo.addSelectionListener(new SelectionAdapter() {
        //
        // public void widgetSelected(SelectionEvent e) {
        // String lang = combo.getText();
        // expression.setLanguage(PatternLanguageType.findLanguageByName(lang));
        // setDirty(true);
        // }
        // });
        // ADD xqliu 2010-02-25 feature 11201
        createDbVersionText(combo, lineComp, null, 30);
        // ~
        Expression expression = BooleanExpressionHelper.createExpression(combo.getText(), null);
        tempExpressionMap.put(combo, expression);
        if (combo.getText().equals(PatternLanguageType.JAVA.getName())) {
            updateLineForJava(combo);
        } else {
            // MOD xqliu 2010-03-23 feature 11201
            updateLineForExpression(combo, expression);
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
            Expression expression = tempExpressionMap.get(combo);
            expression.setBody(patternText.getText());
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
            Text dbVersionText = null;
            if (e.getSource() != null && e.getSource() instanceof Text) {
                dbVersionText = (Text) e.getSource();
            }
            Expression expression = tempExpressionMap.get(this.combo);
            if (expression != null && dbVersionText != null) {
                expression.setVersion(dbVersionText.getText().trim());
            }
            // ~11892
        }

    }

    /**
     * 
     * DOC mzhao IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private class LangCombSelectionListener extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            CCombo combo = (CCombo) e.getSource();
            String lang = combo.getText();
            Expression expression = tempExpressionMap.get(combo);
            if (expression == null) {
                expression = BooleanExpressionHelper.createExpression(lang, null);
                tempExpressionMap.put(combo, expression);
            } else {
                expression.setLanguage(PatternLanguageType.findLanguageByName(lang));
            }
            // MOD xqliu 2010-03-23 feature 11201
            // if (!lang.equals(PatternLanguageType.JAVA.getName())) {
            // // MOD xqliu 2010-03-23 feature 11201
            // updateLineForExpression(combo, expression);
            // // ~11201
            // } else {
            // // Handle java UID.
            // updateLineForJava(combo);
            // }
            // // Update other combos.
            // updateOtherCombos(combo);
            // ~
            setDirty(true);
        }
    }

    /**
     * DOC xqliu Comment method "updateLineForExpression". // MOD xqliu 2010-03-23 feature 11201
     * 
     * @param combo
     * @param expression
     */
    private void updateLineForExpression(final CCombo combo, Expression expression) {
        Composite detailComp = widgetMap.get(combo);
        if (detailComp != null) {
            detailComp.dispose();
        }
        detailComp = new Composite(combo.getParent(), SWT.NONE);
        widgetMap.put(combo, detailComp);
        detailComp.setLayout(new GridLayout(3, false));
        final Text patternText = new Text(detailComp, SWT.BORDER);
        patternText.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridData) patternText.getLayoutData()).widthHint = 600;
        patternText.addModifyListener(new ExpressTextModListener(combo));
        // MOD xqliu 2010-03-23 feature 11201
        patternText.setText(expression.getBody() == null ? "" : expression.getBody());
        // ~11201
        createExpressionEditButton(detailComp, patternText);
        createExpressionDelButton(detailComp, combo);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(detailComp);
        detailComp.getParent().layout();
        definitionSection.setExpanded(false);
        definitionSection.setExpanded(true);
    }

    /**
     * DOC mzhao Comment method "updateLineForJava".
     * 
     * @param combo
     */
    private void updateLineForJava(final CCombo combo) {
        Composite detailComp = widgetMap.get(combo);
        if (detailComp != null) {
            detailComp.dispose();
        }
        detailComp = new Composite(combo.getParent(), SWT.NONE);
        widgetMap.put(combo, detailComp);
        detailComp.setLayout(new GridLayout(4, false));
        Text classNameText = new Text(detailComp, SWT.BORDER);
        classNameText.setLayoutData(new GridData(GridData.FILL_BOTH));
        classNameText.addModifyListener(new NeedToSetDirtyListener());
        ((GridData) classNameText.getLayoutData()).widthHint = 250;
        final Text jarPathText = new Text(detailComp, SWT.BORDER);
        jarPathText.setLayoutData(new GridData(GridData.FILL_BOTH));
        jarPathText.addModifyListener(new NeedToSetDirtyListener());
        ((GridData) jarPathText.getLayoutData()).widthHint = 350;
        Button button = new Button(detailComp, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(combo.getParent().getShell(), SWT.NONE);
                dialog.setFilterExtensions(new String[] { "*.jar" }); //$NON-NLS-1$
                String path = dialog.open();
                if (path != null) {
                    jarPathText.setText(path);
                }
            }
        });
        combo.setData(PluginConstant.CLASS_NAME_TEXT, classNameText);
        combo.setData(PluginConstant.JAR_FILE_PATH, jarPathText);
        createExpressionDelButton(detailComp, combo);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(detailComp);
        definitionSection.setExpanded(false);
        definitionSection.setExpanded(true);
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
            if (combo != cb) {
                String tx = cb.getText();
                // MOD xqliu 2010-03-23 feature 11201
                // cb.setItems(remainDBTypeList.toArray(new String[remainDBTypeList.size()]));
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
        delButton.setToolTipText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.deleteExpression"));
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;
        delButton.setLayoutData(labelGd);
        delButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setDirty(true);
                tempExpressionMap.remove(languageCombo);
                widgetMap.remove(languageCombo);
                expressComp.getParent().dispose();
                definitionSection.setExpanded(false);
                definitionSection.setExpanded(true);
            }
        });

    }

    /**
     * DOC yyi 2009-09-11 Feature:9030
     * 
     * @param expressComp
     * @param patternText
     * 
     * @return
     */
    private void createExpressionEditButton(Composite expressComp, final Text patternText) {
        Button editButton = new Button(expressComp, SWT.PUSH);
        editButton.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        editButton.setToolTipText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        editButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String[] templates = new String[] { "<%=__TABLE_NAME__%>", "<%=__COLUMN_NAMES__%>", "<%=__WHERE_CLAUSE__%>", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        "<%=__GROUP_BY_ALIAS__%>" }; //$NON-NLS-1$
                final ExpressionEditDialog editDialog = new ExpressionEditDialog(null, patternText.getText(), templates);

                if (Dialog.OK == editDialog.open() && !patternText.getText().equals(editDialog.getResult())) {
                    patternText.setText(editDialog.getResult());
                }
            }
        });

    }

    private void createAddButton(final Composite parent) {
        final Button addButton = new Button(parent, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setToolTipText(DefaultMessagesImpl.getString("PatternMasterDetailsPage.add")); //$NON-NLS-1$
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.CENTER;
        labelGd.widthHint = 65;
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            // MOD mzhao feature 11128 Be able to add Java UDI, 2010-01-27
            public void widgetSelected(SelectionEvent e) {
                // MOD xqliu 2010-03-23 feature 11201
                // rebuildRemainDBTypeList();
                // ~11201
                // String language = PatternLanguageType.findLanguageByName(remainDBTypeList.get(0));
                // Expression expression = BooleanExpressionHelper.createExpression(language, null);
                createNewLine();
                // tempExpression.add(expression);
                definitionSection.setExpanded(true);
                setDirty(true);
            }
        });
    }

    // MOD xqliu 2010-03-23 feature 11201
    // /**
    // * DOC xqliu Comment method "rebuildRemainDBTypeList". MOD mzhao feature 11128 Be able to add Java UDI.
    // */
    // private void rebuildRemainDBTypeList() {
    // remainDBTypeList.clear();
    // remainDBTypeList.addAll(allDBTypeList);
    // Collection<Expression> expValues = tempExpressionMap.values();
    // for (Expression expression : expValues) {
    // String language = expression.getLanguage();
    // String languageName = PatternLanguageType.findNameByLanguage(language);
    // remainDBTypeList.remove(languageName);
    // }
    // if (remainDBTypeList.size() == 0) {
    // MessageDialog.openWarning(null,
    // DefaultMessagesImpl.getString("PatternMasterDetailsPage.warning"), DefaultMessagesImpl.getString("PatternMasterDetailsPage.patternExpression")); //$NON-NLS-1$ //$NON-NLS-2$
    // return;
    // }
    // }
    // ~11201

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#getCurrentModelElement(org.eclipse.ui.forms.editor
     * .FormEditor)
     */
    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        if (editor.getEditorInput() instanceof IndicatorEditorInput) {
            IndicatorEditorInput editorInput = (IndicatorEditorInput) editor.getEditorInput();
            return editorInput.getIndicatorDefinition();
        } else if (editor.getEditorInput() instanceof FileEditorInput) {
            FileEditorInput editorInput = (FileEditorInput) editor.getEditorInput();
            return UDIResourceFileHelper.getInstance().findUDI(editorInput.getFile());
        } else {
            return null;
        }
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
        super.doSave(monitor);

        // ADD xqliu 2010-02-25 feature 11201
        if (!checkBeforeSave())
            return;
        // ~

        EList<Expression> expressions = definition.getSqlGenericExpression();
        expressions.clear();
        Iterator<CCombo> it = tempExpressionMap.keySet().iterator();

        CCombo javaUIDCombo = null;
        while (it.hasNext()) {
            CCombo cb = it.next();
            // MOD MOD mzhao feature 11128 Be able to add Java UDI, 2010-01-28
            if (cb.getText().equals(PatternLanguageType.JAVA.getName())) {
                javaUIDCombo = cb;
            } else if (tempExpressionMap.get(cb).getBody() != null
                    && !PluginConstant.EMPTY_STRING.equals(tempExpressionMap.get(cb).getBody())) {
                // MOD xqliu 2010-04-01 bug 11892
                expressions.add(cloneExpression(tempExpressionMap.get(cb)));
                // ~11892
            }
        }

        // Save Java UID
        EList<TaggedValue> tvs = definition.getTaggedValue();
        if (javaUIDCombo != null) {
            boolean isNewTaggedValue = true;
            for (TaggedValue tv : tvs) {
                if (tv.getTag().equals(PluginConstant.CLASS_NAME_TEXT)) {
                    tv.setValue(((Text) javaUIDCombo.getData(PluginConstant.CLASS_NAME_TEXT)).getText());
                    isNewTaggedValue = false;
                    continue;
                }
                if (tv.getTag().equals(PluginConstant.JAR_FILE_PATH)) {
                    tv.setValue(((Text) javaUIDCombo.getData(PluginConstant.JAR_FILE_PATH)).getText());
                }
            }
            if (isNewTaggedValue) {
                TaggedValue classNameTV = TaggedValueHelper.createTaggedValue(PluginConstant.CLASS_NAME_TEXT,
                        ((Text) javaUIDCombo.getData(PluginConstant.CLASS_NAME_TEXT)).getText());
                TaggedValue jarPathTV = TaggedValueHelper.createTaggedValue(PluginConstant.JAR_FILE_PATH, ((Text) javaUIDCombo
                        .getData(PluginConstant.JAR_FILE_PATH)).getText());
                definition.getTaggedValue().add(classNameTV);
                definition.getTaggedValue().add(jarPathTV);
            }
        } else {
            // Remove Java UDI tagged values if there have.
            TaggedValue tvCN = null;
            TaggedValue tvJARP = null;
            for (TaggedValue tv : tvs) {
                if (tv.getTag().equals(PluginConstant.CLASS_NAME_TEXT)) {
                    tvCN = tv;
                    continue;
                }
                if (tv.getTag().equals(PluginConstant.JAR_FILE_PATH)) {
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
            EList<Expression> aggregate1argFunctions = definition.getAggregate1argFunctions();
            aggregate1argFunctions.clear();
            for (AggregateDateExpression ade : afExpressionMapTemp.values()) {
                Expression expression = ade.getAggregateExpression();
                if (expression.getBody() != null && !"".equals(expression.getBody())) { //$NON-NLS-1$
                    aggregate1argFunctions.add(expression);
                }
            }
        }

        if (hasDateExpression) {
            EList<Expression> date1argFunctions = definition.getDate1argFunctions();
            date1argFunctions.clear();
            for (AggregateDateExpression ade : afExpressionMapTemp.values()) {
                Expression expression = ade.getDateExpression();
                if (expression.getBody() != null && !"".equals(expression.getBody())) { //$NON-NLS-1$
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
                    MessageUI.openError("[" + cm.getLanguage()
                            + "] INPUT ERROR:\nThe length of two inputed strings are not equal.");
                    return;
                }
            }
        }

        ReturnCode rc = UDIHelper.validate(definition);
        if (rc.isOk()) {
            // EMFUtil.saveSingleResource(definition.eResource());
            UDIResourceFileHelper.getInstance().save(definition);
            this.isDirty = false;
        } else {
            MessageDialog.openError(null, "error", rc.getMessage());
        }
    }

    /**
     * DOC xqliu Comment method "checkBeforeSave". ADD xqliu 2010-02-25 feature 11201
     * 
     * @return
     */
    private boolean checkBeforeSave() {
        Map<String, Integer> languageVersionCountMap = new HashMap<String, Integer>();
        Iterator<CCombo> it = tempExpressionMap.keySet().iterator();
        while (it.hasNext()) {
            CCombo cb = it.next();
            if (tempExpressionMap.get(cb).getBody() != null
                    && !PluginConstant.EMPTY_STRING.equals(tempExpressionMap.get(cb).getBody())) {
                Expression expression = tempExpressionMap.get(cb);
                String language = expression.getLanguage();
                String version = expression.getVersion();
                if (version != null && !"".equals(version)) {
                    language = language + " V" + expression.getVersion();
                }
                Integer integer = languageVersionCountMap.get(language);
                if (integer == null) {
                    languageVersionCountMap.put(language, 1);
                } else {
                    languageVersionCountMap.put(language, integer + 1);
                }
            }
        }
        Iterator<String> iterator = languageVersionCountMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Integer integer = languageVersionCountMap.get(key);
            if (integer > 1) {
                MessageUI.openError(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.isRepeated", key));
                return false;
            }
        }
        return true;
    }

    /**
     * DOC bZhou Comment method "checkMappingString".
     * 
     * @param c
     * @param r
     * @return
     */
    private boolean checkMappingString(String c, String r) {
        return !"".equals(c) && !"".equals(r) && c.length() == r.length(); //$NON-NLS-1$
    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private final class AggregateDateExpression implements Cloneable {

        private Expression aggregateExpression;

        private Expression dateExpression;

        public Expression getAggregateExpression() {
            return aggregateExpression;
        }

        public void setAggregateExpression(Expression aggregateExpression) {
            this.aggregateExpression = aggregateExpression;
        }

        public Expression getDateExpression() {
            return dateExpression;
        }

        public void setDateExpression(Expression dateExpression) {
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
            return ""; //$NON-NLS-1$
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
            return ""; //$NON-NLS-1$
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
            return ""; //$NON-NLS-1$
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
                    ade.setAggregateExpression(BooleanExpressionHelper.createExpression(getAggregateExpression().getLanguage(),
                            getAggregateExpression().getBody()));
                }
                if (getDateExpression() != null) {
                    ade.setDateExpression(BooleanExpressionHelper.createExpression(getDateExpression().getLanguage(),
                            getDateExpression().getBody()));
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return ade;
        }
    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private final class AggregateVO {

        String horizontalAxis, verticalAxis, bubbleSize;

        Expression aggreagetExpression;

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

        public AggregateVO(Expression aggregateExpression) {
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
            return this.aggreagetExpression == null ? "" : this.aggreagetExpression.getBody() == null ? "" //$NON-NLS-1$ //$NON-NLS-2$
                    : this.aggreagetExpression.getBody();
        }

        public String getLanguage() {
            return this.aggreagetExpression == null ? "" : this.aggreagetExpression.getLanguage() == null ? "" //$NON-NLS-1$ //$NON-NLS-2$
                    : this.aggreagetExpression.getLanguage();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof AggregateVO) {
                AggregateVO vo = (AggregateVO) obj;
                return this.getBody().equals(vo.getBody()) && this.getLanguage().equals(vo.getLanguage());
            }
            return false;
        }

        public int hashCode() {
            return this.getLanguage().concat(this.getBody()).hashCode();
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
                result = ""; //$NON-NLS-1$
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
     */
    private final class CommonContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            Object[] results = new Object[1];
            results[0] = inputElement;
            return results;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private class AggregateLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String result = ""; //$NON-NLS-1$
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
     */
    private final class DateVO {

        String lowerValue, upperValue, total, highlightedValues;

        Expression dateExpression;

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
                if ("".equals(this.getLowerValue()) && "".equals(this.getUpperValue()) && "".equals(this.getTotal()) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        && "".equals(this.getHighlightedValues())) { //$NON-NLS-1$
                    this.dateExpression.setBody(PluginConstant.EMPTY_STRING);
                } else {
                    this.dateExpression.setBody(this.getLowerValue() + ADDITIONAL_FUNCTIONS_SPLIT + this.getUpperValue()
                            + ADDITIONAL_FUNCTIONS_SPLIT + this.getTotal() + ADDITIONAL_FUNCTIONS_SPLIT
                            + this.getHighlightedValues());
                }
            }
        }

        public DateVO(Expression dateExpression) {
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
            return this.dateExpression == null ? "" : this.dateExpression.getBody() == null ? "" : this.dateExpression.getBody(); //$NON-NLS-1$ //$NON-NLS-2$
        }

        public String getLanguage() {
            return this.dateExpression == null ? "" : this.dateExpression.getLanguage() == null ? "" : this.dateExpression //$NON-NLS-1$ //$NON-NLS-2$
                    .getLanguage();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof DateVO) {
                DateVO vo = (DateVO) obj;
                return this.getBody().equals(vo.getBody()) && this.getLanguage().equals(vo.getLanguage());
            }
            return false;
        }

        public int hashCode() {
            return this.getLanguage().concat(this.getBody()).hashCode();
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
                result = ""; //$NON-NLS-1$
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
     */
    private class DateLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String result = ""; //$NON-NLS-1$
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
                result = ""; //$NON-NLS-1$
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
     */
    private class CharactersMappingLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String result = ""; //$NON-NLS-1$
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
     * DOC xqliu Comment method "cloneExpression". ADD xqliu 2010-04-01 bug 11892
     * 
     * @param exp
     * @return
     */
    public static final Expression cloneExpression(Expression exp) {
        Expression newExp = BooleanExpressionHelper.createExpression(exp.getLanguage(), exp.getBody());
        newExp.setVersion(exp.getVersion());
        return newExp;
    }
}
