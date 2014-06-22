package org.talend.dq.helper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;

@PrepareForTest({ ProxyRepositoryFactory.class, WorkspaceUtils.class })
public class PropertyHelperTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    public void testExistDuplicateName() throws PersistenceException {
        String newName = "a/";
        String oldName = "ab";
        ERepositoryObjectType type = mock(ERepositoryObjectType.class);
        IRepositoryViewObject repObj1 = mock(IRepositoryViewObject.class);
        Property prop1 = mock(Property.class);
        when(repObj1.getProperty()).thenReturn(prop1);
        when(prop1.getLabel()).thenReturn("a_");
        when(prop1.getDisplayName()).thenReturn("a?");
        IRepositoryViewObject repObj2 = mock(IRepositoryViewObject.class);
        Property prop2 = mock(Property.class);
        when(repObj2.getProperty()).thenReturn(prop2);
        when(prop2.getDisplayName()).thenReturn("b");
        when(prop2.getLabel()).thenReturn("b");
        List<IRepositoryViewObject> ls = new ArrayList<IRepositoryViewObject>();
        ls.add(repObj1);
        ls.add(repObj2);
        PowerMockito.mockStatic(ProxyRepositoryFactory.class);
        ProxyRepositoryFactory proxFactory = mock(ProxyRepositoryFactory.class);
        when(proxFactory.getAll(type, true, false)).thenReturn(ls);
        when(ProxyRepositoryFactory.getInstance()).thenReturn(proxFactory);
        boolean existDuplicateName = PropertyHelper.existDuplicateName(newName, oldName, type);
        assertTrue(existDuplicateName);
        // no duplicate name
        newName = "c";
        existDuplicateName = PropertyHelper.existDuplicateName(newName, oldName, type);
        assertTrue(!existDuplicateName);

    }

}
