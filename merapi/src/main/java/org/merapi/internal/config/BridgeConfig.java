//////////////////////////////////////////////////////////////////////////////////
////
////  This program is free software; you can redistribute it and/or modify
////  it under the terms of the GNU Lesser General Public License as published
////  by the Free Software Foundation; either version 3 of the License, or (at
////  your option) any later version.
////
////  This program is distributed in the hope that it will be useful, but
////  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
////  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
////  License for more details.
////
////  You should have received a copy of the GNU Lesser General Public License
////  along with this program; if not, see <http://www.gnu.org/copyleft/lesser.html>.
////
//////////////////////////////////////////////////////////////////////////////////
//
//package org.merapi.internal.config;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.FileSystemXmlApplicationContext;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.InvalidPropertiesFormatException;
//import java.util.Properties;
//
///**
// *  The <code>BridgeConfig</code> class is responsible for loading the config xml file use
// *  to configure the Java org.merapi bridge.
// */
//public class BridgeConfig
//{
//    //---------------------------------------------------------------------------
//    //
//    //  Constructors
//    //
//    //---------------------------------------------------------------------------
//
//    /**
//     *  Constructor
//     *
//     *  @param path
//     *            path to the properties file
//     *  @throws IOException
//     *             can't read the file
//     */
//    public BridgeConfig( String path ) throws IOException
//    {
//        __configPath = path;
//
//        readConfig( __configPath );
//
//        if ( __props.containsKey( "springConfig" ) )
//        {
//            readSpringConfig( __props.getProperty( "springConfig" ) );
//        }
//    }
//
//    /**
//     *  No arg. constructor. Just nulls out everything.
//     */
//    public BridgeConfig()
//    {
//        __configPath = null;
//        __context    = null;
//        __props     = null;
//    }
//
//
//    //---------------------------------------------------------------------------
//    //
//    //  Properties
//    //
//    //---------------------------------------------------------------------------
//
//    /**
//     *  The path to the config file.
//     */
//    public String getConfigPath() { return __configPath; }
//    public void setConfigPath( String configPath ) { __configPath = configPath; }
//
//    /**
//     *  The properties read from the config file.
//     */
//    public Properties getProps() { return __props; }
//
//    /**
//     *  The context used to load the <code>IWriter</code> and <code>IReader</code> class types.
//     */
//    public ApplicationContext getContext() { return __context; }
//
//
//    //---------------------------------------------------------------------------
//    //
//    //  Methods
//    //
//    //---------------------------------------------------------------------------
//
//    /**
//     * Reads the config from org.merapi-native-config.xml file
//     *
//     * @param path
//     *            path to the xml file
//     * @return the populated properties object
//     * @throws IOException
//     *             can't read the file
//     * @throws InvalidPropertiesFormatException
//     *             bad file format
//     */
//    public void readConfig( String path ) throws IOException, InvalidPropertiesFormatException
//    {
//        FileInputStream fis = new FileInputStream( path );
//        __props = new Properties();
//        __props.loadFromXML( fis );
//        fis.close();
//    }
//
//    /**
//     * Loads the Spring ApplicationContext
//     *
//     * @param path
//     *            the path the spring config file. scans package org.merapi.* for annotated
//     *            components
//     * @return the ApplicationContext with the beans populated
//     */
//    public void readSpringConfig( String path )
//    {
//        FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext( path );
//        __context = ctx;
//    }
//
//
//    //---------------------------------------------------------------------------
//    //
//    //  Variables
//    //
//    //---------------------------------------------------------------------------
//
//    /**
//     * @private The file path for the config xml file
//     */
//    private String             __configPath = null;
//
//    /**
//     * @private The loaded properties (via the config file)
//     */
//    private Properties         __props      = null;
//
//    /**
//     * @private The Spring context used to load the <code>IReader</code> and
//     *          <code>IWriter</code> class types.
//     */
//    private ApplicationContext __context    = null;
//
//}