package br.com.semanticwot.avahi.tests;

import avahi4j.Address;
import avahi4j.Avahi4JConstants;
import avahi4j.Avahi4JConstants.BrowserEvent;
import avahi4j.Avahi4JConstants.Protocol;
import avahi4j.Client;
import avahi4j.Client.State;
import avahi4j.IClientCallback;
import avahi4j.IServiceBrowserCallback;
import avahi4j.IServiceResolverCallback;
import avahi4j.ServiceBrowser;
import avahi4j.ServiceResolver;
import avahi4j.ServiceResolver.ServiceResolverEvent;
import avahi4j.exceptions.Avahi4JException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DetectorSimulator implements IClientCallback, IServiceBrowserCallback,
        IServiceResolverCallback {

    private Client client;

    private ServiceBrowser browser;

    private List<ServiceResolver> resolvers;

    public DetectorSimulator() throws Avahi4JException {
        resolvers = new ArrayList<ServiceResolver>();
        client = new Client(this);
        client.start();
    }

    public void browse() throws Avahi4JException {
        browser = client.createServiceBrowser(this, Avahi4JConstants.AnyInterface,
                Protocol.ANY , "_mqtt._tcp", null, 0);
    }

    public void stop() {
        browser.release();

        for(ServiceResolver s: resolvers)
            s.release();

        client.stop();
        client.release();
    }

    @Override
    public void clientStateChanged(State state) {
        System.out.println("Client state changed to "+state);
    }

    @Override
    public void serviceCallback(int interfaceNum, Protocol proto,
                                BrowserEvent browserEvent, String name, String type,
                                String domain, int lookupResultFlag) {

        // print event type
        System.out.println(" ****** Service browser event: "+browserEvent);

        if(browserEvent==BrowserEvent.NEW || browserEvent==BrowserEvent.REMOVE){

            // print service details
            System.out.println("Interface: "+interfaceNum + "\nProtocol :"
                    + proto +"\nEvent: " + browserEvent + "\nName: "+name+ "\nType:"
                    + type+ "\nDomain: "+domain+ "\nFlags: "
                    + Avahi4JConstants.lookupResultToString(lookupResultFlag)
                    + "\n");

            // only if it's a new service, resolve it
            if(browserEvent==BrowserEvent.NEW){
                try {
                    resolvers.add(client.createServiceResolver(this,
                            interfaceNum, proto, name, type, domain,
                            Protocol.ANY, 0));
                } catch (Avahi4JException e) {
                    System.out.println("error creating resolver");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void resolverCallback(ServiceResolver resolver, int interfaceNum,
                                 Protocol proto,	ServiceResolverEvent resolverEvent, String name,
                                 String type, String domain, String hostname, Address address,
                                 int port, String txtRecords[], int lookupResultFlag) {

        // print resolved name details
        if(resolverEvent==ServiceResolverEvent.RESOLVER_FOUND) {

            if(name==null && type==null && hostname==null) {
                resolver.release();
                resolvers.remove(resolver);
            } else {
                System.out.println(" ******  Service RESOLVED:\nInterface: "
                        + interfaceNum + "\nProtocol :"	+ proto + "\nName: " + name
                        + "\nType: " + type+ "\nHostname: "+ hostname +"\nDomain: "
                        + domain+ "\nAddress: " + address + "\nFlags: "
                        + Avahi4JConstants.lookupResultToString(lookupResultFlag)
                        + "\nTXT records:");

                for(String s: txtRecords)
                    System.out.println(s);
            }
        } else {
            System.out.println("Unable to resolve name");
        }
    }

    public static void main(String args[]) throws Avahi4JException, IOException, NoSuchFieldException, IllegalAccessException {
        System.setProperty("java.library.path", "/usr/lib");
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);

        DetectorSimulator b = new DetectorSimulator();
        b.browse();
        System.out.println("Press <Enter>");
        System.in.read();
        b.stop();
    }
}
