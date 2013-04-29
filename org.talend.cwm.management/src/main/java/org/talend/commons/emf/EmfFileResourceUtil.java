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
package org.talend.commons.emf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC sizhaoliu class global comment. Detailled comment
 */
public class EmfFileResourceUtil {

    private ResourceSet resourceSet;

    private static Logger log = Logger.getLogger("EmfFileResourceUtil");

    private static EmfFileResourceUtil instance;

    private final DefinitionSwitch<IndicatorDefinition> indicatorSwitch;// = initIndicDef();

    public static EmfFileResourceUtil getInstance() {
        if (instance == null) {
            instance = new EmfFileResourceUtil();
        }
        return instance;
    }

    private EmfFileResourceUtil() {
        resourceSet = new ResourceSetImpl();

        // Initialize the enterprise factories
        FactoriesUtil.initializeAllFactories();

        // Initialize the enterprise packages
        FactoriesUtil.initializeAllPackages();

        // Register the XMI resource factory for all the extensions
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;

        final Collection<String> fileExtensions = FactoriesUtil.getExtensions();
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        for (String extension : fileExtensions) {
            m.put(extension, new CwmResourceFactory());
        }

        indicatorSwitch = initIndicDef();
    }

    private DefinitionSwitch<IndicatorDefinition> initIndicDef() {
        return new DefinitionSwitch<IndicatorDefinition>() {

            @Override
            public IndicatorDefinition caseIndicatorDefinition(IndicatorDefinition object) {
                return object;
            }

        };
    }

    public Resource getFileResource(String string) {
        URI uri = URI.createFileURI(new File(string).getAbsolutePath());
        Resource res = resourceSet.getResource(uri, true);
        return res;
    }

    public List<? extends ModelElement> getAllElement(String parentFolder) {
        List<ModelElement> elementList = new ArrayList<ModelElement>();
        try {
            List<File> allFiles = searchAllFiles(new File(parentFolder));
            // MOD qiongli 2011-4-19.bug 20566,avoid NPE
            ModelElement mod = null;
            for (File file : allFiles) {
                mod = getModelElement(file);
                if (mod != null) {
                    // MOD msjian TDQ-4672 2012-2-17: modify another issue
                    elementList.add(mod);
                    // TDQ-4672~
                }
            }
        } catch (Exception e) {
            log.error(e);
        }

        return elementList;
    }

    public final ModelElement getModelElement(File file) {
        if (file != null) {
            Resource resource = getFileResource(file.getAbsolutePath());

            EList<EObject> contents = resource.getContents();
            if (contents.isEmpty()) {
                log.error("no content in: " + resource);//$NON-NLS-1$
            }

            for (EObject object : contents) {
                ModelElement switchObject = indicatorSwitch.doSwitch(object);

                if (switchObject != null) {
                    return switchObject;
                }
            }
        }

        return null;
    }

    private List<File> searchAllFiles(File file) {
        List<File> fileList = new ArrayList<File>();

        if (file.exists()) {
            for (File child : file.listFiles()) {
                if (child.isDirectory()) {
                    fileList.addAll(searchAllFiles(child));
                    continue;
                }

                if (checkFile(child)) {
                    fileList.add(child);
                }
            }
        }

        return fileList;
    }

    protected boolean checkFile(File file) {
        String path = file.getAbsolutePath();
        String extension = path.substring(path.lastIndexOf(".") + 1);
        return file != null && FactoriesUtil.DEFINITION.equalsIgnoreCase(extension);
    }
}
