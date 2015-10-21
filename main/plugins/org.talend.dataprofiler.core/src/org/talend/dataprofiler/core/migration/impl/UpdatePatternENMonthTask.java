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
package org.talend.dataprofiler.core.migration.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;

public class UpdatePatternENMonthTask extends AbstractWorksapceUpdateTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2015, 10, 21);
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
        // Update EN Month.pattern.
        IFile file = ResourceManager.getPatternRegexFolder().getFolder("date").getFile("EN_Month_0.1.pattern"); //$NON-NLS-1$ //$NON-NLS-2$
        if (file.exists()) {
            Pattern enMonthPattern = PatternResourceFileHelper.getInstance().findPattern(file);
            if (enMonthPattern != null) {
                List<PatternComponent> componentLs = new ArrayList<PatternComponent>();
                String regexBody = "'^(January|June|July|February|March|May|April|August|September|October|November|December)$'"; //$NON-NLS-1$
                componentLs.add(BooleanExpressionHelper.createRegularExpression("SQL", regexBody, ExpressionType.REGEXP)); //$NON-NLS-1$
                enMonthPattern.getComponents().clear();
                enMonthPattern.getComponents().addAll(componentLs);

                ReturnCode rc = PatternResourceFileHelper.getInstance().save(enMonthPattern);

                return rc.isOk();
            }
        }
        return true;
    }

}
