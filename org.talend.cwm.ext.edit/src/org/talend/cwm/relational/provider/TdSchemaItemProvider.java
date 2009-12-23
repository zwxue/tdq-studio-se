/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.cwm.relational.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdSchema;

import org.talend.cwm.softwaredeployment.SoftwaredeploymentFactory;
import org.talend.cwm.xml.XmlFactory;
import orgomg.cwm.objectmodel.core.CorePackage;
import orgomg.cwm.resource.relational.provider.SchemaItemProvider;

/**
 * This is the item provider adapter for a {@link org.talend.cwm.relational.TdSchema} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class TdSchemaItemProvider
    extends SchemaItemProvider
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
    public TdSchemaItemProvider(AdapterFactory adapterFactory) {
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

        }
        return itemPropertyDescriptors;
    }

    /**
     * This returns TdSchema.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return overlayImage(object, getResourceLocator().getImage("full/obj16/TdSchema"));
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText(Object object) {
        String label = ((TdSchema)object).getName();
        return label == null || label.length() == 0 ?
            getString("_UI_TdSchema_type") :
            getString("_UI_TdSchema_type") + " " + label;
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

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 RelationalFactory.eINSTANCE.createTdTable()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 RelationalFactory.eINSTANCE.createTdView()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 RelationalFactory.eINSTANCE.createTdCatalog()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 RelationalFactory.eINSTANCE.createTdSchema()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 RelationalFactory.eINSTANCE.createTdColumn()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 RelationalFactory.eINSTANCE.createTdSqlDataType()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 RelationalFactory.eINSTANCE.createTdTrigger()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 RelationalFactory.eINSTANCE.createTdProcedure()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 SoftwaredeploymentFactory.eINSTANCE.createTdProviderConnection()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 SoftwaredeploymentFactory.eINSTANCE.createTdDataManager()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 SoftwaredeploymentFactory.eINSTANCE.createTdDataProvider()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 SoftwaredeploymentFactory.eINSTANCE.createTdMachine()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 XmlFactory.eINSTANCE.createTdXMLElement()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 XmlFactory.eINSTANCE.createTdXMLContent()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.Literals.NAMESPACE__OWNED_ELEMENT,
                 XmlFactory.eINSTANCE.createTdXMLDocument()));
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return OrgtalendcwmEditPlugin.INSTANCE;
    }

}
