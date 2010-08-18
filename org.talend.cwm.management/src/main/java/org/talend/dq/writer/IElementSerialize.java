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
package org.talend.dq.writer;

import org.eclipse.emf.common.util.URI;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public interface IElementSerialize {

    /**
     * DOC bZhou Comment method "initProperty".
     * 
     * @param element
     * @param property
     * @return
     */
    public Property initProperty(ModelElement element, Property property);

    /**
     * DOC bZhou Comment method "initItem".
     * 
     * @param element
     * @param property
     * @param fileName
     * @return
     */
    public Item initItem(ModelElement element, Property property);

    /**
     * DOC bZhou Comment method "serialize".
     * 
     * @param property
     * @param uri
     * @return
     */
    public ReturnCode serialize(Property property, URI uri);
}
