/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.schema;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Connection Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.schema.ConnectionIndicator#getSchemaIndicators <em>Schema Indicators</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.schema.SchemaPackage#getConnectionIndicator()
 * @model
 * @generated
 */
public interface ConnectionIndicator extends SchemaIndicator {
    /**
     * Returns the value of the '<em><b>Schema Indicators</b></em>' containment reference list.
     * The list contents are of type {@link org.talend.dataquality.indicators.schema.SchemaIndicator}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Schema Indicators</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Schema Indicators</em>' containment reference list.
     * @see org.talend.dataquality.indicators.schema.SchemaPackage#getConnectionIndicator_SchemaIndicators()
     * @model containment="true"
     * @generated
     */
    EList<SchemaIndicator> getSchemaIndicators();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean addSchemaIndicator(SchemaIndicator schemaIndicator);

} // ConnectionIndicator
