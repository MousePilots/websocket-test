/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mousepilots.ws.test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;

/**
 *
 * @author jgeenen
 */
@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class Sessions implements SendHandler{
    
    private static final Logger LOG = Logger.getLogger(Sessions.class.getName());
    
    private final Set<Session> sessions = new HashSet<>();
    
    public void setRegistered(Session session, boolean registered){
        synchronized(sessions){
            if(registered){
                this.sessions.add(session);
            } else {
                this.sessions.remove(session);
            }
        }        
    }
    
    @Schedule(hour = "*", minute = "*", second = "*", persistent = false)
    public void sendCurrentTime(){
        final String message = "the current time is " + LocalDateTime.now();
        synchronized(sessions){
            sessions.forEach( s -> s.getAsyncRemote().sendText(message, this));
        }
    }
    
    @Override
    public void onResult(SendResult result) {
        if(!result.isOK()){
            LOG.log(Level.SEVERE, "error sending message", result.getException());
        }
    }    
    
    
}
