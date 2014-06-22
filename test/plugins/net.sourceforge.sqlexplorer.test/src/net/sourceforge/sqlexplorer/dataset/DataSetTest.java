// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package net.sourceforge.sqlexplorer.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author yyi
 */
public class DataSetTest {

    @Test
    public void testGetColumnIndex() {
    }

    @Test
    public void testGetColumns() {
    }

    @Test
    public void testGetRowCount() {
    }

    @Test
    public void testGetRows() {
    }

    @Test
    public void testGetRow() {
    }

    @Test
    public void testSort() {
    }

    @Test
    public void testGetCaption() {
    }

    @Test
    public void testLoadCellValue() {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        
        ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        
        Long testNumericValue = 100L;
        
        String testTextValue = "test1"; //$NON-NLS-1$

        try {
            Mockito.when(metadata.getColumnCount()).thenReturn(3);
            
            Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
            
            Mockito.when(resultSet.getInt(0)).thenReturn(testNumericValue.intValue());
            
            Mockito.when(resultSet.getBytes(0)).thenReturn(testTextValue.getBytes());
            
            Mockito.when(resultSet.getString(0)).thenReturn(testTextValue).thenThrow(new SQLException());

            DataSet ds = new DataSet("caption", resultSet, new int[] { 1, 2, 3 }, 0); //$NON-NLS-1$

            Comparable intValue = ds.loadCellValue(0, Types.INTEGER, resultSet);
            
            assertEquals(intValue, testNumericValue);
            
            Comparable textValue = ds.loadCellValue(0, Types.VARCHAR, resultSet); // VARCHAR
            
            assertEquals(testTextValue, textValue);
            
            textValue = ds.loadCellValue(0, Types.VARCHAR, resultSet); // VARCHAR

            assertEquals(testTextValue, textValue);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
