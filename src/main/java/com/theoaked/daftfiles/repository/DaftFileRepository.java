package com.theoaked.daftfiles.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.theoaked.daftfiles.dto.DaftFile;

public interface DaftFileRepository extends JpaRepository<DaftFile, Long>{

}
