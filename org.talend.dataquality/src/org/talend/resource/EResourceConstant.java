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
package org.talend.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.DelimitedFileConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQJrxmlItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.properties.TDQSourceFileItem;
import org.talend.dataquality.properties.util.PropertiesSwitch;
import org.talend.dataquality.rules.WhereRule;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public enum EResourceConstant {

    // These three resource constant is compatible with version before 4.1.
    OLD_METADATA("TDQ_Metadata", "TDQ_Metadata", ResourceConstant.READONLY),
    OLD_DB_CONNECTIONS("DB Connections", "TDQ_Metadata/DB Connections", ResourceConstant.READONLY),
    OLD_MDM_CONNECTIONS("MDM Connections", "TDQ_Metadata/MDM Connections", ResourceConstant.READONLY),
    FOLDER("", ""),
    DATA_PROFILING("TDQ_Data Profiling", "TDQ_Data Profiling", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    LIBRARIES("TDQ_Libraries", "TDQ_Libraries", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    METADATA("metadata", "metadata", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    ANALYSIS("Analyses", "TDQ_Data Profiling/Analyses", ResourceConstant.READONLY),
    REPORTS("Reports", "TDQ_Data Profiling/Reports", ResourceConstant.READONLY),
    EXCHANGE("Exchange", "TDQ_Libraries/Exchange", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    INDICATORS("Indicators", "TDQ_Libraries/Indicators", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    USER_DEFINED_INDICATORS(
                            "User Defined Indicators",
                            "TDQ_Libraries/Indicators/User Defined Indicators",
                            ResourceConstant.READONLY),
    // MOD zhsne 18724: Java UDI enhancements add lib folder under UDI folder.
    USER_DEFINED_INDICATORS_LIB("lib", "TDQ_Libraries/Indicators/User Defined Indicators/lib", ResourceConstant.READONLY),
    SYSTEM_INDICATORS("System Indicators", "TDQ_Libraries/Indicators/System Indicators", ResourceConstant.READONLY),
    SYSTEM_INDICATORS_ADVANCED_STATISTICS(
                                          "Advanced Statistics",
                                          "TDQ_Libraries/Indicators/System Indicators/Advanced Statistics",
                                          ResourceConstant.READONLY),
    SYSTEM_INDICATORS_BUSINESS_RULES(
                                     "Business Rules",
                                     "TDQ_Libraries/Indicators/System Indicators/Business Rules",
                                     ResourceConstant.READONLY),
    SYSTEM_INDICATORS_CORRELATION(
                                  "Correlation",
                                  "TDQ_Libraries/Indicators/System Indicators/Correlation",
                                  ResourceConstant.READONLY),
    SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY(
                                            "Functional Dependency",
                                            "TDQ_Libraries/Indicators/System Indicators/Functional Dependency",
                                            ResourceConstant.READONLY),
    SYSTEM_INDICATORS_OVERVIEW("Overview", "TDQ_Libraries/Indicators/System Indicators/Overview", ResourceConstant.READONLY),
    SYSTEM_INDICATORS_PATTERN_FINDER(
                                     "Pattern Finder",
                                     "TDQ_Libraries/Indicators/System Indicators/Pattern Finder",
                                     ResourceConstant.READONLY),
    SYSTEM_INDICATORS_PATTERN_MATCHING(
                                       "Pattern Matching",
                                       "TDQ_Libraries/Indicators/System Indicators/Pattern Matching",
                                       ResourceConstant.READONLY),
    SYSTEM_INDICATORS_ROW_COMPARISON(
                                     "Row Comparison",
                                     "TDQ_Libraries/Indicators/System Indicators/Row Comparison",
                                     ResourceConstant.READONLY),
    SYSTEM_INDICATORS_SIMPLE_STATISTICS(
                                        "Simple Statistics",
                                        "TDQ_Libraries/Indicators/System Indicators/Simple Statistics",
                                        ResourceConstant.READONLY),
    SYSTEM_INDICATORS_SOUNDEX("Soundex", "TDQ_Libraries/Indicators/System Indicators/Soundex", ResourceConstant.READONLY),
    SYSTEM_INDICATORS_SUMMARY_STATISTICS(
                                         "Summary Statistics",
                                         "TDQ_Libraries/Indicators/System Indicators/Summary Statistics",
                                         ResourceConstant.READONLY),
    SYSTEM_INDICATORS_TEXT_STATISTICS(
                                      "Text Statistics",
                                      "TDQ_Libraries/Indicators/System Indicators/Text Statistics",
                                      ResourceConstant.READONLY),
    JRXML_TEMPLATE("JRXML Template", "TDQ_Libraries/JRXML Template", ResourceConstant.READONLY),
    PATTERNS("Patterns", "TDQ_Libraries/Patterns", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    PATTERN_REGEX("Regex", "TDQ_Libraries/Patterns/Regex", ResourceConstant.READONLY),
    PATTERN_SQL("SQL", "TDQ_Libraries/Patterns/SQL", ResourceConstant.READONLY),
    RULES("Rules", "TDQ_Libraries/Rules", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    RULES_SQL("SQL", "TDQ_Libraries/Rules/SQL", ResourceConstant.READONLY),
    SOURCE_FILES("Source Files", "TDQ_Libraries/Source Files", ResourceConstant.READONLY),
    DB_CONNECTIONS("connections", "metadata/connections", ResourceConstant.READONLY),
    MDM_CONNECTIONS("MDMconnections", "metadata/MDMconnections", ResourceConstant.READONLY),
    FILEDELIMITED("fileDelimited", "metadata/fileDelimited", ResourceConstant.READONLY),
    REPORTING_DB("TDQ_reporting_db", "REPORTING_DB", ResourceConstant.READONLY);

    private String name;

    private String path;

    private QualifiedName[] qualifications;

    EResourceConstant(String name, String path, QualifiedName... qualifications) {
        this.name = name;
        this.path = path;
        this.qualifications = qualifications;
    }

    /**
     * Getter for name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Getter for qualifications.
     * 
     * @return the qualifications
     */
    public QualifiedName[] getQualifications() {
        return qualifications;
    }

    /**
     * DOC bzhou Comment method "getPathes".
     * 
     * @return
     */
    public static IPath[] getPathes() {
        List<IPath> allPathes = new ArrayList<IPath>();

        for (EResourceConstant constant : values()) {
            if (constant != FOLDER) {
                allPathes.add(new Path(constant.getPath()));
            }
        }

        return allPathes.toArray(new Path[allPathes.size()]);
    }

    /**
     * 
     * This method is to find the matched Qualifications constant by path.
     * 
     * @param path
     * @return
     */
    public static QualifiedName[] findQualificationsByPath(String path) {
        for (EResourceConstant constant : values()) {
            if (StringUtils.equals(path, constant.getPath())) {
                return constant.getQualifications();
            }
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getTypedConstantList".
     * 
     * @return
     */
    public static List<EResourceConstant> getTypedConstantList() {
        List<EResourceConstant> constantList = new ArrayList<EResourceConstant>();

        constantList.add(ANALYSIS);
        constantList.add(REPORTS);
        constantList.add(DB_CONNECTIONS);
        constantList.add(MDM_CONNECTIONS);
        constantList.add(RULES_SQL);
        constantList.add(INDICATORS);
        constantList.add(JRXML_TEMPLATE);
        constantList.add(PATTERNS);
        constantList.add(SOURCE_FILES);

        return constantList;
    }

    /**
     * DOC bZhou Comment method "getTypedConstant".
     * 
     * @param element
     * @return
     */
    public static EResourceConstant getTypedConstant(ModelElement element) {

        EResourceConstant constatnt = null;

        if (element instanceof Analysis) {
            constatnt = ANALYSIS;
        } else if (element instanceof Report) {
            constatnt = REPORTS;
        } else if (element instanceof IndicatorDefinition) {
            if (element instanceof WhereRule) {
                constatnt = RULES_SQL;
            } else {
                constatnt = USER_DEFINED_INDICATORS;
            }
        } else if (element instanceof IndicatorsDefinitions) {
            constatnt = LIBRARIES;
        } else if (element instanceof Pattern) {
            constatnt = PATTERNS;
        } else if (element instanceof DatabaseConnection) {
            constatnt = DB_CONNECTIONS;
        } else if (element instanceof MDMConnection) {
            constatnt = MDM_CONNECTIONS;
        }

        return constatnt;
    }

    /**
     * DOC bZhou Comment method "getTypedConstant".
     * 
     * @param item
     * @return
     */
    public static EResourceConstant getTypedConstant(Item item) {
        EResourceConstant resConstant = getTypeConstantsFromTDQ(item);
        if (resConstant != null) {

            return resConstant;
        } else {
            return (EResourceConstant) new org.talend.core.model.properties.util.PropertiesSwitch() {

                @Override
                public Object caseDatabaseConnectionItem(DatabaseConnectionItem object) {
                    return DB_CONNECTIONS;
                }

                @Override
                public Object caseMDMConnectionItem(MDMConnectionItem object) {
                    return MDM_CONNECTIONS;
                }

                @Override
                public Object caseFolderItem(FolderItem object) {
                    return FOLDER;
                }

                @Override
                public Object caseDelimitedFileConnectionItem(DelimitedFileConnectionItem object) {
                    return FILEDELIMITED;
                }

            }.doSwitch(item);
        }

    }

    private static EResourceConstant getTypeConstantsFromTDQ(Item item) {
        return (EResourceConstant) new PropertiesSwitch<Object>() {

            @Override
            public Object caseTDQReportItem(TDQReportItem object) {
                return REPORTS;
            }

            @Override
            public Object caseTDQAnalysisItem(TDQAnalysisItem object) {
                return ANALYSIS;
            }

            @Override
            public Object caseTDQBusinessRuleItem(TDQBusinessRuleItem object) {
                return RULES_SQL;
            }

            @Override
            public Object caseTDQIndicatorDefinitionItem(TDQIndicatorDefinitionItem object) {
                return INDICATORS;
            }

            @Override
            public Object caseTDQPatternItem(TDQPatternItem object) {
                return PATTERNS;
            }

            @Override
            public Object caseTDQJrxmlItem(TDQJrxmlItem object) {
                return JRXML_TEMPLATE;
            }

            @Override
            public Object caseTDQSourceFileItem(TDQSourceFileItem object) {
                return SOURCE_FILES;
            }
        }.doSwitch(item);
    }

    /**
     * DOC bZhou Comment method "isMDMConnection".
     * 
     * @param provider
     * @return
     */
    @SuppressWarnings("unused")
    private static boolean isMDMConnection(DataProvider provider) {
        return SwitchHelpers.MDMCONNECTION_SWITCH.doSwitch(provider) == null ? false : true;
    }

    /**
     * 
     * This mehtod is to get the top level folder constant of TOP.
     * 
     * @return An array of EResourceConstant.
     */
    public static EResourceConstant[] getTopConstants() {
        return new EResourceConstant[] { DATA_PROFILING, LIBRARIES, METADATA };
    }

    /**
     * 
     * This method is to see whether the path is a current workspace constant path.
     * 
     * @param path
     * @return true if it is, otherwise false.
     */
    public static boolean isConstantPath(IPath path) {
        if (path != null) {
            for (IPath tmp : getPathes()) {
                if (tmp.equals(path)) {
                    return true;
                }
            }
        }
        return false;
    }
}
