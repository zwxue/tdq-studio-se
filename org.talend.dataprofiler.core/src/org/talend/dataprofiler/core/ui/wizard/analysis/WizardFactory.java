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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.wizard.Wizard;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.pattern.CreatePatternWizard;
import org.talend.dataprofiler.core.sql.CreateSqlFileWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.catalog.CatalogAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnTimeWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.ConnectionAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.schema.SchemaAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.database.DatabaseConnectionWizard;
import org.talend.dataprofiler.core.ui.wizard.dqrules.NewDQRulesWizard;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.analysis.parameters.AnalysisLabelParameter;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;

/**
 * @author zqin
 * 
 */
public class WizardFactory {

    public static Wizard createAnalysisWizard(AnalysisType type, AnalysisParameter parameter) {
        assert type != null;

        switch (type) {
        case COLUMN_CORRELATION:
            if (parameter == null) {
                parameter = new AnalysisParameter();
            }
            parameter.setAnalysisTypeName(type.getLiteral());
            if (((AnalysisLabelParameter) parameter).getCategoryLabel().trim().equals("Numerical Correlation Analysis")) { //$NON-NLS-1$
                return new ColumnWizard(parameter);
            } else {
                return new ColumnTimeWizard(parameter);
            }
        case MULTIPLE_COLUMN:
            if (parameter == null) {
                parameter = new AnalysisParameter();
            }
            parameter.setAnalysisTypeName(type.getLiteral());
            return new ColumnWizard(parameter);
        case COLUMNS_COMPARISON:
            if (parameter == null) {
                parameter = new AnalysisParameter();
            }
            parameter.setAnalysisTypeName(type.getLiteral());
            return new ColumnWizard(parameter);
        case CONNECTION:
            if (parameter == null) {
                parameter = new AnalysisFilterParameter();
            }
            parameter.setAnalysisTypeName(type.getLiteral());
            return new ConnectionAnalysisWizard((AnalysisFilterParameter) parameter);

            // MOD mzhao 2009-02-03 CATALOG and SCHEMA should use
            // PackagesAnalyisParameter.
        case CATALOG:
            if (parameter == null) {
                parameter = new PackagesAnalyisParameter();
            }
            parameter.setAnalysisTypeName(type.getLiteral());
            return new CatalogAnalysisWizard((PackagesAnalyisParameter) parameter);

        case SCHEMA:
            if (parameter == null) {
                parameter = new PackagesAnalyisParameter();
            }
            parameter.setAnalysisTypeName(type.getLiteral());
            return new SchemaAnalysisWizard((PackagesAnalyisParameter) parameter);
        default:
            return null;
        }
    }

    public static Wizard createAnalysisWizard(AnalysisType type) {
        return createAnalysisWizard(type, null);
    }

    public static CreateNewAnalysisWizard createNewAnalysisWizard() {
        return new CreateNewAnalysisWizard();
    }

    /**
     * 
     * DOC xqliu Comment method "createNewDQRuleWizard".
     * 
     * @param parameter
     * @return
     */
    public static NewDQRulesWizard createNewDQRuleWizard(ConnectionParameter parameter) {
        return new NewDQRulesWizard(parameter);
    }

    public static Wizard createSqlFileWizard(IFolder folder) {
        ConnectionParameter parameter = new AnalysisFilterParameter();
        FolderProvider folderProvider = parameter.getFolderProvider();
        if (folderProvider == null) {
            folderProvider = new FolderProvider();
        }

        folderProvider.setFolderResource(folder);
        parameter.setFolderProvider(folderProvider);
        return new CreateSqlFileWizard(parameter);
    }

    public static Wizard createPatternWizard(ExpressionType type) {
        return createPatternWizard(type, null);
    }

    public static Wizard createPatternWizard(ExpressionType type, ConnectionParameter parameter) {
        if (parameter == null) {
            parameter = new ConnectionParameter();
        }
        return new CreatePatternWizard(parameter, type);
    }

    public static Wizard createPatternWizard(ExpressionType type, ConnectionParameter parameter, String expression,
            String language) {
        if (parameter == null) {
            parameter = new ConnectionParameter();
        }
        return new CreatePatternWizard(parameter, type, expression, language);
    }

    public static Wizard createDatabaseConnectionWizard(DBConnectionParameter connectionParam) {

        return new DatabaseConnectionWizard(connectionParam);
    }
}
