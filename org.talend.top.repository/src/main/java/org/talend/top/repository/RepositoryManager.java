// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.top.repository;

import org.talend.core.model.properties.Item;

/***/
public class RepositoryManager {

    public void save() {

    }

    public void refresh() {

    }

    public void lock(final Item item) {

    }

    public void unLock(Item item) {

    }

    public Boolean isEditable(Item item) {
        return true;
    }

    public Boolean isReadOnly() {
        return false;
    }

    public Boolean isLockByOthers(Item item) {
        return false;
    }

    public Boolean isLockByUserOwn(Item item) {
        return false;
    }
}
