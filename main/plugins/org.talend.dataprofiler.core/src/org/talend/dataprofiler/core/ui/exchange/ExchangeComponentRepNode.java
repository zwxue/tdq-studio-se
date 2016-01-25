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
package org.talend.dataprofiler.core.ui.exchange;

import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ExchangeComponentRepNode extends DQRepositoryNode {

    private final IEcosComponent ecosComponent;

    private ENodeType type;

    private String label;

    public IEcosComponent getEcosComponent() {
        return this.ecosComponent;
    }

    public ENodeType getType() {
        return this.type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ExchangeComponentRepNode(IEcosComponent ecosComponent, RepositoryNode parent, ENodeType type) {
        super(null, parent, type);
        this.type = type;
        this.ecosComponent = ecosComponent;
        this.setId(ecosComponent.getName());
        this.label = ecosComponent.getName();
    }

    @Override
    public String getLabel() {
        return this.label == null ? "" : this.label;//$NON-NLS-1$ 
    }
}
