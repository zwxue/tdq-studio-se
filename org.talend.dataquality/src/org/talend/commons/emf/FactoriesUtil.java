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
package org.talend.commons.emf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.talend.cwm.constants.ConstantsFactory;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.RelationalPackage;
import org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.category.CategoryFactory;
import org.talend.dataquality.analysis.category.CategoryPackage;
import org.talend.dataquality.domain.DomainFactory;
import org.talend.dataquality.domain.DomainPackage;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.PatternPackage;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.DefinitionPackage;
import org.talend.dataquality.reports.ReportsFactory;
import org.talend.dataquality.reports.ReportsPackage;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dataquality.rules.RulesPackage;
import orgomg.cwm.foundation.typemapping.TypemappingPackage;
import orgomg.cwm.objectmodel.core.CorePackage;

/**
 * @author scorreia
 * 
 * This class is a utility for CWM and Talend extension Factories initialization. MODSCA 2008-04-03 use
 * Factory.eINSTANCE.getEPackage() instead of FactoryImpl.init() so that implementation packages can be hidden.
 */
public final class FactoriesUtil {

    /**
     * Extension used for the files in which the data provider objects are serialized.
     */
    public static final String PROV = "prv"; //$NON-NLS-1$

    /**
     * Extension used for the files in which the analysis objects are serialized.
     */
    public static final String ANA = "ana"; //$NON-NLS-1$

    /**
     * Extension used for the files in which the catalog or schema objects are serialized.
     */
    public static final String CAT = "cat"; //$NON-NLS-1$

    /**
     * Extension used for the files in which the reports are serialized.
     */
    public static final String REP = "rep"; //$NON-NLS-1$

    /**
     * Extension used for the files of jrxml report. MOD mzhao 2009-05-11, feature:6894
     */
    public static final String JRXML = "jrxml"; //$NON-NLS-1$

    /**
     * Extension used for the files in which the sql are serialized.
     */
    public static final String SQL = "sql"; //$NON-NLS-1$

    /**
     * Extension used for the files which is integrated in TOS properties.
     */
    public static final String PROPERTIES_EXTENSION = "properties"; //$NON-NLS-1$

    /**
     * Extension used for the files which is integrated in TOS item.
     */
    public static final String ITEM_EXTENSION = "item"; //$NON-NLS-1$

    /**
     * Extension used for the files used for software deployment.
     */
    public static final String SOFTWARE_SYSTEM = SoftwaredeploymentPackage.eNAME;

    /**
     * Extension used for the files in which the System Indicators are serialized.
     */
    public static final String DEFINITION = DefinitionPackage.eNAME;

    /**
     * Extension used for the files in which the pattern are serialized.
     */
    public static final String PATTERN = PatternPackage.eNAME;

    /**
     * Extension used for the files in which the DQRule are serialized.
     */
    public static final String DQRULE = RulesPackage.eNAME;

    /**
     * Extension used for xml configuration file
     */
    public static final String XML = "xml";

    private FactoriesUtil() {
    }

    /**
     * Method "initializeAllFactories" calls static method init() for each of the factories in this project. This is
     * needed when writing EMF files.
     */
    public static void initializeAllFactories() {

        // --- talend extension packages
        ConstantsFactory.eINSTANCE.getEPackage();
        org.talend.cwm.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE.getEPackage();
        RelationalFactory.eINSTANCE.getRelationalPackage();

        // --- talend DQ factories
        AnalysisFactory.eINSTANCE.getAnalysisPackage();
        DomainFactory.eINSTANCE.getDomainPackage();
        IndicatorsFactory.eINSTANCE.getIndicatorsPackage();
        ReportsFactory.eINSTANCE.getReportsPackage();
        DefinitionFactory.eINSTANCE.getDefinitionPackage();

        PatternFactory.eINSTANCE.getEPackage();
        CategoryFactory.eINSTANCE.getEPackage();
        org.talend.dataquality.expressions.ExpressionsFactory.eINSTANCE.getEPackage();
        org.talend.dataquality.reports.ReportsFactory.eINSTANCE.getEPackage();
        RulesFactory.eINSTANCE.getEPackage();

        // CWM generated packages
        // TODO scorreia add other factories
        orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE.getEPackage();
        orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.getEPackage();

        orgomg.cwmmip.CwmmipFactory.eINSTANCE.getEPackage();
        orgomg.mof.model.ModelFactory.eINSTANCE.getEPackage();
        orgomg.cwm.foundation.datatypes.DatatypesFactory.eINSTANCE.getEPackage();
        orgomg.cwm.objectmodel.core.CoreFactory.eINSTANCE.getEPackage();
        orgomg.cwm.objectmodel.relationships.RelationshipsFactory.eINSTANCE.getEPackage();
        orgomg.cwm.foundation.typemapping.TypemappingFactory.eINSTANCE.getEPackage();
    }

