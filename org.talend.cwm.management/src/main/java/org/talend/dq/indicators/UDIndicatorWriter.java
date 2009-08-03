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
package org.talend.dq.indicators;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UDIndicatorWriter {

    static Logger log = Logger.getLogger(UDIndicatorWriter.class);

    private static UDIndicatorWriter instance;

    private UDIndicatorWriter() {
    }

    public static UDIndicatorWriter getInstance() {
        if (instance == null) {
            instance = new UDIndicatorWriter();
        }

        return instance;
    }

    public TypedReturnCode<IFile> createUDIndicatorFile(IndicatorDefinition indicatorDefinition, IFolder folder) {
        assert indicatorDefinition != null;

        TypedReturnCode<IFile> trc = new TypedReturnCode<IFile>();

        String fname = DqRepositoryViewService.createFilename(indicatorDefinition.getName(), FactoriesUtil.UDI);
        IFile file = folder.getFile(fname);

        if (file.exists()) {
            log.error("Failed to create user defined indicator file from " + indicatorDefinition.getName());
            trc.setOk(false);
        } else {
            EMFSharedResources.getInstance().addEObjectToResourceSet(file.getFullPath().toString(), indicatorDefinition);
            EMFSharedResources.getInstance().saveLastResource();
            trc.setObject(file);
            trc.setOk(true);
        }

        return trc;
    }
}
