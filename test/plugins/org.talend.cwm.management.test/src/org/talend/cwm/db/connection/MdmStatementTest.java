package org.talend.cwm.db.connection;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import orgomg.cwm.objectmodel.core.ModelElement;

@PrepareForTest({ MdmStatement.class })
public class MdmStatementTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    public void testGetResultSet() {
        // fail("Not yet implemented");
    }

    @Test
    public void tidyResultSet() {
        List<ModelElement> ls = new ArrayList<ModelElement>();
        ModelElement mode = mock(ModelElement.class);
        when(mode.getName()).thenReturn("Picture");
        ModelElement mode2 = mock(ModelElement.class);
        ls.add(mode);
        ls.add(mode2);
        when(mode2.getName()).thenReturn("Family");
        String resultSet[] = new String[] { "<result><Product><Family>[2]</Family><Picture>/imageserver/upload/TalendShop/trucker_hat.jpg?width=150&amp;amp;height=90&amp;amp;preserveAspectRatio=true</Picture></Product></result>" };
        MdmWebserviceConnection mdmWebServiceConn = mock(MdmWebserviceConnection.class);
        MdmStatement mdmStatement = new MdmStatement(mdmWebServiceConn);
        ModelElement[] columnTitles = ls.toArray(new ModelElement[ls.size()]);
        List<Map<String, String>> tidyResultSet = mdmStatement.tidyResultSet(columnTitles, resultSet);
        assertTrue(tidyResultSet.size() == 1);

    }

}
