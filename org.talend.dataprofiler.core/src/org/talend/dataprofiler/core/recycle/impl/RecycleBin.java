// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.recycle.impl;

import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.IRecycleBin;

/**
 * @author qiongli
 * Recycle bin node
 */
public class RecycleBin implements IRecycleBin {

	public String getName() {
		// TODO Auto-generated method stub
		return DefaultMessagesImpl.getString("RecycleBin.resBinName");
	}

}
