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
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.pattern.PatternLanguageType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.pattern.util.PatternSwitch;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.PatternWriter;
import org.talend.utils.sugars.ReturnCode;

/**
 * 
 * DOC zshen class global comment. Detailled comment
 */
public class UpdateSomePatternToMatchMysql extends AbstractWorksapceUpdateTask {

    protected static Logger log = Logger.getLogger(UpdateSomePatternToMatchMysql.class);

    private static final String UPDATE_FILE_NAME = "TDQ_Libraries/Patterns/Regex/phone/International_phone_number.pattern";

    private static final String EXPRESSIONBODY = "'^\\\\+[0-9]{1,3}\\.[0-9]+\\.[0-9]+$'";

    private File updateFile = null;

    public UpdateSomePatternToMatchMysql() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean doExecute() throws Exception {
        if (updateFile == null) {
            updateFile = getWorkspacePath().append(UPDATE_FILE_NAME).toFile();
        }
        if (!updateFile.exists()) {// if International_phone_number.pattern don't exsit.
            return true;
        }
        URI uri = URI.createFileURI(updateFile.getAbsolutePath());
        Resource patternResource = EMFSharedResources.getInstance().getResource(uri, true);
        Pattern thePattern = retirePattern(patternResource);
        for (PatternComponent currentExpression : thePattern.getComponents()) {
            if (currentExpression instanceof RegularExpression) {
                if (PatternLanguageType.MYSQL.getLiteral().equals(((RegularExpression) currentExpression).getExpressionType())) {
                    return true;// if the case of Mysql has been added.
                }
            }
        }
        String language = PatternLanguageType.MYSQL.getLiteral();
        RegularExpression newRegularExpress = BooleanExpressionHelper.createRegularExpression(language, EXPRESSIONBODY);
        String expressionType = DomainHelper.getExpressionType(thePattern);
        newRegularExpress.setExpressionType(expressionType);
        List<PatternComponent> componentsList = new ArrayList<PatternComponent>();
        componentsList.add(newRegularExpress);
        componentsList.addAll(thePattern.getComponents());
        thePattern.getComponents().clear();
        thePattern.getComponents().addAll(componentsList);
        PatternWriter writer = ElementWriterFactory.getInstance().createPatternWriter();
        ReturnCode save = writer.save(thePattern);
        PatternResourceFileHelper.getInstance().clear();

        return save.isOk();

    }

    /**
     * DOC rli Comment method "retireAnalysis".
     * 
     * @param fileResource
     * @return
     */
    private Pattern retirePattern(Resource fileResource) {
        EList<EObject> contents = fileResource.getContents();
        if (contents.isEmpty()) {
            log.error("No content in " + fileResource);
        }
        if (log.isDebugEnabled()) {
            log.debug("Nb elements in contents " + contents.size());
        }
        PatternSwitch<Pattern> mySwitch = new PatternSwitch<Pattern>() {

            public Pattern casePattern(Pattern object) {
                return object;
            }
        };
        Pattern pattern = null;
        if (contents != null && contents.size() != 0) {
            pattern = mySwitch.doSwitch(contents.get(0));
        }
        return pattern;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        return createDate(2010, 10, 9);
    }

}
