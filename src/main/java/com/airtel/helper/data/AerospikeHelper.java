package com.airtel.helper.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aerospike.client.*;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.airtel.helper.Interfaces.IAeroSpikeHelper;
import com.airtel.helper.common.BaseTestClass;
import com.airtel.helper.report.ReportHelper;

/**
 * Created by b0200202 on 12/6/17.
 */
public class AerospikeHelper extends BaseTestClass implements IAeroSpikeHelper {

    ////Port = 3000
    public AerospikeClient getAeroSpikeClientForMultipleNodes(List<String> serverIpList,int port) {
        ClientPolicy policy = new ClientPolicy();
        Host[] hosts = new Host[serverIpList.size()];
        for (int i = 0; i < hosts.length; i++)
            hosts[i] = new Host(serverIpList.get(i), port);
        aeroSpikeClient = new AerospikeClient(policy, hosts);
        return aeroSpikeClient;

    }

    ///port : 30000
    public AerospikeClient getAeroSpikeClientForSingleNode(String serverIp,int port) {
        aeroSpikeClient = new AerospikeClient(serverIp, port);
        return aeroSpikeClient;
    }

    public Record[] getMultipleRecordFromPKList(AerospikeClient client, String nameSpace, String set,
                                                List<String> pkList, String binName) {

        Key[] keys = new Key[pkList.size()];
        for (int i = 0; i < pkList.size(); i++) {
            keys[i] = new Key(nameSpace, set, pkList.get(i));
        }
        BatchPolicy policy = new BatchPolicy();
        Record[] records = client.get(policy, keys, binName);
        return records;

    }

    public Record getRecordForAkey(AerospikeClient client, String namespace, String setName, String value,
                                   String binName) {

        Key key = new Key(namespace, setName, value);
        Record record = client.get(null, key, binName);
        return record;
    }

    public void writeRecordForAKey(AerospikeClient client, String namespace, String setName, String value,
                                   List<Bin> binList) {
        WritePolicy writePolicy = new WritePolicy();
        Key key = new Key(namespace, setName, value);

        if (!binList.isEmpty())
            client.put(writePolicy, key, binList.toArray(new Bin[]{}));

    }

    public Boolean deleteRecordForAkey(AerospikeClient client, String namespace, String setName, String value) {

        Key key = new Key(namespace, setName, value);
        WritePolicy writePolicy = new WritePolicy();

        boolean deleteStatus = client.delete(writePolicy, key);
        return deleteStatus;

    }

    public List<Map<String, Object>> parseAerospikeRecordForaBin(Record[] record) {

        List<Map<String, Object>> hashMapListOfRecord = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < record.length; i++) {
            hashMapListOfRecord.add(record[i].bins);
        }

        return hashMapListOfRecord;

    }

    public List<Map<String, Object>> parseMultipleRecord(Record[] record) {

        List<Map<String, Object>> queryData = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < record.length; i++) {
            if (record[i] != null)
                queryData.add(record[i].bins);
        }

        return queryData;
    }

    public Map<String, Object> parseSingleRecord(Record record) {
        if (record != null)
            return record.bins;
        else
            return null;
    }

    public Record getAllBinsForAKey(AerospikeClient client, String namespace, String setName, String value) {
        Key key = new Key(namespace, setName, value);
        Record record = client.get(null, key);
        return record;
    }

}
