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
package org.talend.dq.indicators.definitions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.CwmResource;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.BoxIndicator;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.DefValueCountIndicator;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.IQRIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.LengthIndicator;
import org.talend.dataquality.indicators.LowFrequencyIndicator;
import org.talend.dataquality.indicators.LowerQuartileIndicator;
import org.talend.dataquality.indicators.MaxLengthIndicator;
import org.talend.dataquality.indicators.MaxValueIndicator;
import org.talend.dataquality.indicators.MeanIndicator;
import org.talend.dataquality.indicators.MedianIndicator;
import org.talend.dataquality.indicators.MinLengthIndicator;
import org.talend.dataquality.indicators.MinValueIndicator;
import org.talend.dataquality.indicators.ModeIndicator;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.SoundexFreqIndicator;
import org.talend.dataquality.indicators.SoundexLowFreqIndicator;
import org.talend.dataquality.indicators.SqlPatternMatchingIndicator;
import org.talend.dataquality.indicators.SumIndicator;
import org.talend.dataquality.indicators.TextIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dataquality.indicators.columnset.MinMaxDateIndicator;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dataquality.indicators.columnset.WeakCorrelationIndicator;
import org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch;
import org.talend.dataquality.indicators.definition.DefinitionPackage;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dataquality.indicators.schema.TableIndicator;
import org.talend.dataquality.indicators.schema.ViewIndicator;
import org.talend.dataquality.indicators.schema.util.SchemaSwitch;
import org.talend.dataquality.indicators.util.IndicatorsSwitch;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * @author scorreia
 * 
 * This class contains the singleton instance for the default indicator' definitions.
 */
public final class DefinitionHandler {

    /**
     * The label of the Regular Expression Matching indicator definition.
     */
    private static final String REGULAR_EXPRESSION_MATCHING = "Regular Expression Matching"; //$NON-NLS-1$

    private static Logger log = Logger.getLogger(DefinitionHandler.class);

    private static DefinitionHandler instance;

    private static final String DQ_RULE_CATEGORY = "_8i9eQBI5Ed6TWL6NwMMHzQ"; //$NON-NLS-1$

    private static final String USER_DEFINED_COUNT_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_COUNT_CATEGORY;

    private static final String USER_DEFINED_FREQUENCY_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_FREQUENCY_CATEGORY;

    private static final String USER_DEFINED_MATCH_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_MATCH_CATEGORY;

    private static final String USER_DEFINED_REAL_VALUE_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_REAL_VALUE_CATEGORY;

    private static final String USER_DEFINED_COMPARISON_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_COMPARISON_CATEGORY;

    private static final String USER_DEFINED_NOMINAL_CORRELATION_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_NOMINAL_CORRELATION_CATEGORY;

    private static final String USER_DEFINED_INTERVAL_CORRELATION_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_INTERVAL_CORRELATION_CATEGORY;

    private static final String USER_DEFINED_TIME_CORRELATION_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_TIME_CORRELATION_CATEGORY;

    private static final String DQ_RULE_DEFINITION = "_UUIyoCOMEd6YB57jaCfKaA"; //$NON-NLS-1$

    private static final String FD_RULE_DEFINITION = "_YqcX0XHpEd6udst2R2sgpA"; //$NON-NLS-1$

    private static Map<String, IndicatorCategory> userDefinedIndicatorCategoryMap;

    private IndicatorsDefinitions indicatorDefinitions;

    /**
     * true if a copy from the file in the plugin directory to the workspace directory is needed.
     */
    private boolean needCopy = false;

    /**
     * plugin relative path to the default file.
     */
    private static final String FILENAME = ".Talend." + FactoriesUtil.TALEND_DEFINITION; //$NON-NLS-1$

    private static final String PLUGIN_PATH = "/org.talend.dataquality/" + FILENAME; //$NON-NLS-1$

    // MOD mzhao 2009-03-13 Feature:6066 Move all folders into one single
    // project.
    private static final String WORKSPACE_PATH = ReponsitoryContextBridge.getProjectName() + "/TDQ_Libraries/"; //$NON-NLS-1$

