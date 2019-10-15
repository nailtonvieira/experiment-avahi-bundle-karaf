package br.com.semanticwot.pswot.gateway.avahi.bundle;

import br.com.semanticwot.pswot.gateway.interfaces.IDetecter;
import java.lang.reflect.Field;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator, IDetecter {
    
    public void start(BundleContext context) throws Exception {
        
        System.setProperty("java.library.path", "/usr/lib");
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);

        DetecterBrowser b = new DetecterBrowser(context);
        b.browse();
        System.out.println("Press <Enter>");
        System.in.read();
        b.stop();
    }

    public void stop(BundleContext context) throws Exception {
        // TODO add deactivation code here
    }

}
