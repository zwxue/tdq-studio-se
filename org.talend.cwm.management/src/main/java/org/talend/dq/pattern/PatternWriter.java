// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class PatternWriter {

    static Logger log = Logger.getLogger(PatternWriter.class);

    public TypedReturnCode<IFile> createPatternFile(Pattern pattern, IFolder folder) {
        assert pattern != null;

        TypedReturnCode<IFile> rc = new TypedReturnCode<IFile>();
        String fname = DqRepositoryViewService.createFilename(pattern.getName(), FactoriesUtil.PATTERN);
        IFile file = folder.getFile(fname);
        if (file.exists()) {
            rc.setReturnCode("Can't create pattern resource file!", false);
            return rc;
        }

        ReturnCode saved = save(pattern, file);

        if (saved.isOk()) {
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + file.getFullPath().toString());
            }
            rc.setObject(file);
        } else {
            rc.setReturnCode("Failed to save the pattern resouce file.", false);
        }
        return rc;
    }

    public ReturnCode save(Pattern pattern, IFile file) {
        ReturnCode rc = new ReturnCode();
        if (!checkFileExtension(file)) {
            rc.setReturnCode("Failed to save pattern, the extent file name is wrong.", false);
            return rc;
        }
        EMFSharedResources util = EMFSharedResources.getInstance();

        boolean added = util.addEObjectToResourceSet(file.getFullPath().toString(), pattern);

        if (!added) {
            rc.setReturnCode("Failed to save pattern: " + util.getLastErrorMessage(), added); //$NON-NLS-1$
            return rc;
        }

        boolean result = util.saveLastResource();
        rc.setOk(result);

        return rc;
    }

    private boolean checkFileExtension(IFile file) {
        return file.getFileExtension().equalsIgnoreCase(FactoriesUtil.PATTERN);
    }
}
