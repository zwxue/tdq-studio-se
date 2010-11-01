package org.talend.dataprofiler.ecos.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataprofiler.ecos.model.impl.EcosCategory;

public class EcosystemServiceTest {

    @Test
    public void testGetCategoryList() {
        List<IEcosCategory> list;
        try {
            list = EcosystemService.getCategoryList("3.1.2");
            for (Iterator<IEcosCategory> iterator = list.iterator(); iterator.hasNext();) {
                EcosCategory object = (EcosCategory) iterator.next();
                System.out.println(object.getVersion());

            }
            assertNotNull(list);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

}
