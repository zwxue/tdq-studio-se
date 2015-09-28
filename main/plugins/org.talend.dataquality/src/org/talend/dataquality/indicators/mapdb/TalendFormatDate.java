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
package org.talend.dataquality.indicators.mapdb;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.talend.dataquality.PluginConstant;

/**
 * ovririd "toString()" to format Date to specify pattern.
 */
public class TalendFormatDate extends Date {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String pattern = null;

    private Date dateObj = null;

    public TalendFormatDate(Date obj) {
        super(obj.getTime());
        this.dateObj = obj;
        if (obj instanceof Timestamp) {
            this.pattern = "yyyy-MM-dd HH:MM:ss.S"; //$NON-NLS-1$
        } else if (obj instanceof Time) {
            this.pattern = "HH:MM:ss"; //$NON-NLS-1$
        } else {
            this.pattern = "yyyy-MM-dd"; //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Date#toString()
     */
    @Override
    public String toString() {
        if (dateObj == null || pattern == null) {
            return PluginConstant.EMPTY_STRING;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(dateObj);
    }

}
