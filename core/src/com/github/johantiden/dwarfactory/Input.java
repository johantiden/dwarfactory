package com.github.johantiden.dwarfactory;

import java.awt.event.KeyEvent;

public class Input {

    boolean wDown = false;
    boolean aDown = false;
    boolean sDown = false;
    boolean dDown = false;
    boolean qDown = false;
    boolean eDown = false;

    public void onKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyChar()) {
            case 'w': wDown = true; return;
            case 'a': aDown = true; return;
            case 's': sDown = true; return;
            case 'd': dDown = true; return;
            case 'q': qDown = true; return;
            case 'e': eDown = true; return;
        }
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        switch (keyEvent.getKeyChar()) {
            case 'w': wDown = false; return;
            case 'a': aDown = false; return;
            case 's': sDown = false; return;
            case 'd': dDown = false; return;
            case 'q': qDown = false; return;
            case 'e': eDown = false; return;
        }
    }

    public void onKeyTyped(KeyEvent keyEvent) {
    }
}
