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
package org.talend.cwm.compare.factory.comparisonlevel;

import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.talend.cwm.softwaredeployment.TdDataProvider;

/**
 * DOC rli class global comment. Detailled comment
 */
public class CatalogSchemaComparisonLevel extends AbstractPartComparisonLevel {

    public CatalogSchemaComparisonLevel(Object selectedObj) {
        super(selectedObj);
    }

    @Override
    protected void handleAddElement(TdDataProvider oldDataProvider, AddModelElement addElement) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleRemoveElement(TdDataProvider oldDataProvider, RemoveModelElement removeElement) {
        // TODO Auto-generated method stub

    }

}
