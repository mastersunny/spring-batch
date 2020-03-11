package com.bkash.springbatch.dao;

import com.bkash.springbatch.model.Report;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportRepository extends CrudRepository<Report, Long> {

    List<Report> findByBankName(String bankName);

}
