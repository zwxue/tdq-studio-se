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

import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.utils.sql.Java2SqlType;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

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

    /**
     * method "getDefaultDataminingType" gets the default type of the content of a column.
     * 
     * @param javaSqlType
     * @return
     */
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

    /**
     * DOC bZhou Comment method "setAuthor".
     * 
     * @param element
     * @param author
     * @return
     */
    public static boolean setAuthor(ModelElement element, String author) {
        return TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.AUTHOR, author);
    }

    /**
     * DOC bZhou Comment method "getAuthor".
     * 
     * @param element
     * @return
     */
    public static String getAuthor(ModelElement element) {
        TaggedValue tv = TaggedValueHelper.getTaggedValue(TaggedValueHelper.AUTHOR, element.getTaggedValue());
        if (tv == null) {
            return PluginConstant.EMPTY_STRING;
        }
        return tv.getValue();
    }

    /**
     * Method "getDescription".
     * 
     * @param element a CWM element
     * @return the description of the element or null
     */
    public static String getDescription(ModelElement element) {
        return TaggedValueHelper.getValue(TaggedValueHelper.DESCRIPTION, element.getTaggedValue());
    }

    /**
     * Method "setDescription".
     * 
     * @param description the functional description to set or create
     * @param element a CWM element
     */
    public static void setDescription(String description, ModelElement element) {
        TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.DESCRIPTION, description);
    }

    /**
     * Method "setDevStatus" sets the development status of the given element.
     * 
     * @param element
     * @param status the state to set.
     * @return
     */
    public static boolean setDevStatus(ModelElement element, DevelopmentStatus status) {
        if (status == null) {
            return false;
        }
        return TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.DEV_STATUS, status.getLiteral());
    }

    /**
     * Method "getDevStatus".
     * 
     * @param element such as Analysis, DataProvider...
     * @return the development status of the element
     */
    public static DevelopmentStatus getDevStatus(ModelElement element) {
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.DEV_STATUS, element.getTaggedValue());
        if (taggedValue == null) {
            return DevelopmentStatus.DRAFT;
        }
        return DevelopmentStatus.get(taggedValue.getValue());
    }

    /**
     * Method "setPurpose".
     * 
     * @param purpose the purpose to set or create
     * @param element a CWM element
     */
    public static void setPurpose(String purpose, ModelElement element) {
        TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.PURPOSE, purpose);
    }

    /**
     * Method "getPurpose".
     * 
     * @param element a CWM element
     * @return the purpose or null
     */
    public static String getPurpose(ModelElement element) {
        return TaggedValueHelper.getValue(TaggedValueHelper.PURPOSE, element);
    }

    /**
     * Method "setVersion" sets the version of the given element.
     * 
     * @param version the version to set
     * @param element the element
     * @return true if the value was not set before.
     */
    public static boolean setVersion(Boolean version, ModelElement element) {
        String statusStr = String.valueOf(version);
        return TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.VERSION, statusStr);
    }

    /**
     * Method "getVersion".
     * 
     * @param element
     * @return the version of the element
     */
    public static Boolean getVersion(ModelElement element) {
        return Boolean.valueOf(TaggedValueHelper.getValue(TaggedValueHelper.VERSION, element));
    }
}