    /**
     * Method "getExtensions".
     * 
     * @return the list of file extensions
     */
    public static List<String> getExtensions() {
        List<String> extensions = new ArrayList<String>();
        // --- Talend extension packages
        extensions.add(SoftwaredeploymentPackage.eNAME);
        extensions.add(RelationalPackage.eNAME);

        // --- Talend DQ extension packages
        extensions.add(IndicatorsPackage.eNAME);
        extensions.add(DomainPackage.eNAME);
        extensions.add(CategoryPackage.eNAME);
        extensions.add(ReportsPackage.eNAME);
        extensions.add(DefinitionPackage.eNAME);

        // --- add specific extensions
        // MOD zshen use same repository API with TOS to persistent metadata
        extensions.add(ITEM_EXTENSION);
        extensions.add(PROV);
        extensions.add(ANA);
        extensions.add(CAT);
        extensions.add(REP);
        extensions.add(PATTERN);
        extensions.add(DQRULE);

        // --- CWM generated packages
        extensions.add(CorePackage.eNAME);
        extensions.add(TypemappingPackage.eNAME);
        // TODO scorreia add other file extensions
        return extensions;
    }

    /**
     * Method "initializeAllPackages" initializes all the EMF packages. This is needed when reading EMF files.
     */
    public static void initializeAllPackages() {

        // --- talend extension packages
        org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage.eINSTANCE.getEFactoryInstance();
        // RelationalPackage.eINSTANCE.getRelationalFactory();

        // --- talend DQ factories
        // AnalysisPackage.eINSTANCE.getEFactoryInstance();
        DomainPackage.eINSTANCE.getEFactoryInstance();
        IndicatorsPackage.eINSTANCE.getEFactoryInstance();
        PatternPackage.eINSTANCE.getEFactoryInstance();
        CategoryPackage.eINSTANCE.getEFactoryInstance();
        org.talend.dataquality.expressions.ExpressionsPackage.eINSTANCE.getEFactoryInstance();
        org.talend.dataquality.reports.ReportsPackage.eINSTANCE.getEFactoryInstance();
        DefinitionPackage.eINSTANCE.getEFactoryInstance();
        org.talend.dataquality.properties.PropertiesPackage.eINSTANCE.getEFactoryInstance();

        // CWM generated packages
        // TODO scorreia add other packages
        orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentPackage.eINSTANCE.getEFactoryInstance();
        orgomg.cwm.resource.relational.RelationalPackage.eINSTANCE.getEFactoryInstance();

        orgomg.cwmmip.CwmmipPackage.eINSTANCE.getEFactoryInstance();
        orgomg.mof.model.ModelPackage.eINSTANCE.getEFactoryInstance();
        orgomg.cwm.foundation.datatypes.DatatypesPackage.eINSTANCE.getEFactoryInstance();
        orgomg.cwm.objectmodel.core.CorePackage.eINSTANCE.getEFactoryInstance();
        orgomg.cwm.objectmodel.relationships.RelationshipsPackage.eINSTANCE.getEFactoryInstance();
        orgomg.cwm.foundation.typemapping.TypemappingPackage.eINSTANCE.getEFactoryInstance();
    }

