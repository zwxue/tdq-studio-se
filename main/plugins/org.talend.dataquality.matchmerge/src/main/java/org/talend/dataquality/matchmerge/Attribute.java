/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
 * @author adjout
 */
public class Attribute {

    private final String label;

    private String value;

    private List<String> values;

    public Attribute(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

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
