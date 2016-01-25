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
package org.talend.dq.analysis.memory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.Collection;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/**
 * DOC yyi class global comment. Detailled comment
 */
public abstract class AbstractMemoryChangeNotifier implements IMemoryChangeNotifier {

    private Collection<IMemoryChangeListener> listeners = new ArrayList<IMemoryChangeListener>();

    private MemoryPoolMXBean tenuredGenPoll;

    protected AbstractMemoryChangeNotifier() {

        initialize();

        final MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        final NotificationEmitter emitter = (NotificationEmitter) mbean;
        emitter.addNotificationListener(new NotificationListener() {

            public void handleNotification(Notification n, Object hb) {
                if (n.getType().equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
                    long maxMemory = tenuredGenPoll.getUsage().getMax();
                    long usedMemory = tenuredGenPoll.getUsage().getUsed();
                    for (IMemoryChangeListener listener : listeners) {
                        listener.onMemoryChange(maxMemory - usedMemory);
                    }
                }
            }
        }, getListenerFilter(), getListenerHandBack());
    }

    private MemoryPoolMXBean findTenuredGenPool() {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP && pool.isUsageThresholdSupported()) {
                return pool;
            }
        }
        return null;
    }

    protected boolean initialize() {
        tenuredGenPoll = findTenuredGenPool();
        return null != tenuredGenPoll;
    }

    protected abstract NotificationFilter getListenerFilter();

    protected abstract NotificationFilter getListenerHandBack();

    public synchronized boolean addListener(IMemoryChangeListener listener) {
        return listeners.add(listener);
    }

    public synchronized boolean removeListener(IMemoryChangeListener listener) {
        return listeners.remove(listener);
    }

    // if threshold is greater than the maximum amount of memory for this memory pool if defined.
    protected void setUsageThreshold(long threshold) {
        // MOD yyi 2012-04-12 TDQ-4916:The usage threshold crossing checking is disabled if it is set to zero.
        if (threshold <= 0) {
            tenuredGenPoll.setUsageThreshold(0);
        } else {
            // the threshold shoudle less than max memory
            tenuredGenPoll.setUsageThreshold(threshold);
        }
    }

    public boolean isUsageThresholdExceeded() {
        if (!AnalysisThreadMemoryChangeNotifier.isAnaMemControl()) {
            return false;
        }
        boolean isExceeded = tenuredGenPoll.isUsageThresholdExceeded();
        if (isExceeded) {
            ManagementFactory.getMemoryMXBean().gc();
            isExceeded = tenuredGenPoll.isUsageThresholdExceeded();
        }
        return isExceeded;
    }

}
