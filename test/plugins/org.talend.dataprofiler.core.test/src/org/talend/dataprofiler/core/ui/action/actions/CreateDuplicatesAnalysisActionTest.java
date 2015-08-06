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
package org.talend.dataprofiler.core.ui.action.actions;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.i18n.Messages;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
@PrepareForTest({ PropertyHelper.class, Messages.class, DefaultMessagesImpl.class, ProjectManager.class, DefinitionHandler.class,
        DependenciesHandler.class, RepositoryNodeHelper.class, WorkbenchUtils.class, ElementWriterFactory.class,
        CorePlugin.class, ConnectionHelper.class })
public class CreateDuplicatesAnalysisActionTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.CreateDuplicatesAnalysisAction#run()}.
     */
    @Test
    public void testRun() {
        try {
            CorePlugin cpMock = mock(CorePlugin.class);
            PowerMockito.mockStatic(CorePlugin.class);
            when(CorePlugin.getDefault()).thenReturn(cpMock);

            ResourceBundle rb = mock(ResourceBundle.class);
            stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb); //$NON-NLS-1$

            PowerMockito.mockStatic(Messages.class);
            when(Messages.getString(anyString())).thenReturn("aa"); //$NON-NLS-1$

            PowerMockito.mockStatic(DefaultMessagesImpl.class);
            when(DefaultMessagesImpl.getString(anyString())).thenReturn("bb"); //$NON-NLS-1$

            Map<ColumnSet, List<TdColumn>> columnsMap = new HashMap<ColumnSet, List<TdColumn>>();
            ColumnSet columSetMock = mock(ColumnSet.class);
            when(columSetMock.getName()).thenReturn("TableName"); //$NON-NLS-1$
            TdColumn columnMock = mock(TdColumn.class);
            List<TdColumn> columns = new ArrayList<TdColumn>();
            columns.add(columnMock);
            columnsMap.put(columSetMock, columns);

            Connection connectionMock = mock(Connection.class);
            PowerMockito.mockStatic(ConnectionHelper.class);
            when(ConnectionHelper.getConnection(columSetMock)).thenReturn(connectionMock);

            PowerMockito.mockStatic(PropertyHelper.class);
            when(PropertyHelper.existDuplicateName(anyString(), anyString(), (ERepositoryObjectType) any()))
                    .thenReturn(
                    false);

            PowerMockito.mockStatic(ProjectManager.class);
            ProjectManager pmMock = mock(ProjectManager.class);
            Project pMock = mock(Project.class);
            User userMock = mock(User.class);
            when(userMock.getLogin()).thenReturn("a@b.cn"); //$NON-NLS-1$
            when(pMock.getAuthor()).thenReturn(userMock);
            when(pmMock.getCurrentProject()).thenReturn(pMock);
            when(ProjectManager.getInstance()).thenReturn(pmMock);

            DefinitionHandler dhMock = mock(DefinitionHandler.class);
            Indicator indMock = mock(Indicator.class);
            when(dhMock.setDefaultIndicatorDefinition(indMock)).thenReturn(true);
            PowerMockito.mockStatic(DefinitionHandler.class);
            when(DefinitionHandler.getInstance()).thenReturn(dhMock);

            DependenciesHandler depHandlerMock = mock(DependenciesHandler.class);
            TypedReturnCode<Dependency> trc = new TypedReturnCode<Dependency>();
            when(depHandlerMock.setDependencyOn((Analysis) any(), (Connection) any())).thenReturn(trc);
            PowerMockito.mockStatic(DependenciesHandler.class);
            when(DependenciesHandler.getInstance()).thenReturn(depHandlerMock);

            PowerMockito.mockStatic(RepositoryNodeHelper.class);
            RepositoryNode rnMock = mock(RepositoryNode.class);
            when(RepositoryNodeHelper.getDataProfilingFolderNode(EResourceConstant.ANALYSIS)).thenReturn(rnMock);

            PowerMockito.mockStatic(WorkbenchUtils.class);
            IFolder folderMock = mock(IFolder.class);
            when(WorkbenchUtils.getFolder(rnMock)).thenReturn(folderMock);

            ElementWriterFactory ewfMock = mock(ElementWriterFactory.class);
            AnalysisWriter awMock = mock(AnalysisWriter.class);
            TypedReturnCode<Object> trcMock = mock(TypedReturnCode.class);
            when(trcMock.isOk()).thenReturn(true);
            TDQAnalysisItem itemMock = mock(TDQAnalysisItem.class);
            when(trcMock.getObject()).thenReturn(itemMock);
            when(awMock.create((Analysis) any(), (IFolder) any())).thenReturn(trcMock);
            when(ewfMock.createAnalysisWrite()).thenReturn(awMock);
            PowerMockito.mockStatic(ElementWriterFactory.class);
            when(ElementWriterFactory.getInstance()).thenReturn(ewfMock);

            stub(method(CorePlugin.class, "refreshDQView", Object.class)); //$NON-NLS-1$

            RepositoryNode connNodeMock = mock(RepositoryNode.class);
            when(RepositoryNodeHelper.recursiveFind(connectionMock)).thenReturn(connNodeMock);

            IEditorPart iepMock = mock(IEditorPart.class);
            when(cpMock.openEditor((AnalysisItemEditorInput) any(), anyString())).thenReturn(iepMock);

            Action action = new CreateDuplicatesAnalysisAction(columnsMap);
            action.run();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