    public static DefinitionHandler getInstance() {
        if (instance == null) {
            instance = new DefinitionHandler();
            // try to copy in workspace
            if (instance.needCopy) {
                instance.copyDefinitionsIntoFolder(ResourceManager.getLibrariesFolder());
            }
        }
        return instance;
    }

    private DefinitionHandler() {
        this.indicatorDefinitions = loadFromFile();
    }

    private IndicatorsDefinitions loadFromFile() {

        Resource definitionsFile = getResourceFromFile();

        EList<EObject> contents = definitionsFile.getContents();
        if (contents == null) {
            log.error("No content found in given resource: " + definitionsFile.getURI());
            return null;
        }
        if (contents.isEmpty()) {
            log.error("No content found in given resource: " + definitionsFile.getURI());
            return null;
        }
        DefinitionSwitch<IndicatorsDefinitions> catSwitch = new DefinitionSwitch<IndicatorsDefinitions>() {

            @Override
            public IndicatorsDefinitions caseIndicatorsDefinitions(IndicatorsDefinitions object) {
                return object;
            }

        };

        return catSwitch.doSwitch(contents.get(0));
    }

    private Resource getResourceFromFile() {
        // MOD scorreia 2008-08-04 use EMFUtil instead of EMFSharedResources
        // because this file does not need to be saved
        // with the other files. Moreover, we need to be able to edit it when
        // needed (with default ".definition" editor
        // for development purposes)
        EMFUtil util = new EMFUtil();
        Resource definitionsFile = null;
        URI uri = URI.createPlatformResourceURI(WORKSPACE_PATH + FILENAME, false);
        try { // load from workspace path
            // do not create it here if it does not exist.
            definitionsFile = util.getResourceSet().getResource(uri, true);
            if (log.isDebugEnabled()) {
                log.debug("Definition of indicators loaded from " + uri);
            }
        } catch (RuntimeException e) {
            if (log.isDebugEnabled()) {
                log.debug("ERROR: " + e.getMessage(), e);
            }
        }
        if (definitionsFile == null) {
            needCopy = true;
            uri = URI.createPlatformPluginURI(PLUGIN_PATH, false);
            try { // load from plugin path
                definitionsFile = util.getResourceSet().getResource(uri, true);
                if (log.isDebugEnabled()) {
                    log.debug("Definition of indicators loaded from " + uri);
                }
            } catch (RuntimeException e) {
                if (log.isDebugEnabled()) {
                    log.debug("ERROR: " + e.getMessage(), e);
                }
            }
        }

        if (definitionsFile == null) {
            // try to load from a local file
            definitionsFile = util.getResourceSet().getResource(URI.createFileURI(".." + File.separator + PLUGIN_PATH), true);
        }
        if (definitionsFile == null) {
            log.error("No resource found at " + PLUGIN_PATH + " URI= " + uri);
            return null;
        }
        return definitionsFile;
    }

    public boolean restaureDefaultRegexDefinitions() {
        EMFUtil util = new EMFUtil();
        Resource definitionsFile = null;
        URI uri = URI.createPlatformPluginURI(PLUGIN_PATH, false);
        // load from plugin path
        definitionsFile = util.getResourceSet().getResource(uri, true);
        EObject origDef = definitionsFile.getContents().get(0);
        if (origDef != null) {
            IndicatorDefinition origIndDefinition = getIndicatorDefinition((IndicatorsDefinitions) origDef,
                    REGULAR_EXPRESSION_MATCHING);
            IndicatorDefinition regexIndDef = this.getIndicatorDefinition(REGULAR_EXPRESSION_MATCHING);
            regexIndDef.getSqlGenericExpression().clear();
            regexIndDef.getSqlGenericExpression().addAll(origIndDefinition.getSqlGenericExpression());
            return true;
        }
        return false;
    }

