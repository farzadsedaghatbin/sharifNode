package com.isc.npsd.sharif.node.services;

import com.isc.npsd.common.util.EncryptUtil;
import com.isc.npsd.common.util.JAXBUtil;
import com.isc.npsd.common.util.JsonUtil;
import com.isc.npsd.common.util.redis.CallbackPipelineMethod;
import com.isc.npsd.common.util.redis.RedisUtil;
import com.isc.npsd.sharif.node.adapter.SharedObjectsContainer;
import com.isc.npsd.sharif.node.entities.File;
import com.isc.npsd.sharif.node.entities.FileStatus;
import com.isc.npsd.sharif.node.entities.FileType;
import com.isc.npsd.sharif.node.entities.schemaobjects.trx.TXRList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import redis.clients.jedis.Pipeline;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by A_Jankeh on 2/26/2017.
 */
@Service
@Scope("prototype")
public class FileProcessor {

    @Autowired
    private FileService fileService;

    public void process(Long fileId) {
        File file = fileService.findById(fileId);
        byte[] decryptedContent = EncryptUtil.decryptAES(file.getContent());
        if (decryptedContent == null || decryptedContent.length == 0)
            file.setFileStatus(FileStatus.REJECTED);
        else {
            String xml = new String(decryptedContent, StandardCharsets.UTF_8).trim();
            try {
                TXRList txrList = (TXRList) JAXBUtil.XmlToObject(xml, FileType.TRANSACTION.getXSDSchema(), FileType.TRANSACTION.getSchemaContext());
                List<TXRList.TXR> transactions = txrList.getTXR();
                System.out.println(">>>>>>>>>>>>> FILE Process : SIZE : " + transactions.size());
                RedisUtil redisUtil = SharedObjectsContainer.getRedisUtil();
                redisUtil.executePipeline(new CallbackPipelineMethod() {
                    @Override
                    public void onExecution(Pipeline pipeline) {
                        transactions.forEach(transaction -> {
                            try {
                                redisUtil.addItemToSet(pipeline, transaction.getMndtReqId() + "_" + transaction.getCBIC() + "_" + transaction.getDBIC(), JsonUtil.getJsonString(transaction));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }, true, false);
                file.setFileStatus(FileStatus.ACCEPTED);

            } catch (JAXBException e) {
                e.printStackTrace();
                file.setFileStatus(FileStatus.REJECTED);
            } catch (SAXException e) {
                e.printStackTrace();
                file.setFileStatus(FileStatus.REJECTED);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                file.setFileStatus(FileStatus.REJECTED);
            }
        }
        fileService.save(file);
    }
}
