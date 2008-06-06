/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.cwm.relational;

import orgomg.cwm.resource.relational.Column;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Td Column</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.cwm.relational.TdColumn#getJavaType <em>Java Type</em>}</li>
 *   <li>{@link org.talend.cwm.relational.TdColumn#getSqlDataType <em>Sql Data Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.cwm.relational.RelationalPackage#getTdColumn()
 * @model
 * @generated
 */
public interface TdColumn extends Column {

    /**
     * Returns the value of the '<em><b>Java Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Java Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Java Type</em>' attribute.
     * @see #setJavaType(int)
     * @see org.talend.cwm.relational.RelationalPackage#getTdColumn_JavaType()
     * @model
     * @generated
     */
    int getJavaType();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdColumn#getJavaType <em>Java Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Java Type</em>' attribute.
     * @see #getJavaType()
     * @generated
     */
    void setJavaType(int value);

    /**
     * Returns the value of the '<em><b>Sql Data Type</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sql Data Type</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Sql Data Type</em>' containment reference.
     * @see #setSqlDataType(TdSqlDataType)
     * @see org.talend.cwm.relational.RelationalPackage#getTdColumn_SqlDataType()
     * @model containment="true"
     * @generated
     */
    TdSqlDataType getSqlDataType();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdColumn#getSqlDataType <em>Sql Data Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Sql Data Type</em>' containment reference.
     * @see #getSqlDataType()
     * @generated
     */
    void setSqlDataType(TdSqlDataType value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The type of the content of the column. This type is a meta-information either set by the user who knows what type of data is contained in the column, or infered from the data.
     * <!-- end-model-doc -->
     * @model contentTypeDataType="orgomg.cwm.objectmodel.core.String"
     * @generated
     */
    void setContentType(String contentType);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The type of the content of the column. This type is a meta-information either set by the user who knows what type of data is contained in the column, or infered from the data.
     * <!-- end-model-doc -->
     * @model kind="operation" dataType="orgomg.cwm.objectmodel.core.String"
     * @generated
     */
    String getContentType();
} // TdColumn
