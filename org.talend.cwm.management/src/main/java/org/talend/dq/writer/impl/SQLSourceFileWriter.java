// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.writer.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.dataquality.properties.TDQSourceFileItem;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * For save the xx.sql file, because it is not an EMF model DOC yyin added 20120614 TDQ-5468
 */
public class SQLSourceFileWriter extends AElementPersistance {

    XmiResourceManager xmiResourceManager;

    SQLSourceFileWriter() {
        super();
        xmiResourceManager = new XmiResourceManager();
    }

    /* (non-Javadoc)
     * @see org.talend.dq.writer.AElementPersistance#notifyResourceChanges()
     */
    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save();
    }

    /* (non-Javadoc)
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.talend.dq.writer.AElementPersistance#save(org.talend.core.model.properties.Item, boolean)
     */
    @Override
    public ReturnCode save(Item item, boolean... careDependency) {
        ReturnCode rc = new ReturnCode();
        if (!(item instanceof TDQSourceFileItem)) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        TDQSourceFileItem sqlItem = (TDQSourceFileItem) item;
        ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
        InputStream stream = null;

        try {
            File file = new File(this.getItemFullPath(sqlItem));
            // stream = file.toURL().openStream();
            // byte[] innerContent = new byte[stream.available()];
            // stream.read(innerContent);

            byteArray.setInnerContentFromFile(file);
        } catch (IOException e) {
            rc.setOk(Boolean.FALSE);
            ExceptionHandler.process(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    rc.setOk(Boolean.FALSE);
                    rc.setMessage(e.getMessage());
                }
            }
        }
        String routineContent = new String(byteArray.getInnerContent());
        byteArray.setInnerContent(routineContent.getBytes());
        // now the item has its content(with empty inner content), should not replace,just set its inner content.
        sqlItem.getContent().setInnerContent(routineContent.getBytes());

        return rc;
    }

    private String getItemFullPath(TDQSourceFileItem item) {
        String statePathStr = null;
        if (item.getState() != null) {
            statePathStr = item.getState().getPath();
            if (!statePathStr.equals("")) {
                statePathStr = "/" + statePathStr;
            }
        }
        String fileName = item.getName() + "_" + item.getProperty().getVersion() + "." + item.getExtension();//$NON-NLS-1$
        IPath typedPath = ResourceManager.getSourceFileFolder().getLocation();
        String fullpath = typedPath.toOSString() + statePathStr + "/" + fileName;//$NON-NLS-1$
        return fullpath;
    }

    public void move(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {



    }

    /**
     * DOC yyin Comment method "saveBeforeMove".
     * 
     * @param newPath
     * 
     * @param sourceNode
     * @param iRepositoryViewObject
     * @throws PersistenceException
     */
    private void saveBeforeMove(IRepositoryViewObject obj, IRepositoryViewObject target, IPath newPath)
            throws PersistenceException {
        Item currentItem = obj.getProperty().getItem();
        if (currentItem.getParent() instanceof FolderItem) {
            ((FolderItem) currentItem.getParent()).getChildren().remove(currentItem);
        }
        FolderItem newFolderItem = (FolderItem) target.getProperty().getItem();
        newFolderItem.getChildren().add(currentItem);
        currentItem.setParent(newFolderItem);

        ItemState state = obj.getProperty().getItem().getState();
        state.setPath(newPath.toString());
        xmiResourceManager.saveResource(state.eResource());

    }

}
