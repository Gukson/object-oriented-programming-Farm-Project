package io.github.gukson.lab07.machine.context;

import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.edu.pwr.tkubik.jp.farm.api.Action;
import pl.edu.pwr.tkubik.jp.farm.api.ICallback;
import pl.edu.pwr.tkubik.jp.farm.api.IWorld;
import pl.edu.pwr.tkubik.jp.farm.api.Role;

public class Machine implements ICallback {
    private final Role role;
    public final Integer id;
    private final IWorld iw;
    public List<Integer> ages;
    private int port;
    private Boolean isRegistered = true;
    private Registry registry;

    public Machine(int port, Role role) throws RemoteException, NotBoundException {
        this.port = port;
        this.role = role;
        registry = LocateRegistry.getRegistry("localhost", 5001);
        this.iw = (IWorld) registry.lookup("World");
        this.id = iw.register((ICallback)UnicastRemoteObject.exportObject(this,port), role);
        this.ages = new ArrayList<>();
    }

    private void calculateWhatToHarvest(){
        System.out.println(ages + " before calculating");
        List<Integer> temp = new ArrayList<>();
        for(int x = 0; x < ages.size(); x++){
            if(ages.get(x) == 10){
                temp.add(x);
            }
        }
        ages = temp;
        System.out.println(ages + " after calculating");
    }

    public void run(){
        isRegistered = true;
        while (isRegistered){
            try {
                move();
                if(role == Role.HARVESTER){
                    calculateWhatToHarvest();
                    harvest();
                }
                else {
                    seed();
                }
                TimeUnit.MILLISECONDS.sleep(350);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        }
        try{
            UnicastRemoteObject.unexportObject(registry, true);
        }catch (NoSuchObjectException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void response(Action action, List<Integer> list) throws RemoteException {
        System.out.println(this.role + " " + this.id + " action: "+ action);
        System.out.println(this.role + " " + this.id + " data: "+ list);

        if(action == Action.MOVE){
            ages = list;
        }

    }

    public void unregister() throws RemoteException {
        var isRegistered = iw.unregister(id);
        System.out.println(this + " " + isRegistered);
    }

    public void move() throws RemoteException {
        iw.move(this.id);
    }

    public void seed() throws RemoteException {
        iw.seed(this.id);
    }

    public void harvest() throws RemoteException {
        iw.harvest(this.id, ages);
    }

    public Role getRole() {
        return role;
    }

    public void setAges(List<Integer> ages) {
        this.ages = ages;
    }

    public int getPort() {
        return port;
    }
}
