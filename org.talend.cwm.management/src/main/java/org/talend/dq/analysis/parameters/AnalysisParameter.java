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
package org.talend.dq.analysis.parameters;

import org.talend.dataquality.analysis.AnalysisType;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class AnalysisParameter extends ConnectionParameter {

    private String analysisTypeName;

    /**
     * Getter for analysisTypeName.
     * 
     * @return the analysisTypeName
     */
    public String getAnalysisTypeName() {
        return analysisTypeName;
    }

    /**
     * Sets the analysisTypeName.
     * 
     * @param analysisTypeName the analysisTypeName to set
     */
    public void setAnalysisTypeName(String typeName) {
        analysisTypeName = typeName;
    }

    public AnalysisType getAnalysisType() {
        if (getAnalysisTypeName() != null) {

            return AnalysisType.get(getAnalysisTypeName());
        } else {

            return null;
        }
    }

    public String getAnalysisName() {

        return super.getName();
    }

    public String getAnalysisPurpose() {

        return super.getPurpose();
    }

    public String getAnalysisDescription() {

        return super.getDescription();
    }

    public String getAnalysisAuthor() {

        return super.getAuthor();
    }

    public String getAnalysisStatus() {

        return super.getStatus();
    }

    public String getAnalysisVersion() {

        return super.getVersion();
    }
}
