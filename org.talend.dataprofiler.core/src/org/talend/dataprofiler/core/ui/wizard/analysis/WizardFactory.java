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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.wizard.Wizard;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.pattern.CreatePatternWizard;
import org.talend.dataprofiler.core.sql.CreateSqlFileWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.ConnectionWizard;
import org.talend.dataprofiler.core.ui.wizard.database.DatabaseConnectionWizard;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.analysis.parameters.DBConnectionParameter;

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
            return new ColumnWizard(parameter);
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
                parameter = new ConnectionAnalysisParameter();
            }
            parameter.setAnalysisTypeName(type.getLiteral());
            return new ConnectionWizard((ConnectionAnalysisParameter) parameter);
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

    public static Wizard createSqlFileWizard(IFolder folder) {
        ConnectionParameter parameter = new ConnectionAnalysisParameter();
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
