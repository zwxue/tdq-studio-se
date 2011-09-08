//============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2011 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
//============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.migration.helper.IndicatorDefinitionFileHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.dbms.DbmsLanguageFactory;


/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class UpdateOracleAverageLengthTask extends AbstractWorksapceUpdateTask {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        // TODO Auto-generated method stub
        return createDate(2011, 9, 8);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        IndicatorDefinition aveWithBlank = IndicatorDefinitionFileHelper.getSystemIndicatorByName("Average Length With Blank");
        IndicatorDefinition aveWithNull = IndicatorDefinitionFileHelper.getSystemIndicatorByName("Average Length With Null");
        IndicatorDefinition aveWithBlankNull = IndicatorDefinitionFileHelper
                .getSystemIndicatorByName("Average Length With Blank and Null");

        String newExpWithBlank = "SELECT SUM(LENGTH(<%=__COLUMN_NAMES__%>)), COUNT(*) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>";
        setNewExpression(aveWithBlank, newExpWithBlank);
        setNewExpression(aveWithNull, newExpWithBlank);
        setNewExpression(aveWithBlankNull, newExpWithBlank);
        return true;
    }

    private void setNewExpression(IndicatorDefinition indDefinition, String newExpOracle) {
        if (indDefinition == null) {
            return;
        }
        EList<TdExpression> sqlExpressions = indDefinition.getSqlGenericExpression();
        String languge = null;
        for (TdExpression tdExp : sqlExpressions) {
            languge = tdExp.getLanguage();
            if (DbmsLanguageFactory.isOracle(languge)) {
                tdExp.setBody(newExpOracle);
            }
        }
        IndicatorDefinitionFileHelper.save(indDefinition);
    }

}
