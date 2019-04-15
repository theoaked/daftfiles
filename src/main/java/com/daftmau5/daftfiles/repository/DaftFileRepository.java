package com.daftmau5.daftfiles.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.daftmau5.daftfiles.dto.DaftFile;

public interface DaftFileRepository extends JpaRepository<DaftFile, Long>{

}
