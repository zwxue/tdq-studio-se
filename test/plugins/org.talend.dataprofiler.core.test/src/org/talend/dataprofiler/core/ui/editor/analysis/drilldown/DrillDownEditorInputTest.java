// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.analysis.drilldown;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.ui.editor.preview.model.MenuItemEntity;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC yyin  class global comment. Detailled comment
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConnectionUtils.class })
public class DrillDownEditorInputTest {

    private DrillDownEditorInput ddInput;

    Indicator currIndicator;

    /**
     * DOC yyin Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Analysis analysis = mock(Analysis.class);
        currIndicator = mock(Indicator.class);
        MenuItemEntity menuItemEntity = mock(MenuItemEntity.class);
        when(menuItemEntity.getLabel()).thenReturn("view rows");

        ChartDataEntity dataEntity = mock(ChartDataEntity.class);
        when(dataEntity.getIndicator()).thenReturn(currIndicator);

        ddInput = new DrillDownEditorInput(analysis, dataEntity, menuItemEntity);

    }

    /**
     * DOC yyin Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.editor.analysis.drilldown.DrillDownEditorInput#getDataSet()}. before the
     * modify, when size 1 != size 2, exception will be throw. after modified, this status can be executed successfully.
     */
    @Test
    public void testGetDataSet() {
        TdXmlElementType analysisElement = mock(TdXmlElementType.class);
        when(currIndicator.getAnalyzedElement()).thenReturn(analysisElement);
        DrillDownEditorInput spydd = spy(ddInput);
        // size 1=2
        List<String> colList = new ArrayList<String>();
        colList.add("id");
        colList.add("name");
        doReturn(colList).when(spydd).filterAdaptColumnHeader();
        // when(spydd.filterAdaptColumnHeader()).thenReturn(colList);

        // size 2=3 for each object
        List<Object[]> newList = new ArrayList<Object[]>();
        Object[] o1 = { "1", null, "name" };
        Object[] o2 = { "2", null, "name2" };
        newList.add(o1);
        newList.add(o2);
        doReturn(newList).when(spydd).filterAdaptDataList();
        // when(spydd.filterAdaptDataList()).thenReturn(newList);
        // XmlPackage pack = mock(XmlPackage.class);
        // EClass ec = mock(EClass.class);
        // when(analysisElement.eClass()).thenReturn(ec);
        // when(ec.eContainer()).thenReturn(pack);
        // when(ec.getClassifierID()).thenReturn(XmlPackage.TD_XML_ELEMENT_TYPE);
        // TdXmlElementType xmlElement = mock(TdXmlElementType.class);
        // List<TdXmlElementType> xlist = new ArrayList<TdXmlElementType>();
        // xlist.add(xmlElement);
        //
        // PowerMockito.mockStatic(ConnectionUtils.class);
        // when(ConnectionUtils.getXMLElements(xmlElement)).thenReturn(xlist);

        Assert.assertNotNull(spydd.getDataSet());
    }

}