    /**
     * DOC bZhou Comment method "isAnalysisFile".
     * 
     * @param fileExtension
     * @return
     */
    public static boolean isAnalysisFile(String fileExtension) {
        return StringUtils.equalsIgnoreCase(ANA, fileExtension);
    }

    /**
     * DOC bZhou Comment method "isReportFile".
     * 
     * @param fileExtension
     * @return
     */
    public static boolean isReportFile(String fileExtension) {
        return StringUtils.equalsIgnoreCase(REP, fileExtension);
    }

    /**
     * DOC bZhou Comment method "isProvFile".
     * 
     * @param fileExtension
     * @return
     */
    public static boolean isProvFile(String fileExtension) {
        return StringUtils.equalsIgnoreCase(PROV, fileExtension);
    }

    /**
     * DOC bZhou Comment method "isPatternFile".
     * 
     * @param fileExtension
     * @return
     */
    public static boolean isPatternFile(String fileExtension) {
        return StringUtils.equalsIgnoreCase(PATTERN, fileExtension);
    }

    /**
     * DOC bZhou Comment method "isDQRuleFile".
     * 
     * @param fileExtension
     * @return
     */
    public static boolean isDQRuleFile(String fileExtension) {
        return StringUtils.equalsIgnoreCase(DQRULE, fileExtension);
    }

    /**
     * DOC bZhou Comment method "isUDIFile".
     * 
     * @param fileExtension
     * @return
     */
    public static boolean isUDIFile(String fileExtension) {
        return StringUtils.equalsIgnoreCase(DEFINITION, fileExtension);
    }

    /**
     * DOC bZhou Comment method "isSQLFile".
     * 
     * @param fileExtension
     * @return
     */
    public static boolean isSQLFile(String fileExtension) {
        return StringUtils.equalsIgnoreCase(SQL, fileExtension);
    }

    /**
     * DOC bZhou Comment method "isCATFile".
     * 
     * @param fileExtension
     * @return
     */
    public static boolean isCATFile(String fileExtension) {
        return StringUtils.equalsIgnoreCase(CAT, fileExtension);
    }

    /**
     * DOC bZhou Comment method "isEmfFile".
     * 
     * @return
     */
    public static boolean isEmfFile(String fileExt) {
        return getExtensions().contains(fileExt);
    }

    /**
     * DOC bZhou Comment method "isJrxmlFile".
     * 
     * @param fileExt
     * @return
     */
    public static boolean isJrxmlFile(String fileExt) {
        return StringUtils.equalsIgnoreCase(fileExt, JRXML);
    }

    public static boolean isItemFile(String fileExt) {
        return StringUtils.equalsIgnoreCase(fileExt, ITEM_EXTENSION);
    }

    public enum EElementEName {
        UNKNOWN("Unknow", ""),

        ANALYSIS("Analysis", FactoriesUtil.ANA),
        REPORT("Report", FactoriesUtil.REP),
        PROVIDER("DataProvider", FactoriesUtil.PROV),
        PATTERN("Pattern", FactoriesUtil.PATTERN),
        RULE("Rule", FactoriesUtil.DQRULE),
        DEFINITION("Definition", FactoriesUtil.DEFINITION),
        JRXML("ReportTemplate", FactoriesUtil.JRXML),
        SQL("SQLFile", FactoriesUtil.SQL),
        ITEM("Item", FactoriesUtil.ITEM_EXTENSION),
        PROPERTY("Property", FactoriesUtil.PROPERTIES_EXTENSION);

        String name, fileExt;

        EElementEName(String name, String fileExt) {
            this.name = name;
            this.fileExt = fileExt;
        }

        public String getFileExt() {
            return fileExt;
        }

        public String getName() {
            return name;
        }

        public static EElementEName findENameByExt(String fileExt) {
            for (EElementEName eName : values()) {
                if (eName.getFileExt().equalsIgnoreCase(fileExt)) {
                    return eName;
                }
            }

            return UNKNOWN;
        }
    }
}
