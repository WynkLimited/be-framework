package com.airtel.helper.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.airtel.helper.common.BaseTestClass;

/**
 * Created by b0200202 on 12/6/17.
 */
public class AerospikeHelper extends BaseTestClass {

	public static AerospikeClient getAeroSpikeClientForMultipleNodes(List<String> serverIpList) {
		ClientPolicy policy = new ClientPolicy();
		Host[] hosts = new Host[serverIpList.size()];
		for (int i = 0; i < hosts.length; i++)
			hosts[i] = new Host(serverIpList.get(i), 3000);
		aeroSpikeClient = new AerospikeClient(policy, hosts);
		return aeroSpikeClient;

	}

	public static AerospikeClient getAeroSpikeClientForSingleNode(String serverIp) {
		aeroSpikeClient = new AerospikeClient(serverIp, 3000);
		return aeroSpikeClient;
	}

	public static Record[] getMultipleRecordFromPKList(AerospikeClient client, String nameSpace, String set,
			List<String> pkList, String binName) {

		Key[] keys = new Key[pkList.size()];
		for (int i = 0; i < pkList.size(); i++) {
			keys[i] = new Key(nameSpace, set, pkList.get(i));
		}
		BatchPolicy policy = new BatchPolicy();
		Record[] records = client.get(policy, keys, binName);
		return records;

	}

	public static Record getRecordForAkey(AerospikeClient client, String namespace, String setName, String value,
			String binName) {

		Key key = new Key(namespace, setName, value);
		Record record = client.get(null, key, binName);
		return record;
	}

	public static void writeRecordForAKey(AerospikeClient client, String namespace, String setName, String value,
			List<Bin> binList) {
		WritePolicy writePolicy = new WritePolicy();
		Key key = new Key(namespace, setName, value);

		if (!binList.isEmpty())
			client.put(writePolicy, key, binList.toArray(new Bin[] {}));

	}

	public static Boolean deleteRecordForAkey(AerospikeClient client, String namespace, String setName, String value) {

		Key key = new Key(namespace, setName, value);
		WritePolicy writePolicy = new WritePolicy();

		boolean deleteStatus = client.delete(writePolicy, key);
		return deleteStatus;

	}

	public static List<Map<String, Object>> parseAerospikeRecordForaBin(Record[] record) {

		List<Map<String, Object>> hashMapListOfRecord = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < record.length; i++) {
			hashMapListOfRecord.add(record[i].bins);
		}

		return hashMapListOfRecord;

	}

	public static List<Map<String, Object>> parseMultipleRecord(Record[] record) {

		List<Map<String, Object>> queryData = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < record.length; i++) {
			if (record[i] != null)
				queryData.add(record[i].bins);
		}

		return queryData;
	}

	public static Map<String, Object> parseSingleRecord(Record record) {
		if (record != null)
			return record.bins;
		else
			return null;
	}

	public static Record getAllBinsForAKey(AerospikeClient client, String namespace, String setName, String value) {
		Key key = new Key(namespace, setName, value);
		Record record = client.get(null, key);
		return record;
	}

}
