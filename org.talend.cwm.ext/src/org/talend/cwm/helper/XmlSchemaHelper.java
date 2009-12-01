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
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.xml.TdXMLDocument;

/**
 * DOC mzhao class global comment. Detailled comment
 */
public final class XmlSchemaHelper {

    private XmlSchemaHelper() {

    }
    public static List<TdXMLDocument> getDocuments(Collection<? extends EObject> elements) {
        List<TdXMLDocument> documents = new ArrayList<TdXMLDocument>();
        for (EObject modelElement : elements) {
            TdXMLDocument document = SwitchHelpers.XMLDOCUMENT_SWITCH.doSwitch(modelElement);
            if (document != null) {
                documents.add(document);
            }
        }
        return documents;
    }
}
