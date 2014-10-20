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
package org.talend.dataquality.indicators.mapdb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

/**
 * created by talend on Aug 26, 2014 Detailled comment
 * 
 */
public class DBValueListMap<K> extends DBValueMap<K, List<Object[]>> {

    /**
     * DOC talend DBValueListMap constructor comment.
     * 
     * @param parentFullPathStr
     * @param fileName
     * @param mapName
     */
    public DBValueListMap(String parentFullPathStr, String fileName, String mapName) {
        super(parentFullPathStr, fileName, mapName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.commons.MapDB.utils.DBValueMap#subValueList(long, long, java.util.Map)
     */
    @Override
    public List<Object[]> subValueList(long fromIndex, long toIndex, Map<Long, K> indexMap) {
        boolean stratToRecord = false;
        List<Object[]> returnList = new ArrayList<Object[]>();
        K fromKey = indexMap.get(fromIndex);
        K toKey = indexMap.get(toIndex);
        Iterator<K> iterator = null;
        int index = 0;
        if (fromKey == null) {
            iterator = this.iterator();
        } else if (toKey == null) {
            NavigableSet<K> tailSet = tailSet(fromKey, true);
            iterator = tailSet.iterator();
        } else {
            NavigableSet<K> tailSet = subSet(fromKey, toKey);
            iterator = tailSet.iterator();
        }

        while (iterator.hasNext()) {
            K next = iterator.next();
            List<Object[]> valueList = this.get(next);
            if (index == 0 && fromKey == null) {
                indexMap.put(0l, next);
            }
            for (Object[] arrayElement : valueList) {

                if (index == fromIndex) {
                    stratToRecord = true;
                    // If firstly element is the start place record fromKey point
                    if (valueList.get(0) == arrayElement && fromKey == null) {
                        indexMap.put(fromIndex, next);
                    }
                } else if (index == toIndex) {
                    // If firstly element is the end place record toKey point
                    if (valueList.get(0) == arrayElement && toKey == null) {
                        indexMap.put(toIndex, next);
                    }
                    return returnList;
                }
                if (stratToRecord == true) {
                    returnList.add(arrayElement);
                }
                index++;
            }

        }
        return returnList;
    }

}
