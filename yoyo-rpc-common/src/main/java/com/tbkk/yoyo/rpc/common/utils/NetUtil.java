package com.tbkk.yoyo.rpc.common.utils;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * net util
 *
 * @author tbkk 2017-11-29 17:00:25
 */
@Slf4j
public class NetUtil {
    /**
     * find avaliable port
     *
     * @param defaultPort
     * @return
     */
    private static int MAX_PORT_COUNT = 65535;

    public static int findAvailablePort(int defaultPort) {
        int portTmp = defaultPort;
        while (portTmp < MAX_PORT_COUNT) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp++;
            }
        }
        portTmp = defaultPort--;
        while (portTmp > 0) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp--;
            }
        }
        throw new YoyoRpcException("no available port.");
    }

    /**
     * check port used
     *
     * @param port
     * @return
     */
    public static boolean isPortUsed(int port) {
        boolean used = false;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            used = false;
        } catch (IOException e) {
            log.info(" yoyo-rpc, port[{}] is in use.", port);
            used = true;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.info("");
                }
            }
        }
        return used;
    }

}
