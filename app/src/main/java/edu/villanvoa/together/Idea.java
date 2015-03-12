package edu.villanvoa.together;

/**
 * Created by aodell on 3/12/15.
 */
public class Idea {
    private String name;
    private String loc;
    private String desc;
    private int id;

    public Idea(String name) {
        this.name = name;
        this.id = 1;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return this.name;
    }

    public String getLoc() {
        return this.loc;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getId() {
        return this.id;
    }
}
