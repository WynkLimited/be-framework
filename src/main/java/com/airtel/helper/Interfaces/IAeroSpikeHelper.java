package com.airtel.helper.Interfaces;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Record;

import java.util.List;
import java.util.Map;

public interface IAeroSpikeHelper {

    AerospikeClient getAeroSpikeClientForMultipleNodes(List<String> serverIpList, int port);

    AerospikeClient getAeroSpikeClientForSingleNode(String serverIp, int port);

    Record[] getMultipleRecordFromPKList(AerospikeClient client, String nameSpace, String set,
                                         List<String> pkList, String binName);

    Record getRecordForAkey(AerospikeClient client, String namespace, String setName, String value,
                            String binName);

    void writeRecordForAKey(AerospikeClient client, String namespace, String setName, String value,
                            List<Bin> binList);

    Boolean deleteRecordForAkey(AerospikeClient client, String namespace, String setName, String value);

    List<Map<String, Object>> parseAerospikeRecordForaBin(Record[] record);

    List<Map<String, Object>> parseMultipleRecord(Record[] record);

    Map<String, Object> parseSingleRecord(Record record);

    Record getAllBinsForAKey(AerospikeClient client, String namespace, String setName, String value);

}
