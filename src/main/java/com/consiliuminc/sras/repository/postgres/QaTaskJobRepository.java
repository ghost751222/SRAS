package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaTaskJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedNativeQuery;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface QaTaskJobRepository extends JpaRepository<QaTaskJob, Long> {

    @Query(value = "select * from qa_task_job where task_date between :start and :end", nativeQuery = true)
    List<QaTaskJob> findByTaskDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = "select * from qa_task_job where TRY_PARSE(psae_TLSubmitTime as date) between :start and :end", nativeQuery = true)
    List<QaTaskJob> findByTaskTLSubmitTime(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
