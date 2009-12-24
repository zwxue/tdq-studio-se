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
package org.talend.cwm.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.talend.cwm.xml.TdXMLElement;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu  class global comment. Detailled comment
 */
public final class XmlElementHelper {

    private XmlElementHelper() {
    }

    /**
     * DOC xqliu Comment method "isLeafNode".
     * 
     * @param element
     * @return
     */
    public static boolean isLeafNode(TdXMLElement element) {
        boolean leafNode = false;
        XSDElementDeclaration xsdElementDeclearation = (XSDElementDeclaration) element.getXsdElementDeclaration();
        if (xsdElementDeclearation != null) {
            XSDTypeDefinition xsdTypeDef = xsdElementDeclearation.getTypeDefinition();
            if (xsdTypeDef instanceof XSDSimpleTypeDefinition) {
                leafNode = true;
            }
        }
        return leafNode;
    }

    public static boolean isFromSameTable(List<TdXMLElement> xmlElements) {
        assert xmlElements != null;

        Set<ModelElement> modelElement = new HashSet<ModelElement>();
        for (TdXMLElement xmlElement : xmlElements) {
            // TODO 10238
            modelElement.add(xmlElement);
        }
        return modelElement.size() == 1;
    }

    /**
     * DOC xqliu Comment method "getFullName".
     * 
     * @param xmlElement
     * @return
     */
    public static String getFullName(TdXMLElement xmlElement) {
        return xmlElement.getName();
    }

}
