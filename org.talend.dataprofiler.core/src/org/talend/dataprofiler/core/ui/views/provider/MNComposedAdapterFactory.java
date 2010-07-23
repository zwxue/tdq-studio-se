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
package org.talend.dataprofiler.core.ui.views.provider;

import java.util.ArrayList;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.talend.dataquality.analysis.provider.AnalysisItemProviderAdapterFactory;
import orgomg.cwm.resource.relational.provider.RelationalItemProviderAdapterFactory;

/**
 * @author rli
 * 
 */
public class MNComposedAdapterFactory {
	private static ComposedAdapterFactory mnCompAdapterFactory;

	public static final ComposedAdapterFactory getAdapterFactory() {
		if (mnCompAdapterFactory == null) {
			mnCompAdapterFactory = new ComposedAdapterFactory(
					createFactoryList());
		}
		return mnCompAdapterFactory;
	}

	public static final ArrayList<AdapterFactory> createFactoryList() {
		ArrayList<AdapterFactory> factories = new ArrayList<AdapterFactory>();
		factories.add(new ResourceItemProviderAdapterFactory());
		factories.add(new RelationalItemProviderAdapterFactory());
        factories.add(new AnalysisItemProviderAdapterFactory());
		factories.add(new ReflectiveItemProviderAdapterFactory());
		return factories;
	}
}
