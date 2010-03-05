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
package org.talend.cwm.helper;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

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

    // pattern tagged values
    public static final String VALID_STATUS = "Validation_Status"; //$NON-NLS-1$

    // ~~~~~~~~~~~~~~~~~~~~~~~~~

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

    public static final String UNIVERSE = "universe";

    public static final String RETRIEVE_ALL = "RETRIEVE_ALL";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~

    // report tagged values
    public static final String GEN_SINGLE_REPORT = "Single output";

    public static final String OUTPUT_TYPE_TAG = "OutputType";

    public static final String OUTPUT_FILENAME_TAG = "OutputFileName";

    public static final String OUTPUT_FOLDER_TAG = "OutputFolder";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * The tag used for setting a technical name.
     */
    public static final String TECH_NAME_TAGGED_VAL = "Technical Name"; //$NON-NLS-1$

    // ~~~~~~~~~~~~~~~~~~~~~~~~~

    // Property tagged values.
    // MOD mzhao feature 7488
    public static final String PROPERTY_FILE = "Property File"; //$NON-NLS-1$

    public static final String TDQ_ELEMENT_FILE = "TDQ Element File"; //$NON-NLS-1$
    // ~~~~~~~~~~~~~~~~~~~~~~~~~

    // overview analysis tagged values
    public static final String RELOAD_DATABASES = "Reload Databases";
    // ~~~~~~~~~~~~~~~~~~~~~~~~~

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
     * Method "setValidStatus" sets the status on the given element.
     * 
     * @param status the status to set
     * @param pattern the element
     * @return true if the value was not set before.
     */
    public static boolean setValidStatus(Boolean status, ModelElement element) {
        String statusStr = String.valueOf(status);
        return TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.VALID_STATUS, statusStr);
    }

    /**
     * Method "getValidStatus".
     * 
     * @param element
     * @return the validation status of the element
     */
    public static Boolean getValidStatus(ModelElement element) {
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.VALID_STATUS, element.getTaggedValue());
        if (taggedValue == null) {
            return false;
        }
        return Boolean.valueOf(taggedValue.getValue());
    }
}
