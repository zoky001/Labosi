/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author grupa_1
 */
@ServerEndpoint("/parkiraliste")
public class ParkiralisteEndpoint {

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void onOpen(Session peer) {
        peers.add(peer);
    }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }

    @OnMessage
    public String onMessage(String message) {
        return null;
    }

    public static void obavijestiPromjenu(String poruka) {
        for (Session peer : peers) {
            try {
                peer.getBasicRemote().sendText(poruka);
            } catch (IOException ex) {
                Logger.getLogger(ParkiralisteEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
