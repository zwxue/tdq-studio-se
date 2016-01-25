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
package org.talend.dataprofiler.core.ui.editor.analysis;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.dataprofiler.core.ui.pref.EditorPreferencePage;

/**
 * DOC yyin class global comment. Detailled comment
 */
@PrepareForTest({ EditorPreferencePage.class })
public class ColumnMasterDetailsPageTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    // private ColumnAnalysisDetailsPage detail;
    //
    // private AnaResourceFileHelper helper_mock;

    /**
     * DOC yyin Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // FormEditor fe = mock(FormEditor.class);
        // mockStatic(EditorPreferencePage.class);
        // expect(EditorPreferencePage.getCurrentFolding()).andReturn(1);
        // replayAll();
        //
        // // AnaResourceFileHelper mhelper = mock(AnaResourceFileHelper.class);
        //
        // FileEditorInput fei = mock(FileEditorInput.class);
        // IFile mfile = mock(IFile.class);
        // when(fe.getEditorInput()).thenReturn(fei, null);
        // when(fei.getFactoryId()).thenReturn("file");
        // when(fei.getFile()).thenReturn(mfile);
        // Analysis mana = mock(Analysis.class);
        //
        // helper_mock = mock(AnaResourceFileHelper.class);
        // PowerMockito.mockStatic(AnaResourceFileHelper.class);
        // when(AnaResourceFileHelper.getInstance()).thenReturn(helper_mock);

        // REASON for CAN NOT MOCK------
        // 1,can not mock final class
        // helper_mock = mock(AnaResourceFileHelper.class);
        // PowerMockito.mockStatic(AnaResourceFileHelper.class);
        // when(AnaResourceFileHelper.getInstance()).thenReturn(helper_mock);

        // 2, can not spy, because the related method is called when using new XXX
        // dparent = PowerMockito.spy(detail);
        // PowerMockito.doNothing().when(dparent, "initialize", fe);

        // detail = new ColumnMasterDetailsPage(fe, "1", "file");

        // stub(EditorPreferencePage.getCurrentFolding()).toReturn(1);

    }

    /**
     * DOC yyin Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#getConnCombo()}.
     */
    @Test
    public void testGetConnCombo() {
        // assertNotNull(detail.getConnCombo());
        // assertEquals(detail.getConnCombo().getClass().toString(),
        // "org.eclipse.nebula.widgets.tablecombo.TableCombo");
    }

}
