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
package org.talend.dataprofiler.core.migration.helper;

import org.apache.log4j.Logger;
import org.talend.dataquality.expressions.TdExpression;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * @author yyi
 * 
 * This class helps to update the splited indicator files.
 */
public class IndicatorDefinitionFileHelper {

    private static Logger log = Logger.getLogger(IndicatorDefinitionFileHelper.class);

    private static DefinitionHandler handler = DefinitionHandler.getInstance();

    /**
     * Get system indiactor by name.
     * 
     * @param name
     * @return IndicatorDefinition with the same name, null if the indiactor not find
     */
    public static IndicatorDefinition getSystemIndicatorByName(String name) {
        return DefinitionHandler.getInstance().getIndicatorDefinition(name);
    }

    /**
     * Add a sql expression for indicator
     * 
     * @param definition
     * @param language
     * @param body
     * @return true if sql expression be added.
     */
    public static boolean addSqlExpression(IndicatorDefinition definition, String language, String body) {
        if (null == definition)
            return false;

        TdExpression e = BooleanExpressionHelper.createTdExpression(language, body);
        return definition.getSqlGenericExpression().add(e);
    }

    /**
     * Save an indicator
     * 
     * @param definition
     * @return true if indicator saved.
     */
    public static boolean save(IndicatorDefinition definition) {
        return IndicatorResourceFileHelper.getInstance().save(definition).isOk();
    }
}
