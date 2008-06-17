/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.reports.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.talend.dataquality.analysis.Analysis;

import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.ReportsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Analysis Map</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.reports.impl.AnalysisMapImpl#getAnalysis <em>Analysis</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.AnalysisMapImpl#isMustRefresh <em>Must Refresh</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AnalysisMapImpl extends EObjectImpl implements AnalysisMap {
    /**
     * The cached value of the '{@link #getAnalysis() <em>Analysis</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAnalysis()
     * @generated
     * @ordered
     */
    protected Analysis analysis;

    /**
     * The default value of the '{@link #isMustRefresh() <em>Must Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isMustRefresh()
     * @generated
     * @ordered
     */
    protected static final boolean MUST_REFRESH_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isMustRefresh() <em>Must Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isMustRefresh()
     * @generated
     * @ordered
     */
    protected boolean mustRefresh = MUST_REFRESH_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AnalysisMapImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ReportsPackage.Literals.ANALYSIS_MAP;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Analysis getAnalysis() {
        if (analysis != null && analysis.eIsProxy()) {
            InternalEObject oldAnalysis = (InternalEObject)analysis;
            analysis = (Analysis)eResolveProxy(oldAnalysis);
            if (analysis != oldAnalysis) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReportsPackage.ANALYSIS_MAP__ANALYSIS, oldAnalysis, analysis));
            }
        }
        return analysis;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Analysis basicGetAnalysis() {
        return analysis;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAnalysis(Analysis newAnalysis) {
        Analysis oldAnalysis = analysis;
        analysis = newAnalysis;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.ANALYSIS_MAP__ANALYSIS, oldAnalysis, analysis));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isMustRefresh() {
        return mustRefresh;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMustRefresh(boolean newMustRefresh) {
        boolean oldMustRefresh = mustRefresh;
        mustRefresh = newMustRefresh;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.ANALYSIS_MAP__MUST_REFRESH, oldMustRefresh, mustRefresh));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ReportsPackage.ANALYSIS_MAP__ANALYSIS:
                if (resolve) return getAnalysis();
                return basicGetAnalysis();
            case ReportsPackage.ANALYSIS_MAP__MUST_REFRESH:
                return isMustRefresh() ? Boolean.TRUE : Boolean.FALSE;
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ReportsPackage.ANALYSIS_MAP__ANALYSIS:
                setAnalysis((Analysis)newValue);
                return;
            case ReportsPackage.ANALYSIS_MAP__MUST_REFRESH:
                setMustRefresh(((Boolean)newValue).booleanValue());
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ReportsPackage.ANALYSIS_MAP__ANALYSIS:
                setAnalysis((Analysis)null);
                return;
            case ReportsPackage.ANALYSIS_MAP__MUST_REFRESH:
                setMustRefresh(MUST_REFRESH_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ReportsPackage.ANALYSIS_MAP__ANALYSIS:
                return analysis != null;
            case ReportsPackage.ANALYSIS_MAP__MUST_REFRESH:
                return mustRefresh != MUST_REFRESH_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (mustRefresh: ");
        result.append(mustRefresh);
        result.append(')');
        return result.toString();
    }

} //AnalysisMapImpl
