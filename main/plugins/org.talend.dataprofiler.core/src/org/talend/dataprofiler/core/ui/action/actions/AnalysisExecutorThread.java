// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.analysis.AnalysisExecutorSelector;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class AnalysisExecutorThread implements Runnable {

    private Analysis analysis;

    private IProgressMonitor monitor;

    public IProgressMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    private ReturnCode executed;

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public ReturnCode getExecuted() {
        return executed;
    }

    public void setExecuted(ReturnCode executed) {
        this.executed = executed;
    }

    /**
     * 
     * DOC xqliu Comment method "AnalysisRunner".
     * 
     * @param analysis
     */
    public AnalysisExecutorThread(Analysis analysis) {
        setAnalysis(analysis);
    }

    /**
     * 
     * DOC xqliu AnalysisExecutorThread constructor comment.
     * 
     * @param analysis
     * @param monitor
     */
    public AnalysisExecutorThread(Analysis analysis, IProgressMonitor monitor) {
        setAnalysis(analysis);
        setMonitor(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {

        setExecuted(AnalysisExecutorSelector.executeAnalysis(getAnalysis(), getMonitor()));
    }

}
