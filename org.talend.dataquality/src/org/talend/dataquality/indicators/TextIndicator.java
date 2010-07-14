/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Text Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.TextIndicator#getAverageLengthIndicator <em>Average Length Indicator</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.TextIndicator#getMaxLengthIndicator <em>Max Length Indicator</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.TextIndicator#getMinLengthIndicator <em>Min Length Indicator</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextIndicator()
 * @model
 * @generated
 */
public interface TextIndicator extends CompositeIndicator {

    /**
     * Returns the value of the '<em><b>Average Length Indicator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Average Length Indicator</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Average Length Indicator</em>' containment reference.
     * @see #setAverageLengthIndicator(AverageLengthIndicator)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextIndicator_AverageLengthIndicator()
     * @model containment="true"
     * @generated
     */
    AverageLengthIndicator getAverageLengthIndicator();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.TextIndicator#getAverageLengthIndicator <em>Average Length Indicator</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Average Length Indicator</em>' containment reference.
     * @see #getAverageLengthIndicator()
     * @generated
     */
    void setAverageLengthIndicator(AverageLengthIndicator value);

    /**
     * Returns the value of the '<em><b>Max Length Indicator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Max Length Indicator</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Max Length Indicator</em>' containment reference.
     * @see #setMaxLengthIndicator(MaxLengthIndicator)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextIndicator_MaxLengthIndicator()
     * @model containment="true"
     * @generated
     */
    MaxLengthIndicator getMaxLengthIndicator();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.TextIndicator#getMaxLengthIndicator <em>Max Length Indicator</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Max Length Indicator</em>' containment reference.
     * @see #getMaxLengthIndicator()
     * @generated
     */
    void setMaxLengthIndicator(MaxLengthIndicator value);

    /**
     * Returns the value of the '<em><b>Min Length Indicator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Min Length Indicator</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Min Length Indicator</em>' containment reference.
     * @see #setMinLengthIndicator(MinLengthIndicator)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getTextIndicator_MinLengthIndicator()
     * @model containment="true"
     * @generated
     */
    MinLengthIndicator getMinLengthIndicator();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.TextIndicator#getMinLengthIndicator <em>Min Length Indicator</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Min Length Indicator</em>' containment reference.
     * @see #getMinLengthIndicator()
     * @generated
     */
    void setMinLengthIndicator(MinLengthIndicator value);
} // TextIndicator