    /**
     * Method "getIndicatorsDefinitions".
     * 
     * @return the singleton analysis categories (or throws an exception if a problem occured)
     */
    public IndicatorsDefinitions getIndicatorsDefinitions() {
        if (indicatorDefinitions == null) {
            indicatorDefinitions = loadFromFile();
        }
        if (indicatorDefinitions == null) {
            throw new RuntimeException(Messages.getString("DefinitionHandler.IndicatorsDefinition")); //$NON-NLS-1$
        }
        return indicatorDefinitions;
    }

    /**
     * Method "copyDefinitionsIntoFolder".
     * 
     * @param folder the folder where to copy the default resource.
     * @return the copied resource.
     */
    public Resource copyDefinitionsIntoFolder(File folder) {
        URI destinationUri = URI.createFileURI(folder.getAbsolutePath());
        Resource resource = getIndicatorsDefinitions().eResource();
        EMFUtil.changeUri(resource, destinationUri);
        if (EMFUtil.saveResource(resource)) {
            if (log.isInfoEnabled()) {
                log.info("Indicator default definitions correctly saved in " + resource.getURI());
            } else {
                log.warn("Failed to save default indicator definitions in " + resource.getURI());
            }
        }
        return resource;
    }

    /**
     * DOC bZhou Comment method "copyDefinitionsIntoFolder".
     * 
     * @param ifolder
     * @return
     */
    public Resource copyDefinitionsIntoFolder(IFolder ifolder) {
        File folder = new File(ifolder.getLocationURI());
        return copyDefinitionsIntoFolder(folder);
    }

    /**
     * Method "setDefaultIndicatorDefinition" sets the indicator's default definition.
     * 
     * @param indicator the indicator
     * @return true when set, false when not set.
     */
    public boolean setDefaultIndicatorDefinition(Indicator indicator) {
        return indicatorSwitch.doSwitch(indicator);
    }

    /**
     * Method "saveResource" saves the indicator definitions (in .Talend.definition file).
     * 
     * @return true if no problem
     */
    public boolean saveResource() {
        return EMFUtil.saveSingleResource(this.indicatorDefinitions.eResource());
    }

    /**
     * Method "getIndicatorDefinition".
     * 
     * @param label the label of the definition to get
     * @return the definition with the given label
     */
    public IndicatorDefinition getIndicatorDefinition(String label) {
        return this.getIndicatorDefinition(indicatorDefinitions, label);
    }

    private IndicatorDefinition getIndicatorDefinition(IndicatorsDefinitions indDefinitions, String label) {
        EList<IndicatorDefinition> definitions = indDefinitions.getIndicatorDefinitions();
        for (IndicatorDefinition indicatorDefinition : definitions) {
            if (indicatorDefinition != null && indicatorDefinition.getLabel() != null
                    && indicatorDefinition.getLabel().compareTo(label) == 0) {
                return indicatorDefinition;
            }
        }
        return null;
    }

    /**
     * DOC jet Comment method "removeRegexFunction".
     * <p>
     * Just remove a UDF from .Talend.definition File.
     * 
     * @param dbmsName
     * @return
     */
    public boolean removeRegexFunction(String dbmsName) {

        IndicatorDefinition regexIndDef = this.getIndicatorDefinition(REGULAR_EXPRESSION_MATCHING);
        EList<Expression> sqlGenericExpression = regexIndDef.getSqlGenericExpression();
        for (Expression expression : sqlGenericExpression) {
            if (dbmsName.equals(expression.getLanguage())) {
                sqlGenericExpression.remove(expression);
                DefinitionHandler.getInstance().saveResource();
                return true;
            }
        }

        return false;
    }

    public boolean updateRegex(String dbmsName, String regexpFunction) {
        boolean ok = true;
        boolean replaced = false;
        IndicatorDefinition regexIndDef = this.getIndicatorDefinition(REGULAR_EXPRESSION_MATCHING);
        EList<Expression> sqlGenericExpression = regexIndDef.getSqlGenericExpression();

        for (Expression expression : sqlGenericExpression) {
            if (dbmsName.equals(expression.getLanguage())) {
                // FIXME scorreia this comparison should be made by
                // DbmsLanguageFactory?
                replaced = replaceBodyWith(expression, regexpFunction);
            }
        }
        if (!replaced) {
            // add new expression
            String genericSQL = getGenericSQL(dbmsName, regexpFunction);
            Expression createdExpression = BooleanExpressionHelper.createExpression(dbmsName, genericSQL);
            sqlGenericExpression.add(createdExpression);
        }
        return ok;
    }

