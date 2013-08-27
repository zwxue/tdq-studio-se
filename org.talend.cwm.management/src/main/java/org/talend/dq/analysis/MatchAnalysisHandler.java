// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis;

import java.util.Collection;

import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyin  class global comment. Detailled comment
 */
public class MatchAnalysisHandler extends AnalysisHandler {

    private String DEFAULT_LOADED_ROW_COUNT = "default_loaded_row_count";

    public boolean addColumnToAnalyze(ModelElement modelElement) {
        assert analysis != null;
        assert analysis.getContext() != null;
        return analysis.getContext().getAnalysedElements().add(modelElement);
    }

    public boolean addColumnsToAnalyze(Collection<ModelElement> modelElement) {
        assert analysis != null;
        assert analysis.getContext() != null;
        return analysis.getContext().getAnalysedElements().addAll(modelElement);
    }

    public boolean setColumnsToAnalyze(Collection<ModelElement> modelElement) {
        assert analysis != null;
        assert analysis.getContext() != null;
        analysis.getContext().getAnalysedElements().clear();
        return analysis.getContext().getAnalysedElements().addAll(modelElement);
    }

    /**
     * DOC yyin Comment method "changeDefaultRowLoaded".
     * 
     * @param text
     */
    public void changeDefaultRowLoaded(String text) {
        analysis.getParameters().setMaxNumberRows(Integer.valueOf(text));
    }

    /**
     * find in analysis's parameter if has: DefaultLoadedRowCount".if has:get its value, if not, create one
     * 
     * @return
     */
    public String getDefaultLoadedRowCount() {
        return String.valueOf(analysis.getParameters().getMaxNumberRows());
    }


}
