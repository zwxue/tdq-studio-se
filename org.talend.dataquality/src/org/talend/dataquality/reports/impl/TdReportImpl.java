/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.reports.impl;

import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.PresentationParameter;
import org.talend.dataquality.reports.ReportsFactory;
import org.talend.dataquality.reports.ReportsPackage;
import org.talend.dataquality.reports.TdReport;
import orgomg.cwmx.analysis.informationreporting.impl.ReportImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Td Report</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getPresentationParams <em>Presentation Params</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getLastExecutionDate <em>Last Execution Date</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getAnalysisMap <em>Analysis Map</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getInputJrxml <em>Input Jrxml</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getOutputReportFolder <em>Output Report Folder</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getReportType <em>Report Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TdReportImpl extends ReportImpl implements TdReport {

    /**
     * The cached value of the '{@link #getPresentationParams() <em>Presentation Params</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getPresentationParams()
     * @generated
     * @ordered
     */
    protected EList<PresentationParameter> presentationParams;

    /**
     * The default value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCreationDate()
     * @generated
     * @ordered
     */
    protected static final Date CREATION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCreationDate()
     * @generated
     * @ordered
     */
    protected Date creationDate = CREATION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getLastExecutionDate() <em>Last Execution Date</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getLastExecutionDate()
     * @generated
     * @ordered
     */
    protected static final Date LAST_EXECUTION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLastExecutionDate() <em>Last Execution Date</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getLastExecutionDate()
     * @generated
     * @ordered
     */
    protected Date lastExecutionDate = LAST_EXECUTION_DATE_EDEFAULT;

    /**
     * The cached value of the '{@link #getAnalysisMap() <em>Analysis Map</em>}' containment reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getAnalysisMap()
     * @generated
     * @ordered
     */
    protected EList<AnalysisMap> analysisMap;

    /**
     * The default value of the '{@link #getInputJrxml() <em>Input Jrxml</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInputJrxml()
     * @generated
     * @ordered
     */
    protected static final String INPUT_JRXML_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInputJrxml() <em>Input Jrxml</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getInputJrxml()
     * @generated
     * @ordered
     */
    protected String inputJrxml = INPUT_JRXML_EDEFAULT;

    /**
     * The default value of the '{@link #getOutputReportFolder() <em>Output Report Folder</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getOutputReportFolder()
     * @generated
     * @ordered
     */
    protected static final String OUTPUT_REPORT_FOLDER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getOutputReportFolder() <em>Output Report Folder</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getOutputReportFolder()
     * @generated
     * @ordered
     */
    protected String outputReportFolder = OUTPUT_REPORT_FOLDER_EDEFAULT;

    /**
     * The default value of the '{@link #getReportType() <em>Report Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReportType()
     * @generated
     * @ordered
     */
    protected static final String REPORT_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReportType() <em>Report Type</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getReportType()
     * @generated
     * @ordered
     */
    protected String reportType = REPORT_TYPE_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected TdReportImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ReportsPackage.Literals.TD_REPORT;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EList<PresentationParameter> getPresentationParams() {
        if (presentationParams == null) {
            presentationParams = new EObjectContainmentEList<PresentationParameter>(PresentationParameter.class, this, ReportsPackage.TD_REPORT__PRESENTATION_PARAMS);
        }
        return presentationParams;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setCreationDate(Date newCreationDate) {
        Date oldCreationDate = creationDate;
        creationDate = newCreationDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.TD_REPORT__CREATION_DATE, oldCreationDate, creationDate));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setLastExecutionDate(Date newLastExecutionDate) {
        Date oldLastExecutionDate = lastExecutionDate;
        lastExecutionDate = newLastExecutionDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE, oldLastExecutionDate, lastExecutionDate));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EList<AnalysisMap> getAnalysisMap() {
        if (analysisMap == null) {
            analysisMap = new EObjectContainmentEList<AnalysisMap>(AnalysisMap.class, this, ReportsPackage.TD_REPORT__ANALYSIS_MAP);
        }
        return analysisMap;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getInputJrxml() {
        return inputJrxml;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setInputJrxml(String newInputJrxml) {
        String oldInputJrxml = inputJrxml;
        inputJrxml = newInputJrxml;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.TD_REPORT__INPUT_JRXML, oldInputJrxml, inputJrxml));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getOutputReportFolder() {
        return outputReportFolder;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setOutputReportFolder(String newOutputReportFolder) {
        String oldOutputReportFolder = outputReportFolder;
        outputReportFolder = newOutputReportFolder;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.TD_REPORT__OUTPUT_REPORT_FOLDER, oldOutputReportFolder, outputReportFolder));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setReportType(String newReportType) {
        String oldReportType = reportType;
        reportType = newReportType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.TD_REPORT__REPORT_TYPE, oldReportType, reportType));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT addAnalysis(Analysis analysis)
     */
    public boolean addAnalysis(Analysis analysis) {
        return this.getComponent().add(analysis);
    }

    /**
     * <!-- begin-user-doc --> Remove the analysis. Return true if the analysis is removed from the report and from its
     * internal map, false otherwise. <!-- end-user-doc -->
     * 
     * @generated NOT removeAnalysis(Analysis analysis)
     */
    public boolean removeAnalysis(Analysis analysis) {
        EList<AnalysisMap> anMaps = this.getAnalysisMap();
        boolean removedFromMap = false;
        for (AnalysisMap anMap : anMaps) {
            if (analysis.equals(anMap.getAnalysis())) {
                anMaps.remove(anMap);
                removedFromMap = true;
                break;
            }
        }
        return this.getComponent().remove(analysis) && removedFromMap;
    }

    /**
     * <!-- begin-user-doc --> Returns true is the boolean has been set correctly, false otherwise. False could mean
     * that the given analysis does not appear in this report. <!-- end-user-doc -->
     * 
     * @generated NOT boolean setMustRefresh(Analysis analysis, boolean mustRefresh)
     */
    public boolean setMustRefresh(Analysis analysis, boolean mustRefresh) {
        boolean ok = false;
        EList<AnalysisMap> anMaps = this.getAnalysisMap();
        for (AnalysisMap anMap : anMaps) {
            if (analysis.equals(anMap.getAnalysis())) {
                ok = true;
                anMap.setMustRefresh(mustRefresh);
                break;
            }
        }
        return ok;
    }

    /**
     * <!-- begin-user-doc --> Add the analysis to this report and set its refresh status. <!-- end-user-doc -->
     * 
     * @generated NOT boolean addAnalysis(Analysis analysis, boolean mustRefresh)
     */
    public boolean addAnalysis(Analysis analysis, boolean mustRefresh) {
        boolean added = this.addAnalysis(analysis);
        boolean statusSet = this.setMustRefresh(analysis, mustRefresh);
        return added && statusSet;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT mustRefresh(Analysis analysis)
     */
    public boolean mustRefresh(Analysis analysis) {
        boolean yes = false;
        EList<AnalysisMap> anMaps = this.getAnalysisMap();
        for (AnalysisMap anMap : anMaps) {
            if (analysis.equals(anMap.getAnalysis())) {
                yes = anMap.isMustRefresh();
                break;
            }
        }
        return yes;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT setReportPresentationParam(String type, String formula)
     */
    public PresentationParameter setReportPresentationParam(String type, String formula) {
        PresentationParameter param = null;
        for (PresentationParameter params : this.getPresentationParams()) {
            if (params.getType() != null && params.getType().equals(type)) {
                param = params;
                break;
            }
        }

        if (param == null) {
            // create a new one and add it the list
            param = ReportsFactory.eINSTANCE.createPresentationParameter();
            this.getPresentationParams().add(param);
        }
        param.setType(type);
        param.setFormula(BooleanExpressionHelper.createExpressionNode(null, formula));

        return param;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                return ((InternalEList<?>)getPresentationParams()).basicRemove(otherEnd, msgs);
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                return ((InternalEList<?>)getAnalysisMap()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                return getPresentationParams();
            case ReportsPackage.TD_REPORT__CREATION_DATE:
                return getCreationDate();
            case ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE:
                return getLastExecutionDate();
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                return getAnalysisMap();
            case ReportsPackage.TD_REPORT__INPUT_JRXML:
                return getInputJrxml();
            case ReportsPackage.TD_REPORT__OUTPUT_REPORT_FOLDER:
                return getOutputReportFolder();
            case ReportsPackage.TD_REPORT__REPORT_TYPE:
                return getReportType();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                getPresentationParams().clear();
                getPresentationParams().addAll((Collection<? extends PresentationParameter>)newValue);
                return;
            case ReportsPackage.TD_REPORT__CREATION_DATE:
                setCreationDate((Date)newValue);
                return;
            case ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE:
                setLastExecutionDate((Date)newValue);
                return;
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                getAnalysisMap().clear();
                getAnalysisMap().addAll((Collection<? extends AnalysisMap>)newValue);
                return;
            case ReportsPackage.TD_REPORT__INPUT_JRXML:
                setInputJrxml((String)newValue);
                return;
            case ReportsPackage.TD_REPORT__OUTPUT_REPORT_FOLDER:
                setOutputReportFolder((String)newValue);
                return;
            case ReportsPackage.TD_REPORT__REPORT_TYPE:
                setReportType((String)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                getPresentationParams().clear();
                return;
            case ReportsPackage.TD_REPORT__CREATION_DATE:
                setCreationDate(CREATION_DATE_EDEFAULT);
                return;
            case ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE:
                setLastExecutionDate(LAST_EXECUTION_DATE_EDEFAULT);
                return;
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                getAnalysisMap().clear();
                return;
            case ReportsPackage.TD_REPORT__INPUT_JRXML:
                setInputJrxml(INPUT_JRXML_EDEFAULT);
                return;
            case ReportsPackage.TD_REPORT__OUTPUT_REPORT_FOLDER:
                setOutputReportFolder(OUTPUT_REPORT_FOLDER_EDEFAULT);
                return;
            case ReportsPackage.TD_REPORT__REPORT_TYPE:
                setReportType(REPORT_TYPE_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                return presentationParams != null && !presentationParams.isEmpty();
            case ReportsPackage.TD_REPORT__CREATION_DATE:
                return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
            case ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE:
                return LAST_EXECUTION_DATE_EDEFAULT == null ? lastExecutionDate != null : !LAST_EXECUTION_DATE_EDEFAULT.equals(lastExecutionDate);
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                return analysisMap != null && !analysisMap.isEmpty();
            case ReportsPackage.TD_REPORT__INPUT_JRXML:
                return INPUT_JRXML_EDEFAULT == null ? inputJrxml != null : !INPUT_JRXML_EDEFAULT.equals(inputJrxml);
            case ReportsPackage.TD_REPORT__OUTPUT_REPORT_FOLDER:
                return OUTPUT_REPORT_FOLDER_EDEFAULT == null ? outputReportFolder != null : !OUTPUT_REPORT_FOLDER_EDEFAULT.equals(outputReportFolder);
            case ReportsPackage.TD_REPORT__REPORT_TYPE:
                return REPORT_TYPE_EDEFAULT == null ? reportType != null : !REPORT_TYPE_EDEFAULT.equals(reportType);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (creationDate: ");
        result.append(creationDate);
        result.append(", lastExecutionDate: ");
        result.append(lastExecutionDate);
        result.append(", inputJrxml: ");
        result.append(inputJrxml);
        result.append(", outputReportFolder: ");
        result.append(outputReportFolder);
        result.append(", reportType: ");
        result.append(reportType);
        result.append(')');
        return result.toString();
    }

} // TdReportImpl
