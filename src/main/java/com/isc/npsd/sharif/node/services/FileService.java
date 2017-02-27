package com.isc.npsd.sharif.node.services;

import com.isc.npsd.sharif.node.entities.File;
import com.isc.npsd.sharif.node.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by A_Jankeh on 2/26/2017.
 */
@Service
@Scope("prototype")
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public File findById(long id) {
        return fileRepository.findOne(id);
    }

    public File save(File file) {
        return fileRepository.save(file);
    }
}
