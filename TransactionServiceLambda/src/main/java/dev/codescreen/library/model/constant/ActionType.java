package dev.codescreen.library.model.constant;

import javax.swing.*;

public enum ActionType {
    PING("ping"),
    AUTHORIZATION("authorization"),
    LOAD("load");
    public final String actionName;
    private ActionType(String actionName) {
        this.actionName = actionName;
    }
}
