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

import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.impl.RegularExpressionImpl;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * created by yyin on 2012-8-13 Detailled comment
 * 
 */
public class UpdatePatternExpressionToTdExpressionTask extends AbstractWorksapceUpdateTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2012, 8, 13);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        IPath realtivePath = ResourceManager.getPatternRegexFolder().getFullPath().append("/text")//$NON-NLS-1$
                .makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        RootContainer<String, IRepositoryViewObject> tdqViewObjects = ProxyRepositoryFactory.getInstance()
                .getTdqRepositoryViewObjects(ERepositoryObjectType.TDQ_PATTERN_REGEX, realtivePath.toString());
        for (IRepositoryViewObject viewObject : tdqViewObjects.getMembers()) {
            Item item = viewObject.getProperty().getItem();
            Pattern pattern = null;
            if (item != null && item instanceof TDQPatternItem) {
                TDQPatternItem patternItem = (TDQPatternItem) item;
                pattern = patternItem.getPattern();
                EList<PatternComponent> components = pattern.getComponents();
                for (PatternComponent pComponet : components) {
                    // change each expression to TdExpression type.
                    RegularExpressionImpl regularExpress = (RegularExpressionImpl) pComponet;
                    Expression ex = regularExpress.getExpression();
                    if (ex instanceof TdExpression) {
                        continue;
                    }
                    TdExpression tdExpression = RelationalFactory.eINSTANCE.createTdExpression();
                    tdExpression.setBody(ex.getBody());
                    tdExpression.setLanguage(ex.getLanguage());
                    // tdExpression.setModificationDate(null);
                    regularExpress.setExpression(tdExpression);
                }
                ElementWriterFactory.getInstance().createPatternWriter().save(patternItem, true);
            }
        }
        return true;
    }

}
