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

package org.talend.dataquality;

/**
 * 
 * DOC mzhao Plugin constants. This class is originally developed for handle
 * TDCP launch issues. As root project name must defined at "low level plugin".
 */
public final class PluginConstant {
	// MOD mzhao 2009-03-13 TDQ/TOP root project
	private static String rootProjectName;

	public static String getRootProjectName() {
		return rootProjectName;
	}

	public static void setRootProjectName(String rootProjectName) {
		PluginConstant.rootProjectName = rootProjectName;
	}

}
