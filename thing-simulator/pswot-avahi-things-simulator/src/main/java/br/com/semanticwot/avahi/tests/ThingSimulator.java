package br.com.semanticwot.avahi.tests;

import java.util.Vector;

import avahi4j.Avahi4JConstants;
import avahi4j.Client;
import avahi4j.EntryGroup;
import avahi4j.IClientCallback;
import avahi4j.IEntryGroupCallback;
import avahi4j.Avahi4JConstants.Protocol;
import avahi4j.EntryGroup.State;
import avahi4j.exceptions.Avahi4JException;
import java.lang.reflect.Field;

public class ThingSimulator implements IClientCallback, IEntryGroupCallback {

    /**
     * The Avahi4J {@link Client} object. This is the start.
     */
    private Client client;

    /**
     * The service's {@link EntryGroup}, which contains the service's details.
     */
    private EntryGroup group;

    /**
     * A list of TXT records for this service.
     */
    private Vector<String> records;

    /**
     * This method builds the test object.
     *
     * @throws Avahi4JException if there is an error creating or starting the
     * Avahi4J {@link Client}.
     */
    public ThingSimulator() throws Avahi4JException {
        records = new Vector<String>();
        client = new Client(this);
        client.start();
        System.out.println("FQDN: " + client.getFQDN());
        System.out.println("Hostname: " + client.getHostName());
        System.out.println("domain name: " + client.getDomainName());
        System.out.println("state: " + client.getState());
    }

    /**
     * This method creates the {@link EntryGroup}, adds some TXT records, a
     * name, a service type and port number. It then proceeds to publish the
     * service. If there is a name conflict, it asks Avahi4J for an alternate
     * name and tries again.
     *
     * @param number_services
     * @throws Avahi4JException if there is an error creating the
     * {@link EntryGroup}
     */
    public void addService(int number_services) throws Avahi4JException {
        int result;

        // create some fake TXT records
        for (int i = 1; i < number_services+1; i++) {

            client = new Client(this);
            client.start();

            // create group
            group = client.createEntryGroup(this);

            records = new Vector<String>();
            System.out.println(i);
            records.add("pswot={\"id\":\" " + i + " \",\n" + 
                    "\"device\": \"funcionalidade_" + i + "\",\n" + 
                    "\"vendor\": \"WISER\",\n" + 
                    "\"metadataType\": \"DRIVER\",\n" + 
                    "\"metadataValue\": \"driver_" + i + "\"}");

            // add service
            System.out.println("\n\nAdding new service to group");
            result = group.addService(Avahi4JConstants.AnyInterface, Protocol.ANY,
                    "TestService" + i, "_mqtt._tcp", null, null, 1883, records);
            if (result != Avahi4JConstants.AVAHI_OK) {
                System.out.println("Error adding service to group: "
                        + Avahi4JConstants.getErrorString(result));

                // try with an alternate name
                String newName = EntryGroup.
                        findAlternativeServiceName("TestService" + i);
                System.out.
                        println("\n\nRe-trying with new service name: " + newName);
                result = group.addService(Avahi4JConstants.AnyInterface,
                        Protocol.ANY,
                        newName, "_mqtt._tcp", null, null, 1515, records);
                if (result != Avahi4JConstants.AVAHI_OK) {
                    System.out.println("Error adding service to group: "
                            + Avahi4JConstants.getErrorString(result));
                }
            }

            // commit service
            System.out.println("Committing group");
            result = group.commit();
            if (result != Avahi4JConstants.AVAHI_OK) {
                System.out.println("Error committing group: "
                        + Avahi4JConstants.getErrorString(result));
            }

            System.out.println("done");

        }
    }

    /**
     * This method resets (un-publishes) the service
     */
    public void resetService() {
        int result;

        // reset group
        System.out.println("Resetting group");
        result = group.reset();
        if (result != Avahi4JConstants.AVAHI_OK) {
            System.out.println("Error resetting group: "
                    + Avahi4JConstants.getErrorString(result));
        } else {
            System.out.println("done");
        }
    }

    /**
     * This method releases the {@link EntryGroup} and {@link Client}
     */
    public void stop() {
        group.release();
        client.stop();
        client.release();
    }

    /**
     * This callback method is invoked whenever the Avahi4J {@link Client}'s
     * state changes. See {@link State} for a list of possible client states.
     */
    @Override
    public void clientStateChanged(Client.State state) {
        System.out.println("client's new state: " + state);
    }

    /**
     * This callback method is invoked whenever the {@link EntryGroup}'s state
     * changes. See {@link State} for a list of possible states.
     */
    @Override
    public void groupStateChanged(State newState) {
        System.out.println("Group's new state: " + newState);
    }

    /**
     * Main method which publishes a service, updates it & release it
     *
     * @param args (does not expect any argument)
     * @throws Exception If there is an error of some sort
     */
    public static void main(String args[]) throws Exception {
        System.setProperty("java.library.path", "/usr/lib/jni");
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);

        ThingSimulator t = new ThingSimulator();
        System.out.println("Press <Enter>");
        System.in.read();
        t.addService(100);
        System.out.println("Press <Enter>");
        System.in.read();
        t.resetService();
        System.out.println("Press <Enter>");
        System.in.read();
        t.stop();
    }
}
