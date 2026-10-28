package com.airtel.helper.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.airtel.helper.Interfaces.IAeroSpikeHelper;

/**
 * Aerospike helper using the current Java client APIs.
 */
public class AerospikeHelper implements IAeroSpikeHelper {

	private static final Logger log = LoggerFactory.getLogger(AerospikeHelper.class);

	private volatile AerospikeClient aeroSpikeClient;

	@Override
	public AerospikeClient getAeroSpikeClientForMultipleNodes(List<String> serverIpList, int port) {
		Objects.requireNonNull(serverIpList, "serverIpList");
		ClientPolicy policy = new ClientPolicy();
		Host[] hosts = serverIpList.stream().map(ip -> new Host(ip, port)).toArray(Host[]::new);
		aeroSpikeClient = new AerospikeClient(policy, hosts);
		log.info("Aerospike multi-node client created for {} hosts on port {}", hosts.length, port);
		return aeroSpikeClient;
	}

	@Override
	public AerospikeClient getAeroSpikeClientForSingleNode(String serverIp, int port) {
		aeroSpikeClient = new AerospikeClient(serverIp, port);
		log.info("Aerospike client created for {}:{}", serverIp, port);
		return aeroSpikeClient;
	}

	@Override
	public Record[] getMultipleRecordFromPKList(AerospikeClient client, String nameSpace, String set, List<String> pkList,
			String binName) {
		Objects.requireNonNull(client, "client");
		Objects.requireNonNull(pkList, "pkList");
		Key[] keys = pkList.stream().map(pk -> new Key(nameSpace, set, pk)).toArray(Key[]::new);
		return client.get(new BatchPolicy(), keys, binName);
	}

	@Override
	public Record getRecordForAkey(AerospikeClient client, String namespace, String setName, String value,
			String binName) {
		Objects.requireNonNull(client, "client");
		return client.get(null, new Key(namespace, setName, value), binName);
	}

	@Override
	public void writeRecordForAKey(AerospikeClient client, String namespace, String setName, String value,
			List<Bin> binList) {
		Objects.requireNonNull(client, "client");
		if (binList == null || binList.isEmpty()) {
			return;
		}
		client.put(new WritePolicy(), new Key(namespace, setName, value), binList.toArray(Bin[]::new));
	}

	@Override
	public Boolean deleteRecordForAkey(AerospikeClient client, String namespace, String setName, String value) {
		Objects.requireNonNull(client, "client");
		return client.delete(new WritePolicy(), new Key(namespace, setName, value));
	}

	@Override
	public List<Map<String, Object>> parseAerospikeRecordForaBin(Record[] records) {
		return extractBins(records, false);
	}

	@Override
	public List<Map<String, Object>> parseMultipleRecord(Record[] records) {
		return extractBins(records, true);
	}

	@Override
	public Map<String, Object> parseSingleRecord(Record record) {
		return record != null ? record.bins : null;
	}

	@Override
	public Record getAllBinsForAKey(AerospikeClient client, String namespace, String setName, String value) {
		Objects.requireNonNull(client, "client");
		return client.get(null, new Key(namespace, setName, value));
	}

	private static List<Map<String, Object>> extractBins(Record[] records, boolean skipNulls) {
		if (records == null || records.length == 0) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> result = new ArrayList<>(records.length);
		for (Record record : records) {
			if (record == null) {
				if (!skipNulls) {
					result.add(null);
				}
				continue;
			}
			result.add(record.bins);
		}
		return result;
	}
}
