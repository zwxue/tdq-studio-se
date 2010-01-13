// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.List;

import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * The interface class to handle the change when drop columns.
 */
public abstract class AbstractColumnDropTree extends AbstractPagePart {

    public static final String COLUMNVIEWER_KEY = "COLUMNVIEWER_KEY"; //$NON-NLS-1$

    public abstract boolean canDrop(ModelElement modelElement);

    public abstract void dropModelElements(List<? extends ModelElement> modelElements, int index);
}
