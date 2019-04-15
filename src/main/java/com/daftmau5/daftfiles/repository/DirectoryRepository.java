package com.daftmau5.daftfiles.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.daftmau5.daftfiles.dto.Directory;

public interface DirectoryRepository extends JpaRepository<Directory, Long>{

}
