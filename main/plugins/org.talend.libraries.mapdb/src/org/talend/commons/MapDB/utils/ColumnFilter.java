// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.commons.MapDB.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * created by talend on Aug 26, 2014 Detailled comment
 * 
 */
public class ColumnFilter {

    Integer[] remaindColumnIndexs = null;

    public ColumnFilter(Integer[] columnIndexArray) {
        Arrays.sort(columnIndexArray);
        remaindColumnIndexs = columnIndexArray;
    }

    public List<Object[]> filterArray(List<Object[]> inputData) {
        List<Object[]> retrunList = new ArrayList<Object[]>();

        for (int i = 0; i < inputData.size(); i++) {
            Object filterArray[] = new Object[remaindColumnIndexs.length];
            for (int remainIndex = 0; remainIndex < remaindColumnIndexs.length; remainIndex++) {
                if (inputData.get(i).length >= remaindColumnIndexs[remainIndex]) {
                    filterArray[remainIndex] = inputData.get(i)[remaindColumnIndexs[remainIndex]];
                }
            }
            retrunList.add(filterArray);
        }
        return retrunList;
    }

    public List<Object> filter(List<Object> inputData) {
        List<Object> retrunList = new ArrayList<Object>();

        for (Integer remaindColumnIndex : remaindColumnIndexs) {
            if (inputData.size() >= remaindColumnIndex) {
                retrunList.add(inputData.get(remaindColumnIndex));
            }
        }
        return retrunList;
    }
}
