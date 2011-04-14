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
package org.talend.cwm.compare;

import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.match.engine.GenericMatchEngine;
import org.eclipse.emf.ecore.EObject;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;

/**
 * yyi 2011-03-30 19137:reload database list on editing connection url
 */
public class ModelElementMatchEngine extends GenericMatchEngine {

    private static boolean changeUrl = false;

    /*
     * ADD yyi 2011-03-30 19137:reload database list on editing connection url
     * 
     * @see org.eclipse.emf.compare.match.engine.GenericMatchEngine#isSimilar(org.eclipse.emf.ecore.EObject,
     * org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean isSimilar(EObject obj1, EObject obj2) throws FactoryException {

        // FIXME : revert r58560
        
//        DatabaseConnection connection1 = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(obj1);
//        DatabaseConnection connection2 = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(obj2);
//
//        if (connection1 != null && connection2 != null) {
//            changeUrl = ConnectionHelper.isUrlChanged(connection1);
//        }
//        return changeUrl ? false : super.isSimilar(obj1, obj2);
        
        return Boolean.TRUE;
    }

    /*
     * ADD yyi 2011-03-30 19137:reload database list on editing connection url
     * 
     * @see org.eclipse.emf.compare.match.engine.GenericMatchEngine#reset()
     */
    @Override
    public void reset() {
        changeUrl = false;
        super.reset();
    }
}
