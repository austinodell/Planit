package edu.villanvoa.together;

/**
 * Created by aodell on 3/12/15.
 */
public class Friend {
    public final String id;
    public final String name;
    public final boolean isReal;

    Friend(String id, String name) {
        this.id = id;
        this.name = name;
        isReal = true;
    }

    Friend(String text) {
        id = "0";
        name = text;
        isReal = false;
    }
}
