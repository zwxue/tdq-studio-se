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
package org.talend.dataprofiler.core.migration.helper;

import org.talend.cwm.relational.TdExpression;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.definition.CharactersMapping;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * @author yyi
 * 
 * This class helps to update the splited indicator files.
 */
public final class IndicatorDefinitionFileHelper {

    private IndicatorDefinitionFileHelper() {
    }

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
     * Add a sql expression for indicator.
     * 
     * @param definition
     * @param language
     * @param body
     * @return true if sql expression be added.
     */
    public static boolean addSqlExpression(IndicatorDefinition definition, String language, String body) {
        if (null == definition) {
            return false;
        }

        TdExpression e = BooleanExpressionHelper.createTdExpression(language, body);
        return definition.getSqlGenericExpression().add(e);
    }

    /**
     * keep the modify date
     * 
     * @param definition
     * @param language
     * @param body
     * @param modifyDate
     * @return
     */
    public static boolean addSqlExpression(IndicatorDefinition definition, String language, String body, String modifyDate) {
        if (null == definition) {
            return false;
        }

        TdExpression e = BooleanExpressionHelper.createTdExpression(language, body);
        e.setModificationDate(modifyDate);
        return definition.getSqlGenericExpression().add(e);
    }

    /**
     * Add a sql expression for indicator.
     * 
     * @param definition
     * @param name
     * @param language
     * @param body
     * @param modifyDate
     * @return
     */
    public static boolean addSqlExpression(IndicatorDefinition definition, String name, String language, String body,
            String modifyDate) {
        if (null == definition) {
            return false;
        }

        TdExpression e = BooleanExpressionHelper.createTdExpression(language, body);
        e.setName(name);
        e.setModificationDate(modifyDate);
        return definition.getSqlGenericExpression().add(e);
    }

    /**
     * remove a sql expression for indicator.
     * 
     * @param definition
     * @param language
     * @param body
     * @return true if sql expression be removed.
     */
    public static boolean removeSqlExpression(IndicatorDefinition definition, String language) {
        if (null == definition) {
            return false;
        }
        TdExpression removeExpression = null;
        for (TdExpression e : definition.getSqlGenericExpression()) {
            if (e.getLanguage().equals(language)) {
                removeExpression = e;
            }
        }
        if (removeExpression != null) {
            return definition.getSqlGenericExpression().remove(removeExpression);
        }
        return false;

    }

    /**
     * remove a sql expression by name.
     * 
     * @param definition
     * @param name
     * @return
     */
    public static boolean removeSqlExpressionByName(IndicatorDefinition definition, String name) {
        if (null == definition || null == name) {
            return false;
        }
        TdExpression removeExpression = null;
        for (TdExpression e : definition.getSqlGenericExpression()) {
            if (name.equals(e.getName())) {
                removeExpression = e;
            }
        }
        if (removeExpression != null) {
            return definition.getSqlGenericExpression().remove(removeExpression);
        }
        return false;

    }

    /**
     * Save an indicator.
     * 
     * @param definition
     * @return true if indicator saved.
     */
    public static boolean save(IndicatorDefinition definition) {
        return IndicatorResourceFileHelper.getInstance().save(definition).isOk();
    }

    /**
     * 
     * judge if exist a sq expression with specify language.
     * 
     * @param definition
     * @param language
     * @return
     */
    public static boolean isExistSqlExprWithLanguage(IndicatorDefinition definition, String language) {
        if (null == definition) {
            return false;
        }
        for (TdExpression e : definition.getSqlGenericExpression()) {
            if (e.getLanguage().equals(language)) {
                return true;
            }
        }
        return false;
    }

    public static boolean addCharacterMapping(IndicatorDefinition definition, String newLanguage, String name,
            String newCharactersToReplace, String newReplacementCharacters) {
        if (null == definition) {
            return false;
        }
        CharactersMapping newMapping = DefinitionFactory.eINSTANCE.createCharactersMapping();
        newMapping.setLanguage(newLanguage);
        newMapping.setCharactersToReplace(newCharactersToReplace);
        newMapping.setReplacementCharacters(newReplacementCharacters);
        newMapping.setName(name);

        return definition.getCharactersMapping().add(newMapping);
    }
}
