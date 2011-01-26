// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.Expression;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public class PatternRepNode extends RepositoryNode {

    /**
     * DOC klliu PatternRepNode constructor comment.
     * @param object
     * @param parent
     * @param type
     */
    public PatternRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        List<IRepositoryNode> languageElement = new ArrayList<IRepositoryNode>();
        IRepositoryViewObject object = this.getObject();
        TDQPatternItem patternItem = (TDQPatternItem) object.getProperty().getItem();
        Pattern pattern = patternItem.getPattern();
        EList<PatternComponent> components = pattern.getComponents();
        for (PatternComponent component : components) {
            RegularExpression re = (RegularExpression) component;
            Expression expression = re.getExpression();
            String language = expression.getLanguage();
            PatternLanguageRepNode plrn = new PatternLanguageRepNode(this, ENodeType.TDQ_REPOSITORY_ELEMENT);
            plrn.setId(language);
            plrn.setLabel(language);
            languageElement.add(plrn);
        }
        return languageElement;
    }
}