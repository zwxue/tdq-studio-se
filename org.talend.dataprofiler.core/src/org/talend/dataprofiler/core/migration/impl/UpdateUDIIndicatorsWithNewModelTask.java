// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceService;

/**
 * Update UDI Indicators With the New Model from version 5.3.
 * 
 * @author msjian 2013-2-25 TDQ-6872
 * 
 */
public class UpdateUDIIndicatorsWithNewModelTask extends AbstractWorksapceUpdateTask {

    Logger log = Logger.getLogger(UpdateUDIIndicatorsWithNewModelTask.class);

    private String old_one = "xmlns:dataquality.indicators.definition=\"http://dataquality.indicators.definition\""; //$NON-NLS-1$

    private String new_one = "xmlns:dataquality.indicators.definition.userdefine=\"http://dataquality.indicators.definition.userdefine\""; //$NON-NLS-1$

    private String old_two = "dataquality.indicators.definition:IndicatorDefinition"; //$NON-NLS-1$

    private String new_two = "dataquality.indicators.definition.userdefine:UDIndicatorDefinition"; //$NON-NLS-1$

    public Date getOrder() {
        return createDate(2013, 2, 25);
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    @Override
    protected boolean doExecute() throws Exception {
        boolean result = true;
        File udiIndicatorFolder = getWorkspacePath().append(EResourceConstant.USER_DEFINED_INDICATORS.getPath()).toFile();

        String[] indicatorFileExtentionName = { FactoriesUtil.DEFINITION };

        try {
            result &= FilesUtils.migrateFolder(udiIndicatorFolder, indicatorFileExtentionName, initIndicatorReplaceMap(), log);
            if (isWorksapcePath()) {
                for (IRepositoryViewObject viewObject : ProxyRepositoryFactory.getInstance().getAll(
                        ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS)) {
                    ProxyRepositoryFactory.getInstance().reload(viewObject.getProperty());
                }
            }
            ResourceService.refreshStructure();
        } catch (Exception e) {
            result = false;
            log.error(e, e);
        }

        List<IndicatorDefinition> indiDefinitions = DefinitionHandler.getInstance().getIndicatorsDefinitions();
        for (IndicatorDefinition indiDefinition : indiDefinitions) {
            if (indiDefinition != null) {
                if (indiDefinition instanceof UDIndicatorDefinition) {
                    IndicatorCategory category = IndicatorCategoryHelper.getCategory(indiDefinition);
                    if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                        // set default value from templates
                        EList<TdExpression> viewValidRowsList = ((UDIndicatorDefinition) indiDefinition)
                                .getViewValidRowsExpression();
                        EList<TdExpression> viewInvalidRowsList = ((UDIndicatorDefinition) indiDefinition)
                                .getViewInvalidRowsExpression();
                        EList<TdExpression> viewValidValuesList = ((UDIndicatorDefinition) indiDefinition)
                                .getViewValidValuesExpression();
                        EList<TdExpression> viewInvalidValuesList = ((UDIndicatorDefinition) indiDefinition)
                                .getViewInvalidValuesExpression();

                        EList<TdExpression> sqlGenericExpression = indiDefinition.getSqlGenericExpression();
                        if (sqlGenericExpression != null) {
                            for (TdExpression tdExp : sqlGenericExpression) {
                                String language = tdExp.getLanguage();
                                String version = tdExp.getVersion();

                                // if not exist, add one.(when do migration more than one time, will add one)
                                if (!UDIUtils.checkExistInList(viewValidRowsList, language, version)) {
                                    // for match is View Valid Rows template
                                    String body = UDIHelper.getQueryFromTemplates(2, language, category);
                                    viewValidRowsList.add(UDIUtils.createNewTdExpression(language, version,
                                            replaceQueryForMatchUDI(body)));
                                }

                                if (!UDIUtils.checkExistInList(viewInvalidRowsList, language, version)) {
                                    // for match is View Invalid Rows Template
                                    String body = UDIHelper.getQueryFromTemplates(3, language, category);
                                    viewInvalidRowsList.add(UDIUtils.createNewTdExpression(language, version,
                                            replaceQueryForMatchUDI(body)));
                                }

                                if (!UDIUtils.checkExistInList(viewValidValuesList, language, version)) {
                                    // for match is View Valid Values Template
                                    String body = UDIHelper.getQueryFromTemplates(4, language, category);
                                    viewValidValuesList.add(UDIUtils.createNewTdExpression(language, version,
                                            replaceQueryForMatchUDI(body)));

                                }

                                if (!UDIUtils.checkExistInList(viewInvalidValuesList, language, version)) {
                                    // for match is View Invalid Values Template
                                    String body = UDIHelper.getQueryFromTemplates(5, language, category);
                                    viewInvalidValuesList.add(UDIUtils.createNewTdExpression(language, version,
                                            replaceQueryForMatchUDI(body)));
                                }
                            }
                        }
                    } else {
                        // for others is view rows template
                        EList<TdExpression> viewRowsList = ((UDIndicatorDefinition) indiDefinition).getViewRowsExpression();
                        EList<TdExpression> sqlGenericExpression = indiDefinition.getSqlGenericExpression();
                        if (sqlGenericExpression != null) {
                            for (TdExpression tdExp : sqlGenericExpression) {
                                String language = tdExp.getLanguage();
                                String version = tdExp.getVersion();

                                // if not exist, add one.(when do migration more than one time, will add one)
                                if (!UDIUtils.checkExistInList(viewRowsList, language, version)) {
                                    String body = UDIHelper.getQueryFromTemplates(2, language, category);

                                    GenericSQLHandler genericSQLHandler = new GenericSQLHandler(body);
                                    if (IndicatorCategoryHelper.isUserDefRealValue(category)) {
                                        // replace <COLUMN_EXPRESSION_TEXT_FIELD>
                                        genericSQLHandler.replaceUDIColumn(PluginConstant.EMPTY_STRING);

                                    } else if (IndicatorCategoryHelper.isUserDefFrequency(category)) {
                                        // replace <FIRST_COLUMN_EXPRESSION_TEXT_FIELD>
                                        // replace <SECOND_COLUMN_EXPRESSION_TEXT_FIELD>
                                        genericSQLHandler.replaceUDIFirstColumn(PluginConstant.EMPTY_STRING)
                                                .replaceUDISecondColumn(PluginConstant.EMPTY_STRING);
                                    }
                                    viewRowsList.add(UDIUtils.createNewTdExpression(language, version, genericSQLHandler
                                            .replaceUDIQueryToMatch().getSqlString()));
                                }
                            }
                        }

                    }
                    result &= ElementWriterFactory.getInstance().createIndicatorDefinitionWriter().save(indiDefinition).isOk();
                }
            }
        }
        DefinitionHandler.getInstance().reloadIndicatorsDefinitions();
        return result;
    }

    /**
     * DOC msjian Comment method "replaceQueryForMatchUDI".
     * 
     * @param genericSQLHandler
     * @return
     */
    private String replaceQueryForMatchUDI(String body) {
        GenericSQLHandler genericSQLHandler = new GenericSQLHandler(body);
        // replace <MATCHING_EXPRESSION_TEXT_FIELD>
        genericSQLHandler.replaceUDIMatching(PluginConstant.EMPTY_STRING);
        // replace <WHERE_TEXT_FIELD>
        genericSQLHandler.replaceUDIWhere(PluginConstant.EMPTY_STRING);

        return genericSQLHandler.replaceUDIQueryToMatch().getSqlString();
    }

    /**
     * DOC msjian Comment method "initIndicatorReplaceMap".
     * 
     * @return
     */
    private Map<String, String> initIndicatorReplaceMap() {

        Map<String, String> replaceStringMap = new HashMap<String, String>();

        replaceStringMap.put(old_one, new_one);
        replaceStringMap.put(old_two, new_two);

        return replaceStringMap;
    }
}
