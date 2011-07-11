package org.talend.dq.helper;

import java.util.Comparator;

import org.talend.repository.model.IRepositoryNode;

/**
 * comparator for IRepositoryNode.
 * 
 * DOC Administrator class global comment. Detailled comment
 */
public class RepositoryNodeComparator implements Comparator<IRepositoryNode> {

        public int compare(IRepositoryNode o1, IRepositoryNode o2) {
            if (o1 == null || o2 == null) {
                return 0;
            }
            String label1 = o1.getLabel();
            String label2 = o2.getLabel();
            if (label1 == null || label2 == null) {
                return 0;
            }
            return label1.compareTo(label2);
        }

    }