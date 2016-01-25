// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.sql.converters;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.DomainFactory;
import org.talend.sqltools.ZQueryHelper;
import orgomg.cwm.resource.relational.ColumnSet;

import Zql.ZExpression;
import Zql.ZQuery;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class CwmZQuery {

    private static Logger log = Logger.getLogger(CwmZQuery.class);

    private final ZQuery zQuery = new ZQuery();

    /**
     * Method "addFrom" adds the name of the column set to the from part of the statement.
     * 
     * @param columnSet
     * @return true if at least one name is found.
     */
    public boolean addFrom(ColumnSet columnSet) {
        String[] columnSetNames = ColumnSetHelper.getColumnSetNames(columnSet);
        if (columnSetNames.length == 0) {
            return false;
        }
        this.zQuery.addFrom(ZQueryHelper.createFromVector(columnSetNames));
        return true;
    }

    /**
     * Method "addSelect" add the elements of the column set to the select statement.
     * 
     * @param columnSet a set of columns
     * @return
     */
    public boolean addSelect(ColumnSet columnSet) {
        return addSelect(ColumnSetHelper.getColumns(columnSet));
    }

    public boolean addSelect(Collection<? extends TdColumn> columns) {
        String[] columnFullNames = ColumnHelper.getColumnFullNames(columns);
        if (columnFullNames.length == 0) {
            return false;
        }
        this.zQuery.addSelect(ZQueryHelper.createSelectVector(columnFullNames));
        return true;
    }

    public boolean addSelect(TdColumn column) {
        return this.addSelect(Collections.singleton(column));
    }

    /**
     * Method "addSelect" add the given string to the select part of the statement.
     * 
     * @param selectPart the select part
     * @return true
     */
    public boolean addSelect(String... selectPart) {
        this.zQuery.addSelect(ZQueryHelper.createSelectVector(selectPart));
        return true;
    }

    /**
     * Method "evalFromStatement" evaluates the different ColumnSets used in the given set of columns.
     * 
     * @param columnSet views only as a set of column that could belong to different Tables or views.
     * @return the list of Tables or Views
     */
    public List<ColumnSet> evalFromStatement(ColumnSet columnSet) {
        return evalFromStatement(ColumnSetHelper.getColumns(columnSet));
    }

    public List<ColumnSet> evalFromStatement(Collection<? extends TdColumn> columns) {
        // TODO implement me
        return null;
    }

    public boolean addWhere(CwmZExpression<?> zExpr) {
        ZExpression expr = zExpr.generateZExpression();
        if (expr == null) {
            return false;
        }
        this.zQuery.addWhere(expr);
        return true;
    }

    public String generateStatement() {
        try {
            return this.zQuery.toString();
        } catch (RuntimeException e) {
            log.error(e, e);
        }
        return null;
    }

    /**
     * Method "generateDomain" generates a Domain from the current statement.
     * 
     * @return the generated domain
     */
    public Domain generateDomain() {
        Domain domain = DomainFactory.eINSTANCE.createDomain();

        return domain;
    }

    /**
     * Method "addFrom".
     * 
     * @param columnSets the column sets (table, views) to add in the from part of the SQL statement.
     * @return true if ok
     */
    public boolean addFrom(Set<? extends ColumnSet> columnSets) {
        // TODO scorreia maybe check that all column sets (table, views) belong to the same database.
        Set<String> columnSetNames = new HashSet<String>();
        for (ColumnSet columnSet : columnSets) {
            String name = columnSet.getName();
            if (name == null) {
                log.error(Messages.getString("CwmZQuery.NullName", columnSet)); //$NON-NLS-1$
                return false;
            }
            columnSetNames.add(name);
        }

        if (columnSetNames.isEmpty()) {
            return false;
        }
        this.zQuery.addFrom(ZQueryHelper.createFromVector(columnSetNames.toArray(new String[columnSetNames.size()])));

        return true;
    }
}
