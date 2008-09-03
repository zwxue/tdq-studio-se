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
import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.softwaredeployment.TdDataProvider;

/**
 * DOC rli class global comment. Detailled comment
 */
public class TableViewComparisonLevel extends AbstractPartComparisonLevel {

    public TableViewComparisonLevel(Object selectedObj) {
        super(selectedObj);
    }

    @Override
    public void reloadCurrentLevelElement() {
        // super.reloadCurrentLevelElement();
        // EObject parent = ((AbstractDatabaseFolderNode) selectedObj).getParent();
        // SwitchHelpers.c
    }

    @Override
    protected void handleAddElement(AddModelElement addElement) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleRemoveElement(RemoveModelElement removeElement) {
        // TODO Auto-generated method stub

    }

    @Override
    protected TdDataProvider findDataProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected EObject getSavedReloadObject() {
        // TODO Auto-generated method stub
        return null;
    }

}
