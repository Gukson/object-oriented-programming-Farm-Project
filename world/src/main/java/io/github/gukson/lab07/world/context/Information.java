package io.github.gukson.lab07.world.context;

import pl.edu.pwr.tkubik.jp.farm.api.Action;
import pl.edu.pwr.tkubik.jp.farm.api.ICallback;
import pl.edu.pwr.tkubik.jp.farm.api.IWorld;
import pl.edu.pwr.tkubik.jp.farm.api.Role;

import javax.swing.*;

public class Information {

    private Integer x;
    private Integer y;
    private String facing;
    private Role rola;
    private Integer id;
    private Integer position;
    private JLabel label;
    public Information(Integer y, Integer x, String facing, Role rola, Integer id, Integer position, JLabel label ) {
        this.x = x;
        this.y = y;
        this.facing = facing;
        this.rola = rola;
        this.id = id;
        this.position = position;
        this.label = label;

    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public String getFacing() {
        return facing;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public Role getRola() {
        return rola;
    }

    public void setRola(Role rola) {
        this.rola = rola;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }
}
