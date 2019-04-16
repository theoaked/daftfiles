package com.theoaked.daftfiles.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.theoaked.daftfiles.dto.Directory;

public interface DirectoryRepository extends JpaRepository<Directory, Long>{

}
