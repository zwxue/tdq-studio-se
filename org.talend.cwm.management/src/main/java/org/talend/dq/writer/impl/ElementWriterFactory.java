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
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.writer.AElementPersistance;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.foundation.softwaredeployment.SoftwareSystem;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;
import orgomg.cwmx.analysis.informationset.Rule;

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

    /**
     * DOC bZhou Comment method "create".
     * 
     * @param element
     * @return
     */
    public AElementPersistance create(ModelElement element) {
        if (element instanceof Analysis) {
            return createAnalysisWrite();
        } else if (element instanceof Report) {
            return createReportWriter();
        } else if (element instanceof DataProvider) {
            return createDataProviderWriter();
        } else if (element instanceof Pattern) {
            return createPatternWriter();
        } else if (element instanceof IndicatorDefinition) {
            return createUDIndicatorWriter();
        } else if (element instanceof Rule) {
            return createdRuleWriter();
        } else if (element instanceof SoftwareSystem) {
            return createSoftwareSystemWriter();
        } else if (element instanceof IndicatorDefinition) {
            return createSYSIndicatorWriter();
        }

        return null;
    }
}
