package linda.server;

import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.infrastructure.Client;

/**
 * Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it
 * is connected to.
 */
public class LindaClient implements Linda, Client {
    LindaServer server;

    /**
     * Initializes the Linda implementation.
     * 
     * @param serverURI the URI of the server, e.g.
     *                  "rmi://localhost:4000/LindaServer" or
     *                  "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {
        try {
            this.server = (LindaServer) java.rmi.Naming.lookup(serverURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Tuple t) {
        // TODO Auto-generated method stub

    }

    @Override
    public Tuple take(Tuple template) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tuple read(Tuple template) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tuple tryTake(Tuple template) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tuple tryRead(Tuple template) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void debug(String prefix) {
        // TODO Auto-generated method stub

    }

    // TO BE COMPLETED

}
