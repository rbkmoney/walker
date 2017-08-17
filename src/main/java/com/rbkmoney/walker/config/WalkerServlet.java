package com.rbkmoney.walker.config;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.walker.WalkerSrv;
import com.rbkmoney.woody.api.event.CompositeServiceEventListener;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import com.rbkmoney.woody.thrift.impl.http.event.HttpServiceEventLogListener;
import com.rbkmoney.woody.thrift.impl.http.event.ServiceEventLogListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/walker")
public class WalkerServlet extends GenericServlet {

    private Servlet thriftServlet;

    @Autowired
    private WalkerSrv.Iface requestHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(WalkerSrv.Iface.class, requestHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
