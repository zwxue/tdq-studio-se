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
package org.talend.dq.analysis.parameters;

import orgomg.cwm.objectmodel.core.Package;

/**
 * The schema/catalog's analyis parameter.
 */
public class PackagesAnalyisParameter extends AnalysisFilterParameter {

    private Package[] packages;

    public Package[] getPackages() {
        return packages;
    }

    public void setPackages(Package[] packages) {
        this.packages = packages;
    }

}
