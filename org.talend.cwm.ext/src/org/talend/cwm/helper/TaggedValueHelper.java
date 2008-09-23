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
package org.talend.cwm.helper;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.constants.DevelopmentStatus;
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
     * The tag used for setting a technical name.
     */
    public static final String TECH_NAME_TAGGED_VAL = "Technical Name";

    /**
     * The tag used when setting a column content type.
     */
    public static final String DATA_CONTENT_TYPE_TAGGED_VAL = "Content Type";

    /**
     * The tag used when setting the development status of an object such as analysis, connection...
     */
    public static final String DEV_STATUS = "Status";

    public static final String AUTHOR = "Author";

    private static final String EMPTY_STRING = "";

    private static final String DEFAULT_FORMAT = "pdf";

    public static final String DESCRIPTION = "Description";

    public static final String PURPOSE = "Purpose";

    public static final String COMMENT = "Comment";

    /**
     * A status to tell that the object is valid or invalid.
     */
    public static final String VALID_STATUS = "Validation_Status";

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

    /**
     * Method "setComment" sets a comment on the given element.
     * 
     * @param comment the comment to set
     * @param element the element
     * @return true if the value was not set before.
     */
    public static boolean setComment(String comment, ModelElement element) {
        return setTaggedValue(element, COMMENT, comment);
    }

    /**
     * Method "getComment".
     * 
     * @param element
     * @return
     */
    public static String getComment(ModelElement element) {
        return getValue(COMMENT, element);
    }

    /**
     * Method "getDescription".
     * 
     * @param element a CWM element
     * @return the description of the element or null
     */
    public static String getDescription(ModelElement element) {
        return getValue(DESCRIPTION, element.getTaggedValue());
    }

    /**
     * Method "setDescription".
     * 
     * @param description the functional description to set or create
     * @param element a CWM element
     */
    public static void setDescription(String description, ModelElement element) {
        setTaggedValue(element, DESCRIPTION, description);
    }

    /**
     * Method "setPurpose".
     * 
     * @param purpose the purpose to set or create
     * @param element a CWM element
     */
    public static void setPurpose(String purpose, ModelElement element) {
        setTaggedValue(element, PURPOSE, purpose);
    }

    /**
     * Method "getPurpose".
     * 
     * @param element a CWM element
     * @return the purpose or null
     */
    public static String getPurpose(ModelElement element) {
        return getValue(PURPOSE, element);
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
     * Method "createProperties".
     * 
     * @param taggedValues a list of pairs (key,value).
     * @return a properties from the given tag-value collection
     */
    public static Properties createProperties(Collection<TaggedValue> taggedValues) {
        Properties properties = new Properties();
        for (TaggedValue taggedValue : taggedValues) {
            if (taggedValue == null) {
                continue;
            }
            properties.put(taggedValue.getTag(), taggedValue.getValue());
        }
        return properties;
    }

    /**
     * Method "setTechnicalName".
     * 
     * @param element the CWM model element to which a tagged value with a technical name will be attached
     * @param technicalName the technical name of the given element
     * @return true if the technical name was not set before.
     */
    public static boolean setTechnicalName(ModelElement element, String technicalName) {
        return setTaggedValue(element, TECH_NAME_TAGGED_VAL, technicalName);
    }

    /**
     * Method "getTechnicalName".
     * 
     * @param element a cwm element
     * @return the technical name of the element (or null if none)
     */
    public static String getTechnicalName(ModelElement element) {
        return getValue(TECH_NAME_TAGGED_VAL, element);
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
        return setTaggedValue(element, DEV_STATUS, status.getLiteral());
    }

    public static boolean setAuthor(ModelElement element, String author) {
        return setTaggedValue(element, AUTHOR, author);
    }

    public static String getAuthor(ModelElement element) {
        TaggedValue tv = getTaggedValue(AUTHOR, element.getTaggedValue());
        if (tv == null) {
            return EMPTY_STRING;
        }
        return tv.getValue();
    }

    /**
     * Method "getDevStatus".
     * 
     * @param element such as Analysis, DataProvider...
     * @return the development status of the element
     */
    public static DevelopmentStatus getDevStatus(ModelElement element) {
        TaggedValue taggedValue = getTaggedValue(DEV_STATUS, element.getTaggedValue());
        if (taggedValue == null) {
            return DevelopmentStatus.DRAFT;
        }
        return DevelopmentStatus.get(taggedValue.getValue());
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

}
