/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.analysis.category.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.talend.dataquality.analysis.category.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class CategoryFactoryImpl extends EFactoryImpl implements CategoryFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static CategoryFactory init() {
        try {
            CategoryFactory theCategoryFactory = (CategoryFactory)EPackage.Registry.INSTANCE.getEFactory("http://org.talend.dataquality.category"); 
            if (theCategoryFactory != null) {
                return theCategoryFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new CategoryFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CategoryFactoryImpl() {
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
            case CategoryPackage.ANALYSIS_CATEGORY: return createAnalysisCategory();
            case CategoryPackage.ANALYSIS_CATEGORIES: return createAnalysisCategories();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AnalysisCategory createAnalysisCategory() {
        AnalysisCategoryImpl analysisCategory = new AnalysisCategoryImpl();
        return analysisCategory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AnalysisCategories createAnalysisCategories() {
        AnalysisCategoriesImpl analysisCategories = new AnalysisCategoriesImpl();
        return analysisCategories;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CategoryPackage getCategoryPackage() {
        return (CategoryPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static CategoryPackage getPackage() {
        return CategoryPackage.eINSTANCE;
    }

} //CategoryFactoryImpl
