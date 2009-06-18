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
package org.talend.cwm.dependencies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.reports.TdReport;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author scorreia
 * 
 *         A singleton class to handle dependencies between objects.
 * 
 *         PTODO scorreia clean code of this class.
 */
public final class DependenciesHandler {

	/**
	 * As specified in CWM document at p. 67, the dependency kind can be of two
	 * types "Usage" or "Abstraction", but can also be of other types.
	 */
	public static final String USAGE = "Usage"; //$NON-NLS-1$

	private static DependenciesHandler instance;

	private DependenciesHandler() {
		// this.dependencyResource = loadFromFile(PATH_NAME);
	}

	public static DependenciesHandler getInstance() {
		if (instance == null) {
			instance = new DependenciesHandler();
		}
		return instance;
	}

	/**
	 * Method "clearDependencies" is to be used before a file is deleted. The
	 * root element is given as argument and the dependencies on this element
	 * are removed in all resources that depend on this element.
	 * 
	 * @param elementToDelete
	 *            a root element which file will be deleted.
	 * @return the list of modified dependencies
	 */
	public List<Resource> clearDependencies(ModelElement elementToDelete) {
		EList<Dependency> clientDependencies;
		// get the client dependencies (
		clientDependencies = elementToDelete.getClientDependency();
		// locate resource of each Dependency object
		List<Resource> modifiedResources = new ArrayList<Resource>();
		for (Dependency dependency : clientDependencies) {
			Resource dependencyResource = dependency.eResource();
			if (dependencyResource != null) {
				modifiedResources.add(dependencyResource);
			}
		}
		// this clears also the reverse dependencies: remove the elementToDelete
		// from
		// Dependency.getClient()
		clientDependencies.clear();

		// now get the objects that depend on the elementToDelete
		EList<Dependency> supplierDependencies = elementToDelete
				.getSupplierDependency();
		for (Dependency dependency : supplierDependencies) {
			EList<ModelElement> client = dependency.getClient();
			// get the resource of each client
			for (ModelElement modelElement : client) {
				Resource clientResource = modelElement.eResource();
				if (clientResource != null) {
					modifiedResources.add(clientResource);
				}
			}
			// clear the dependencies of all clients
			// this clear the corresponding getClientDependency() of each client
			// (objects that requires the
			// elementToDelete)
			client.clear();
		}
		return modifiedResources;
	}

	/**
	 * Method "removeDependenciesBetweenModels" is to be used before a model
	 * dependency(elementToRemove) removed from the elementFromRemove. The
	 * elementToRemove not truly to deleted, not a file to deleted, just remove
	 * the dependency between elementFromRemove and elementToRemove,
	 * 
	 * @param elementFromRemove
	 * @param elementToRemove
	 * @return
	 */
	public List<Resource> removeDependenciesBetweenModels(
			ModelElement elementFromRemove,
			List<? extends ModelElement> elementToRemove) {
		EList<Dependency> clientDependencies;
		List<Resource> toRemoveResources = new ArrayList<Resource>();
		for (ModelElement modelElement : elementToRemove) {
			toRemoveResources.add(modelElement.eResource());
		}
		// get the client dependencies (
		clientDependencies = elementFromRemove.getClientDependency();
		// locate resource of each Dependency object
		List<Resource> modifiedResources = new ArrayList<Resource>();
		Iterator<Dependency> dependencyIterator = clientDependencies.iterator();
		while (dependencyIterator.hasNext()) {
			Dependency dependency = dependencyIterator.next();
			Resource dependencyResource = dependency.eResource();
			if (!toRemoveResources.contains(dependencyResource)) {
				continue;
			}
			if (dependencyResource != null) {
				modifiedResources.add(dependencyResource);
				dependencyIterator.remove();
			}
		}
		return modifiedResources;
	}

