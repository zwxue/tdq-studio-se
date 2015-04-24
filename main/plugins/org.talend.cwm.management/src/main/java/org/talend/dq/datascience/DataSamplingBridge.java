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
package org.talend.dq.datascience;

import java.util.ArrayList;
import java.util.List;

import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.sampling.ReservoirSampler;
import org.talend.utils.exceptions.TalendException;

/**
 * created by zhao<br>
 * Bridge from data source extraction to data sample API
 *
 */
public class DataSamplingBridge {

    private SamplingOption samplingOption;

    private ReservoirSampler<Object[]> reservoirSampler;

    private List<Object[]> reservoirSamplingData = null;

    private int sampleSize = 0;

    private SamplingDataSource<?> dataSource;

    private int recordCursor = 0;

    public void setSampleSize(int size) {
        sampleSize = size;
    }

    public boolean hasNext() throws TalendException {
        return dataSource.hasNext();
    }

    /**
     * 
     * DOC zhao Do prepare work before getting real data, work such as JDBC or file connection creation.
     * 
     * @return true if success, false otherwise.
     * @throws TalendException When unexpected exception occurs
     */
    public boolean prepareData() throws TalendException {
        // Reset record cursor
        recordCursor = 0;
        reservoirSamplingData = new ArrayList<Object[]>();
        switch (samplingOption) {
        case TopN:
            break;
        case Percentage:
            break;
        case Reservoir:
            reservoirSampler = new ReservoirSampler<Object[]>(sampleSize, RANDOM_SEED);
            reservoirSampler.clear();
            while (dataSource.hasNext()) {
                reservoirSampler.onNext(dataSource.getRecord());
            }
            reservoirSampler.onCompleted(true);
            reservoirSamplingData = reservoirSampler.sample();
            break;
        default:
            break;
        }
        return false;
    }

    /**
     * 
     * Get one record from iterator of datasoure, note that this function is data-source type dependent such a case is
     * that in JDBC connection, the interation is from ResultSet while in file connection it's from file delimiter API.
     * 
     * @return true if success, false otherwise
     * @throws TalendException occurs when there are unexpected exceptions.
     */
    public Object[] getRecord() throws TalendException {
        switch (samplingOption) {
        case TopN:
            break;
        case Percentage:
            break;
        case Reservoir:
            if (reservoirSampler == null) {
                throw new TalendException(Messages.getString("DataSamplingBridge_no_init")); //$NON-NLS-1$
            }
            return reservoirSamplingData.get(recordCursor);
        default:
            break;
        }
        recordCursor++;
        return null;
    }

    /**
     * 
     * DOC zhao Finallize the data sample , some operation need to be done here such as closing JDBC connection.
     * 
     * @return true if success, false otherwise.
     * @throws TalendException When unexpected exception occurs
     */
    public boolean finalizeDataSampling() throws TalendException {
        return false;
    }

    public static final long RANDOM_SEED = 12345678;

}
