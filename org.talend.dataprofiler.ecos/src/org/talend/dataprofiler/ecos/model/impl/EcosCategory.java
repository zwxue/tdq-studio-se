package org.talend.dataprofiler.ecos.model.impl;

import java.util.Collections;
import java.util.List;

import org.talend.dataprofiler.ecos.jobs.ComponentSearcher;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataprofiler.ecos.model.IEcosComponent;

/**
 * @author jet
 *
 */
public class EcosCategory implements IEcosCategory{
	
	String id;
	
	String name;
	
	int counter;
	
	String version;

	public EcosCategory() {
		super();
	}
	
	public EcosCategory(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}


	
	
	public void setVersion(String version) {
		this.version = version;
	}



	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	
	List<IEcosComponent> components = Collections.EMPTY_LIST;
	
	boolean reload = false;
	
	

	public boolean isReload() {
		return reload;
	}

	public void setReload(boolean reload) {
		this.reload = reload;
	}

	/* (non-Javadoc)
	 * @see org.talend.dataprofiler.ecos.model.IEcosCategory#getComponent()
	 */
	public List<IEcosComponent> getComponent() {
		if(components.isEmpty() || isReload() ){
			components = ComponentSearcher.getAvailableComponentExtensions(version, this, reload);
		}
		return components;
	}

	public int getCounter() {
		return counter;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("name:").append(getName()).append("\n");
		sb.append("counter:").append(getCounter()).append("\n");
		return sb.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	
	

}
