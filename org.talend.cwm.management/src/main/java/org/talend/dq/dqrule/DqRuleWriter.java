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
package org.talend.dq.dqrule;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class DqRuleWriter {

    static Logger log = Logger.getLogger(DqRuleWriter.class);

    private static DqRuleWriter instance;

    private DqRuleWriter() {
    }

    public static DqRuleWriter getInstance() {
        if (instance == null) {
            instance = new DqRuleWriter();
        }

        return instance;
    }

    public TypedReturnCode<IFile> createDqRuleFile(WhereRule rule, IFolder folder) {
        assert rule != null;

        TypedReturnCode<IFile> trc = new TypedReturnCode<IFile>();

        String fname = DqRepositoryViewService.createFilename(rule.getName(), FactoriesUtil.DQRULE);
        IFile file = folder.getFile(fname);

        if (file.exists()) {
            log.error("Failed to create dq rule file from " + rule.getName());
            trc.setOk(false);
        } else {
            EMFSharedResources.getInstance().addEObjectToResourceSet(file.getFullPath().toString(), rule);
            EMFSharedResources.getInstance().saveLastResource();
            trc.setObject(file);
            trc.setOk(true);
        }

        return trc;
    }
}
