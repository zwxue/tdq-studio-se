// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.Functions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 19 juin 2015. This function works like GenerateFromList, the difference is that the parameter
 * is now a String holding the path to a file in the user’s computer.
 *
 */
public abstract class GenerateFromFile<T2> extends Function<T2> {

    private BufferedReader in= null;

    protected List<String> StringTokens = new ArrayList<>();

    protected void init() {
        try {
            in = new BufferedReader(new FileReader(parameters[0]));
            while (in.ready()) {
                StringTokens.add(in.readLine().trim());
            }
            in.close();
        } catch (IOException | NullPointerException e) {
            // We do nothing here because in is already set.
        }
    }

    @Override
    public abstract T2 generateMaskedRow(T2 t);
}
