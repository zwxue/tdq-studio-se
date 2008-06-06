/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.cwm.relational;

import orgomg.cwm.resource.relational.SQLSimpleType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Td Sql Data Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.cwm.relational.TdSqlDataType#getJavaDataType <em>Java Data Type</em>}</li>
 *   <li>{@link org.talend.cwm.relational.TdSqlDataType#getNullable <em>Nullable</em>}</li>
 *   <li>{@link org.talend.cwm.relational.TdSqlDataType#isUnsignedAttribute <em>Unsigned Attribute</em>}</li>
 *   <li>{@link org.talend.cwm.relational.TdSqlDataType#isCaseSensitive <em>Case Sensitive</em>}</li>
 *   <li>{@link org.talend.cwm.relational.TdSqlDataType#isAutoIncrement <em>Auto Increment</em>}</li>
 *   <li>{@link org.talend.cwm.relational.TdSqlDataType#getLocalTypeName <em>Local Type Name</em>}</li>
 *   <li>{@link org.talend.cwm.relational.TdSqlDataType#getSearchable <em>Searchable</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.cwm.relational.RelationalPackage#getTdSqlDataType()
 * @model
 * @generated
 */
public interface TdSqlDataType extends SQLSimpleType {
    /**
     * Returns the value of the '<em><b>Java Data Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * SQL data type from java.sql.Types
     * <!-- end-model-doc -->
     * @return the value of the '<em>Java Data Type</em>' attribute.
     * @see #setJavaDataType(int)
     * @see org.talend.cwm.relational.RelationalPackage#getTdSqlDataType_JavaDataType()
     * @model
     * @generated
     */
    int getJavaDataType();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdSqlDataType#getJavaDataType <em>Java Data Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Java Data Type</em>' attribute.
     * @see #getJavaDataType()
     * @generated
     */
    void setJavaDataType(int value);

    /**
     * Returns the value of the '<em><b>Nullable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * can you use null for this type?
     * <!-- end-model-doc -->
     * @return the value of the '<em>Nullable</em>' attribute.
     * @see #setNullable(short)
     * @see org.talend.cwm.relational.RelationalPackage#getTdSqlDataType_Nullable()
     * @model
     * @generated
     */
    short getNullable();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdSqlDataType#getNullable <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Nullable</em>' attribute.
     * @see #getNullable()
     * @generated
     */
    void setNullable(short value);

    /**
     * Returns the value of the '<em><b>Unsigned Attribute</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * is it unsigned?
     * <!-- end-model-doc -->
     * @return the value of the '<em>Unsigned Attribute</em>' attribute.
     * @see #setUnsignedAttribute(boolean)
     * @see org.talend.cwm.relational.RelationalPackage#getTdSqlDataType_UnsignedAttribute()
     * @model dataType="orgomg.cwm.objectmodel.core.Boolean"
     * @generated
     */
    boolean isUnsignedAttribute();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdSqlDataType#isUnsignedAttribute <em>Unsigned Attribute</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Unsigned Attribute</em>' attribute.
     * @see #isUnsignedAttribute()
     * @generated
     */
    void setUnsignedAttribute(boolean value);

    /**
     * Returns the value of the '<em><b>Case Sensitive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Case Sensitive</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Case Sensitive</em>' attribute.
     * @see #setCaseSensitive(boolean)
     * @see org.talend.cwm.relational.RelationalPackage#getTdSqlDataType_CaseSensitive()
     * @model dataType="orgomg.cwm.objectmodel.core.Boolean"
     * @generated
     */
    boolean isCaseSensitive();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdSqlDataType#isCaseSensitive <em>Case Sensitive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Case Sensitive</em>' attribute.
     * @see #isCaseSensitive()
     * @generated
     */
    void setCaseSensitive(boolean value);

    /**
     * Returns the value of the '<em><b>Auto Increment</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * can it be used for an auto-increment value?
     * <!-- end-model-doc -->
     * @return the value of the '<em>Auto Increment</em>' attribute.
     * @see #setAutoIncrement(boolean)
     * @see org.talend.cwm.relational.RelationalPackage#getTdSqlDataType_AutoIncrement()
     * @model dataType="orgomg.cwm.objectmodel.core.Boolean"
     * @generated
     */
    boolean isAutoIncrement();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdSqlDataType#isAutoIncrement <em>Auto Increment</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Auto Increment</em>' attribute.
     * @see #isAutoIncrement()
     * @generated
     */
    void setAutoIncrement(boolean value);

    /**
     * Returns the value of the '<em><b>Local Type Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * localized version of the type name (may be null)
     * <!-- end-model-doc -->
     * @return the value of the '<em>Local Type Name</em>' attribute.
     * @see #setLocalTypeName(String)
     * @see org.talend.cwm.relational.RelationalPackage#getTdSqlDataType_LocalTypeName()
     * @model dataType="orgomg.cwm.objectmodel.core.String"
     * @generated
     */
    String getLocalTypeName();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdSqlDataType#getLocalTypeName <em>Local Type Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Local Type Name</em>' attribute.
     * @see #getLocalTypeName()
     * @generated
     */
    void setLocalTypeName(String value);

    /**
     * Returns the value of the '<em><b>Searchable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * can you use "WHERE" based on this type.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Searchable</em>' attribute.
     * @see #setSearchable(short)
     * @see org.talend.cwm.relational.RelationalPackage#getTdSqlDataType_Searchable()
     * @model
     * @generated
     */
    short getSearchable();

    /**
     * Sets the value of the '{@link org.talend.cwm.relational.TdSqlDataType#getSearchable <em>Searchable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Searchable</em>' attribute.
     * @see #getSearchable()
     * @generated
     */
    void setSearchable(short value);

} // TdSqlDataType
