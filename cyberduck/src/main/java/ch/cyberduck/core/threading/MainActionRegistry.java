package ch.cyberduck.core.threading;

/**
 * @version  $Id: MainActionRegistry.java 5068 2009-08-12 21:05:44Z dkocher $
 */
public class MainActionRegistry extends AbstractActionRegistry<MainAction> {

    /**
	 * @uml.property  name="instance"
	 * @uml.associationEnd  
	 */
    private static MainActionRegistry instance = null;

    private static final Object lock = new Object();

    public static MainActionRegistry instance() {
        synchronized(lock) {
            if(null == instance) {
                instance = new MainActionRegistry();
            }
            return instance;
        }
    }
}
