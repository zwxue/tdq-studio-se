/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.talend.dataquality.analysis.provider.DataqualityEditPlugin;

import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternMatchingIndicator;

/**
 * This is the item provider adapter for a {@link org.talend.dataquality.indicators.PatternMatchingIndicator} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class PatternMatchingIndicatorItemProvider
    extends IndicatorItemProvider
    implements	
        IEditingDomainItemProvider,	
        IStructuredItemContentProvider,	
        ITreeItemContentProvider,	
        IItemLabelProvider,	
        IItemPropertySource {
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PatternMatchingIndicatorItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addMatchingValueCountPropertyDescriptor(object);
            addNotMatchingValueCountPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Matching Value Count feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addMatchingValueCountPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_PatternMatchingIndicator_matchingValueCount_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_PatternMatchingIndicator_matchingValueCount_feature", "_UI_PatternMatchingIndicator_type"),
                 IndicatorsPackage.Literals.PATTERN_MATCHING_INDICATOR__MATCHING_VALUE_COUNT,
                 true,
                 false,
                 false,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Not Matching Value Count feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addNotMatchingValueCountPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_PatternMatchingIndicator_notMatchingValueCount_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_PatternMatchingIndicator_notMatchingValueCount_feature", "_UI_PatternMatchingIndicator_type"),
                 IndicatorsPackage.Literals.PATTERN_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT,
                 true,
                 false,
                 false,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This returns PatternMatchingIndicator.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return overlayImage(object, getResourceLocator().getImage("full/obj16/PatternMatchingIndicator"));
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText(Object object) {
        String label = ((PatternMatchingIndicator)object).getName();
        return label == null || label.length() == 0 ?
            getString("_UI_PatternMatchingIndicator_type") :
            getString("_UI_PatternMatchingIndicator_type") + " " + label;
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void notifyChanged(Notification notification) {
        updateChildren(notification);

        switch (notification.getFeatureID(PatternMatchingIndicator.class)) {
            case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__MATCHING_VALUE_COUNT:
            case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
     * that can be created under this object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);
    }

}
