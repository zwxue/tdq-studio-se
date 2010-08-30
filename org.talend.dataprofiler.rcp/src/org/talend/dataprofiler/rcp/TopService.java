// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.rcp;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.SystemException;
import org.talend.core.ICoreService;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.ColumnNameChanged;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.repository.model.IRepositoryNode;

/**
 * 
 * DOC zshen class global comment. Detailled comment
 */
public class TopService implements ICoreService {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#addWorkspaceTaskDone(java.lang.String)
     */
    public void addWorkspaceTaskDone(String task) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#checkJob(java.lang.String)
     */
    public boolean checkJob(String name) throws BusinessException {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#componentsReset()
     */
    public void componentsReset() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#createStatsLogAndImplicitParamter(org.talend.core.model.general.Project)
     */
    public void createStatsLogAndImplicitParamter(Project project) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#deleteAllJobs(boolean)
     */
    public void deleteAllJobs(boolean fromPluginModel) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#deleteRoutinefile(org.talend.core.model.repository.IRepositoryViewObject)
     */
    public void deleteRoutinefile(IRepositoryViewObject objToDelete) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#filterSpecialChar(java.lang.String)
     */
    public String filterSpecialChar(String input) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getColumnNameChanged(org.talend.core.model.metadata.IMetadataTable,
     * org.talend.core.model.metadata.IMetadataTable)
     */
    public List<ColumnNameChanged> getColumnNameChanged(IMetadataTable oldTable, IMetadataTable newTable) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getContextFileNameForPerl(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    public String getContextFileNameForPerl(String projectName, String jobName, String version, String context) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getContextFlagFromQueryUtils()
     */
    public boolean getContextFlagFromQueryUtils() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getImageWithDocExt(java.lang.String)
     */
    public Image getImageWithDocExt(String extension) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getImageWithSpecial(org.eclipse.swt.graphics.Image)
     */
    public ImageDescriptor getImageWithSpecial(Image source) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getJavaJobFolderName(java.lang.String, java.lang.String)
     */
    public String getJavaJobFolderName(String jobName, String version) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getJavaProjectFolderName(org.talend.core.model.properties.Item)
     */
    public String getJavaProjectFolderName(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getLanTypeString()
     */
    public String getLanTypeString() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getLastUser()
     */
    public String getLastUser() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getNewMetadataColumns(org.talend.core.model.metadata.IMetadataTable,
     * org.talend.core.model.metadata.IMetadataTable)
     */
    public List<ColumnNameChanged> getNewMetadataColumns(IMetadataTable oldTable, IMetadataTable newTable) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.ICoreService#getParameterUNIQUENAME(org.talend.designer.core.model.utils.emf.talendfile.NodeType)
     */
    public String getParameterUNIQUENAME(NodeType node) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getRemoveMetadataColumns(org.talend.core.model.metadata.IMetadataTable,
     * org.talend.core.model.metadata.IMetadataTable)
     */
    public List<ColumnNameChanged> getRemoveMetadataColumns(IMetadataTable oldTable, IMetadataTable newTable) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getRoot()
     */
    public IRepositoryNode getRoot() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getRootProjectNameForPerl(org.talend.core.model.properties.Item)
     */
    public String getRootProjectNameForPerl(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getRoutineAndJars()
     */
    public Map<String, List<URI>> getRoutineAndJars() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getSpecificResourceInJavaProject(org.eclipse.core.runtime.IPath)
     */
    public IResource getSpecificResourceInJavaProject(IPath path) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getSpecificResourceInPerlProject(org.eclipse.core.runtime.IPath)
     */
    public IResource getSpecificResourceInPerlProject(IPath path) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getTemplateString()
     */
    public String getTemplateString() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#initializeComponents(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void initializeComponents(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#initializeForTalendStartupJob()
     */
    public void initializeForTalendStartupJob() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#initializeTemplates()
     */
    public Job initializeTemplates() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#isAlreadyBuilt(org.talend.core.model.general.Project)
     */
    public boolean isAlreadyBuilt(Project project) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#isContainContextParam(java.lang.String)
     */
    public boolean isContainContextParam(String code) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#isKeyword(java.lang.String)
     */
    public boolean isKeyword(String word) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#readWorkspaceTasksDone()
     */
    public List<String> readWorkspaceTasksDone() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#removeItemRelations(org.talend.core.model.properties.Item)
     */
    public void removeItemRelations(Item item) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#removeJobLaunch(org.talend.core.model.repository.IRepositoryViewObject)
     */
    public void removeJobLaunch(IRepositoryViewObject objToDelete) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#setFlagForQueryUtils(boolean)
     */
    public void setFlagForQueryUtils(boolean flag) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#syncAllRoutines()
     */
    public void syncAllRoutines() throws SystemException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#syncAllRules()
     */
    public void syncAllRules() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#syncLibraries(org.eclipse.core.runtime.IProgressMonitor[])
     */
    public void syncLibraries(IProgressMonitor... monitorWrap) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#validateValueForDBType(java.lang.String)
     */
    public String validateValueForDBType(String columnName) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#synchronizeMapptingXML()
     */
    public void synchronizeMapptingXML() {
        // I do not know what the method is supposed to be doing to I do nothing.
    }

}
