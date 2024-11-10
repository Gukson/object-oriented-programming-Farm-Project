module machine {
    requires farm;
    requires java.desktop;
    requires transitive java.rmi;
    exports io.github.gukson.lab07.machine.context;
    exports io.github.gukson.lab07.machine.gui;
}