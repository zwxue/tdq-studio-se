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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

/**
 * created by talend on Jul 15, 2014 Detailled comment
 * 
 */
public class DBMapTest {

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#hashCode()}.
     */
    @Test
    public void testHashCode() {
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap();
        DBMapParameter dbMapParameter = new DBMapParameter();
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(dbMap1.hashCode(), dbMap2.hashCode());
        dbMap1.put("key1", 100l); //$NON-NLS-1$
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(dbMap1.hashCode(), dbMap2.hashCode());
        dbMap1.put("key2", 100l); //$NON-NLS-1$
        Assert.assertNotSame(dbMap1.hashCode(), dbMap2.hashCode());

    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#size()}.
     */
    @Test
    public void testSize() {
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap();
        DBMapParameter dbMapParameter = new DBMapParameter();
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(0, dbMap1.size());
        Assert.assertEquals(0, dbMap2.size());
        dbMap1.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(1, dbMap1.size());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(1, dbMap2.size());
        dbMap2.put("key2", 100l); //$NON-NLS-1$
        Assert.assertEquals(2, dbMap2.size());
        dbMap1.put("key2", 100l); //$NON-NLS-1$
        Assert.assertEquals(2, dbMap1.size());
        dbMap1.close();
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap();
        DBMapParameter dbMapParameter = new DBMapParameter();
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap1.isEmpty());
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap1.put("key1", 100l); //$NON-NLS-1$
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(false, dbMap1.isEmpty());
        Assert.assertEquals(false, dbMap2.isEmpty());
        dbMap1.remove("key1"); //$NON-NLS-1$
        dbMap2.remove("key1"); //$NON-NLS-1$
        Assert.assertEquals(true, dbMap1.isEmpty());
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap1.close();
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#clear()}.
     */
    @Test
    public void testClear() {
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap();
        DBMapParameter dbMapParameter = new DBMapParameter();
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap1.isEmpty());
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap1.put("key1", 100l); //$NON-NLS-1$
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(false, dbMap1.isEmpty());
        Assert.assertEquals(false, dbMap2.isEmpty());
        dbMap1.clear();
        dbMap2.clear();
        Assert.assertEquals(true, dbMap1.isEmpty());
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap1.close();
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#DBMap(DBMapParameter, boolean)} .
     */
    @Test
    public void testDBMapDBMapParameter() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(false, dbMap2.isEmpty());
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#close()}.
     */
    @Test
    public void testClose() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Assert.assertEquals(false, dbMap2.isEmpty());
        dbMap2.close();
        try {
            while (!dbMap2.isClosed()) {
                dbMap2.put("key2", 100l); //$NON-NLS-1$
            }
            Assert.fail("progress should not come here, the IllegalAccessError should be throw"); //$NON-NLS-1$
        } catch (IllegalAccessError e) {
            // nothing to do
        }
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#entrySet()}.
     */
    @Test
    public void testEntrySet() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        for (Entry<TupleArray, Long> entry : dbMap2.entrySet()) {
            Assert.assertEquals("key1", entry.getKey().keyArray[0]); //$NON-NLS-1$
            Assert.assertEquals(100l, entry.getValue().longValue());
        }
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#containsValue(java.lang.Object)}.
     */
    @Test
    public void testContainsValueObject() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        boolean containsValue = dbMap2.containsValue(100l);
        Assert.assertEquals(true, containsValue);
        containsValue = dbMap2.containsValue(101l);
        Assert.assertEquals(false, containsValue);
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#containsKey(java.lang.Object)}.
     */
    @Test
    public void testContainsKeyObject() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        boolean containsKey = dbMap2.containsKey("key1"); //$NON-NLS-1$
        Assert.assertEquals(true, containsKey);
        containsKey = dbMap2.containsKey("key2"); //$NON-NLS-1$
        Assert.assertEquals(false, containsKey);
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#get(java.lang.Object)}.
     */
    @Test
    public void testGetObject() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        Long value = dbMap2.get("key1"); //$NON-NLS-1$
        Assert.assertEquals(100l, value.longValue());
        value = dbMap2.get("key2"); //$NON-NLS-1$
        Assert.assertNull(value);
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#put(java.lang.String, java.lang.Long)}.
     */
    @Test
    public void testPutStringLong() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        try {
            dbMap2.put((TupleArray) null, 100l);
            Assert.fail("should have a NullPointerException be throw"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // nothing to do
        }
        try {
            dbMap2.put("key2", null); //$NON-NLS-1$
            Assert.fail("should have a NullPointerException be throw"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // nothing to do
        }
        dbMap2.close();
        // BtreeMap
        dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        try {
            dbMap2.put((TupleArray) null, 100l);
            Assert.fail("should have a NullPointerException be throw"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // nothing to do
        }
        try {
            dbMap2.put("key2", null); //$NON-NLS-1$
            Assert.fail("should have a NullPointerException be throw"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // nothing to do
        }
        dbMap2.close();
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#remove(java.lang.Object)}.
     */
    @Test
    public void testRemoveObject() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(true, dbMap2.isEmpty());
        dbMap2.put("key1", 100l); //$NON-NLS-1$
        dbMap2.remove("key1"); //$NON-NLS-1$
        Assert.assertEquals(true, dbMap2.isEmpty());
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#putAll(java.util.Map)}.
     */
    @Test
    public void testPutAllMapOfQextendsStringQextendsLong() {
        Map<TupleArray, Long> tempMap = new HashMap<TupleArray, Long>();
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap(dbMapParameter);
        TupleArrayKeyMap dbMap2 = new TupleArrayKeyMap(dbMapParameter);
        tempMap.put(new TupleArray(1, new String[] { "name1" }), 1l);
        tempMap.put(new TupleArray(1, new String[] { "name2" }), 2l);
        dbMap1.putAll(tempMap);
        dbMap2.putAll(tempMap);
        Assert.assertEquals(1l, dbMap1.get(new TupleArray(1, new String[] { "name1" })).longValue());
        Assert.assertEquals(2l, dbMap2.get(new TupleArray(1, new String[] { "name2" })).longValue());
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#keySet()}.
     */
    @Test
    public void testKeySet() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap(dbMapParameter);
        dbMap1.put("name1", 1l); //$NON-NLS-1$
        dbMap1.put("name2", 2l); //$NON-NLS-1$
        for (TupleArray key : dbMap1.keySet()) {
            Assert.assertNotNull(key);
            Assert.assertEquals(true, dbMap1.containsKey(key));
            Assert.assertNotNull(dbMap1.get(key));
        }
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#values()}.
     */
    @Test
    public void testValues() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap(dbMapParameter);
        dbMap1.put("name1", 1l); //$NON-NLS-1$
        dbMap1.put("name2", 2l); //$NON-NLS-1$
        for (long value : dbMap1.values()) {
            Assert.assertNotNull(value);
            Assert.assertEquals(true, dbMap1.containsValue(value));
        }
    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap(dbMapParameter);
        TupleArrayKeyMap dbMap2 = dbMap1;
        TupleArrayKeyMap dbMap3 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals(dbMap1, dbMap2);
        Assert.assertEquals(dbMap1, dbMap3);
        Assert.assertEquals(dbMap2, dbMap3);

        dbMap1.put("name1", 1l); //$NON-NLS-1$
        Assert.assertEquals(dbMap1, dbMap2);
        Assert.assertEquals(false, dbMap3.equals(dbMap1));
        Assert.assertEquals(false, dbMap3.equals(dbMap2));

        dbMap3.put("name1", 1l); //$NON-NLS-1$
        Assert.assertEquals(dbMap1, dbMap2);
        Assert.assertEquals(dbMap1, dbMap3);
        Assert.assertEquals(dbMap2, dbMap3);

    }

    /**
     * Test method for {@link org.talend.commons.MapDB.utils.TupleArrayKeyMap#toString()}.
     */
    @Test
    public void testToString() {
        DBMapParameter dbMapParameter = null;
        TupleArrayKeyMap dbMap1 = new TupleArrayKeyMap(dbMapParameter);
        Assert.assertEquals("{}", dbMap1.toString()); //$NON-NLS-1$
        dbMap1.put("name1", 1l); //$NON-NLS-1$
        dbMap1.put("name2", 2l); //$NON-NLS-1$
        Assert.assertEquals("{Tuple[1, name1]=1, Tuple[1, name2]=2}", dbMap1.toString()); //$NON-NLS-1$
    }

}
