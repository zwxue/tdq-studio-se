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
package org.talend.cwm.helper;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * @author scorreia
 * 
 * This class is a helper for handling TaggedValues.
 */
public final class TaggedValueHelper {

    /**
     * The tag used when setting a column content type.
     */
    public static final String DATA_CONTENT_TYPE_TAGGED_VAL = "Content Type"; //$NON-NLS-1$

    /**
     * The tag used when setting the development status of an object such as analysis, connection...
     */

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private static final String DEFAULT_FORMAT = "pdf"; //$NON-NLS-1$

    public static final String GEN_SINGLE_REPORT = "Single output"; //$NON-NLS-1$

    /**
     * A status to tell that the object is valid or invalid.
     */
    public static final String VALID_STATUS = "Validation_Status"; //$NON-NLS-1$

    // metadata tagged values

    public static final String AUTHOR = "Author"; //$NON-NLS-1$

    public static final String DESCRIPTION = "Description"; //$NON-NLS-1$

    public static final String DEV_STATUS = "Status"; //$NON-NLS-1$

    public static final String PURPOSE = "Purpose"; //$NON-NLS-1$

    public static final String VERSION = "Version"; //$NON-NLS-1$

    // ~~~~~~~~~~~~~~~~~~~~~~~~~

    // TABLE/VIEW/COLUMN tagged values
    public static final String TABLE_FILTER = "Table Filter"; //$NON-NLS-1$

    public static final String VIEW_FILTER = "View Filter"; //$NON-NLS-1$

    public static final String COLUMN_FILTER = "Column Filter"; //$NON-NLS-1$

    public static final String COMMENT = "Comment"; //$NON-NLS-1$

    // ~~~~~~~~~~~~~~~~~~~~~~~~~

    // data connection tagged values
    public static final String USER = "user";

    public static final String PASSWORD = "password";

    public static final String HOST = "HOST";

    public static final String PORT = "PORT";

    public static final String DBTYPE = "DBTYPE";

    public static final String DBNAME = "DBNAME";

    public static final String DB_IDENTIFIER_QUOTE_STRING = "DB IdentifierQuoteString"; //$NON-NLS-1$

    // ~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * The tag used for setting a technical name.
     */
    public static final String TECH_NAME_TAGGED_VAL = "Technical Name"; //$NON-NLS-1$

    // ~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Method "setSingleGenReport".
     * 
     * @param single true if the generated report must overwrite the existing report
     * @param report a report
     * @return true if the value was not set before.
     */
    public static boolean setSingleGenReport(Boolean single, Report report) {
        String statusStr = String.valueOf(single);
        return setTaggedValue(report, GEN_SINGLE_REPORT, statusStr);
    }

    /**
     * Method "getSingleGenReport".
     * 
     * @param report
     * @return true when a single generated file must be used. False when the generated file must not overwrite the
     * existing generated files.
     */
    public static Boolean getSingleGenReport(Report report) {
        final String value = getValue(GEN_SINGLE_REPORT, report);
        if (value == null || value.trim().length() == 0) {
            return false;
        }
        return Boolean.valueOf(value);
    }

    /**
     * Method "setValidStatus" sets the status on the given element.
     * 
     * @param status the status to set
     * @param element the element
     * @return true if the value was not set before.
     */
    public static boolean setValidStatus(Boolean status, ModelElement element) {
        String statusStr = String.valueOf(status);
        return setTaggedValue(element, VALID_STATUS, statusStr);
    }

    /**
     * Method "getValidStatus".
     * 
     * @param element
     * @return the validation status of the element
     */
    public static Boolean getValidStatus(ModelElement element) {
        return Boolean.valueOf(getValue(VALID_STATUS, element));
    }

    private TaggedValueHelper() {
    }

    /**
     * Method "createTaggedValue".
     * 
     * @param tag the tag of the tagged value to create
     * @param value the value of the tagged value to create
     * @return the created tagged value
     */
    public static TaggedValue createTaggedValue(String tag, String value) {
        TaggedValue taggedValue = CoreFactory.eINSTANCE.createTaggedValue();
        taggedValue.setTag(tag);
        taggedValue.setValue(value);
        return taggedValue;
    }

    /**
     * Method "getValue" retrieves the value corresponding to the first matching tag.
     * 
     * @param tag the tag to match
     * @param taggedValues the tagged values in which to search for the given tag
     * @return the value (if found) or null
     */
    public static String getValue(String tag, Collection<TaggedValue> taggedValues) {
        TaggedValue taggedValue = getTaggedValue(tag, taggedValues);
        if (taggedValue == null) {
            return null;
        }
        return taggedValue.getValue();
    }

    /**
     * Method "getTaggedValue" retrieves the tagged value corresponding to the first matching tag.
     * 
     * @param tag the tag to match
     * @param taggedValues the tagged values in which to search for the given tag
     * @return the tagged value (if found) or null
     */
    public static TaggedValue getTaggedValue(String tag, Collection<TaggedValue> taggedValues) {
        if (tag == null) {
            return null;
        }
        TaggedValue value = null;
        for (TaggedValue taggedValue : taggedValues) {
            if (taggedValue == null) {
                continue;
            }
            if (tag.compareTo(taggedValue.getTag()) == 0) {
                value = taggedValue;
            }
        }
        return value;
    }

    /**
     * Method "setTaggedValue".
     * 
     * @param element the CWM model element to which a tagged value will be attached (if not already set)
     * @param tag the tag
     * @param value the value to set
     * @return true if the value was not set before.
     */
    public static boolean setTaggedValue(ModelElement element, String tag, String value) {
        EList<TaggedValue> taggedValues = element.getTaggedValue();
        TaggedValue currentValue = TaggedValueHelper.getTaggedValue(tag, taggedValues);
        boolean create = (currentValue == null);
        if (create) {
            taggedValues.add(TaggedValueHelper.createTaggedValue(tag, value));
        } else {
            currentValue.setValue(value);
        }
        return create;
    }

    /**
     * Method "getValue".
     * 
     * @param tag the key to find the value
     * @param element the element
     * @return the value of the tagged valued of the element or the empty string
     */
    public static String getValue(String tag, ModelElement element) {
        TaggedValue tv = getTaggedValue(tag, element.getTaggedValue());
        if (tv == null) {
            return EMPTY_STRING;
        }
        return tv.getValue();
    }

    public static String getFileValue(String tag, ModelElement element) {
        TaggedValue tv = getTaggedValue(tag, element.getTaggedValue());
        if (tv == null) {
            return DEFAULT_FORMAT;
        }
        return tv.getValue();
    }

    // database tagged values

}
