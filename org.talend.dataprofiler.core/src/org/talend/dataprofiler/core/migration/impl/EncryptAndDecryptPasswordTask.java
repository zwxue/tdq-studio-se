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
package org.talend.dataprofiler.core.migration.impl;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.ResourceManager;
import org.talend.dataquality.exception.ExceptionHandler;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.security.CryptoHelper;
import orgomg.cwm.foundation.softwaredeployment.ProviderConnection;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC hcheng class global comment. Delete the password stored in Tagged values
 * of the dataprovider. Encrypt the password stored in the Tagged values of the
 * providerConnection.
 * 
 */
public class EncryptAndDecryptPasswordTask extends AbstractMigrationTask {

	public boolean execute() {
		IFolder folder = ResourceManager.getMetadataFolder().getFolder(
				PluginConstant.DB_CONNECTIONS);
		try {
			IResource[] resource = folder.members();
			if (resource.length > 0) {
				for (IResource re : resource) {
					if (re instanceof IFile) {
						IFile file = (IFile) re;
						TdDataProvider tdDataProvider = PrvResourceFileHelper
								.getInstance().findProvider(file).getObject();
						encryptDataProvider(tdDataProvider);
					}

				}
			}
		} catch (CoreException e) {
			ExceptionHandler.process(e);
			return false;
		}

		return true;
	}

	/**
	 * DOC hcheng Comment method "encryptDataProvider".
	 * 
	 * @param dataProvider
	 * @return
	 */
	public static TdDataProvider encryptDataProvider(TdDataProvider dataProvider) {
		// Delete DataProvider password tag
		EList<TaggedValue> dpList = dataProvider.getTaggedValue();
		if (dpList != null && dpList.size() > 0) {
			int dpSize = dpList.size();
			for (int j = 0; j < dpSize; ++j) {
				Object dpObj = dpList.get(j);
				if (dpObj != null) {
					TaggedValue dp = (TaggedValue) dpObj;
					if (org.talend.dq.PluginConstant.PASSWORD_PROPERTY
							.equals(dp.getTag())) {
						dpList.remove(dp);
						j--;
						dpSize--;
					}
				}
			}
		}

		// Connection password encrypt
		EList<ProviderConnection> elist = dataProvider.getResourceConnection();

		for (int i = 0; i < elist.size(); ++i) {
			Object obj = elist.get(i);
			if (obj != null) {
				TdProviderConnection tdpConn = (TdProviderConnection) obj;
				EList<TaggedValue> tvList = tdpConn.getTaggedValue();
				if (tvList != null && tvList.size() > 0) {
					int tvSize = tvList.size();
					for (int j = 0; j < tvSize; ++j) {
						Object tvObj = tvList.get(j);
						if (tvObj != null) {
							TaggedValue tv = (TaggedValue) tvObj;
							if (org.talend.dq.PluginConstant.PASSWORD_PROPERTY
									.equals(tv.getTag())) {
								tv.setValue(new CryptoHelper(
										DataProviderHelper.PASSPHRASE)
										.encrypt(tv.getValue()));
							}
						}
					}
				}
			}
		}

		PrvResourceFileHelper.getInstance().save(dataProvider);
		return dataProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
	 */
	public Date getOrder() {
		Calendar calender = Calendar.getInstance();
		calender.set(2009, 3, 3);
		return calender.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#
	 * getMigrationTaskType()
	 */
	public MigrationTaskType getMigrationTaskType() {
		return MigrationTaskType.FILE;
	}
}
