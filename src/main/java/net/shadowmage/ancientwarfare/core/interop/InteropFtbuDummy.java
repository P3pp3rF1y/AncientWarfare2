package net.shadowmage.ancientwarfare.core.interop;

import java.util.UUID;

public class InteropFtbuDummy implements InteropFtbuInterface {

    @Override
    public boolean areFriends(String player1, String player2) {
        return false;
    }

}
