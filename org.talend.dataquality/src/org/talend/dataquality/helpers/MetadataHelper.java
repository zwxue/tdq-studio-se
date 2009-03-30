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
package org.talend.dataquality.helpers;

import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.utils.sql.Java2SqlType;

/**
 * @author scorreia
 * 
 * This class is a helper for handling data quality metadata.
 */
public final class MetadataHelper {

    private MetadataHelper() {
    }

    /**
     * Method "setDataminingType" sets the type of the content of a column.
     * 
     * @param type
     * @param column
     */
    public static void setDataminingType(DataminingType type, TdColumn column) {
        column.setContentType(type.getLiteral());
    }

    /**
     * Method "getDataminingType" gets the type of the content of a column.
     * 
     * @param column
     * @return the DataminingType or null if none has been set.
     */
    public static DataminingType getDataminingType(TdColumn column) {
        return DataminingType.get(column.getContentType());
    }

    public static DataminingType getDefaultDataminingType(int javaSqlType) {

        if (Java2SqlType.isTextInSQL(javaSqlType)) {
            return DataminingType.NOMINAL;
        }

        if (Java2SqlType.isNumbericInSQL(javaSqlType)) {
            return DataminingType.INTERVAL;
        }

        if (Java2SqlType.isDateInSQL(javaSqlType)) {
            return DataminingType.INTERVAL;
        }

        if (Java2SqlType.isOtherTypeInSQL(javaSqlType)) {
            return DataminingType.OTHER;
        }

        return null;
    }
}
