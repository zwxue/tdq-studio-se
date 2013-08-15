// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views.provider;

import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelDecorator;


/**
 * created by zshen on Jun 3, 2013
 * Detailled comment
 *
 */
public class DQRepositoryViewCellLableProvider extends DecoratingStyledCellLabelProvider {

    /**
     * DOC zshen DQRepositoryViewCellLableProvider constructor comment.
     *
     * @param labelProvider
     * @param decorator
     * @param decorationContext
     */
    public DQRepositoryViewCellLableProvider(IStyledLabelProvider labelProvider, ILabelDecorator decorator,
            IDecorationContext decorationContext) {
        super(labelProvider, decorator, decorationContext);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
     */
    @Override
    public String getToolTipText(Object element) {
        // TODO return a meaningfull message.
        return "";
    }

}
