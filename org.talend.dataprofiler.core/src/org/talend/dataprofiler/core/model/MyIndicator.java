// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorValueType;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dataquality.rules.JoinElement;
import orgomg.cwm.analysis.businessnomenclature.VocabularyElement;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;
import orgomg.cwm.analysis.transformation.DataObjectSet;
import orgomg.cwm.foundation.businessinformation.Description;
import orgomg.cwm.foundation.businessinformation.Document;
import orgomg.cwm.foundation.businessinformation.ResponsibleParty;
import orgomg.cwm.foundation.expressions.ElementNode;
import orgomg.cwm.management.warehouseoperation.ChangeRequest;
import orgomg.cwm.management.warehouseoperation.Measurement;
import orgomg.cwm.objectmodel.core.Constraint;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Namespace;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.objectmodel.core.Stereotype;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwm.objectmodel.core.VisibilityKind;
import orgomg.cwmx.resource.dmsii.DASDLProperty;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class MyIndicator implements UserDefIndicator {

    private Integer count = 1000;

    public Long getCount(Object dataValue) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getDatatype() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Long getDistinctValueCount() {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Object> getDistinctValues() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getDuplicateValueCount() {
        // TODO Auto-generated method stub
        return null;
    }

    public Double getFrequency(Object dataValue) {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getMatchingValueCount() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getNotMatchingValueCount() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getUniqueValueCount() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Object> getUniqueValues() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getUserCount() {
        // TODO Auto-generated method stub
        return new Long(count);
    }

    public String getValue() {
        return "test value";
    }

    public HashMap<Object, Long> getValueToFreq() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDatatype(int value) {
        // TODO Auto-generated method stub

    }

    public void setDistinctValueCount(Long value) {
        // TODO Auto-generated method stub

    }

    public void setMatchingValueCount(Long value) {
        // TODO Auto-generated method stub

    }

    public void setNotMatchingValueCount(Long value) {
        // TODO Auto-generated method stub

    }

    public void setUniqueValueCount(Long value) {
        // TODO Auto-generated method stub

    }

    public void setUserCount(Long value) {
        // TODO Auto-generated method stub

    }

    public void setValue(String value) {
        // TODO Auto-generated method stub

    }

    public void setValueToFreq(HashMap<Object, Long> value) {
        // TODO Auto-generated method stub

    }

    public boolean finalizeComputation() {
        // TODO Auto-generated method stub
        return true;
    }

    public ModelElement getAnalyzedElement() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getCount() {
        // TODO Auto-generated method stub
        return null;
    }

    public DataminingType getDataminingType() {
        // TODO Auto-generated method stub
        return null;
    }

    public IndicatorDefinition getIndicatorDefinition() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getInstanceValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Expression> getInstantiatedExpressions() {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression getInstantiatedExpressions(String language) {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getIntegerValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<JoinElement> getJoinConditions() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLongDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long getNullCount() {
        // TODO Auto-generated method stub
        return null;
    }

    public IndicatorParameters getParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPurpose() {
        // TODO Auto-generated method stub
        return null;
    }

    public Double getRealValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public IndicatorValueType getValueType() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean handle(Object data) {
        count++;
        return true;
    }

    public boolean isComputed() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean prepare() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean reset() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setAnalyzedElement(ModelElement value) {
        // TODO Auto-generated method stub

    }

    public void setComputed(boolean value) {
    }

    public void setCount(Long value) {
        // TODO Auto-generated method stub

    }

    public void setDataminingType(DataminingType value) {
        // TODO Auto-generated method stub

    }

    public void setIndicatorDefinition(IndicatorDefinition value) {
        // TODO Auto-generated method stub

    }

    public boolean setInstantiatedExpression(Expression expression) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setNullCount(Long value) {
        // TODO Auto-generated method stub

    }

    public void setParameters(IndicatorParameters value) {
        // TODO Auto-generated method stub

    }

    public boolean storeSqlResults(List<Object[]> objects) {
        // TODO Auto-generated method stub
        return false;
    }

    public EList<ChangeRequest> getChangeRequest() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Dependency> getClientDependency() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Constraint> getConstraint() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<DASDLProperty> getDasdlProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Description> getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Document> getDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<ElementNode> getElementNode() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Package> getImporter() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Measurement> getMeasurement() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public Namespace getNamespace() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<RenderedObject> getRenderedObject() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<ResponsibleParty> getResponsibleParty() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<DataObjectSet> getSet() {
        // TODO Auto-generated method stub
        return null;
    }

    public Stereotype getStereotype() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<Dependency> getSupplierDependency() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<TaggedValue> getTaggedValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public VisibilityKind getVisibility() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<VocabularyElement> getVocabularyElement() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setName(String value) {
        // TODO Auto-generated method stub

    }

    public void setNamespace(Namespace value) {
        // TODO Auto-generated method stub

    }

    public void setStereotype(Stereotype value) {
        // TODO Auto-generated method stub

    }

    public void setVisibility(VisibilityKind value) {
        // TODO Auto-generated method stub

    }

    public TreeIterator<EObject> eAllContents() {
        // TODO Auto-generated method stub
        return null;
    }

    public EClass eClass() {
        // TODO Auto-generated method stub
        return null;
    }

    public EObject eContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    public EStructuralFeature eContainingFeature() {
        // TODO Auto-generated method stub
        return null;
    }

    public EReference eContainmentFeature() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<EObject> eContents() {
        // TODO Auto-generated method stub
        return null;
    }

    public EList<EObject> eCrossReferences() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object eGet(EStructuralFeature feature) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object eGet(EStructuralFeature feature, boolean resolve) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean eIsProxy() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean eIsSet(EStructuralFeature feature) {
        // TODO Auto-generated method stub
        return false;
    }

    public Resource eResource() {
        // TODO Auto-generated method stub
        return null;
    }

    public void eSet(EStructuralFeature feature, Object newValue) {
        // TODO Auto-generated method stub

    }

    public void eUnset(EStructuralFeature feature) {
        // TODO Auto-generated method stub

    }

    public EList<Adapter> eAdapters() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean eDeliver() {
        // TODO Auto-generated method stub
        return false;
    }

    public void eNotify(Notification notification) {
        // TODO Auto-generated method stub

    }

    public void eSetDeliver(boolean deliver) {
        // TODO Auto-generated method stub

    }

}
