package io.github.gukson.lab07.world.context;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private Integer freeSpace;
    private Integer occupiedSpace;
    private Plant[] plants; //synchronized?
    private Plant[] seedsQueue;
    private Integer x, y;


    public Field(Integer freeSpace, Integer y, Integer x) {
        this.x = x;
        this.y = y;
        this.freeSpace = freeSpace;
        this.occupiedSpace = 0;
        this.plants = new Plant[4];
        this.seedsQueue = new Plant[1];
    }

    public List<Integer> getPlantList() {
        List<Integer> list = new ArrayList<>();
        for (int x = 0; x < 4; x++) {
            if(plants[x] == null){
                list.add(0);
            }
            else{
                list.add(plants[x].getAge());
            }
        }
        return list;
    }

    public void setPlantInQueue(String name) {
        seedsQueue[0] = new Plant(1, name);
    }

    public Integer getFreeSpace() {
        return freeSpace;
    }

    public Integer getOccupiedSpace() {
        return occupiedSpace;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Plant[] getPlants() {
        return plants;
    }


    public void setFreeSpace(Integer freeSpace) {
        this.freeSpace = freeSpace;
    }

    public Plant[] getSeedsQueue() {
        return seedsQueue;
    }

    public void setOccupiedSpace(Integer occupiedSpace) {
        this.occupiedSpace = occupiedSpace;
    }

    public void setPlants(Plant[] plants) {
        this.plants = plants;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
