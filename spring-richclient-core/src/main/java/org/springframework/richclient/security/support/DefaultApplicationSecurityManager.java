/*
 * Copyright (c) 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.security.support;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.security.ApplicationSecurityManager;
import org.springframework.richclient.security.AuthenticationEvent;
import org.springframework.richclient.security.AuthenticationFailedEvent;
import org.springframework.richclient.security.LoginEvent;
import org.springframework.richclient.security.LogoutEvent;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.SpringSecurityException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;

/**
 * Default implementation of ApplicationSecurityManager. It provides basic processing for
 * login and logout actions and the event lifecycle.
 * <p>
 * Instances of this class should be configured with an instance of
 * {@link org.springframework.security.AuthenticationManager} to be used to handle authentication
 * (login) requests. This would be done like this:
 * 
 * <pre>
 *         &lt;bean id=&quot;securityManager&quot;
 *               class=&quot;org.springframework.richclient.security.support.DefaultApplicationSecurityManager&quot;&gt;
 *            &lt;property name=&quot;authenticationManager&quot; ref=&quot;authenticationManager&quot;/&gt;
 *         &lt;/bean&gt;
 *         
 *         &lt;bean id=&quot;authenticationManager&quot;
 *           class=&quot;org.springframework.security.providers.ProviderManager&quot;&gt;
 *           &lt;property name=&quot;providers&quot;&gt;
 *               &lt;list&gt;
 *                   &lt;ref bean=&quot;remoteAuthenticationProvider&quot; /&gt;
 *               &lt;/list&gt;
 *           &lt;/property&gt;
 *       &lt;/bean&gt;
 *       
 *       &lt;bean id=&quot;remoteAuthenticationProvider&quot;
 *           class=&quot;org.springframework.security.providers.rcp.RemoteAuthenticationProvider&quot;&gt;
 *           &lt;property name=&quot;remoteAuthenticationManager&quot; ref=&quot;remoteAuthenticationManager&quot; /&gt;
 *       &lt;/bean&gt;
 *    
 *       &lt;bean id=&quot;remoteAuthenticationManager&quot;
 *           class=&quot;org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean&quot;&gt;
 *           &lt;property name=&quot;serviceUrl&quot;&gt;
 *               &lt;value&gt;http://localhost:8080/myserver/rootContext/RemoteAuthenticationManager&lt;/value&gt;
 *           &lt;/property&gt;
 *           &lt;property name=&quot;serviceInterface&quot;&gt;
 *               &lt;value&gt;org.springframework.security.providers.rcp.RemoteAuthenticationManager&lt;/value&gt;
 *           &lt;/property&gt;
 *       &lt;/bean&gt;
 * </pre>
 * 
 * If this is not done, then an attempt will be made to "auto-configure" by locating an
 * appropriate authentication manager in the application context. In order, a search will
 * be made for a bean that implements one of these classes:
 * <ol>
 * <li>ProviderManager</li>
 * <li>AuthenticationProvider</li>
 * <li>AuthenticationManager</li>
 * </ol>
 * The first instance to be located will be used to handle authentication requests.
 * <p>
 * 
 * @author Larry Streepy
 * 
 */
public class DefaultApplicationSecurityManager implements ApplicationSecurityManager, InitializingBean {

    private final Log logger = LogFactory.getLog( getClass() );

    private AuthenticationManager authenticationManager = null;

    private Authentication currentAuthentication = null;

    /**
     * Default constructor.
     */
    public DefaultApplicationSecurityManager() {
        this( false );
    }

    /**
     * Constructor invoked when we are created as the default implementation by
     * ApplicationServices. Since this bean won't be defined in the context under these
     * circumstances, we need to perform some auto-configuration of our own.
     * <p>
     * Auto-configuration consists of trying to locate an AuthenticationManager (in one of
     * several classes) in the application context. This auto-configuration is also
     * attempted after the bean is constructed by the context if the authenticationManager
     * property has not been set. See {@see #afterPropertiesSet()}.
     * 
     * @param autoConfigure pass true to perform auto-configuration
     * @throws IllegalArgumentException If the auto-configuration fails
     * @see #afterPropertiesSet()
     */
    public DefaultApplicationSecurityManager(boolean autoConfigure) {
        if( autoConfigure ) {
            afterPropertiesSet();
        }
    }

