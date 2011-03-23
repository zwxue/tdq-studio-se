// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.sql;

import org.apache.log4j.Logger;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.ui.action.AbstractImportSourceFileAction;
import org.talend.dataquality.properties.PropertiesFactory;
import org.talend.dataquality.properties.TDQFileItem;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportSqlFileAction extends AbstractImportSourceFileAction {

    protected static Logger log = Logger.getLogger(ImportSqlFileAction.class);

    /**
     * DOC bZhou ImportSqlFileAction constructor comment.
     */
    public ImportSqlFileAction(RepositoryNode node) {
        super(node);
        setText("Import SQL");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractImportSourceFileAction#getFilterExtensions()
     */
    @Override
    protected String[] getFilterExtensions() {
        return new String[] { "*.sql" };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractImportFileAction#getRepositoryType()
     */
    @Override
    public ERepositoryObjectType getRepositoryType() {
        return ERepositoryObjectType.TDQ_SOURCE_FILES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractImportSourceFileAction#createTDQFileItem()
     */
    @Override
    protected TDQFileItem createTDQFileItem() {
        return PropertiesFactory.eINSTANCE.createTDQSourceFileItem();
    }
}
