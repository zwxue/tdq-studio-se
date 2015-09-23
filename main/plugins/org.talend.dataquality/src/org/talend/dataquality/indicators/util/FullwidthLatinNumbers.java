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
package org.talend.dataquality.indicators.util;

/**
 * DOC talend class global comment. Detailled comment
 */
public class FullwidthLatinNumbers extends ChainResponsibilityHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getReplaceStr()
     */
    @Override
    protected String getReplaceStr() {
        return "9";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getRegex()
     */
    @Override
    protected String getRegex() {
        // ０１３４５６７８９
        return "[\\uFF10-\\uFF19]";
    }

}
