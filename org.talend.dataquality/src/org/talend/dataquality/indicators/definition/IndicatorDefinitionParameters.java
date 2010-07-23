/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.definition;

import org.talend.dataquality.expressions.TdExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Indicator Definition Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.definition.IndicatorDefinitionParameters#getParameterKey <em>Parameter Key</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.definition.IndicatorDefinitionParameters#getParameterValue <em>Parameter Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.definition.DefinitionPackage#getIndicatorDefinitionParameters()
 * @model
 * @generated
 */
public interface IndicatorDefinitionParameters extends TdExpression {
	/**
	 * Returns the value of the '<em><b>Parameter Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Key</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Key</em>' attribute.
	 * @see #setParameterKey(String)
	 * @see org.talend.dataquality.indicators.definition.DefinitionPackage#getIndicatorDefinitionParameters_ParameterKey()
	 * @model
	 * @generated
	 */
	String getParameterKey();

	/**
	 * Sets the value of the '{@link org.talend.dataquality.indicators.definition.IndicatorDefinitionParameters#getParameterKey <em>Parameter Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameter Key</em>' attribute.
	 * @see #getParameterKey()
	 * @generated
	 */
	void setParameterKey(String value);

	/**
	 * Returns the value of the '<em><b>Parameter Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Value</em>' attribute.
	 * @see #setParameterValue(String)
	 * @see org.talend.dataquality.indicators.definition.DefinitionPackage#getIndicatorDefinitionParameters_ParameterValue()
	 * @model
	 * @generated
	 */
	String getParameterValue();

	/**
	 * Sets the value of the '{@link org.talend.dataquality.indicators.definition.IndicatorDefinitionParameters#getParameterValue <em>Parameter Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameter Value</em>' attribute.
	 * @see #getParameterValue()
	 * @generated
	 */
	void setParameterValue(String value);

} // IndicatorDefinitionParameters
