package org.ethereum.datasource;

import org.ethereum.db.ByteArrayWrapper;
import org.iq80.leveldb.DBException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashMapDB implements KeyValueDataSource {

    Map<ByteArrayWrapper, byte[]> storage = new HashMap<>();


    @Override
    public synchronized void delete(byte[] arg0) throws DBException {
        storage.remove(new ByteArrayWrapper(arg0));
    }


    @Override
    public synchronized byte[] get(byte[] arg0) throws DBException {
        return storage.get(new ByteArrayWrapper(arg0));
    }


    @Override
    public synchronized void put(byte[] key, byte[] value) throws DBException {
        storage.put(new ByteArrayWrapper(key), value);
    }

    /**
     * Returns the number of items added to this Mock DB
     *
     * @return int
     */
    public synchronized int getAddedItems() {
        return storage.size();
    }

    @Override
    public synchronized void init() {
    }

    @Override
    public synchronized void setName(String name) {
    }

    @Override
    public Set<byte[]> keys() {
	Set<ByteArrayWrapper> wrappers = storage.keySet();
	Set<byte[]> out = new HashSet<>();
	for ( ByteArrayWrapper wrapper : wrappers )
	    out.add( wrapper.getData() );
	return out;
    }

    @Override
    public void updateBatch(Map<byte[], byte[]> rows) {
	for ( Map.Entry<byte[],byte[]> entry : rows.entrySet() )
	    this.put( entry.getKey(), entry.getValue() );
    }

    @Override
    public void close() {

    }
}
