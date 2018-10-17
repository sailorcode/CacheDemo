package com.trendytech.tcmp.queryengine.support.vsphere;

import com.trendytech.tcmp.queryengine.core.platform.vsphere.VCServer;
import com.vmware.vim25.mo.ServerConnection;

public interface ConnectionPool {
    public ServerConnection getConnection(VCServer vcServer);

    public void releaseConnection(ServerConnection connection);
}
