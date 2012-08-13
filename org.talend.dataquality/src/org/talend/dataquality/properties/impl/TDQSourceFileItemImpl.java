/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.properties.impl;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.helper.ByteArrayResource;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.utils.URIHelper;
import org.talend.dataquality.properties.TDQSourceFileItem;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>TDQ Source File Item</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class TDQSourceFileItemImpl extends TDQFileItemImpl implements TDQSourceFileItem {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected TDQSourceFileItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return org.talend.dataquality.properties.PropertiesPackage.Literals.TDQ_SOURCE_FILE_ITEM;
    }

    @Override
    public String getFileExtension() {
        return FileConstants.SQL_EXTENSION;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public ByteArray getContent() {
        if (content != null) {
            if (content.eIsProxy()) {
                return super.getContent();
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

        if ("platform".equals(proxyUri.scheme()) && proxyUri.segmentCount() > 1 && "resource".equals(proxyUri.segment(0))) { //$NON-NLS-1$ //$NON-NLS-2$
            for (Object element : resourceSet.getResources()) {
                Resource resource = (Resource) element;
                if (theURIConverter.normalize(resource.getURI()).equals(normalizedURI)) {
                    byteArrayResource = (ByteArrayResource) resource;
                }
            }

            if (byteArrayResource == null) {
                byteArrayResource = new ByteArrayResource(resourceUri);
                resourceSet.getResources().add(byteArrayResource);
            }

            try {
                byteArrayResource.load(null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            for (Object element : resourceSet.getResources()) {
                Resource resource = (Resource) element;
                if (theURIConverter.normalize(resource.getURI()).equals(normalizedURI)) {
                    byteArrayResource = (ByteArrayResource) resource;
                }
            }
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

} // TDQSourceFileItemImpl
