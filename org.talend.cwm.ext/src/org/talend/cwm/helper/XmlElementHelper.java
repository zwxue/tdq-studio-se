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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.talend.cwm.xml.TdXMLContent;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.TdXMLElement;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu  class global comment. Detailled comment
 */
public final class XmlElementHelper {

    public static final String SLASH = "/";

    public static final String DOUBLE_SLASH = "//";

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

    /**
     * DOC xqliu Comment method "isFromSameTable".
     * 
     * @param xmlElements
     * @return
     */
    public static boolean isFromSameTable(List<TdXMLElement> xmlElements) {
        assert xmlElements != null;

        Set<String> modelElementNames = new HashSet<String>();
        for (TdXMLElement xmlElement : xmlElements) {
            modelElementNames.add(getFullName(xmlElement));
        }
        return modelElementNames.size() == 1;
    }

    /**
     * DOC xqliu Comment method "getFullName".
     * 
     * @param xmlElement
     * @return
     */
    public static String getFullName(TdXMLElement xmlElement) {
        ModelElement parentElement = getParentElement(xmlElement);
        if (parentElement != null) {
            return DOUBLE_SLASH + parentElement.getName() + SLASH + xmlElement.getName();
        }
        return DOUBLE_SLASH + xmlElement.getName();
    }

    /**
     * DOC xqliu Comment method "getParentElement".
     * 
     * @param xmlElement
     * @return
     */
    public static ModelElement getParentElement(TdXMLElement xmlElement) {
        EObject temp = xmlElement.eContainer();
        if (temp instanceof TdXMLContent) {
            EObject eContainer = temp.eContainer();
            if (eContainer instanceof TdXMLElement || eContainer instanceof TdXMLDocument) {
                return (ModelElement) eContainer;
            }
        } else if (temp instanceof TdXMLElement || temp instanceof TdXMLDocument) {
            return (ModelElement) temp;
        }
        return null;
    }

    /**
     * DOC xqliu Comment method "getParentElementName".
     * 
     * @param xmlElement
     * @return
     */
    public static String getParentElementName(TdXMLElement xmlElement) {
        ModelElement parentElement = getParentElement(xmlElement);
        return parentElement == null ? "" : parentElement.getName();
    }

    /**
     * DOC xqliu Comment method "clearLeafNode".
     * 
     * @param modelElements
     * @return
     */
    public static List<ModelElement> clearLeafNode(List<? extends ModelElement> modelElements) {
        List<ModelElement> result = new ArrayList<ModelElement>();
        for (ModelElement me : modelElements) {
            if (me instanceof TdXMLElement) {
                if (!isLeafNode((TdXMLElement) me)) {
                    result.add(me);
                }
            } else {
                result.add(me);
            }
        }
        return result;
    }

    /**
     * DOC xqliu Comment method "getLeafNode".
     * 
     * @param modelElements
     * @return
     */
    public static List<TdXMLElement> getLeafNode(List<? extends ModelElement> modelElements) {
        List<TdXMLElement> result = new ArrayList<TdXMLElement>();
        for (ModelElement me : modelElements) {
            if (me instanceof TdXMLElement && isLeafNode((TdXMLElement) me)) {
                result.add((TdXMLElement) me);
            }
        }
        return result;
    }
}
