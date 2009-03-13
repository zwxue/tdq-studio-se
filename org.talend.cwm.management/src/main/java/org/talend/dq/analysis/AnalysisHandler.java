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
package org.talend.dq.analysis;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.RelationalPackage;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionInformations;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Table;

/**
 * DOC rli class global comment. Detailled comment
 */
public class AnalysisHandler {

    protected Analysis analysis;

    private ExecutionInformations resultMetadata;

    public AnalysisHandler() {
        super();
    }

    /**
     * Method "setAnalysis".
     * 
     * @param columnAnalysis the analysis to set
     */
    public void setAnalysis(Analysis columnAnalysis) {
        this.analysis = columnAnalysis;
        this.resultMetadata = columnAnalysis.getResults().getResultMetadata();
    }

    /**
     * Method "getAnalysis".
     * 
     * @return the analysis
     */
    public Analysis getAnalysis() {
        return this.analysis;
    }

    public String getName() {
        assert analysis != null;
        return this.analysis.getName();
    }

    public void setName(String name) {
        assert analysis != null;
        this.analysis.setName(name);
    }

    public String getPurpose() {
        assert analysis != null;
        return TaggedValueHelper.getPurpose(analysis);
    }

    public void setPurpose(String purpose) {
        assert analysis != null;
        TaggedValueHelper.setPurpose(purpose, analysis);
    }

    public String getDescription() {
        assert analysis != null;
        return TaggedValueHelper.getDescription(analysis);
    }

    public void setDescription(String description) {
        assert analysis != null;
        TaggedValueHelper.setDescription(description, analysis);
    }

    public String getAuthor() {

        assert analysis != null;
        return TaggedValueHelper.getAuthor(analysis);
    }

    public void setAuthor(String anthor) {

        assert analysis != null;
        TaggedValueHelper.setAuthor(analysis, anthor);
    }

    public String getStatus() {

        assert analysis != null;
        return TaggedValueHelper.getDevStatus(analysis).getLiteral();
    }

    public void setStatus(String status) {

        assert analysis != null;
        TaggedValueHelper.setDevStatus(analysis, DevelopmentStatus.get(status));
    }

    public void clearAnalysis() {
        assert analysis != null;
        assert analysis.getContext() != null;
        analysis.getContext().getAnalysedElements().clear();
        analysis.getResults().getIndicators().clear();
    }

    public String getExecuteData() {
        if (resultMetadata.getExecutionDate() != null) {
            DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT);

            return format.format(resultMetadata.getExecutionDate());
        } else {
            return ""; //$NON-NLS-1$
        }
    }

    public String getExecuteDuration() {
        return resultMetadata.getExecutionDuration() / 1000.0d + " s"; //$NON-NLS-1$
    }

    public String getExecuteNumber() {
        return String.valueOf(resultMetadata.getExecutionNumber());
    }

    public String getLastExecutionNumberOk() {
        if (resultMetadata != null) {
            return String.valueOf(resultMetadata.getLastExecutionNumberOk());
        } else {
            return "0"; //$NON-NLS-1$
        }
    }

    public ExecutionInformations getResultMetadata() {
        return resultMetadata;
    }

    public String getConnectionName() {
        return analysis.getContext().getConnection() == null ? "" : analysis.getContext().getConnection().getName(); //$NON-NLS-1$
    }

    public String getTableNames() {
        String str = ""; //$NON-NLS-1$
        for (String aStr : getColumnSetOwnerNames()) {
            str = str + aStr + " "; //$NON-NLS-1$
        }

        return str;
    }

    /**
     * Method "getSchemaNames".
     * 
     * @return the schema names concatenated or the empty string (never null)
     */
    public String getSchemaNames() {
        String str = ""; //$NON-NLS-1$
        for (ColumnSet columnSet : getColumnSets()) {
            Package schema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
            if (schema != null && RelationalPackage.eINSTANCE.getTdSchema().equals(schema.eClass())) {
                str = str + schema.getName() + " "; //$NON-NLS-1$
            }
        }
        return str;
    }

    /**
     * Method "getCatalogNames".
     * 
     * @return the catalog names concatenated or the empty string (never null)
     */
    public String getCatalogNames() {
        String str = ""; //$NON-NLS-1$
        for (ColumnSet columnSet : getColumnSets()) {
            Package schema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
            if (schema == null) {
                continue;
            }
            if (RelationalPackage.eINSTANCE.getTdCatalog().equals(schema.eClass())) {
                str = str + schema.getName() + " "; //$NON-NLS-1$
            } else {
                Package catalog = ColumnSetHelper.getParentCatalogOrSchema(schema);
                if (catalog != null) {
                    str = str + catalog.getName() + " "; //$NON-NLS-1$
                }
            }
        }
        return str;
    }

    public boolean isCatalogExisting() {
        return getCatalogNames().trim().length() != 0 ? true : false;
    }

    public boolean isSchemaExisting() {
        return getSchemaNames().trim().length() != 0 ? true : false;
    }

    private ColumnSet[] getColumnSets() {
        List<ColumnSet> existingTables = new ArrayList<ColumnSet>();

        for (ModelElement element : getAnalyzedColumns()) {
            if (element instanceof Column) {
                ColumnSet columnSet = ColumnHelper.getColumnSetOwner((Column) element);
                if (!existingTables.contains(columnSet)) {
                    existingTables.add(columnSet);
                }
            } else if (element instanceof Table) {
                ColumnSet columnSet = (ColumnSet) element;
                if (!existingTables.contains(columnSet)) {
                    existingTables.add(columnSet);
                }
            }
        }

        return existingTables.toArray(new ColumnSet[existingTables.size()]);
    }

    public EList<ModelElement> getAnalyzedColumns() {
        return analysis.getContext().getAnalysedElements();
    }

    private String[] getColumnSetOwnerNames() {
        List<String> existingTables = new ArrayList<String>();

        for (ModelElement element : getAnalyzedColumns()) {
            if (element instanceof Column) {
                String tableName = ColumnHelper.getColumnSetFullName((Column) element);
                if (!existingTables.contains(tableName)) {
                    existingTables.add(tableName);
                }
            } else if (element instanceof Table) {
                String tableName = ((Table) element).getName();
                if (!existingTables.contains(tableName)) {
                    existingTables.add(tableName);
                }
            }
        }

        return existingTables.toArray(new String[existingTables.size()]);
    }

}