    /**
     * DOC scorreia Comment method "getGenericSQL".
     * 
     * @param dbmsName
     * @param regexpFunction
     * @return
     */
    private String getGenericSQL(String dbmsName, String regexpFunction) {
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dbmsName);
        return dbmsLanguage.createGenericSqlWithRegexFunction(regexpFunction);
    }

    /**
     * DOC scorreia Comment method "replaceBodyWith".
     * 
     * @param expression
     * @param regexpFunction
     * @return
     */
    private boolean replaceBodyWith(Expression expression, String regexpFunction) {
        expression.setBody(this.getGenericSQL(expression.getLanguage(), regexpFunction));
        return true;
    }

    /**
     * Note: scorreia. All indicator definitions defined in .Talend.definition file must be implemented here.
     * 
     * WARNING: The label of the indicator definition in .Talend.definition must be exactly the same as the strings used
     * here.
     */
    private final IndicatorsSwitch<Boolean> indicatorSwitch = new IndicatorsSwitch<Boolean>() {

        private final SchemaSwitch<Boolean> schemaIndicatorSwitch = new SchemaSwitch<Boolean>() {

            /*
             * (non-Javadoc)
             * 
             * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch#
             * caseCatalogIndicator(org.talend.dataquality .indicators.schema.CatalogIndicator)
             */
            @Override
            public Boolean caseCatalogIndicator(CatalogIndicator object) {
                return setIndicatorDefinition(object, "Catalog Overview");
            }

            /*
             * (non-Javadoc)
             * 
             * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch#
             * caseConnectionIndicator(org.talend.dataquality .indicators.schema.ConnectionIndicator)
             */
            @Override
            public Boolean caseConnectionIndicator(ConnectionIndicator object) {
                return setIndicatorDefinition(object, "Connection Overview"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch#
             * caseSchemaIndicator(org.talend.dataquality .indicators.schema.SchemaIndicator)
             */
            @Override
            public Boolean caseSchemaIndicator(SchemaIndicator object) {
                return setIndicatorDefinition(object, "Schema Overview"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch#
             * caseTableIndicator(org.talend.dataquality. indicators.schema.TableIndicator)
             */
            @Override
            public Boolean caseTableIndicator(TableIndicator object) {
                return setIndicatorDefinition(object, "Table Overview"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch# caseViewIndicator(org.talend.dataquality.
             * indicators.schema.ViewIndicator)
             */
            @Override
            public Boolean caseViewIndicator(ViewIndicator object) {
                return setIndicatorDefinition(object, "View Overview"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch#
             * defaultCase(org.eclipse.emf.ecore.EObject)
             */
            @Override
            public Boolean defaultCase(EObject object) {
                return false;
            }

        };

        private final ColumnsetSwitch<Boolean> columnIndicatorSwitch = new ColumnsetSwitch<Boolean>() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch
             * #caseRowMatchingIndicator(org.talend. dataquality.indicators.columnset.RowMatchingIndicator)
             */
            @Override
            public Boolean caseRowMatchingIndicator(RowMatchingIndicator object) {
                return setIndicatorDefinition(object, "Row Comparison"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch
             * #caseColumnSetMultiValueIndicator(org .talend.dataquality.indicators
             * .columnset.ColumnSetMultiValueIndicator)
             */
            @Override
            public Boolean caseColumnSetMultiValueIndicator(ColumnSetMultiValueIndicator object) {
                return setIndicatorDefinition(object, "Multiple Column Frequency Table"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @seeorg.talend.dataquality.indicators.columnset.util.ColumnsetSwitch#caseSimpleStatIndicator(org.talend.
             * dataquality.indicators.columnset.SimpleStatIndicator)
             */
            @Override
            public Boolean caseSimpleStatIndicator(SimpleStatIndicator object) {
                return setIndicatorDefinition(object, "Multiple Column Simple Statistics"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch
             * #caseWeakCorrelationIndicator(org.talend .dataquality.indicators.columnset.WeakCorrelationIndicator)
             */
            @Override
            public Boolean caseWeakCorrelationIndicator(WeakCorrelationIndicator object) {
                return setIndicatorDefinition(object, "Multiple Column Correlation"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch
             * #caseCountAvgNullIndicator(org.talend .dataquality.indicators.columnset.CountAvgNullIndicator)
             */
            @Override
            public Boolean caseCountAvgNullIndicator(CountAvgNullIndicator object) {
                return setIndicatorDefinition(object, "Averaged Multiple Column Frequency Table"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * 
             * 
             * @seeorg.talend.dataquality.indicators.columnset.util.ColumnsetSwitch #caseMinMaxDateIndicator(org.talend.
             * dataquality.indicators.columnset.MinMaxDateIndicator)
             */
            @Override
            public Boolean caseMinMaxDateIndicator(MinMaxDateIndicator object) {
                return setIndicatorDefinition(object, "Min Max Date Multiple Column Frequency Table"); //$NON-NLS-1$
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch
             * #defaultCase(org.eclipse.emf.ecore.EObject )
             */
            @Override
            public Boolean defaultCase(EObject object) {
                // try schemaswitch
                return schemaIndicatorSwitch.doSwitch(object);
            }

        };

        @Override
        public Boolean defaultCase(EObject object) {
            // try with columnSetSwitch
            return columnIndicatorSwitch.doSwitch(object);
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch#
         * caseDefValueCountIndicator(org.talend.dataquality .indicators.DefValueCountIndicator)
         */
        @Override
        public Boolean caseDefValueCountIndicator(DefValueCountIndicator object) {
            return setIndicatorDefinition(object, "Default Value Count"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch#
         * caseLowFrequencyIndicator(org.talend.dataquality. indicators.LowFrequencyIndicator)
         */
        @Override
        public Boolean caseLowFrequencyIndicator(LowFrequencyIndicator object) {
            return setIndicatorDefinition(object, "Low Frequency Table"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# casePatternFreqIndicator(org.talend.dataquality.
         * indicators.PatternFreqIndicator)
         */
        @Override
        public Boolean casePatternFreqIndicator(PatternFreqIndicator object) {
            return setIndicatorDefinition(object, "Pattern Frequency Table"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch#
         * casePatternLowFreqIndicator(org.talend.dataquality .indicators.PatternLowFreqIndicator)
         */
        @Override
        public Boolean casePatternLowFreqIndicator(PatternLowFreqIndicator object) {
            return setIndicatorDefinition(object, "Pattern Low Frequency Table"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch#
         * caseRegexpMatchingIndicator(org.talend.dataquality .indicators.RegexpMatchingIndicator)
         */
        @Override
        public Boolean caseRegexpMatchingIndicator(RegexpMatchingIndicator object) {
            return setIndicatorDefinition(object, REGULAR_EXPRESSION_MATCHING);
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch#
         * caseSqlPatternMatchingIndicator(org.talend.dataquality .indicators.SqlPatternMatchingIndicator)
         */
        @Override
        public Boolean caseSqlPatternMatchingIndicator(SqlPatternMatchingIndicator object) {
            return setIndicatorDefinition(object, "SQL Pattern Matching"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch#
         * casePatternMatchingIndicator(org.talend.dataquality .indicators.PatternMatchingIndicator)
         */
        @Override
        public Boolean casePatternMatchingIndicator(PatternMatchingIndicator object) {
            return setIndicatorDefinition(object, "Pattern Matching Indicator"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseCountsIndicator(CountsIndicator object) {
            return setIndicatorDefinition(object, "Simple Statistics"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseTextIndicator(TextIndicator object) {
            return setIndicatorDefinition(object, "Text Statistics"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseUniqueCountIndicator(UniqueCountIndicator object) {
            return setIndicatorDefinition(object, "Unique Count"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseSumIndicator(SumIndicator object) {
            return setIndicatorDefinition(object, "Sum"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseRowCountIndicator(RowCountIndicator object) {
            return setIndicatorDefinition(object, "Row Count"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseRangeIndicator(RangeIndicator object) {
            return setIndicatorDefinition(object, "Range"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseNullCountIndicator(NullCountIndicator object) {
            return setIndicatorDefinition(object, "Null Count"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseModeIndicator(ModeIndicator object) {
            return setIndicatorDefinition(object, "Mode"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseMinValueIndicator(MinValueIndicator object) {
            return setIndicatorDefinition(object, "Minimum"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseMinLengthIndicator(MinLengthIndicator object) {
            return setIndicatorDefinition(object, "Minimal Length"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseMedianIndicator(MedianIndicator object) {
            return setIndicatorDefinition(object, "Median"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseMeanIndicator(MeanIndicator object) {
            return setIndicatorDefinition(object, "Mean"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseMaxValueIndicator(MaxValueIndicator object) {
            return setIndicatorDefinition(object, "Maximum"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseMaxLengthIndicator(MaxLengthIndicator object) {
            return setIndicatorDefinition(object, "Maximal Length"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseLengthIndicator(LengthIndicator object) {
            return setIndicatorDefinition(object, "Length"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseIQRIndicator(IQRIndicator object) {
            return setIndicatorDefinition(object, "IQR"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseFrequencyIndicator(FrequencyIndicator object) {
            return setIndicatorDefinition(object, "Frequency Table"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# caseSoundexFreqIndicator(org.talend.dataquality.
         * indicators.SoundexFreqIndicator)
         */
        @Override
        public Boolean caseSoundexFreqIndicator(SoundexFreqIndicator object) {
            return setIndicatorDefinition(object, "Soundex Frequency Table"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch#
         * caseSoundexLowFreqIndicator(org.talend.dataquality .indicators.SoundexLowFreqIndicator)
         */
        @Override
        public Boolean caseSoundexLowFreqIndicator(SoundexLowFreqIndicator object) {
            return setIndicatorDefinition(object, "Soundex Low Frequency Table"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseDuplicateCountIndicator(DuplicateCountIndicator object) {
            return setIndicatorDefinition(object, "Duplicate Count"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseDistinctCountIndicator(DistinctCountIndicator object) {
            return setIndicatorDefinition(object, "Distinct Count"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseBoxIndicator(BoxIndicator object) {
            return setIndicatorDefinition(object, "Summary Statistics"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseBlankCountIndicator(BlankCountIndicator object) {
            return setIndicatorDefinition(object, "Blank Count"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseAverageLengthIndicator(AverageLengthIndicator object) {
            return setIndicatorDefinition(object, "Average Length"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseLowerQuartileIndicator(LowerQuartileIndicator object) {
            return setIndicatorDefinition(object, "Lower Quartile"); //$NON-NLS-1$
        }

        @Override
        public Boolean caseUpperQuartileIndicator(UpperQuartileIndicator object) {
            return setIndicatorDefinition(object, "Upper Quartile"); //$NON-NLS-1$
        }

    }; // EOC IndicatorsSwitch

    private Boolean setIndicatorDefinition(Indicator indicator, String definitionLabel) {
        // get the definition
        IndicatorDefinition indicatorDefinition = this.getIndicatorDefinition(definitionLabel);
        if (indicatorDefinition == null) {
            return false;
        }
        // else
        indicator.setIndicatorDefinition(indicatorDefinition);
        return true;
    }

    /**
     * Method "getDQRuleIndicatorCategory".
     * 
     * @return the category of the DQ Rule indicators
     */
    public IndicatorCategory getDQRuleIndicatorCategory() {
        return getIndicatorCategory(DQ_RULE_CATEGORY);
    }

    /**
     * Method "getDQRuleDefaultIndicatorDefinition".
     * 
     * @return the default indicator definition of the DQ rule.
     */
    public IndicatorDefinition getDQRuleDefaultIndicatorDefinition() {
        return getDefaultIndicatorDefinition(DQ_RULE_DEFINITION);
    }

    public IndicatorDefinition getFDRuleDefaultIndicatorDefinition() {
        return getDefaultIndicatorDefinition(FD_RULE_DEFINITION);
    }

    private IndicatorDefinition getDefaultIndicatorDefinition(String definitionId) {
        CwmResource resource = (CwmResource) this.indicatorDefinitions.eResource();
        EObject object = resource.getEObject(definitionId);
        if (object != null && DefinitionPackage.eINSTANCE.getIndicatorDefinition().equals(object.eClass())) {
            return (IndicatorDefinition) object;
        }
        return null;
    }

    public IndicatorCategory getUserDefinedCountIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_COUNT_CATEGORY);
    }

    public IndicatorCategory getUserDefinedMatchIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_MATCH_CATEGORY);
    }

    public IndicatorCategory getUserDefinedFrequencyIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_FREQUENCY_CATEGORY);
    }

    public IndicatorCategory getUserDefinedRealValueIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_REAL_VALUE_CATEGORY);
    }

    public IndicatorCategory getUserDefinedComparisonIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_COMPARISON_CATEGORY);
    }

    public IndicatorCategory getUserDefinedNominalCorrelationIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_NOMINAL_CORRELATION_CATEGORY);
    }

    public IndicatorCategory getUserDefinedIntervalCorrelationIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_INTERVAL_CORRELATION_CATEGORY);
    }

    public IndicatorCategory getUserDefinedTimeCorrelationIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_TIME_CORRELATION_CATEGORY);
    }

    private IndicatorCategory getIndicatorCategory(String categoryId) {
        CwmResource resource = (CwmResource) this.indicatorDefinitions.eResource();
        EObject object = resource.getEObject(categoryId);
        if (object != null && DefinitionPackage.eINSTANCE.getIndicatorCategory().equals(object.eClass())) {
            return (IndicatorCategory) object;
        }
        return null;
    }

    public Map<String, IndicatorCategory> getUserDefinedIndicatorCategoryMap() {
        if (userDefinedIndicatorCategoryMap == null) {
            userDefinedIndicatorCategoryMap = new HashMap<String, IndicatorCategory>();

            // init user defined indicator categories
            List<IndicatorCategory> categoryList = new ArrayList<IndicatorCategory>();
            categoryList.add(getUserDefinedCountIndicatorCategory());
            categoryList.add(getUserDefinedFrequencyIndicatorCategory());
            categoryList.add(getUserDefinedMatchIndicatorCategory());
            categoryList.add(getUserDefinedRealValueIndicatorCategory());
            // categoryList.add(getUserDefinedComparisonIndicatorCategory());
            // categoryList.add(getUserDefinedIntervalCorrelationIndicatorCategory());
            // categoryList.add(getUserDefinedNominalCorrelationIndicatorCategory());
            // categoryList.add(getUserDefinedTimeCorrelationIndicatorCategory());

            for (IndicatorCategory category : categoryList) {
                userDefinedIndicatorCategoryMap.put(category.getLabel(), category);
            }

            categoryList = null;
        }
        return userDefinedIndicatorCategoryMap;
    }

    public Collection<String> getUserDefinedIndicatorCategoryLabels() {
        return getUserDefinedIndicatorCategoryMap().keySet();
    }

    public Collection<IndicatorCategory> getUserDefinedIndicatorCategoryList() {
        return getUserDefinedIndicatorCategoryMap().values();
    }

    public IndicatorCategory getIndicatorCategoryByLabel(String label) {
        return getUserDefinedIndicatorCategoryMap().get(label);
    }

    public static IFile getTalendDefinitionFile() {
        return ResourceManager.getLibrariesFolder().getFile(FILENAME);
    }

    public static void reload() {
        instance = null;
    }
}
