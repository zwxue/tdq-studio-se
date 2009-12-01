/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.cwm.xml;

import org.eclipse.emf.common.util.EList;
import orgomg.cwm.resource.xml.Content;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Td XML Content</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.cwm.xml.TdXMLContent#getXmlElements <em>Xml Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.cwm.xml.XmlPackage#getTdXMLContent()
 * @model
 * @generated
 */
public interface TdXMLContent extends Content {

    /**
     * Returns the value of the '<em><b>Xml Elements</b></em>' containment reference list.
     * The list contents are of type {@link org.talend.cwm.xml.TdXMLElement}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Xml Elements</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Xml Elements</em>' containment reference list.
     * @see org.talend.cwm.xml.XmlPackage#getTdXMLContent_XmlElements()
     * @model containment="true"
     * @generated
     */
    EList<TdXMLElement> getXmlElements();

} // TdXMLContent
