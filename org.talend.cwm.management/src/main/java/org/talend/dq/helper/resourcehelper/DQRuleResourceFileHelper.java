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
package org.talend.dq.helper.resourcehelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.ecore.EObject;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dataquality.rules.util.RulesSwitch;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class DQRuleResourceFileHelper extends ResourceFileMap {

    private static DQRuleResourceFileHelper instance;

    RulesSwitch<WhereRule> rulesSwitch = new RulesSwitch<WhereRule>() {

        @Override
        public WhereRule caseWhereRule(WhereRule object) {
            return object;
        }

    };

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
        if (checkFile(file)) {
            return (WhereRule) getModelElement(file);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#checkFile(org.eclipse.core.resources.IFile)
     */
    @Override
    protected boolean checkFile(IFile file) {
        return file != null && FactoriesUtil.DQRULE.equalsIgnoreCase(file.getFileExtension());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#doSwitch(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public ModelElement doSwitch(EObject object) {
        return rulesSwitch.doSwitch(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#getTypedFolder()
     */
    @Override
    public IFolder getTypedFolder() {
        return ResourceManager.getRulesFolder();
    }

    /**
     * Getter for rulesSwitch.
     * 
     * @return the rulesSwitch
     */
    public RulesSwitch<WhereRule> getRulesSwitch() {
        return this.rulesSwitch;
    }
}
