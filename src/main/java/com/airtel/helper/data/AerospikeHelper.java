package com.airtel.helper.data;

import com.aerospike.client.*;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.ClientPolicy;

import java.util.*;

/**
 * Created by b0200202 on 12/6/17.
 */
public class AerospikeHelper {


    public static AerospikeClient getAeroSpikeClientForMultipleNodes(List<String> serverIpList)
    {
        ClientPolicy policy = new ClientPolicy();

        Host [] hosts= new Host[serverIpList.size()];
        for (int i =0 ;i<hosts.length;i++)
            hosts[i]= new Host(serverIpList.get(i),3000);
        return  new AerospikeClient(policy,hosts);

    }

    public  static AerospikeClient getAeroSpikeClientForSingleNode(String serverIp)
    {
        AerospikeClient aerospikeClient = new AerospikeClient(serverIp,3000);

        return  aerospikeClient;
    }



    public  static  Record [] getMultipleRecordFromPKList(AerospikeClient client,String nameSpace,String set,List<String> pkList,String binName)
    {

        Key [] keys = new Key[pkList.size()];
        for (int i =0;i<pkList.size();i++)
        {
            keys[i]=new Key(nameSpace,set,pkList.get(i));
        }
        BatchPolicy policy = new BatchPolicy();
        Record[] records = client.get(policy, keys,binName);
        System.out.println(records);

        return  records;

    }

    public static Record getRecordForAkey(AerospikeClient client,String namespace,String setName,String value ,String binName)
    {

        Key key = new Key(namespace,setName,value);
        Record record = client.get(null,key,binName);
        return  record;
    }

    public static List <Map<String,Object>> parseAerospikeRecordForaBin(Record []record)
    {

        List<Map<String,Object>> hashMapListOfRecord = new ArrayList<Map<String,Object>>() ;
        for (int i =0 ;i<record.length;i++)
        {
            hashMapListOfRecord.add(record[i].bins);
        }


        return  hashMapListOfRecord;

    }

    public  static List<Map<String,Object>> parseMultipleRecord(Record [] record)
    {

        List<Map<String,Object>> queryData =new ArrayList<Map<String, Object>>();

        for (int i=0 ;i <record.length;i++)
        {
            if (record[i]!=null)
            queryData.add(record[i].bins);
        }

        return  queryData;
    }

    public static  Map<String,Object> parseSingleRecord(Record record)
    {
        if (record !=null)
            return  record.bins;
        else
            return  null;
    }



}
