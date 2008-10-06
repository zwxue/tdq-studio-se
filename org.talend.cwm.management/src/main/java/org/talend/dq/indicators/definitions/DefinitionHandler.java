// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFUtil;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.BoxIndicator;
import org.talend.dataquality.indicators.CountsIndicator;
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
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.SqlPatternMatchingIndicator;
import org.talend.dataquality.indicators.SumIndicator;
import org.talend.dataquality.indicators.TextIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import org.talend.dataquality.indicators.util.IndicatorsSwitch;

/**
 * @author scorreia
 * 
 * This class contains the singleton instance for the default indicator' definitions.
 */
public final class DefinitionHandler {

    private static Logger log = Logger.getLogger(DefinitionHandler.class);

    private static DefinitionHandler instance;

    private IndicatorsDefinitions indicatorDefinitions;

    /**
     * true if a copy from the file in the plugin directory to the workspace directory is needed.
     */
    private boolean needCopy = false;

    /**
     * plugin relative path to the default file.
     */
    private static final String FILENAME = ".Talend.definition";

    private static final String PLUGIN_PATH = "/org.talend.dataquality/" + FILENAME;

    private static final String WORKSPACE_PATH = "Libraries/";

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
        // MOD scorreia 2008-08-04 use EMFUtil instead of EMFSharedResources because this file does not need to be saved
        // with the other files. Moreover, we need to be able to edit it when needed (with default ".definition" editor
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
            throw new RuntimeException("Indicators' definition not loaded!");
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

    public Resource copyDefinitionsIntoFolder(URI destinationUri) {
        Resource resource = getIndicatorsDefinitions().eResource();
        EMFUtil.changeUri(resource, destinationUri);
        if (EMFUtil.saveResource(resource)) {
            if (log.isInfoEnabled()) {
                log.info("Indicator default definitions correctly saved in " + resource.getURI());
            }
        } else {
            log.error("Failed to save default indicator definitions in " + resource.getURI());

        }
        return resource;
    }

