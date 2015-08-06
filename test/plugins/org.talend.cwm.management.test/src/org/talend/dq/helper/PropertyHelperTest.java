// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import static org.junit.Assert.assertFalse;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;

public class PropertyHelperTest {

    ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    private static Project originalProject;

    @BeforeClass
    public static void beforeAllTests() {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        RepositoryContext repositoryContext = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
        if (repositoryContext != null) {
            originalProject = repositoryContext.getProject();
        }
    }

    @AfterClass
    public static void afterAllTests() {
        if (originalProject != null) {
            Context ctx = CoreRuntimePlugin.getInstance().getContext();
            RepositoryContext repositoryContext = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
            repositoryContext.setProject(originalProject);
        }
    }

    /**
     * DOC zshen Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject(); //$NON-NLS-1$
        UnitTestBuildHelper.createRealProject("testForPropertyHelperTDQ"); //$NON-NLS-1$
    }

    @After
    public void tearDown() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject();
    }

    @Test
    public void testRealExistDuplicateName() throws PersistenceException {
        // --- create indicators
        // Indicator
        CountsIndicator createCountsIndicator = IndicatorsFactory.eINSTANCE.createCountsIndicator();
        List<IRepositoryViewObject> all = null;
        IndicatorDefinition createIndicatorDefinition = null;
        try {
            all = factory.getAll(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
            for (IRepositoryViewObject indicatorViewObject : all) {
                if (indicatorViewObject.getLabel().equalsIgnoreCase("ROW COUNT")) {
                    createIndicatorDefinition = (IndicatorDefinition) PropertyHelper.getModelElement(indicatorViewObject
                            .getProperty());
                    break;
                }
            }
        } catch (PersistenceException e1) {
            e1.printStackTrace();
            Assert.fail(e1.getMessage());
        }

        createCountsIndicator.setIndicatorDefinition(createIndicatorDefinition);

        boolean existDuplicateName = PropertyHelper.existDuplicateName("Copy of Row Count", "Row Count",
                ERepositoryObjectType.TDQ_INDICATOR_ELEMENT);
        assertFalse(existDuplicateName);
    }


}
