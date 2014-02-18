/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talend.dataquality.matchmerge;

import java.util.ArrayList;
import java.util.List;

/**
 * A attribute is a "column" in a {@link org.talend.dataquality.matchmerge.Record record}.
 */
public class Attribute {

    private final String label;

    private String value;

    private List<String> values;

    /**
     * Creates a new attribute (column name / value pair).
     * @param label Name of the column.
     * @param value Value associated with the column.
     */
    public Attribute(String label, String value) {
        this.label = label;
        this.value = value;
    }

    /**
     * @return The column name.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return The column's value (always as string, never typed).
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the merged column value.
     * @param value A string value for the column. For custom types, provide a consistent representation of the data
     *              since the string is used for match.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return All the values that lead to the merged value (i.e. the value returned by {@link #getValue()}).
     */
    public List<String> getValues() {
        if (values == null) {
            values = new ArrayList<String>();
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }
}
