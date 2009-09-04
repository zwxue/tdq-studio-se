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
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.dataquality.rules.DQRule;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dataquality.rules.util.RulesSwitch;
import org.talend.dq.writer.impl.DQRuleWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.Expression;

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

    public IFile getWhereRuleFile(WhereRule whereRule, IFolder[] folders) {
        IFile file = null;
        if (resourcesNumberChanged) {
            try {
                for (int i = 0; i < folders.length; i++) {
                    searchAllWhereRules(folders[i]);
                }
            } catch (CoreException e) {
                log.error(e, e);
            }
            resourcesNumberChanged = false;
        }
        Set<IFile> keySet = whereRulesMap.keySet();
        for (IFile file2 : keySet) {
            WhereRule whereRule2 = whereRulesMap.get(file2);
            Expression e = whereRule.getSqlGenericExpression().get(0);
            Expression e2 = whereRule2.getSqlGenericExpression().get(0);
            String et = e.getLanguage();
            String et2 = e2.getLanguage();
            if (whereRule2.getName().equals(whereRule.getName())) {
                boolean b = et == null && et2 == null;
                b = b || (et != null && et.equals(et2));
                if (b) {
                    file = file2;
                }
            }
        }
        return file;
    }

    private void searchAllWhereRules(IFolder folder) throws CoreException {
        for (IResource resource : folder.members()) {
            if (resource.getType() == IResource.FOLDER) {
                searchAllWhereRules(folder.getFolder(resource.getName()));
                continue;
            }
            IFile file = (IFile) resource;
            findWhereRule(file);

        }
    }

    /**
     * DOC bZhou Comment method "save".
     * 
     * @param dqrule
     * @return
     */
    public ReturnCode save(DQRule dqrule) {
        DQRuleWriter writer = ElementWriterFactory.getInstance().createdRuleWriter();
        return writer.save(dqrule);
    }
}
