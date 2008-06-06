/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Text Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.TextParameters#isUseBlank <em>Use Blank</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.TextParameters#getMatchingAlgorithm <em>Matching Algorithm</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.TextParameters#isIgnoreCase <em>Ignore Case</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.TextParameters#isUseNulls <em>Use Nulls</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextParameters()
 * @model
 * @generated
 */
public interface TextParameters extends EObject {
    /**
     * Returns the value of the '<em><b>Use Blank</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Use Blank</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Use Blank</em>' attribute.
     * @see #setUseBlank(boolean)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextParameters_UseBlank()
     * @model default="true"
     * @generated
     */
    boolean isUseBlank();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.TextParameters#isUseBlank <em>Use Blank</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Use Blank</em>' attribute.
     * @see #isUseBlank()
     * @generated
     */
    void setUseBlank(boolean value);

    /**
     * Returns the value of the '<em><b>Matching Algorithm</b></em>' attribute.
     * The literals are from the enumeration {@link org.talend.dataquality.indicators.MatchingAlgorithm}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Matching Algorithm</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Matching Algorithm</em>' attribute.
     * @see org.talend.dataquality.indicators.MatchingAlgorithm
     * @see #setMatchingAlgorithm(MatchingAlgorithm)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextParameters_MatchingAlgorithm()
     * @model
     * @generated
     */
    MatchingAlgorithm getMatchingAlgorithm();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.TextParameters#getMatchingAlgorithm <em>Matching Algorithm</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Matching Algorithm</em>' attribute.
     * @see org.talend.dataquality.indicators.MatchingAlgorithm
     * @see #getMatchingAlgorithm()
     * @generated
     */
    void setMatchingAlgorithm(MatchingAlgorithm value);

    /**
     * Returns the value of the '<em><b>Ignore Case</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ignore Case</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ignore Case</em>' attribute.
     * @see #setIgnoreCase(boolean)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextParameters_IgnoreCase()
     * @model
     * @generated
     */
    boolean isIgnoreCase();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.TextParameters#isIgnoreCase <em>Ignore Case</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ignore Case</em>' attribute.
     * @see #isIgnoreCase()
     * @generated
     */
    void setIgnoreCase(boolean value);

    /**
     * Returns the value of the '<em><b>Use Nulls</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Use Nulls</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Use Nulls</em>' attribute.
     * @see #setUseNulls(boolean)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextParameters_UseNulls()
     * @model
     * @generated
     */
    boolean isUseNulls();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.TextParameters#isUseNulls <em>Use Nulls</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Use Nulls</em>' attribute.
     * @see #isUseNulls()
     * @generated
     */
    void setUseNulls(boolean value);

} // TextParameters
