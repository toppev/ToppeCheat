package com.toppecraft.toppecheat.violations;

import com.toppecraft.toppecheat.checksystem.CheckType;

import java.util.HashMap;

/**
 * Store for violation levels.
 *
 * @author Toppe5
 * @since 2.0
 */
public class ViolationLevelStore {

    private HashMap<CheckType, Violation> store = new HashMap<CheckType, Violation>();

    public HashMap<CheckType, Violation> getStore() {
        return store;
    }
}