    /**
     * Set the authentication manager to use.
     * @param authenticationManager instance to use for authentication requests
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Get the authentication manager in use.
     * @return authenticationManager instance used for authentication requests
     */
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    /**
     * Process a login attempt and fire all related events. If the authentication fails,
     * then a {@link AuthenticationFailedEvent} is published and the exception is
     * rethrown. If the authentication succeeds, then an {@link AuthenticationEvent} is
     * published, followed by a {@link LoginEvent}.
     * 
     * @param authentication token to use for the login attempt
     * @return Authentication token resulting from a successful call to
     *         {@link AuthenticationManager#authenticate(org.springframework.security.Authentication)}.
     * @see org.springframework.richclient.security.ApplicationSecurityManager#doLogin(org.springframework.security.Authentication)
     * @throws SpringSecurityException If the authentication attempt fails
     */
    public Authentication doLogin(Authentication authentication) {
        final ApplicationContext appCtx = Application.instance().getApplicationContext();

        Authentication result = null;

        try {
            result = getAuthenticationManager().authenticate( authentication );
        } catch( SpringSecurityException e ) {
            logger.info( "authentication failed: " + e.getMessage() );

            // Fire application event to advise of failed login
            appCtx.publishEvent( new AuthenticationFailedEvent( authentication, e ) );

            // rethrow the exception
            throw e;
        }

        // Handle success or failure of the authentication attempt
        if( logger.isDebugEnabled() ) {
            logger.debug( "successful login - update context holder and fire event" );
        }

        // Commit the successful Authentication object to the secure ContextHolder
        SecurityContextHolder.getContext().setAuthentication( result );
        setAuthentication( result );

        // Fire application events to advise of new login
        appCtx.publishEvent( new AuthenticationEvent( result ) );
        appCtx.publishEvent( new LoginEvent( result ) );

        return result;
    }

    /**
     * Return if a user is currently logged in, meaning that a previous call to doLogin
     * resulted in a valid authentication request.
     * @return true if a user is logged in
     */
    public boolean isUserLoggedIn() {
        return getAuthentication() != null;
    }

    /**
     * Get the authentication token for the currently logged in user.
     * @return authentication token, null if not logged in
     */
    public Authentication getAuthentication() {
        return currentAuthentication;
    }

    /**
     * Set the authenticaiton token.
     * @param authentication token to install as current.
     */
    protected void setAuthentication(Authentication authentication) {
        currentAuthentication = authentication;
    }

    /**
     * Determine if the currently authenticated user has the role provided. Note that role
     * comparisons are case sensitive.
     * 
     * @param role to check
     * @return true if the user has the role requested
     */
    public boolean isUserInRole(String role) {
        boolean inRole = false;

        Authentication authentication = getAuthentication();
        if( authentication != null ) {
            GrantedAuthority[] authorities = authentication.getAuthorities();
            for( int i = 0; i < authorities.length; i++ ) {
                if( role.equals( authorities[i].getAuthority() ) ) {
                    inRole = true;
                    break;
                }
            }
        }
        return inRole;
    }

    /**
     * Perform a logout. Set the current authentication token to null (in both the
     * per-thread security context and the global context), then publish an
     * {@link AuthenticationEvent} followed by a {@link LogoutEvent}.
     * @return Authentication token that was in place prior to the logout.
     * @see org.springframework.richclient.security.ApplicationSecurityManager#doLogout()
     */
    public Authentication doLogout() {
        Authentication existing = getAuthentication();

        // Make the Authentication object null if a SecureContext exists
        SecurityContextHolder.getContext().setAuthentication( null );
        setAuthentication( null );

        // Fire application event to advise of logout
        ApplicationContext appCtx = Application.instance().getApplicationContext();
        appCtx.publishEvent( new AuthenticationEvent( null ) );
        appCtx.publishEvent( new LogoutEvent( existing ) );

        return existing;
    }

    /**
     * Ensure that we have an authentication manager to work with. If one has not been
     * specifically wired in, then look for beans to "auto-wire" in. Look for a bean of
     * one of the following types (in order): {@link ProviderManager},
     * {@link AuthenticationProvider}, and {@link AuthenticationManager}.
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        // Ensure that we have our authentication manager
        if( authenticationManager == null ) {
            if( logger.isDebugEnabled() ) {
                logger.debug( "No AuthenticationManager defined, look for one" );
            }

            // Try the class types in sequence
            Class[] types = new Class[] { ProviderManager.class, AuthenticationProvider.class,
                    AuthenticationManager.class };

            for( int i = 0; i < types.length; i++ ) {
                if( tryToWire( types[i] ) ) {
                    break;
                }
            }
        }

        // If we still don't have one, then that's it
        if( authenticationManager == null ) {
            throw new IllegalArgumentException( "authenticationManager must be defined" );
        }
    }

    /**
     * Try to locate and "wire in" a suitable authentication manager.
     * @param type The type of bean to look for
     * @return true if we found and wired a suitable bean
     */
    protected boolean tryToWire(Class type) {
        boolean success = false;
        String className = type.getName();
        Map map = Application.instance().getApplicationContext().getBeansOfType( type );
        if( logger.isDebugEnabled() ) {
            logger.debug( "Search for '" + className + "' found: " + map );
        }

        if( map.size() == 1 ) {
            // Got one - wire it in
            Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();
            String name = (String) entry.getKey();
            AuthenticationManager am = (AuthenticationManager) entry.getValue();

            setAuthenticationManager( am );
            success = true;

            if( logger.isInfoEnabled() ) {
                logger.info( "Auto-configuration using '" + name + "' as authenticationManager" );
            }
        } else if( map.size() > 1 ) {
            if( logger.isInfoEnabled() ) {
                logger.info( "Need a single '" + className + "', found: " + map.keySet() );
            }
        } else {
            // Size 0, no potentials
            if( logger.isInfoEnabled() ) {
                logger.info( "Auto-configuration did not find a suitable authenticationManager of type " + type );
            }
        }

        return success;
    }
}
