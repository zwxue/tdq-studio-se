package org.talend.dq.helper;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;

public class RepositoryNodeHelperTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    public void testFindNearestSystemFolderNode() {
        RepositoryNode node = mock(RepositoryNode.class);
        RepositoryNode parent1 = mock(RepositoryNode.class);
        when(parent1.getType()).thenReturn(ENodeType.SIMPLE_FOLDER);
        when(node.getParent()).thenReturn(parent1);
        RepositoryNode parent2 = mock(RepositoryNode.class);
        when(parent2.getType()).thenReturn(ENodeType.SYSTEM_FOLDER);
        when(parent1.getParent()).thenReturn(parent2);
        RepositoryNode parent3 = mock(RepositoryNode.class);
        when(parent3.getType()).thenReturn(ENodeType.SYSTEM_FOLDER);
        when(parent2.getParent()).thenReturn(parent3);
        RepositoryNode findNearestSysNode = RepositoryNodeHelper.findNearestSystemFolderNode(node);
        assertTrue(findNearestSysNode.equals(parent2));
    }
}
