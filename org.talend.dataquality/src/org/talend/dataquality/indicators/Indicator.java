/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators;

import java.math.BigInteger;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.rules.JoinElement;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Indicator</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#getCount <em>Count</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#getNullCount <em>Null Count</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#getAnalyzedElement <em>Analyzed Element</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#getDataminingType <em>Datamining Type</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#getIndicatorDefinition <em>Indicator Definition</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#getInstantiatedExpressions <em>Instantiated Expressions</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#isComputed <em>Computed</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.Indicator#getJoinConditions <em>Join Conditions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator()
 * @model
 * @generated
 */
public interface Indicator extends ModelElement {

    /**
     * Returns the value of the '<em><b>Count</b></em>' attribute. The default value is <code>"0"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Count</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Count</em>' attribute.
     * @see #setCount(BigInteger)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_Count()
     * @model default="0"
     * @generated
     */
    Long getCount();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.Indicator#getCount <em>Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Count</em>' attribute.
     * @see #getCount()
     * @generated
     */
    void setCount(Long value);

    /**
     * Returns the value of the '<em><b>Null Count</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Null Count</em>' attribute isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Null Count</em>' attribute.
     * @see #setNullCount(Long)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_NullCount()
     * @model default="0"
     * @generated
     */
    Long getNullCount();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.Indicator#getNullCount <em>Null Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Null Count</em>' attribute.
     * @see #getNullCount()
     * @generated
     */
    void setNullCount(Long value);

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference isn't clear, there really should be more of
     * a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference.
     * @see #setParameters(IndicatorParameters)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_Parameters()
     * @model containment="true"
     * @generated
     */
    IndicatorParameters getParameters();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.Indicator#getParameters <em>Parameters</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Parameters</em>' containment reference.
     * @see #getParameters()
     * @generated
     */
    void setParameters(IndicatorParameters value);

    /**
     * Returns the value of the '<em><b>Analyzed Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Analyzed Element</em>' reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Analyzed Element</em>' reference.
     * @see #setAnalyzedElement(ModelElement)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_AnalyzedElement()
     * @model
     * @generated
     */
    ModelElement getAnalyzedElement();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.Indicator#getAnalyzedElement <em>Analyzed Element</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Analyzed Element</em>' reference.
     * @see #getAnalyzedElement()
     * @generated
     */
    void setAnalyzedElement(ModelElement value);

    /**
     * Returns the value of the '<em><b>Datamining Type</b></em>' attribute.
     * The literals are from the enumeration {@link org.talend.dataquality.indicators.DataminingType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Datamining Type</em>' attribute isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Datamining Type</em>' attribute.
     * @see org.talend.dataquality.indicators.DataminingType
     * @see #setDataminingType(DataminingType)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_DataminingType()
     * @model
     * @generated
     */
    DataminingType getDataminingType();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.Indicator#getDataminingType <em>Datamining Type</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Datamining Type</em>' attribute.
     * @see org.talend.dataquality.indicators.DataminingType
     * @see #getDataminingType()
     * @generated
     */
    void setDataminingType(DataminingType value);

    /**
     * Returns the value of the '<em><b>Indicator Definition</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Indicator Definition</em>' reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Indicator Definition</em>' reference.
     * @see #setIndicatorDefinition(IndicatorDefinition)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_IndicatorDefinition()
     * @model
     * @generated
     */
    IndicatorDefinition getIndicatorDefinition();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.Indicator#getIndicatorDefinition <em>Indicator Definition</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Indicator Definition</em>' reference.
     * @see #getIndicatorDefinition()
     * @generated
     */
    void setIndicatorDefinition(IndicatorDefinition value);

    /**
     * Returns the value of the '<em><b>Instantiated Expressions</b></em>' containment reference list.
     * The list contents are of type {@link orgomg.cwm.objectmodel.core.Expression}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Instantiated Expressions</em>' containment reference list isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Instantiated Expressions</em>' containment reference list.
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_InstantiatedExpressions()
     * @model containment="true"
     * @generated
     */
    EList<Expression> getInstantiatedExpressions();

    /**
     * Returns the value of the '<em><b>Computed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * a flag that tells whether this indicator is computed or not.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Computed</em>' attribute.
     * @see #setComputed(boolean)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_Computed()
     * @model
     * @generated
     */
    boolean isComputed();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.Indicator#isComputed <em>Computed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Computed</em>' attribute.
     * @see #isComputed()
     * @generated
     */
    void setComputed(boolean value);

    /**
     * Returns the value of the '<em><b>Join Conditions</b></em>' containment reference list.
     * The list contents are of type {@link org.talend.dataquality.rules.JoinElement}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Join Conditions</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Join Conditions</em>' containment reference list.
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getIndicator_JoinConditions()
     * @model containment="true"
     * @generated
     */
    EList<JoinElement> getJoinConditions();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean handle(Object data);

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean reset();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @model kind="operation"
     * @generated
     */
    String getPurpose();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @model kind="operation"
     * @generated
     */
    String getLongDescription();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> Prepares this indicator for a computation.
     * This method can be used for initialization some internal state data from parameters. Or initializing some
     * internal values before handling data. <!-- end-model-doc -->
     * 
     * @model
     * @generated
     */
    boolean prepare();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> can be called after having given all
     * objects to handle. Subclasses can use this method for doing some computation that must be done at the end only.
     * <!-- end-model-doc -->
     * 
     * @model
     * @generated
     */
    boolean finalizeComputation();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> Stores the results of an SQL query. <!--
     * end-model-doc -->
     * 
     * @model objectsDataType="org.talend.dataquality.indicators.ObjectArray"
     * @generated
     */
    boolean storeSqlResults(List<Object[]> objects);

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> Returns the instantiated expression for
     * the given language or null. <!-- end-model-doc -->
     * 
     * @model
     * @generated
     */
    Expression getInstantiatedExpressions(String language);

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> Sets the expression in the list of
     * instantiated expressions. If an expression for the given language already exists, it is updated. Otherwise the
     * expression is added to the list. <!-- end-model-doc -->
     * 
     * @model
     * @generated
     */
    boolean setInstantiatedExpression(Expression expression);

    /**
     * <!-- begin-user-doc -->
     * 
     * Returns the integer value if the indicator is an integer valued indicator. Can return null. This method must be
     * implemented in order to store the value in the datamart.
     * 
     * <!-- end-user-doc -->
     * 
     * @model kind="operation"
     * @generated
     */
    Long getIntegerValue();

    /**
     * <!-- begin-user-doc -->
     * 
     * Returns the real value if the indicator is a real valued indicator. Can return null. This method must be
     * implemented in order to store the value in the datamart.
     * 
     * <!-- end-user-doc -->
     * 
     * @model kind="operation"
     * @generated
     */
    Double getRealValue();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model kind="operation"
     * @generated
     */
    IndicatorValueType getValueType();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Return the instance value of the indicator or null when not applicable.
     * <!-- end-model-doc -->
     * @model kind="operation"
     * @generated
     */
    String getInstanceValue();

} // Indicator
