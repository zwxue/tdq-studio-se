/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.rules.impl;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.talend.dataquality.rules.*;
import org.talend.dataquality.rules.DQRule;
import org.talend.dataquality.rules.InferredDQRule;
import org.talend.dataquality.rules.JoinElement;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dataquality.rules.RulesPackage;
import org.talend.dataquality.rules.SpecifiedDQRule;
import org.talend.dataquality.rules.WhereRule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RulesFactoryImpl extends EFactoryImpl implements RulesFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static RulesFactory init() {
        try {
            RulesFactory theRulesFactory = (RulesFactory)EPackage.Registry.INSTANCE.getEFactory("http://dataquality.rules"); 
            if (theRulesFactory != null) {
                return theRulesFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new RulesFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RulesFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case RulesPackage.DQ_RULE: return createDQRule();
            case RulesPackage.SPECIFIED_DQ_RULE: return createSpecifiedDQRule();
            case RulesPackage.INFERRED_DQ_RULE: return createInferredDQRule();
            case RulesPackage.WHERE_RULE: return createWhereRule();
            case RulesPackage.JOIN_ELEMENT: return createJoinElement();
            case RulesPackage.PARSER_RULE: return createParserRule();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case RulesPackage.TD_EXPRESSION_LIST:
                return createTdExpressionListFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case RulesPackage.TD_EXPRESSION_LIST:
                return convertTdExpressionListToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DQRule createDQRule() {
        DQRuleImpl dqRule = new DQRuleImpl();
        return dqRule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SpecifiedDQRule createSpecifiedDQRule() {
        SpecifiedDQRuleImpl specifiedDQRule = new SpecifiedDQRuleImpl();
        return specifiedDQRule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public InferredDQRule createInferredDQRule() {
        InferredDQRuleImpl inferredDQRule = new InferredDQRuleImpl();
        return inferredDQRule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public WhereRule createWhereRule() {
        WhereRuleImpl whereRule = new WhereRuleImpl();
        return whereRule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JoinElement createJoinElement() {
        JoinElementImpl joinElement = new JoinElementImpl();
        return joinElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ParserRule createParserRule() {
        ParserRuleImpl parserRule = new ParserRuleImpl();
        return parserRule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public List createTdExpressionListFromString(EDataType eDataType, String initialValue) {
        return (List)super.createFromString(initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertTdExpressionListToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RulesPackage getRulesPackage() {
        return (RulesPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static RulesPackage getPackage() {
        return RulesPackage.eINSTANCE;
    }

} //RulesFactoryImpl
