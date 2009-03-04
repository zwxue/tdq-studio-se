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
package org.talend.dq.helper.resourcehelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dataquality.rules.util.RulesSwitch;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class DQRuleResourceFileHelper extends ResourceFileMap {

    private static Logger log = Logger.getLogger(DQRuleResourceFileHelper.class);

    private static DQRuleResourceFileHelper instance;

    private Map<IFile, WhereRule> whereRulesMap = new HashMap<IFile, WhereRule>();

    private DQRuleResourceFileHelper() {
        super();
    }

    public static DQRuleResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new DQRuleResourceFileHelper();
        }
        return instance;
    }

    /**
     * 
     * DOC xqliu Comment method "findWhereRule".
     * 
     * @param file
     * @return
     */
    public WhereRule findWhereRule(IFile file) {
        WhereRule whereRule = whereRulesMap.get(file);
        if (whereRule != null) {
            return whereRule;
        }
        Resource fileResource = getFileResource(file);
        whereRule = retireDQRule(fileResource);
        if (whereRule != null) {
            whereRulesMap.put(file, whereRule);
        }
        return whereRule;
    }

    /**
     * DOC xqliu Comment method "retireDQRule".
     * 
     * @param fileResource
     * @return
     */
    private WhereRule retireDQRule(Resource fileResource) {
        EList<EObject> contents = fileResource.getContents();
        if (contents.isEmpty()) {
            log.error("No content in " + fileResource);
        }
        if (log.isDebugEnabled()) {
            log.debug("Nb elements in contents " + contents.size());
        }
        RulesSwitch<WhereRule> mySwitch = new RulesSwitch<WhereRule>() {

            @Override
            public WhereRule caseWhereRule(WhereRule object) {
                return object;
            }

        };
        WhereRule whereRule = null;
        if (contents != null && contents.size() != 0) {
            whereRule = mySwitch.doSwitch(contents.get(0));
        }
        return whereRule;
    }

    public Collection<WhereRule> getAllDQRules(IFolder patternFodler) {
        return Collections.EMPTY_LIST;
    }
}
