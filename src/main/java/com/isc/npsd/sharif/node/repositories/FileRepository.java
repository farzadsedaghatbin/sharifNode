package com.isc.npsd.sharif.node.repositories;

import com.isc.npsd.sharif.node.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by A_Jankeh on 2/26/2017.
 */
public interface FileRepository extends JpaRepository<File, Long> {
}
