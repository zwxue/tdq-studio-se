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
package org.talend.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public enum EResourceConstant {

    DATA_PROFILING("TDQ_Data Profiling", "TDQ_Data Profiling", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    LIBRARIES("TDQ_Libraries", "TDQ_Libraries", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    METADATA("TDQ_Metadata", "TDQ_Metadata", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    ANALYSIS("Analyses", "TDQ_Data Profiling/Analyses", ResourceConstant.READONLY),
    REPORTS("Reports", "TDQ_Data Profiling/Reports", ResourceConstant.READONLY),
    EXCHANGE("Exchange", "TDQ_Libraries/Exchange", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    INDICATORS("Indicators", "TDQ_Libraries/Indicators", ResourceConstant.READONLY, ResourceConstant.NO_SUBFOLDER),
    JRXML_TEMPLATE("JRXML Template", "TDQ_Libraries/JRXML Template", ResourceConstant.READONLY),
    PATTERNS("Patterns", "TDQ_Libraries/Patterns", ResourceConstant.READONLY),
    RULES("Rules", "TDQ_Libraries/Rules", ResourceConstant.READONLY),
    SOURCE_FILES("Source Files", "TDQ_Libraries/Source Files", ResourceConstant.READONLY),
    USER_DEFINED_INDICATORS(
                            "User Defined Indicators",
                            "TDQ_Libraries/Indicators/User Defined Indicators",
                            ResourceConstant.READONLY),
    PATTERN_REGEX("Regex", "TDQ_Libraries/Patterns/Regex", ResourceConstant.READONLY),
    PATTERN_SQL("SQL", "TDQ_Libraries/Patterns/SQL", ResourceConstant.READONLY),
    RULES_SQL("SQL", "TDQ_Libraries/Rules/SQL", ResourceConstant.READONLY),
    DB_CONNECTIONS("DB Connections", "TDQ_Metadata/DB Connections", ResourceConstant.READONLY),
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
            allPathes.add(new Path(constant.getPath()));
        }

        return allPathes.toArray(new Path[allPathes.size()]);
    }

    /**
     * DOC bzhou Comment method "findQualificationsByPath".
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
}
