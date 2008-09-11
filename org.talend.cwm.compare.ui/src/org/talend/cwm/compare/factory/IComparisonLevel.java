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
package org.talend.cwm.compare.factory;

import org.talend.cwm.compare.exception.ReloadCompareException;

/**
 * The class use to reload the current level element, after that it will save the updated elements.
 */
public interface IComparisonLevel {

    public void reloadCurrentLevelElement() throws ReloadCompareException;
}
