package org.talend.dataprofiler.ecos.model;

import java.util.List;

/**
 * a category in exchange.
 * <p>
 * take <a url='http://www.talendforge.org/exchange/top/index.php?cid=1'><code>regex</code></a> for example. It contain
 * name, counter and component.
 * 
 * @author gyichao
 * 
 */
public interface IEcosCategory {

    String getId();

    /**
     * The name for Category. can not be null.
     * 
     * @return <code>String</code> name of the category.
     */
    String getName();

    /**
     * The number of component in the Category. it must greater than 0 or equal to 0.
     * 
     * @return <code>int</code> number of children in the category.
     */
    int getCounter();

    /**
     * Components in the category.
     * 
     * @return {@linkplain IEcosComponent} in {@linkplain List}. empty list if there is no any component.
     */
    List<IEcosComponent> getComponent();
}
