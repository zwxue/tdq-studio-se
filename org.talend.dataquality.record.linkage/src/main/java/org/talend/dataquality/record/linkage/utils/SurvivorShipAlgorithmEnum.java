// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * created by HHB on 2013-8-23 Detailled comment
 * 
 */
public enum SurvivorShipAlgorithmEnum {

    CONCATENATE(0, "Concatenate", "Concatenate"),
    MOST_COMMON(1, "Most common", "MostCommon"),
    MOST_RECENT(2, "Most recent", "MostRecent"),
    MOST_ANCIENT(3, "Most ancient", "MostAncient"),
    LONGEST(4, "Longest (for strings)", "Longest"),
    SHORTEST(5, "Shortest (for strings)", "Shortest"),
    LARGEST(6, "Largest (for numbers)", "Largest"),
    SMALLEST(7, "Smallest (for numbers)", "Smallest");

    private int index;

    private String value;

    private String componentValueName;

    SurvivorShipAlgorithmEnum(int index, String value, String componentValueName) {
        this.index = index;
        this.value = value;
        this.componentValueName = componentValueName;
    }

    public int getIndex() {
        return this.index;
    }

    public String getValue() {
        return this.value;
    }

    public String getComponentValueName() {
        return this.componentValueName;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (SurvivorShipAlgorithmEnum theType : values()) {
            list.add(theType.getValue());
        }
        return list.toArray(new String[list.size()]);
    }

    public static SurvivorShipAlgorithmEnum getTypeByValue(String value) {
        for (SurvivorShipAlgorithmEnum element : SurvivorShipAlgorithmEnum.values()) {
            if (element.getValue().equalsIgnoreCase(value)) {
                return element;
            }
        }

        return null;
    }

    /**
     * 
     * 
     * @param index
     * @return null can not find this index
     */
    public static SurvivorShipAlgorithmEnum getTypeByIndex(int index) {
        for (SurvivorShipAlgorithmEnum element : SurvivorShipAlgorithmEnum.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }
}
