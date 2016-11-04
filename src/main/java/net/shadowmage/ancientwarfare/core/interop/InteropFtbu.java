package net.shadowmage.ancientwarfare.core.interop;

import java.util.UUID;

import ftb.utils.api.FriendsAPI;

public class InteropFtbu implements InteropFtbuInterface {

    @Override
    public boolean areFriends(String player1, String player2) {
        if (FriendsAPI.areFriends(player1, player2))
            return true;
        return false;
    }

}
