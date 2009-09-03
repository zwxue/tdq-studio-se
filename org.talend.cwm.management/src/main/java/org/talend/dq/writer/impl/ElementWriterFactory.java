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
package org.talend.dq.writer.impl;


/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ElementWriterFactory {

    private static ElementWriterFactory instance;

    private ElementWriterFactory() {
    }

    /**
     * DOC bZhou Comment method "getInstance".
     * 
     * @return
     */
    public static ElementWriterFactory getInstance() {
        if (instance == null) {
            instance = new ElementWriterFactory();
        }
        return instance;
    }

    /**
     * DOC bZhou Comment method "createAnalysisWrite".
     * 
     * @return
     */
    public AnalysisWriter createAnalysisWrite() {
        return new AnalysisWriter();
    }

    /**
     * DOC bZhou Comment method "createReportWriter".
     * 
     * @return
     */
    public ReportWriter createReportWriter() {
        return new ReportWriter();
    }

    /**
     * DOC bZhou Comment method "createDataProviderWriter".
     * 
     * @return
     */
    public DataProviderWriter createDataProviderWriter() {
        return new DataProviderWriter();
    }

    /**
     * DOC bZhou Comment method "createPatternWriter".
     * 
     * @return
     */
    public PatternWriter createPatternWriter() {
        return new PatternWriter();
    }

    /**
     * DOC bZhou Comment method "createUDIndicatorWriter".
     * 
     * @return
     */
    public UDIndicatorWriter createUDIndicatorWriter() {
        return new UDIndicatorWriter();
    }
}