	/**
	 * 
	 * DOC mzhao 2009-06-12 feature 5887 Comment method
	 * "removeSupplierDependenciesBetweenModels". This method removes supplier
	 * dependencies. See
	 * {@link DependenciesHandler#removeDependenciesBetweenModels(ModelElement, List)}
	 * 
	 * @param elementFromRemove
	 * @param elementToRemove
	 * @return
	 */
	public List<Resource> removeSupplierDependenciesBetweenModels(
			ModelElement elementFromRemove,
			List<? extends ModelElement> elementToRemove) {
		EList<Dependency> supplierDependencies;
		List<Resource> toRemoveResources = new ArrayList<Resource>();
		for (ModelElement modelElement : elementToRemove) {
			toRemoveResources.add(modelElement.eResource());
		}
		// get the client dependencies (
		supplierDependencies = elementFromRemove.getSupplierDependency();

		List<Resource> modifiedResources = new ArrayList<Resource>();
		for (Dependency dependency : supplierDependencies) {
			EList<ModelElement> client = dependency.getClient();
			// get the resource of each client
			Iterator<ModelElement> dependencyIterator = client.iterator();
			while (dependencyIterator.hasNext()) {
				Resource clientResource = dependencyIterator.next().eResource();
				if (clientResource != null) {
					if (toRemoveResources.contains(clientResource)) {
						modifiedResources.add(clientResource);
						dependencyIterator.remove();
					}
				}
			}
		}

		return modifiedResources;
	}

	/**
	 * Method "createUsageDependencyOn".
	 * 
	 * Example Analysis depends on the exitence of a DataProvider. This method
	 * must be called createUsageDependencyOn(Analysis, DataProvider). The
	 * created dependency is added to the DataProvider in its
	 * "client dependencies" and to the Analysis in its "supplier dependencies".
	 * See OMG CWM spec paragraph 4.3.2.7.
	 * 
	 * The resource in which the dependency is stored is the supplier's
	 * resource.
	 * 
	 * @param kind
	 *            the kind of dependency
	 * @param clientElement
	 *            the element that requires the requiredElement
	 * @param supplierElement
	 *            the required element
	 * @return the Dependency of clientElement on requiredElement
	 */
	Dependency createDependencyOn(String kind, ModelElement clientElement,
			ModelElement supplierElement) {
		Dependency dependency = ModelElementHelper.createDependencyOn(kind,
				clientElement, supplierElement);
		Resource supplierResource = supplierElement.eResource();
		if (supplierResource != null) {
			supplierResource.getContents().add(dependency);
		}
		return dependency;
	}

	/**
	 * Method "removeSupplierDependencies". The given element (supplier) is an
	 * element required by other elements (the clients). This method gets all
	 * the dependencies that link the supplier to the clients. Then for each
	 * client, the dependency toward the supplier is removed. And finally the
	 * list of dependencies that link the supplier to its clients is suppressed.
	 * 
	 * @param supplierElement
	 *            an element that is required by other elements
	 * @return
	 */
	// boolean removeSupplierDependencies(ModelElement supplierElement) {
	// return ModelElementHelper.removeSupplierDependencies(supplierElement);
	// }
	/**
	 * Method "getDependencyBetween" the dependency that relates the supplier to
	 * the client. This method looks into the list of dependencies of both the
	 * supplier and the client.
	 * 
	 * @param kind
	 *            the kind of dependency looked for (could be null)
	 * @param clientElement
	 * @param supplierElement
	 * @return the dependency that relates the supplier to the client or null if
	 *         none is found.
	 */
	// Dependency getDependencyBetween(String kind, ModelElement clientElement,
	// ModelElement supplierElement) {
	// return ModelElementHelper.getDependencyBetween(kind, clientElement,
	// supplierElement);
	// }
	/**
	 * Method "createUsageDependencyOn".
	 * 
	 * @param clientElement
	 *            the analysis that depends on the data provider.
	 * @param dataManager
	 *            the data provider
	 * @return a true return code if the dependency has been correctly added to
	 *         the resource of the supplier element. Return false otherwise. In
	 *         any case, the dependency is created and the getObject() method
	 *         returns it.
	 */
	TypedReturnCode<Dependency> createUsageDependencyOn(
			ModelElement clientElement, ModelElement dataManager) {
		assert dataManager != null;
		Dependency dependency = createDependencyOn(USAGE, clientElement,
				dataManager);
		TypedReturnCode<Dependency> rc = new TypedReturnCode<Dependency>();
		rc.setObject(dependency);
		return rc;
	}

