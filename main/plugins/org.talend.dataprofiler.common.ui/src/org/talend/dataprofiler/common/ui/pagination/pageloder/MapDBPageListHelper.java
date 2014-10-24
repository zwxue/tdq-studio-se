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
package org.talend.dataprofiler.common.ui.pagination.pageloder;

import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.talend.cwm.indicator.ColumnFilter;
import org.talend.cwm.indicator.DataValidation;
import org.talend.dataquality.indicators.mapdb.AbstractDB;
import org.talend.dataquality.indicators.mapdb.DBMap;
import org.talend.dataquality.indicators.mapdb.DBValueMap;

/**
 * created by talend on Aug 15, 2014 Detailled comment
 * 
 */
public class MapDBPageListHelper {

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, Map<Long, T> indexMap) {
        return createPage(db, controller, indexMap, db.size(), (ColumnFilter) null);
    }

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, Map<Long, T> indexMap,
            long itemsSize) {
        return createPage(db, controller, indexMap, itemsSize, (ColumnFilter) null);
    }

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, Map<Long, T> indexMap,
            long itemsSize, DataValidation dataValidator) {
        long totalSize = itemsSize;
        long pageSize = controller.getPageSize();
        long pageIndex = controller.getPageOffset();

        long fromIndex = pageIndex;
        long toIndex = pageIndex + pageSize;
        if (toIndex > totalSize) {
            toIndex = totalSize;
        }

        List<Object[]> content = db.subList(fromIndex, toIndex, indexMap, dataValidator);

        return new PageResult<Object[]>(content, totalSize);
    }

    public static <T> PageResult<Object[]> createPage(AbstractDB<T> db, PageableController controller, Map<Long, T> indexMap,
            long itemsSize, ColumnFilter filter) {
        long totalSize = itemsSize;
        long pageSize = controller.getPageSize();
        long pageIndex = controller.getPageOffset();

        long fromIndex = pageIndex;
        long toIndex = pageIndex + pageSize;
        if (toIndex > totalSize) {
            toIndex = totalSize;
        }

        List<Object[]> content = db.subList(fromIndex, toIndex, indexMap);
        if (filter != null) {
            content = filter.filterArray(content);
        }

        if (content.size() < totalSize) {
            totalSize = content.size();
        }
        return new PageResult<Object[]>(content, totalSize);
    }

    public static <K, V> PageResult<Object[]> createPageByValue(DBValueMap<K, V> db, PageableController controller,
            Map<Long, K> indexMap, long itemsSize, ColumnFilter filter) {
        long totalSize = itemsSize;
        long pageSize = controller.getPageSize();
        long pageIndex = controller.getPageOffset();

        long fromIndex = pageIndex;
        long toIndex = pageIndex + pageSize;
        if (toIndex > totalSize) {
            toIndex = totalSize;
        }
        List<Object[]> content = ((DBMap<K, V>) db).subValueList(fromIndex, toIndex, indexMap);
        if (filter != null) {
            content = filter.filterArray(content);
        }
        return new PageResult<Object[]>(content, content.size());
    }

}
