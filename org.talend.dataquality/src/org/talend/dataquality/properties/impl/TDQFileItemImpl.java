/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.properties.impl;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.helper.ByteArrayResource;
import org.talend.core.model.properties.impl.TDQItemImpl;
import org.talend.core.repository.utils.URIHelper;
import org.talend.dataquality.properties.PropertiesPackage;
import org.talend.dataquality.properties.TDQFileItem;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>TDQ File Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.properties.impl.TDQFileItemImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TDQFileItemImpl#getExtension <em>Extension</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TDQFileItemImpl#getContent <em>Content</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TDQFileItemImpl extends TDQItemImpl implements TDQFileItem {

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getExtension() <em>Extension</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getExtension()
     * @generated
     * @ordered
     */
    protected static final String EXTENSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getExtension() <em>Extension</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getExtension()
     * @generated
     * @ordered
     */
    protected String extension = EXTENSION_EDEFAULT;

    /**
     * The cached value of the '{@link #getContent() <em>Content</em>}' reference.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getContent()
     * @generated
     * @ordered
     */
    protected ByteArray content;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected TDQFileItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return PropertiesPackage.Literals.TDQ_FILE_ITEM;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc --> normalize the name. <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void setName(String newName) {
        String oldName = name;
        name = WorkspaceUtils.normalize(newName);
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_FILE_ITEM__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getExtension() {
        return extension;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setExtension(String newExtension) {
        String oldExtension = extension;
        extension = newExtension;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_FILE_ITEM__EXTENSION, oldExtension, extension));
    }

    private ByteArray getContentOld() {
        if (content != null && content.eIsProxy()) {
            InternalEObject oldContent = (InternalEObject) content;
            content = (ByteArray) eResolveProxy(oldContent);
            if (content != oldContent) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, PropertiesPackage.TDQ_FILE_ITEM__CONTENT,
                            oldContent, content));
            }
        }
        return content;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public ByteArray getContent() {
        if (content != null) {
            if (content.eIsProxy()) {
                return getContentOld();
            } else {// load the content from the file
                loadContentFromFile();
            }
        }
        return content;
    }

    @Override
    public EObject eResolveProxy(InternalEObject proxy) {
        if (!proxy.eClass().equals(org.talend.core.model.properties.PropertiesPackage.eINSTANCE.getByteArray())) {
            return super.eResolveProxy(proxy);
        }

        URI proxyUri = proxy.eProxyURI();
        URI resourceUri = proxyUri.trimFragment();
        // bug 0020095
        if (eResource() == null) {
            return super.eResolveProxy(proxy);
        }
        ResourceSet resourceSet = eResource().getResourceSet();
        ByteArrayResource byteArrayResource = null;

        URIConverter theURIConverter = resourceSet.getURIConverter();
        URI normalizedURI = theURIConverter.normalize(resourceUri);

        for (Object element : resourceSet.getResources()) {
            Resource resource = (Resource) element;
            if (theURIConverter.normalize(resource.getURI()).equals(normalizedURI)) {
                // MOD TDQ-7211 yyin 20130508: remove the current bytearray resource to avoid load the old one and
                // lost the changes of the file
                resourceSet.getResources().remove(resource);
                break;
            }
        }

        // MOD TDQ-7211 yyin 20130508: create the bytearray resource from the file to make sure using the new
        // changes of the file.
        byteArrayResource = new ByteArrayResource(resourceUri);
        resourceSet.getResources().add(byteArrayResource);

        try {
            byteArrayResource.load(null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (byteArrayResource != null && byteArrayResource.isLoaded()) {
            EObject object = byteArrayResource.getEObject(proxyUri.fragment().toString());
            if (object != null) {
                return object;
            }
        }

        throw new UnsupportedOperationException();
    }

    private void loadContentFromFile() {
        if (content.eResource() != null) {
            IFile file = URIHelper.getFile(content.eResource().getURI());
            try {
                content.setInnerContentFromFile(file);
            } catch (IOException e) {
                // ignore this exception because after moving the resource , the path has been changed.
            } catch (CoreException e) {

            }
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ByteArray basicGetContent() {
        return content;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setContent(ByteArray newContent) {
        ByteArray oldContent = content;
        content = newContent;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_FILE_ITEM__CONTENT, oldContent, content));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case PropertiesPackage.TDQ_FILE_ITEM__NAME:
                return getName();
            case PropertiesPackage.TDQ_FILE_ITEM__EXTENSION:
                return getExtension();
            case PropertiesPackage.TDQ_FILE_ITEM__CONTENT:
                if (resolve) return getContent();
                return basicGetContent();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case PropertiesPackage.TDQ_FILE_ITEM__NAME:
                setName((String)newValue);
                return;
            case PropertiesPackage.TDQ_FILE_ITEM__EXTENSION:
                setExtension((String)newValue);
                return;
            case PropertiesPackage.TDQ_FILE_ITEM__CONTENT:
                setContent((ByteArray)newValue);
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
            case PropertiesPackage.TDQ_FILE_ITEM__NAME:
                setName(NAME_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_FILE_ITEM__EXTENSION:
                setExtension(EXTENSION_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_FILE_ITEM__CONTENT:
                setContent((ByteArray)null);
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
            case PropertiesPackage.TDQ_FILE_ITEM__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case PropertiesPackage.TDQ_FILE_ITEM__EXTENSION:
                return EXTENSION_EDEFAULT == null ? extension != null : !EXTENSION_EDEFAULT.equals(extension);
            case PropertiesPackage.TDQ_FILE_ITEM__CONTENT:
                return content != null;
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
        result.append(" (name: ");
        result.append(name);
        result.append(", extension: ");
        result.append(extension);
        result.append(')');
        return result.toString();
    }

} // TDQFileItemImpl
