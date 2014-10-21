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

/**
 * created by talend on Oct 20, 2014 Detailled comment
 * 
 */
public interface IMapDBFactory {

    public static IMapDBFactory instance = MapDBFactorImpl.getInstance();

    <K, V> DBMap<K, V> createDBMap(String parentFullPathStr, String fileName, String mapName);

    ColumnSetDBMap createColumnSetDBMap(String parentFullPathStr, String fileName, String mapName);

    <K> DBValueListMap<K> createDBValueListMap(String parentFullPathStr, String fileName, String mapName);

    <K> DBSet<K> createDBSet(String parentFullPathStr, String fileName, String setName);
}
