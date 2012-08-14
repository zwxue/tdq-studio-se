// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.PatternPackage;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.pattern.impl.RegularExpressionImpl;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.PatternWriter;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * created by yyin on 2012-8-13 Detailled comment
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ProxyRepositoryFactory.class, ResourceManager.class, ElementWriterFactory.class })
public class UpdatePatternExpressionToTdExpressionTaskTest {

    UpdatePatternExpressionToTdExpressionTask upTask;

    /**
     * DOC yyin Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

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
     * {@link org.talend.dataprofiler.core.migration.impl.UpdatePatternExpressionToTdExpressionTask#doExecute()}.
     * 
     * @throws Exception
     */
    @Test
    public void testDoExecute() throws Exception {
        ProxyRepositoryFactory proxFactory = mock(ProxyRepositoryFactory.class);
        stub(method(ProxyRepositoryFactory.class, "getInstance")).toReturn(proxFactory);
        IRepositoryViewObject repObject = mock(IRepositoryViewObject.class);
        RootContainer<String, IRepositoryViewObject> container = new RootContainer<String, IRepositoryViewObject>();
        List<IRepositoryViewObject> ls = new ArrayList<IRepositoryViewObject>();
        ls.add(repObject);
        container.addMember("1", repObject);

        when(proxFactory.getTdqRepositoryViewObjects(ERepositoryObjectType.TDQ_PATTERN_REGEX, "text")).thenReturn(container);
        Property prop = mock(Property.class);
        TDQPatternItem item = mock(TDQPatternItem.class);
        when(repObject.getProperty()).thenReturn(prop);
        when(prop.getItem()).thenReturn(item);

        Pattern pattern = mock(Pattern.class);
        InternalEObject eo = mock(InternalEObject.class);
        EList<PatternComponent> components = new EObjectContainmentEList<PatternComponent>(PatternComponent.class, eo,
                PatternPackage.PATTERN__COMPONENTS);
        RegularExpression re = PatternFactory.eINSTANCE.createRegularExpression();
        TdExpression td = RelationalFactory.eINSTANCE.createTdExpression();
        td.setBody("body");
        td.setLanguage("SQL");
        re.setExpression(td);
        components.add(re);
        when(pattern.getComponents()).thenReturn(components);
        when(item.getPattern()).thenReturn(pattern);

        // mock something for super-class AbstractWorksapceUpdateTask
        IFolder folder = mock(IFolder.class);
        when(folder.getFullPath()).thenReturn(new Path(""));
        stub(method(ResourceManager.class, "getPatternRegexFolder")).toReturn(folder);
        IProject proj = mock(IProject.class);
        when(proj.getFullPath()).thenReturn(new Path(""));
        when(proj.getLocation()).thenReturn(new Path(""));
        stub(method(ResourceManager.class, "getRootProject")).toReturn(proj);

        ElementWriterFactory ewFactory = mock(ElementWriterFactory.class);
        PatternWriter pw = mock(PatternWriter.class);
        when(pw.save(item, true)).thenReturn(null);
        stub(method(ElementWriterFactory.class, "getInstance")).toReturn(ewFactory);
        when(ewFactory.createPatternWriter()).thenReturn(pw);

        upTask = new UpdatePatternExpressionToTdExpressionTask();
        boolean success = upTask.doExecute();
        assertTrue(success);
        assertEquals(components.size(), 1);
        for (PatternComponent pComponet : components) {
            RegularExpressionImpl regularExpress = (RegularExpressionImpl) pComponet;
            Expression ex = regularExpress.getExpression();
            assertEquals(ex.getBody(), "body");
            assertEquals(ex.getLanguage(), "SQL");
        }
    }

}
