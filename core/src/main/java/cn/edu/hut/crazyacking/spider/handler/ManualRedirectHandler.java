package cn.edu.hut.crazyacking.spider.handler;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.protocol.HttpContext;

import java.net.URI;

class ManualRedirectHandler implements RedirectHandler {

    @Override
    public URI getLocationURI(HttpResponse arg0, HttpContext arg1)
            throws ProtocolException {
        // TODO: 2016/11/9 crazyacking 自动生成方法存根
        return null;
    }

    @Override
    public boolean isRedirectRequested(HttpResponse arg0, HttpContext arg1) {
        /*
        由于我们需要手动处理所有的redirect，所以直接return false
         */
        return false;
    }
}
