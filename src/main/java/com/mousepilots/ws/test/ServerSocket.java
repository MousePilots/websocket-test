/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mousepilots.ws.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author jgeenen
 */
@ServerEndpoint("/socket")
public class ServerSocket{

    private static final Logger LOG = Logger.getLogger(ServerSocket.class.getName());
    
    @Inject
    private Sessions sessions;
    
    @OnOpen
    public void open(Session session) {
        sessions.setRegistered(session, true);
        LOG.log(Level.INFO, "opened and registered {0}", session);
    }

    @OnClose
    public void close(Session session) {
        sessions.setRegistered(session, false);
        LOG.log(Level.INFO, "closed and unregistered {0}", session);
    }

    @OnError
    public void onError(Throwable error) {
        LOG.log(Level.SEVERE, "socket error", error);
    }

    /**
     * Echoes back the message
     * @param message
     * @param session 
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.log(Level.INFO, "received {0} for {1}", new Object[]{message, session});
        //horrible and lazy hack for callback
        session.getAsyncRemote().sendText(message, sessions);
    }

}
