package kr.kuvh.linebot.altcoin.bot.service;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Properties;

@Service
public class BitcoinRPCService {

    @Autowired
    private Properties nodeConfig;

    private BtcdClient client;

    @PostConstruct
    private void init() throws Exception {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm).build();

        client = new BtcdClientImpl(httpProvider, nodeConfig);
    }

    public BtcdClient getClient() {
        return this.client;
    }

}
