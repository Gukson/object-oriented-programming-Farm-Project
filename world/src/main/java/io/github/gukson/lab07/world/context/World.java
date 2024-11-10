package io.github.gukson.lab07.world.context;

import io.github.gukson.lab07.machine.context.Machine;
import pl.edu.pwr.tkubik.jp.farm.api.Action;
import pl.edu.pwr.tkubik.jp.farm.api.ICallback;
import pl.edu.pwr.tkubik.jp.farm.api.IWorld;
import pl.edu.pwr.tkubik.jp.farm.api.Role;
import io.github.gukson.lab07.world.gui.WorldGui;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class World extends UnicastRemoteObject implements IWorld {
    private final Field[][] fieldArea = new Field[5][5];
    private final ArrayList<ICallback> icList = new ArrayList<>();
    private final ArrayList<Information> machineInfo = new ArrayList<>();
    private WorldGui worldGui;
    private JPanel machinePanel;
    private int clientID = 0;
//    private List<Machine> listaMaszyn = new ArrayList<>();

    public World() throws RemoteException {
        setup();
        Registry r = LocateRegistry.createRegistry(5001);
        r.rebind("World", this);
        System.out.println("waiting");
    }

    @Override
    public int register(ICallback ic, Role role) throws RemoteException {
//        System.out.println("Client Registered");
        icList.add(ic);
        Information info = startPosition(role, ++clientID);
        assert info != null;
//        ic.response(Action.REGISTER, List.of(info.getId()));
        System.out.println("Registered " + role);
        machineInfo.add(info);
        return info.getId();
    }

    private Information startPosition(Role role, int id) {
        Set<Integer> kind = machineInfo.stream().filter(item -> item.getRola() == role).map(Information::getPosition).collect(Collectors.toSet());
        for (int i = 0; i < 5; i++) {
            if (!kind.contains(i)) {
                int y = 0;
                int x = i;
                String faceing = "down";
                if (role == Role.SEEDER) {
                    faceing = "right";
                    y = i;
                    x = 0;
                }
                return new Information(y, x, faceing, role, id, i, this.worldGui.newMachine(i, role));
            }
        }
        return null;
    }

    @Override
    public boolean unregister(int id) throws RemoteException {
        Information currInfo = machineInfo.stream().filter(item -> item.getId() == id).findFirst().get();
        machinePanel.remove(currInfo.getLabel());
        machineInfo.remove(currInfo);
        machinePanel.repaint();
        machinePanel.revalidate();
//        icList.get(id - 1).response(Action.UNREGISTER, List.of(1));
        return true;
    }

    @Override
    public void move(int id) throws RemoteException {
        Information currInfo = machineInfo.stream().filter(item -> item.getId() == id).findFirst().get();
        newPosition(currInfo, machinePanel,machineInfo);
        icList.get(id-1).response(Action.MOVE, fieldArea[currInfo.getY()][currInfo.getX()].getPlantList());
    }

    @Override
    public void seed(int id) throws RemoteException {
        Information currInfo = machineInfo.stream().filter(item -> item.getId() == id).findFirst().get();
        Field requestedField;
        synchronized (fieldArea){
            requestedField = fieldArea[currInfo.getY()][currInfo.getX()];
        }
        if(requestedField.getSeedsQueue()[0] != null){
            for(int i = 0; i <4; i++){
                if(requestedField.getPlants()[i] == null){
                    requestedField.getPlants()[i] = new Plant(1,requestedField.getSeedsQueue()[0].getName());
                    requestedField.getSeedsQueue()[0] = null;
                    icList.get(id-1).response(Action.SEED,List.of(1));
                    break;
                }
            }
        }
        else {
            icList.get(id-1).response(Action.SEED,List.of(0));
        }
    }

    @Override
    public void harvest(int id, List<Integer> var2) throws RemoteException {
        Information currInfo = machineInfo.stream().filter(item -> item.getId() == id).findFirst().get();
        Field requestedField;
        synchronized (fieldArea){
            requestedField = fieldArea[currInfo.getY()][currInfo.getX()];
        }
        for(Integer i: var2){
            if(requestedField.getPlants()[i] != null && requestedField.getPlants()[i].getAge() == 10){
                requestedField.getPlants()[i] = null;
                worldGui.getFieldlabels()[currInfo.getY()][currInfo.getX()].getLabels()[i].setIcon(new ImageIcon("./world/src/main/resources/data/emptyfield.png"));
                icList.get(id-1).response(Action.HARVEST,List.of(1));
                return;
            }
        }
        icList.get(id-1).response(Action.HARVEST,List.of(0));

    }

    private void setup() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                fieldArea[y][x] = new Field(100, y, x);
            }
        }
        this.worldGui = new WorldGui(fieldArea);
        machinePanel = worldGui.getMachinePanel();
        worldGui.setVisible(true);
        Time time = new Time(fieldArea, worldGui.getFieldlabels(), worldGui.getContentPane());
        Thread timer = new Thread(time);
        timer.start();
    }

    public void newPosition(Information info, JPanel machinePane, ArrayList<Information> machineInfo) {
        switch (info.getFacing()) {
            case "up":
                if (info.getY() == 0 && Objects.equals(info.getFacing(), "up")) {
                    info.setFacing("down");
                    info.getLabel().setIcon(new ImageIcon("./world/src/main/resources/data/harvester-down.png"));
                } else if (isThisFieldEmpty(info.getX(), info.getY() - 1, machineInfo)) {
                    info.setY(info.getY() - 1);
                    for (int x = 0; x < 50; x += 1) {
                        info.getLabel().setLocation(info.getLabel().getX(), info.getLabel().getY() - 2);
                        machinePane.repaint();
                        machinePane.revalidate();
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                break;
            case "down":
                if (info.getY() == 4 && Objects.equals(info.getFacing(), "down")) {
                    info.setFacing("up");
                    info.getLabel().setIcon(new ImageIcon("./world/src/main/resources/data/harvester-up.png"));
                } else if (isThisFieldEmpty(info.getX(), info.getY() + 1, machineInfo)) {
                    info.setY(info.getY() + 1);
                    for (int x = 0; x < 50; x++) {
                        info.getLabel().setLocation(info.getLabel().getX(), info.getLabel().getY() + 2);
                        machinePane.repaint();
                        machinePane.revalidate();
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                break;
            case "left":
                if (info.getX() == 0 && Objects.equals(info.getFacing(), "left")) {
                    info.setFacing("right");
                    info.getLabel().setIcon(new ImageIcon("./world/src/main/resources/data/seeder-right.png"));
                } else if (isThisFieldEmpty(info.getX() - 1, info.getY(), machineInfo)) {
                    info.setX(info.getX() - 1);
                    for (int x = 0; x < 50; x++) {
                        info.getLabel().setLocation(info.getLabel().getX() - 2, info.getLabel().getY());
                        machinePane.repaint();
                        machinePane.revalidate();
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                break;
            case "right":
                if (info.getX() == 4 && Objects.equals(info.getFacing(), "right")) {
                    info.setFacing("left");
                    info.getLabel().setIcon(new ImageIcon("./world/src/main/resources/data/seeder-left.png"));
                } else if (isThisFieldEmpty(info.getX() + 1, info.getY(), machineInfo)) {
                    info.setX(info.getX() + 1);
                    for (int x = 0; x < 50; x++) {
                        info.getLabel().setLocation(info.getLabel().getX() + 2, info.getLabel().getY());
                        machinePane.repaint();
                        machinePane.revalidate();
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                break;
        }
    }

    public Boolean isThisFieldEmpty(Integer x, Integer y, ArrayList<Information> machineInfo) {
        if (machineInfo.stream().anyMatch(item -> (Objects.equals(item.getX(), x) && Objects.equals(item.getY(), y)))) {
            return false;
        }
        return true;
    }
}
