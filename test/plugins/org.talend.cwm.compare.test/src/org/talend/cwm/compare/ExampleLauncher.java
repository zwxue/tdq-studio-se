// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

/***********************************************************************************************************************
 * Copyright (c) 2006, 2007 Obeo. All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Obeo - initial API and implementation
 **********************************************************************************************************************/
package org.talend.cwm.compare;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.metamodel.UnmatchElement;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.talend.commons.emf.EMFUtil;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This application will try and launch an headless model comparison.
 * 
 * @author Cedric Brun <a href="mailto:cedric.brun@obeo.fr">cedric.brun@obeo.fr</a>
 */
public final class ExampleLauncher {

    protected static Logger log = Logger.getLogger(ExampleLauncher.class);

    /**
     * This class doesn't need to be instantiated.
     */
    private ExampleLauncher() {
        // prevents instantiation
    }

    /**
     * Launcher of this application.
     * 
     * @param args Arguments of the launch.
     */
    public static void main(String[] args) {
        new EMFUtil(); // MODSCA 2008-03-31 added for correct loading of XMI files.

        if (args.length == 2 && new File(args[0]).canRead() && new File(args[1]).canRead()) {
            // Creates the resourceSet where we'll load the models
            final ResourceSet resourceSet = new ResourceSetImpl();
            try {
                // Loads the two models passed as arguments
                final EObject model1 = ModelUtils.load(new File(args[0]), resourceSet);
                final EObject model2 = ModelUtils.load(new File(args[1]), resourceSet);

                // MODSCA 2008-03-31 add option for ignoring some elements
                Map<String, Object> options = new HashMap<String, Object>();
                options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
                options.put(MatchOptions.OPTION_SEARCH_WINDOW, 500);

                // Creates the match then the diff model for those two models
                // MOD scorreia old version of doMatch() method:
                // final MatchModel match = MatchService.doMatch(model1, model2, new NullProgressMonitor(), options);
                final MatchModel match = MatchService.doMatch(model1, model2, options);
                final DiffModel diff = DiffService.doDiff(match, false);

                // MODSCA2008-03-31 play around with the elements
                // EList matchedElements = match.getMatchedElements();
                // for (Object m : matchedElements) {
                // MatchElement elt = (MatchElement) m;
                //
                // }
                EList<UnmatchElement> unMatchedElements = match.getUnmatchedElements();
                for (Object object : unMatchedElements) {
                    UnmatchElement unMatched = (UnmatchElement) object;
                    ModelElement modelElt = (ModelElement) unMatched.getElement();
                    System.out.println("Unmatched elt= " + modelElt.getName());
                }
                // System.out.println("LEFT MODEL=" + match.getLeftModel());
                // EList<DiffElement> ownedElements = diff.getOwnedElements();
                // for (Object oe : ownedElements) {
                // System.out.println(oe.g);
                // }
                // if (true) {
                // return;
                // }

                // Prints the results
                try {
                    System.out.println(ModelUtils.serialize(match));
                    System.out.println(ModelUtils.serialize(diff));
                } catch (IOException e) {
                    log.error(e, e);
                }

                // Serializes the result as "result.emfdiff" in the directory this class has been called from.
                String outputFile = "out/result.emfdiff";
                System.out.println("saving emfdiff as \"" + outputFile + "\""); //$NON-NLS-1$
                final ComparisonResourceSnapshot snapshot = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
                snapshot.setDate(Calendar.getInstance().getTime());
                snapshot.setMatch(match);
                snapshot.setDiff(diff);
                ModelUtils.save(snapshot, outputFile); //$NON-NLS-1$
            } catch (IOException e) {
                // shouldn't be thrown
                log.error(e, e);
            } catch (InterruptedException e) {
                log.error(e, e);
            }
        } else {
            System.out.println("usage : Launcher <Model1> <Model2>"); //$NON-NLS-1$
        }
    }
}
