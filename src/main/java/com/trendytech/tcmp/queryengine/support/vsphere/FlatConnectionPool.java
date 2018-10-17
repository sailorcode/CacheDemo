package com.trendytech.tcmp.queryengine.support.vsphere;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.commons.lang3.StringUtils;

import com.trendytech.tcmp.queryengine.core.platform.vsphere.VCServer;
import com.vmware.vim25.mo.ServerConnection;
import com.vmware.vim25.mo.ServiceInstance;

public class FlatConnectionPool implements ConnectionPool {

    @Override
    public ServerConnection getConnection(VCServer vcServer) {
        try {
            URL url = null;
            if (StringUtils.isNotEmpty(vcServer.getProxyIp())) {
                int port = vcServer.getProxyPort() == 0 ? 443 : vcServer.getProxyPort();
                url = new URL("https://" + vcServer.getProxyIp() + ":" + port + "/sdk");
            } else {
                int port = vcServer.getPort() == 0 ? 443 : vcServer.getPort();
                url = new URL("https://" + vcServer.getIp() + ":" + port + "/sdk");
            }

            ServiceInstance si = new ServiceInstance(url, vcServer.getUsername(), vcServer.getPassword(), true);
            return si.getServerConnection();
        } catch (RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseConnection(ServerConnection connection) {
        try {
            if (connection != null) {
                connection.getServiceInstance().getSessionManager().logout();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
