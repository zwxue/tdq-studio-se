// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.pattern;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.dataprofiler.core.ui.action.actions.ImportObject;
import org.talend.dataquality.rules.ParserRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;

/**
 * Junit test case for the class org.talend.dataprofiler.core.pattern.ImportFactory.
 */
public class ImportFactoryTest {

    /**
     * DOC xqliu Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // some code here
    }

    /**
     * DOC xqliu Comment method "tearDownAfterClass".
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // some code here
    }

    /**
     * DOC xqliu Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject();
        UnitTestBuildHelper.createRealProject("testForImportFactoryTDQ"); //$NON-NLS-1$
    }

    /**
     * DOC xqliu Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // some code here
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.pattern.ImportFactory#importParserRuleToStucture(org.talend.dataprofiler.core.ui.action.actions.ImportObject, org.eclipse.core.resources.IFolder, boolean, boolean, java.lang.String)}
     * None Empty With Double Quote.
     */
    @Test
    public void testImportParserRuleToStuctureNoneEmptyWithDoubleQuote() throws IOException {
        // the parser rule name, use it to get the imported file name
        String parserRuleName = "parserRule"; //$NON-NLS-1$
        String parserRuleFileName = parserRuleName + "_0.1.rules"; //$NON-NLS-1$

        // the expect expression's values: name and body will add double quote after import, lang will not add double
        // quote after import
        String name = "\"\"a\"\""; //$NON-NLS-1$
        String lang = "\"Enumeration\""; //$NON-NLS-1$
        String body = "\"\"'a'|'b'\"\""; //$NON-NLS-1$

        File dict = new File(""); //$NON-NLS-1$
        File importFile = new File(dict.getAbsolutePath() + File.separator + "paser_rule.csv"); //$NON-NLS-1$
        if (!importFile.exists()) {
            importFile.createNewFile();
        }
        // set the csv file's head
        String content = "\"Label\"\t\"Purpose\"\t\"Description\"\t\"Author\"\t\"Relative_Path\"\t\"Name\"\t\"Type\"\t\"Value\"\n"; //$NON-NLS-1$
        // set the csv file's content: the clumn's value is none empty with double quote
        content += "\"" + parserRuleName //$NON-NLS-1$
                + "\"\t\"\"\t\"\"\t\"trunk@talend.com\"\t\"\"\t\"\\\"a\\\"\"\t\"\\\"Enumeration\\\"\"\t\"\\\"'a'|'b'\\\"\""; //$NON-NLS-1$
        BufferedWriter output = new BufferedWriter(new FileWriter(importFile));
        output.write(content);
        output.close();

        List<File> pJarfilesMock = new ArrayList<File>();
        ImportObject importObject = ImportObject.createImportObject(importFile, pJarfilesMock);

        IFolder parserRuleFolder = ResourceManager.getOneFolder(EResourceConstant.RULES_PARSER);

        boolean skip = false;
        boolean rename = true;

        String importItemName = "importItemName"; //$NON-NLS-1$

        List<ReturnCode> importParserRuleToStucture = ImportFactory.importParserRuleToStucture(importObject, parserRuleFolder,
                skip, rename, importItemName);

        for (ReturnCode rc : importParserRuleToStucture) {
            assertTrue(rc.isOk());
        }

        // the imported parser rule's name is a
        IFile parserRuleFile = parserRuleFolder.getFile(parserRuleFileName);
        ParserRule parserRule = (ParserRule) DQRuleResourceFileHelper.getInstance().findDQRule(parserRuleFile);
        List<TdExpression> expressions = parserRule.getExpression();
        for (TdExpression expression : expressions) {
            assertTrue(name.equals(expression.getName()));
            assertTrue(lang.equals(expression.getLanguage()));
            assertTrue(body.equals(expression.getBody()));
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.pattern.ImportFactory#importParserRuleToStucture(org.talend.dataprofiler.core.ui.action.actions.ImportObject, org.eclipse.core.resources.IFolder, boolean, boolean, java.lang.String)}
     * None Empty Without Double Quote.
     */
    @Test
    public void testImportParserRuleToStuctureNoneEmptyWithoutDoubleQuote() throws IOException {
        // the parser rule name, use it to get the imported file name
        String parserRuleName = "parserRule"; //$NON-NLS-1$
        String parserRuleFileName = parserRuleName + "_0.1.rules"; //$NON-NLS-1$

        // the expect expression's values: name and body will add double quote after import, lang will not add double
        // quote after import
        String name = "\"a\""; //$NON-NLS-1$
        String lang = "Enumeration"; //$NON-NLS-1$
        String body = "\"'a'|'b'\""; //$NON-NLS-1$

        File dict = new File(""); //$NON-NLS-1$
        File importFile = new File(dict.getAbsolutePath() + File.separator + "paser_rule.csv"); //$NON-NLS-1$
        if (!importFile.exists()) {
            importFile.createNewFile();
        }
        // set the csv file's head
        String content = "\"Label\"\t\"Purpose\"\t\"Description\"\t\"Author\"\t\"Relative_Path\"\t\"Name\"\t\"Type\"\t\"Value\"\n"; //$NON-NLS-1$
        // set the csv file's content: the clumn's value is none empty without double quote
        content += "\"" + parserRuleName //$NON-NLS-1$
                + "\"\t\"\"\t\"\"\t\"trunk@talend.com\"\t\"\"\t\"a\"\t\"Enumeration\"\t\"'a'|'b'\""; //$NON-NLS-1$
        BufferedWriter output = new BufferedWriter(new FileWriter(importFile));
        output.write(content);
        output.close();

        List<File> pJarfilesMock = new ArrayList<File>();
        ImportObject importObject = ImportObject.createImportObject(importFile, pJarfilesMock);

        IFolder parserRuleFolder = ResourceManager.getOneFolder(EResourceConstant.RULES_PARSER);

        boolean skip = false;
        boolean rename = true;

        String importItemName = "importItemName"; //$NON-NLS-1$

        List<ReturnCode> importParserRuleToStucture = ImportFactory.importParserRuleToStucture(importObject, parserRuleFolder,
                skip, rename, importItemName);

        for (ReturnCode rc : importParserRuleToStucture) {
            assertTrue(rc.isOk());
        }

        // the imported parser rule's name is a
        IFile parserRuleFile = parserRuleFolder.getFile(parserRuleFileName);
        ParserRule parserRule = (ParserRule) DQRuleResourceFileHelper.getInstance().findDQRule(parserRuleFile);
        List<TdExpression> expressions = parserRule.getExpression();
        for (TdExpression expression : expressions) {
            assertTrue(name.equals(expression.getName()));
            assertTrue(lang.equals(expression.getLanguage()));
            assertTrue(body.equals(expression.getBody()));
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.pattern.ImportFactory#importParserRuleToStucture(org.talend.dataprofiler.core.ui.action.actions.ImportObject, org.eclipse.core.resources.IFolder, boolean, boolean, java.lang.String)}
     * Empty With Double Quote.
     */
    @Test
    public void testImportParserRuleToStuctureEmptyWithDoubleQuote() throws IOException {
        // the parser rule name, use it to get the imported file name
        String parserRuleName = "parserRule"; //$NON-NLS-1$
        String parserRuleFileName = parserRuleName + "_0.1.rules"; //$NON-NLS-1$

        // the expect expression's values: name and body will add double quote after import, lang will remove any double
        // quote after import(why???)
        String name = "\"\"\"\""; //$NON-NLS-1$
        String lang = ""; //$NON-NLS-1$
        String body = "\"\"\"\""; //$NON-NLS-1$

        File dict = new File(""); //$NON-NLS-1$
        File importFile = new File(dict.getAbsolutePath() + File.separator + "paser_rule.csv"); //$NON-NLS-1$
        if (!importFile.exists()) {
            importFile.createNewFile();
        }
        // set the csv file's head
        String content = "\"Label\"\t\"Purpose\"\t\"Description\"\t\"Author\"\t\"Relative_Path\"\t\"Name\"\t\"Type\"\t\"Value\"\n"; //$NON-NLS-1$
        // set the csv file's content: the clumn's value is empty with double quote
        content += "\"" + parserRuleName //$NON-NLS-1$
                + "\"\t\"\"\t\"\"\t\"trunk@talend.com\"\t\"\"\t\"\\\"\\\"\"\t\"\\\"\\\"\"\t\"\\\"\\\"\""; //$NON-NLS-1$
        BufferedWriter output = new BufferedWriter(new FileWriter(importFile));
        output.write(content);
        output.close();

        List<File> pJarfilesMock = new ArrayList<File>();
        ImportObject importObject = ImportObject.createImportObject(importFile, pJarfilesMock);

        IFolder parserRuleFolder = ResourceManager.getOneFolder(EResourceConstant.RULES_PARSER);

        boolean skip = false;
        boolean rename = true;

        String importItemName = "importItemName"; //$NON-NLS-1$

        List<ReturnCode> importParserRuleToStucture = ImportFactory.importParserRuleToStucture(importObject, parserRuleFolder,
                skip, rename, importItemName);

        for (ReturnCode rc : importParserRuleToStucture) {
            assertTrue(rc.isOk());
        }

        // the imported parser rule's name is a
        IFile parserRuleFile = parserRuleFolder.getFile(parserRuleFileName);
        ParserRule parserRule = (ParserRule) DQRuleResourceFileHelper.getInstance().findDQRule(parserRuleFile);
        List<TdExpression> expressions = parserRule.getExpression();
        for (TdExpression expression : expressions) {
            assertTrue(name.equals(expression.getName()));
            assertTrue(lang.equals(expression.getLanguage()));
            assertTrue(body.equals(expression.getBody()));
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.pattern.ImportFactory#importParserRuleToStucture(org.talend.dataprofiler.core.ui.action.actions.ImportObject, org.eclipse.core.resources.IFolder, boolean, boolean, java.lang.String)}
     * Empty Without Double Quote.
     */
    @Test
    public void testImportParserRuleToStuctureEmptyWithoutDoubleQuote() throws IOException {
        // the parser rule name, use it to get the imported file name
        String parserRuleName = "parserRule"; //$NON-NLS-1$
        String parserRuleFileName = parserRuleName + "_0.1.rules"; //$NON-NLS-1$

        // the expect expression's values: name and body will add double quote after import, lang will not add double
        // quote after import
        String name = "\"\""; //$NON-NLS-1$
        String lang = ""; //$NON-NLS-1$
        String body = "\"\""; //$NON-NLS-1$

        File dict = new File(""); //$NON-NLS-1$
        File importFile = new File(dict.getAbsolutePath() + File.separator + "paser_rule.csv"); //$NON-NLS-1$
        if (!importFile.exists()) {
            importFile.createNewFile();
        }
        // set the csv file's head
        String content = "\"Label\"\t\"Purpose\"\t\"Description\"\t\"Author\"\t\"Relative_Path\"\t\"Name\"\t\"Type\"\t\"Value\"\n"; //$NON-NLS-1$
        // set the csv file's content: the clumn's value is empty without double quote
        content += "\"" + parserRuleName //$NON-NLS-1$
                + "\"\t\"\"\t\"\"\t\"trunk@talend.com\"\t\"\"\t\"\"\t\"\"\t\"\""; //$NON-NLS-1$
        BufferedWriter output = new BufferedWriter(new FileWriter(importFile));
        output.write(content);
        output.close();

        List<File> pJarfilesMock = new ArrayList<File>();
        ImportObject importObject = ImportObject.createImportObject(importFile, pJarfilesMock);

        IFolder parserRuleFolder = ResourceManager.getOneFolder(EResourceConstant.RULES_PARSER);

        boolean skip = false;
        boolean rename = true;

        String importItemName = "importItemName"; //$NON-NLS-1$

        List<ReturnCode> importParserRuleToStucture = ImportFactory.importParserRuleToStucture(importObject, parserRuleFolder,
                skip, rename, importItemName);

        for (ReturnCode rc : importParserRuleToStucture) {
            assertTrue(rc.isOk());
        }

        // the imported parser rule's name is a
        IFile parserRuleFile = parserRuleFolder.getFile(parserRuleFileName);
        ParserRule parserRule = (ParserRule) DQRuleResourceFileHelper.getInstance().findDQRule(parserRuleFile);
        List<TdExpression> expressions = parserRule.getExpression();
        for (TdExpression expression : expressions) {
            assertTrue(name.equals(expression.getName()));
            assertTrue(lang.equals(expression.getLanguage()));
            assertTrue(body.equals(expression.getBody()));
        }
    }
}
