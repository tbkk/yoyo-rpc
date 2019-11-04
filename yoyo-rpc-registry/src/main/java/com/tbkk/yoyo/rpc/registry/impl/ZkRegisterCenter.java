package com.tbkk.yoyo.rpc.registry.impl;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import com.tbkk.yoyo.rpc.registry.RegisterCenter;

import com.tbkk.yoyo.rpc.registry.utils.YoyoZkClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * service register for "zookeeper"
 *
 *  /yoyo-rpc/dev/
 *              - key01(service01)
 *                  - value01 (ip:port01)
 *                  - value02 (ip:port02)
 * @author tbkk
 */
@Slf4j
public class ZkRegisterCenter extends RegisterCenter {
    // param
    public static final String ENV = "env";
    // zk register address, like "ip1:port,ip2:port,ip3:port"
    public static final String ZK_ADDRESS = "zkaddress";
    // zk register digest
    public static final String ZK_DIGEST = "zkdigest";
    private static final String zkBasePath = "/yoyo-rpc";


    private String zkEnvPath;

    @Getter
    private YoyoZkClient yoyoZkClient = null;

    @Getter
    private volatile boolean refreshThreadStop = false;

    private Thread refreshThread = null;

    @Getter
    private volatile ConcurrentMap<String, TreeSet<String>> registryData = new ConcurrentHashMap<>();
    @Getter
    private volatile ConcurrentMap<String, TreeSet<String>> discoveryData = new ConcurrentHashMap<>();


    /**
     * key 2 path
     * @param   nodeKey
     * @return  znodePath
     */
    public String keyToFullPath(String nodeKey){
        return zkBasePath.concat("/").concat(nodeKey);
        //return zkEnvPath + "/" + nodeKey;
    }

    /**
     * path 2 key
     * @param   nodePath
     * @return  nodeKey
     */
    public String fullPathToKey(String nodePath){
        if (StringUtils.startsWith(nodePath, zkEnvPath))
        {
            return nodePath.substring(zkEnvPath.length()+1);
        }
        return null;
    }

    // ------------------------------ util ------------------------------


    /**
     * @param param
     *      Environment.ZK_ADDRESS  ：zk address
     *      Environment.ZK_DIGEST   ：zk didest
     *      Environment.ENV         ：env
     */
    @Override
    public void start(Map<String, String> param) {
        String zkAddress = param.get(ZK_ADDRESS);
        String zkDigest = param.get(ZK_DIGEST);
        String env = param.get(ENV);

        if (zkAddress==null || zkAddress.trim().length()==0) {
            throw new YoyoRpcException("zkaddress can not be empty");
        }

        if (env==null || env.trim().length()==0) {
            throw new YoyoRpcException("env can not be empty");
        }

        start(zkAddress,zkDigest,env);
    }

    private void start(String zkAddress, String zkDigest, String env)
    {

        zkEnvPath = zkBasePath.concat("/").concat(env);
        yoyoZkClient = new YoyoZkClient(zkAddress, zkEnvPath, zkDigest, new ZkRegisterCenterWatcher(this));
        yoyoZkClient.initClient();
        refreshThread = new ZkRegisterCenterRefreshThread(this);
        refreshThread.setName("ZkRegisterCenterRefreshThread");
        refreshThread.setDaemon(true);
        refreshThread.start();
        log.info("yoyo-rpc, ZkRegisterCenter init success. [env={}]", env);
    }

    @Override
    public void stop() {
        if (yoyoZkClient !=null) {
            yoyoZkClient.destroy();
        }
        if (refreshThread != null) {
            refreshThreadStop = true;
            refreshThread.interrupt();
        }
    }

    /**
     * refresh discovery data, and cache
     *
     * @param key
     */
    public void refreshDiscoveryData(String key){

        Set<String> keys = new HashSet<>();
        if (key!=null && key.trim().length()>0) {
            keys.add(key);
        } else {
            if (discoveryData.size() > 0) {
                keys.addAll(discoveryData.keySet());
            }
        }

        if (keys.size() > 0) {
            for (String keyItem: keys) {

                // add-values
                String path = keyToFullPath(keyItem);
                Map<String, String> childPathData = yoyoZkClient.getChildPathData(path);

                // exist-values
                TreeSet<String> existValues = discoveryData.get(keyItem);
                if (existValues == null) {
                    existValues = new TreeSet<>();
                    discoveryData.put(keyItem, existValues);
                }

                if (childPathData.size() > 0) {
                	existValues.clear();
                    existValues.addAll(childPathData.keySet());
                }
            }
            log.info("yoyo-rpc, refresh discovery data success, discoveryData = {}", discoveryData);
        }
    }

    /**
     * refresh register data
     */
    public void refreshRegistryData(){
        if (registryData.size() > 0) {
            for (Map.Entry<String, TreeSet<String>> item: registryData.entrySet()) {
                String key = item.getKey();
                for (String value:item.getValue()) {
                    // make path, child path
                    String path = keyToFullPath(key);
                    yoyoZkClient.setChildPathData(path, value, "");
                }
            }
            log.info("yoyo-rpc, refresh register data success, registryData = {}", registryData);
        }
    }

    @Override
    public boolean register(Set<String> keys, String value) {
        for (String key : keys) {
            // local cache
            TreeSet<String> values = registryData.get(key);
            if (values == null) {
                values = new TreeSet<>();
                registryData.put(key, values);
            }
            values.add(value);

            // make path, child path
            String path = keyToFullPath(key);
            yoyoZkClient.setChildPathData(path, value, "");
        }
        log.info("yoyo-rpc, register success, keys = {}, value = {}", keys, value);
        return true;
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        for (String key : keys) {
            TreeSet<String> values = discoveryData.get(key);
            if (values != null) {
                values.remove(value);
            }
            String path = keyToFullPath(key);
            yoyoZkClient.deleteChildPath(path, value);
        }
        log.info("yoyo-rpc, remove success, keys = {}, value = {}", keys, value);
        return true;
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        if (keys==null || keys.size()==0) {
            return null;
        }
        Map<String, TreeSet<String>> registryDataTmp = new HashMap<>();
        for (String key : keys) {
            TreeSet<String> valueSetTmp = discovery(key);
            if (valueSetTmp != null) {
                registryDataTmp.put(key, valueSetTmp);
            }
        }
        return registryDataTmp;
    }

    @Override
    public TreeSet<String> discovery(String key) {

        // local cache
        TreeSet<String> values = discoveryData.get(key);
        if (values == null) {

            // refreshDiscoveryData (one)：first use
            refreshDiscoveryData(key);

            values = discoveryData.get(key);
        }

        return values;
    }

}
