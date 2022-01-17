package linda.server;

import java.rmi.RemoteException;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.RemoteCallback;
import linda.Tuple;
import linda.server.infrastructure.Client;
import linda.server.log.LogLevel;
import linda.server.log.Logger;

/**
 * Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it
 * is connected to.
 */
public class LindaClient extends Client implements Linda {

    /**
     * Initializes the Linda implementation.
     * 
     * @param serverURI the URI of the server, e.g.
     *                  "rmi://localhost:4000/LindaServer" or
     *                  "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {
        super(serverURI);
    }

    @Override
    public void write(Tuple t) {
        try {
            this.server.write(t);
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Tuple take(Tuple template) {
        try {
            return this.server.take(template);
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Tuple read(Tuple template) {
        try {
            return this.server.read(template);
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Tuple tryTake(Tuple template) {
        try {
            return this.server.tryTake(template);
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Tuple tryRead(Tuple template) {
        try {
            return this.server.tryRead(template);
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        try {
            return this.server.takeAll(template);
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        try {
            return this.server.readAll(template);
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        RemoteCallback rcb;
        try {
            rcb = new RemoteCallback(callback);
            this.server.eventRegister(mode, timing, template, rcb);
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void debug(String prefix) {
        try {
            System.out.printf(this.server.fetchDebug(prefix));
        } catch (RemoteException e) {
            Logger.log(e.getMessage().toString(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

}
