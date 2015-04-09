package test.ethereum.db;

import org.ethereum.datasource.KeyValueDataSource;
import org.ethereum.db.ByteArrayWrapper;

import org.iq80.leveldb.DBException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// this class is so completely not thread-safe
public class MockDB implements KeyValueDataSource {

    Map<ByteArrayWrapper, byte[]> storage = new HashMap<>();


    @Override
    public void delete(byte[] arg0) throws DBException {
        storage.remove(arg0);
    }


    @Override
    public byte[] get(byte[] arg0) throws DBException {
        return storage.get(new ByteArrayWrapper(arg0));
    }


    @Override
    public byte[] put(byte[] key, byte[] value) throws DBException {
        return storage.put(new ByteArrayWrapper(key), value);
    }

    /**
     * Returns the number of items added to this Mock DB
     *
     * @return int
     */
    public int getAddedItems() {
        return storage.size();
    }

    @Override
    public void init() {

    }

    @Override
    public void setName(String name) {

    }

    @Override
    public Set<byte[]> keys() {
	Set<byte[]> out = new HashSet<>();
	for( ByteArrayWrapper wrapper : storage.keySet() ) 
	    out.add( wrapper.getData() );
	return out;
    }

    @Override
    public void updateBatch(Map<byte[], byte[]> rows) {
	for( Map.Entry<byte[],byte[]> entry : rows.entrySet() )
	    this.put( entry.getKey(), entry.getValue() );
    }

    @Override
    public void close() {

    }
}
