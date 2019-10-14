package br.com.semanticwot.pswot.gateway.avahi.bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import avahi4j.Address;
import avahi4j.Avahi4JConstants;
import avahi4j.Client;
import avahi4j.IClientCallback;
import avahi4j.IServiceBrowserCallback;
import avahi4j.IServiceResolverCallback;
import avahi4j.ServiceBrowser;
import avahi4j.ServiceResolver;
import avahi4j.Avahi4JConstants.BrowserEvent;
import avahi4j.Avahi4JConstants.Protocol;
import avahi4j.Client.State;
import avahi4j.ServiceResolver.ServiceResolverEvent;
import avahi4j.exceptions.Avahi4JException;
import br.com.semanticwot.pswot.gateway.interfaces.IDiscoveryService;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import java.lang.reflect.Field;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DetecterBrowser implements IClientCallback,
        IServiceBrowserCallback,
        IServiceResolverCallback {

    /**
     * The Avahi4J {@link Client} object
     */
    private Client client;

    /**
     * A {@link ServiceBrowser} object used to look for "_mqtt._tcp" services
     */
    private ServiceBrowser browser;

    /**
     * Each matching service is resolved by a {@link ServiceResolver} object,
     * which is kept open and a reference is stored in this list, so it can be
     * released upon exit
     */
    private List<ServiceResolver> resolvers;
    
    
    private ServiceReference serviceReference_discovery;

    private IDiscoveryService discovery;
    
    /**
     * This method builds the test object
     *
     * @throws Avahi4JException if there is an error creating or starting the
     * {@link Client} object.
     */
    public DetecterBrowser(BundleContext context) throws Avahi4JException {
        
        System.out.println("start PSWoT Core");
        serviceReference_discovery = context.getServiceReference(IDiscoveryService.class.getName());
        discovery =(IDiscoveryService)context.getService(serviceReference_discovery);
                
        resolvers = new ArrayList<ServiceResolver>();
        client = new Client(this);
        client.start();
    }

    /**
     * This method looks for "_mqtt._tcp" services. Matching services are
     * delivered to the callback method
     * {@link #serviceCallback(int, avahi4j.Avahi4JConstants.Protocol, avahi4j.Avahi4JConstants.BrowserEvent, String, String, String, int) serviceCallback()},
     * which then resolves the services.
     *
     * @throws Avahi4JException if there is an error creating the
     * {@link ServiceBrowser} object
     */
    public void browse() throws Avahi4JException {
        // browse for "_test._tcp" services
        browser = client.createServiceBrowser(this,
                Avahi4JConstants.AnyInterface,
                Protocol.ANY, "_mqtt._tcp", null, 0);
    }

    /**
     * This method releases the {@link ServiceBrowser} object created in
     * {@link #browse()}. After that, items in the {@link ServiceResolver} list
     * are freed, the client stopped and released.
     */
    public void stop() {
        // release the browser first so no more ServiceResolver can be added
        // to the list
        browser.release();

        // we can now safely release items in the list
        for (ServiceResolver s : resolvers) {
            s.release();
        }

        // stop and release the client
        client.stop();
        client.release();
    }

    /**
     * This callback method is invoked whenever the Avahi4J {@link Client}'s
     * state changes. See {@link State} for a list of possible client states.
     */
    @Override
    public void clientStateChanged(State state) {
        System.out.println("Client state changed to " + state);
    }

    /**
     * This callback method is invoked whenever a new matching service is
     * discovered. This method prints the service details, and creates a
     * {@link ServiceResolver} object to resolve the IP address of the computer
     * offering the service. The {@link ServiceResolver} is kept open so changes
     * in the service's records are received. A reference to the
     * {@link ServiceResolver} is also stored in {@link #resolvers} so it can be
     * released upon exit.
     */
    @Override
    public void serviceCallback(int interfaceNum, Protocol proto,
            BrowserEvent browserEvent, String name, String type,
            String domain, int lookupResultFlag) {

        // print event type
        System.out.println(" ****** PSWoT Service browser event: " + browserEvent);

        if (browserEvent == BrowserEvent.NEW || browserEvent
                == BrowserEvent.REMOVE) {

            // print service details
            System.out.println("Interface: " + interfaceNum + "\nProtocol :"
                    + proto + "\nEvent: " + browserEvent + "\nName: " + name
                    + "\nType:"
                    + type + "\nDomain: " + domain + "\nFlags: "
                    + Avahi4JConstants.lookupResultToString(lookupResultFlag)
                    + "\n");

            // only if it's a new service, resolve it
            if (browserEvent == BrowserEvent.NEW) {
                try {
                    // ServiceResolvers are kept open and a reference is stored
                    // in a list so they can be freed upon exit
                    resolvers.add(client.createServiceResolver(this,
                            interfaceNum, proto, name, type, domain,
                            Protocol.INET, 0));       
                } catch (Avahi4JException e) {
                    System.out.println("error creating resolver");
                    e.printStackTrace();
                }
            }
        }
    }

    private int cont_services = 1;
    private Monitor monitor;
    @Override
    public void resolverCallback(ServiceResolver resolver, int interfaceNum,
            Protocol proto, ServiceResolverEvent resolverEvent, String name,
            String type, String domain, String hostname, Address address,
            int port, String txtRecords[], int lookupResultFlag) {

        if (cont_services == 1){
            monitor = MonitorFactory.start("resolver");
        }
        // print resolved name details
        if (resolverEvent == ServiceResolverEvent.RESOLVER_FOUND) {
            if (name == null && type == null && hostname == null) {
                // if null, the service has disappeared, release the resolver
                // and remove it from the list
                resolver.release();
                resolvers.remove(resolver);
            } else {
                System.out.println("cont_services: " + cont_services);
                if (cont_services == 200){ // two interfaces = number_services * 2
                    monitor.stop();
                    System.out.println("monitor average: " + monitor.getAvg());
                }
                cont_services++;
                
                System.out.println(" ****** PSWoT Device RESOLVED:\nInterface: "
                        + interfaceNum + "\nProtocol :" + proto + "\nName: "
                        + name
                        + "\nType: " + type + "\nHostname: " + hostname
                        + "\nDomain: "
                        + domain + "\nAddress: " + address + "\nFlags: "
                        + Avahi4JConstants.
                        lookupResultToString(lookupResultFlag)
                        + "\nTXT records:");

                for (String s : txtRecords) {
                    System.out.println(s);
                }
                
                // System.out.println("Valor do discovery: " + discovery.discovery());
            }
        } else {
            System.out.println("Unable to resolve name");
        }
    }
}