    public static DefinitionHandler getInstance() {
        if (instance == null) {
            instance = new DefinitionHandler();
            // try to copy in workspace
            if (instance.needCopy) {
                instance.copyDefinitionsIntoFolder(URI.createPlatformResourceURI(WORKSPACE_PATH, false));
            }
        }
        return instance;
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
     * Method "getIndicatorDefinition".
     * 
     * @param label the label of the definition to get
     * @return the definition with the given label
     */
    private IndicatorDefinition getIndicatorDefinition(String label) {
        EList<IndicatorDefinition> definitions = this.indicatorDefinitions.getIndicatorDefinitions();
        for (IndicatorDefinition indicatorDefinition : definitions) {
            if (indicatorDefinition != null && indicatorDefinition.getLabel() != null
                    && indicatorDefinition.getLabel().compareTo(label) == 0) {
                return indicatorDefinition;
            }
        }
        return null;
    }

 
    
    /**
     * Note: scorreia. All indicator definitions defined in .Talend.definition file must be implemented here.
     * 
     * WARNING: The label of the indicator definition in .Talend.definition must be exactly the same as the strings used
     * here.
     */
    private final IndicatorsSwitch<Boolean>  indicatorSwitch = new IndicatorsSwitch<Boolean>()   {
        private final ColumnsetSwitch<Boolean> columnIndicatorSwitch = new ColumnsetSwitch<Boolean>() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch#caseRowMatchingIndicator(org.talend.
             * dataquality.indicators.columnset.RowMatchingIndicator)
             */
            @Override
            public Boolean caseRowMatchingIndicator(RowMatchingIndicator object) {
                return setIndicatorDefinition(object, "Row Comparison");
            }
            
            /*
             * (non-Javadoc)
             * 
             * @see
             * org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch#defaultCase(org.eclipse.emf.ecore.EObject
             * )
             */
            @Override
            public Boolean defaultCase(EObject object) {
                return false;
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
         * @see
         * org.talend.dataquality.indicators.util.IndicatorsSwitch#caseLowFrequencyIndicator(org.talend.dataquality.
         * indicators.LowFrequencyIndicator)
         */
        @Override
        public Boolean caseLowFrequencyIndicator(LowFrequencyIndicator object) {
            return setIndicatorDefinition(object, "Low Frequency Table");
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.indicators.util.IndicatorsSwitch#caseRegexpMatchingIndicator(org.talend.dataquality
         * .indicators.RegexpMatchingIndicator)
         */
        @Override
        public Boolean caseRegexpMatchingIndicator(RegexpMatchingIndicator object) {
            return setIndicatorDefinition(object, "Regular Expression Matching");
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.indicators.util.IndicatorsSwitch#caseSqlPatternMatchingIndicator(org.talend.dataquality
         * .indicators.SqlPatternMatchingIndicator)
         */
        @Override
        public Boolean caseSqlPatternMatchingIndicator(SqlPatternMatchingIndicator object) {
            return setIndicatorDefinition(object, "SQL Pattern Matching");
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.indicators.util.IndicatorsSwitch#casePatternMatchingIndicator(org.talend.dataquality
         * .indicators.PatternMatchingIndicator)
         */
        @Override
        public Boolean casePatternMatchingIndicator(PatternMatchingIndicator object) {
            return setIndicatorDefinition(object, "Pattern Matching Indicator");
        }

        @Override
        public Boolean caseCountsIndicator(CountsIndicator object) {
            return setIndicatorDefinition(object, "Simple Statistics");
        }

        @Override
        public Boolean caseTextIndicator(TextIndicator object) {
            return setIndicatorDefinition(object, "Text Statistics");
        }

        @Override
        public Boolean caseUniqueCountIndicator(UniqueCountIndicator object) {
            return setIndicatorDefinition(object, "Unique Count");
        }

        @Override
        public Boolean caseSumIndicator(SumIndicator object) {
            return setIndicatorDefinition(object, "Sum");
        }

        @Override
        public Boolean caseRowCountIndicator(RowCountIndicator object) {
            return setIndicatorDefinition(object, "Row Count");
        }

        @Override
        public Boolean caseRangeIndicator(RangeIndicator object) {
            return setIndicatorDefinition(object, "Range");
        }

        @Override
        public Boolean caseNullCountIndicator(NullCountIndicator object) {
            return setIndicatorDefinition(object, "Null Count");
        }

        @Override
        public Boolean caseModeIndicator(ModeIndicator object) {
            return setIndicatorDefinition(object, "Mode");
        }

        @Override
        public Boolean caseMinValueIndicator(MinValueIndicator object) {
            return setIndicatorDefinition(object, "Minimum");
        }

        @Override
        public Boolean caseMinLengthIndicator(MinLengthIndicator object) {
            return setIndicatorDefinition(object, "Minimal Length");
        }

        @Override
        public Boolean caseMedianIndicator(MedianIndicator object) {
            return setIndicatorDefinition(object, "Median");
        }

        @Override
        public Boolean caseMeanIndicator(MeanIndicator object) {
            return setIndicatorDefinition(object, "Mean");
        }

        @Override
        public Boolean caseMaxValueIndicator(MaxValueIndicator object) {
            return setIndicatorDefinition(object, "Maximum");
        }

        @Override
        public Boolean caseMaxLengthIndicator(MaxLengthIndicator object) {
            return setIndicatorDefinition(object, "Maximal Length");
        }

        @Override
        public Boolean caseLengthIndicator(LengthIndicator object) {
            return setIndicatorDefinition(object, "Length");
        }

        @Override
        public Boolean caseIQRIndicator(IQRIndicator object) {
            return setIndicatorDefinition(object, "IQR");
        }

        @Override
        public Boolean caseFrequencyIndicator(FrequencyIndicator object) {
            return setIndicatorDefinition(object, "Frequency Table");
        }

        @Override
        public Boolean caseDuplicateCountIndicator(DuplicateCountIndicator object) {
            return setIndicatorDefinition(object, "Duplicate Count");
        }

        @Override
        public Boolean caseDistinctCountIndicator(DistinctCountIndicator object) {
            return setIndicatorDefinition(object, "Distinct Count");
        }

        @Override
        public Boolean caseBoxIndicator(BoxIndicator object) {
            return setIndicatorDefinition(object, "Summary Statistics");
        }

        @Override
        public Boolean caseBlankCountIndicator(BlankCountIndicator object) {
            return setIndicatorDefinition(object, "Blank Count");
        }

        @Override
        public Boolean caseAverageLengthIndicator(AverageLengthIndicator object) {
            return setIndicatorDefinition(object, "Average Length");
        }

        @Override
        public Boolean caseLowerQuartileIndicator(LowerQuartileIndicator object) {
            return setIndicatorDefinition(object, "Lower Quartile");
        }

        @Override
        public Boolean caseUpperQuartileIndicator(UpperQuartileIndicator object) {
            return setIndicatorDefinition(object, "Upper Quartile");
        }


    };
    
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
    
 
}
