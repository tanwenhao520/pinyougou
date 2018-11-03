package com.tan;

import org.csource.fastdfs.*;
import org.junit.Test;

public class FastDFS_Test {
    @Test
    public  void test1() throws Exception{
        String conf_class = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();

        ClientGlobal.init(conf_class);

        TrackerClient tc = new TrackerClient();

        TrackerServer ts = tc.getConnection();

        StorageServer ss = null;

        StorageClient storageClient = new StorageClient(ts,ss);

        String[] jpgs = storageClient.upload_file("E:\\111.jpg", "jpg", null);

        if(jpgs.length > 0 && jpgs != null){
            for (String jpg : jpgs) {
                System.out.println(jpg);
            }
        }

        String groupName = jpgs[0];

        String filename = jpgs[1];

        ServerInfo[] fetchStorage = tc.getFetchStorages(ts, groupName, filename);
        for (ServerInfo serverInfo : fetchStorage) {
            System.out.println("ip="+serverInfo.getIpAddr()+"   端口="+serverInfo.getPort());
        }

        String url = "http://" + fetchStorage[0].getIpAddr() + "/" +groupName + "/" + filename;

    }
}
