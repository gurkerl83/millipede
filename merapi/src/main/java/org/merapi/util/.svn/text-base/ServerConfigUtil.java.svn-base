/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.merapi.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utilities for accessing server configuration. Note that this class cannot
 * have dependencies on anything but bootstrap and jdk classes. Anything more
 * than this needs to be defined in the ServerConfig interface and provided
 * in the associated implementation.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version <tt>$Revision: 1.2.6.3 $</tt>
 */
public class ServerConfigUtil
{
   private static final String ANY = "0.0.0.0";

//   /**
//    * Retrieve the default bind address for the server
//    *
//    * @return the default bind adress
//    */
//   public static String getDefaultBindAddress()
//   {
//      return System.getProperty(ServerConfig.SERVER_BIND_ADDRESS);
//   }
//
//   /**
//    * Retrieve the default bind address, but only if it is specific
//    *
//    * @return the specific bind address
//    */
//   public static String getSpecificBindAddress()
//   {
//      String address = System.getProperty(ServerConfig.SERVER_BIND_ADDRESS);
//      if (address == null || address.equals(ANY))
//         return null;
//      return address;
//   }

   /**
    * Fix the remote inet address.
    *
    * If we pass the address to the client we don't want to
    * tell it to connect to 0.0.0.0, use our host name instead
    * @param address the passed address
    * @return the fixed address
    */
   public static InetAddress fixRemoteAddress(InetAddress address)
   {
      try
      {
         if (address == null || InetAddress.getByName(ANY).equals(address))
            return InetAddress.getLocalHost();
      }
      catch (UnknownHostException ignored)
      {
      }
      return address;
   }

   /**
    * Fix the remote address.
    *
    * If we pass the address to the client we don't want to
    * tell it to connect to 0.0.0.0, use our host name instead
    * @param address the passed address
    * @return the fixed address
    */
   public static String fixRemoteAddress(String address)
   {
      try
      {
         if (address == null || ANY.equals(address))
            return InetAddress.getLocalHost().getHostName();
      }
      catch (UnknownHostException ignored)
      {
      }
      return address;
   }

//   /**
//    * Get the default partition name
//    *
//    * @return the default partition name
//    */
//   public static String getDefaultPartitionName()
//   {
//      return System.getProperty(ServerConfig.PARTITION_NAME_PROPERTY, ServerConfig.DEFAULT_PARITION_NAME);
//   }
//
//   /**
//    * Whether to load native directories
//    *
//    * @return true when loading native directories
//    */
//   public static boolean isLoadNative()
//   {
//      return Boolean.getBoolean(ServerConfig.NATIVE_LOAD_PROPERTY);
//   }

}
