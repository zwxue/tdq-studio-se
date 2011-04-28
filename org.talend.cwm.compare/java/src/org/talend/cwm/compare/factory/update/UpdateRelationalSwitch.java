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

package org.talend.cwm.compare.factory.update;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.compare.i18n.DefaultMessagesImpl;
import orgomg.cwm.foundation.keysindexes.KeyRelationship;
import orgomg.cwm.foundation.keysindexes.UniqueKey;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;
import orgomg.cwm.resource.relational.util.RelationalSwitch;


/**
 * DOC scorreia  class global comment. Detailled comment
 */
class UpdateRelationalSwitch extends RelationalSwitch<Boolean> {
    private static Logger log = Logger.getLogger(UpdateRelationalSwitch.class);
    
    private UpdateCoreSwitch updateCoreSwitch = new UpdateCoreSwitch();
    
    private EObject recentElement;

    public void setRightElement(EObject rightElement) {
        // MOD klliu add recentElement getter/setter
        this.setRecentElement(rightElement);
        this.updateCoreSwitch.setRightElement(rightElement);
    }
    
    @Override
    public Boolean caseForeignKey(ForeignKey object) {
        log.error(DefaultMessagesImpl.getString("UpdateRelationalSwitch.errorUpdateOfObj", object));//$NON-NLS-1$
        // TODO Auto-generated method stub
        return super.caseForeignKey(object);
    }

    @Override
    public Boolean caseKeyRelationship(KeyRelationship object) {
        log.error(DefaultMessagesImpl.getString("UpdateRelationalSwitch.errorUpdateOfObj", object));//$NON-NLS-1$
        // TODO Auto-generated method stub
        return super.caseKeyRelationship(object);
    }
    @Override
    public Boolean casePrimaryKey(PrimaryKey object) {
        log.error(DefaultMessagesImpl.getString("UpdateRelationalSwitch.errorUpdateOfObj", object));//$NON-NLS-1$
        // TODO Auto-generated method stub
        return super.casePrimaryKey(object);
    }



    @Override
    public Boolean caseUniqueKey(UniqueKey object) {
        log.error(DefaultMessagesImpl.getString("UpdateRelationalSwitch.errorUpdateOfObj", object));//$NON-NLS-1$
        // TODO Auto-generated method stub
        return super.caseUniqueKey(object);
    }

    @Override
    public Boolean defaultCase(EObject object) {
        return this.updateCoreSwitch.doSwitch(object);
    }

    public void setRecentElement(EObject recentElement) {
        this.recentElement = recentElement;
    }

    public EObject getRecentElement() {
        return recentElement;
    }

}
