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
package org.talend.dq.writer.impl;

import org.talend.commons.emf.FactoriesUtil;
import org.talend.dq.writer.AElementPersistance;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ElementWriterFactory {

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

    /**
     * DOC bZhou Comment method "createdRuleWriter".
     * 
     * @return
     */
    public DQRuleWriter createdRuleWriter() {
        return new DQRuleWriter();
    }

    /**
     * DOC bZhou Comment method "createSoftwareSystemWriter".
     * 
     * @return
     */
    public SoftwareSystemWriter createSoftwareSystemWriter() {
        return new SoftwareSystemWriter();
    }

    /**
     * DOC bZhou Comment method "createSYSIndicatorWriter".
     * 
     * @return
     */
    public SYSIndicatorWriter createSYSIndicatorWriter() {
        return new SYSIndicatorWriter();
    }

    /**
     * DOC bZhou Comment method "create".
     * 
     * @param fileExtension
     * @return
     */
    public AElementPersistance create(String fileExtension) {

        if (FactoriesUtil.ANA.equals(fileExtension)) {
            return createAnalysisWrite();
        } else if (FactoriesUtil.REP.equals(fileExtension)) {
            return createReportWriter();
        } else if (FactoriesUtil.PROV.equals(fileExtension)) {
            return createDataProviderWriter();
        } else if (FactoriesUtil.PATTERN.equals(fileExtension)) {
            return createPatternWriter();
        } else if (FactoriesUtil.UDI.equals(fileExtension)) {
            return createUDIndicatorWriter();
        } else if (FactoriesUtil.DQRULE.equals(fileExtension)) {
            return createdRuleWriter();
        } else if (FactoriesUtil.SOFTWARE_SYSTEM.equals(fileExtension)) {
            return createSoftwareSystemWriter();
        } else if (FactoriesUtil.TALEND_DEFINITION.equals(fileExtension)) {
            return createSYSIndicatorWriter();
        }

        return null;
    }
}