	// /**
	// * Method "createUsageDependencyOn".
	// *
	// * @param report the analysis that depends on the data provider.
	// * @param analysis the data provider
	// * @return a true return code if the dependency has been correctly added
	// to the resource of the supplier element.
	// * Return false otherwise. In any case, the dependency is created and the
	// getObject() method returns it.
	// */
	// TypedReturnCode<Dependency> createUsageDependencyOn(TdReport report,
	// Analysis analysis) {
	// assert analysis != null;
	// Dependency dependency = createDependencyOn(USAGE, report, analysis);
	// TypedReturnCode<Dependency> rc = new TypedReturnCode<Dependency>();
	// rc.setObject(dependency);
	// return rc;
	// }

	/**
	 * Method "setDependencyOn" sets the dependency between the analysis and the
	 * data manager.
	 * 
	 * @param analysis
	 *            the analysis which requires the data manager
	 * @param dataManager
	 *            the data manager required by the analysis
	 * @return a true return code if the dependency has been correctly added to
	 *         the resource of the supplier element. Return false otherwise. The
	 *         dependency is created only if needed and the getObject() method
	 *         returns it.
	 */
	public TypedReturnCode<Dependency> setDependencyOn(Analysis analysis,
			DataManager dataManager) {
		return setUsageDependencyOn(analysis, dataManager);
	}

	/**
	 * Method "setUsageDependencyOn".
	 * 
	 * @param client
	 *            the element which depends on the supplier
	 * @param supplier
	 *            the element needed by the client element
	 * @return the dependency object between the two given elements
	 */
	public TypedReturnCode<Dependency> setUsageDependencyOn(
			ModelElement client, ModelElement supplier) {
		// get the supplier's usage dependencies
		EList<Dependency> supplierDependencies = supplier
				.getSupplierDependency();
		// search for clients into them
		for (Dependency dependency : supplierDependencies) {
			if (USAGE.compareTo(dependency.getKind()) == 0) {
				// if exist return true with the dependency
				EObject resolvedObject = ResourceHelper.resolveObject(
						dependency.getClient(), client);
				if (resolvedObject == null) {
					// if not add analysis to the dependency
					dependency.getClient().add(client);
				}

				TypedReturnCode<Dependency> rc = new TypedReturnCode<Dependency>();
				rc.setObject(dependency);
				return rc;
			}
		}
		// if not exist create a usage dependency
		return createUsageDependencyOn(client, supplier);
	}

	/**
	 * Method "setDependencyOn" sets the dependency between the report and the
	 * analysis.
	 * 
	 * @param report
	 * @param analysis
	 * @return a true return code if the dependency has been correctly added to
	 *         the resource of the supplier element. Return false otherwise. The
	 *         dependency is created only if needed and the getObject() method
	 *         returns it.
	 */
	public TypedReturnCode<Dependency> setDependencyOn(TdReport report,
			Analysis analysis) {
		return setUsageDependencyOn(report, analysis);
	}

	public TypedReturnCode<Dependency> setDependencyOn(Indicator indicator,
			Pattern pattern) {
		return setUsageDependencyOn(indicator, pattern);
	}

	// /**
	// * Method "getDependencyBetween".
	// *
	// * @param clientElement
	// * @param dataManager
	// * @return the dependency between the given elements or null.
	// */
	// Dependency getDependencyBetween(Analysis clientElement, DataManager
	// dataManager) {
	// return getDependencyBetween(USAGE, clientElement, dataManager);
	// }
	//
	// /**
	// * Method "getDependencyBetween".
	// *
	// * @param report
	// * @param analysis
	// * @return the dependency between the given elements or null.
	// */
	// Dependency getDependencyBetween(TdReport report, Analysis analysis) {
	// return getDependencyBetween(USAGE, report, analysis);
	// }

}
