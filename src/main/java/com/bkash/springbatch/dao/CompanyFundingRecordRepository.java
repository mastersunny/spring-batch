package com.bkash.springbatch.dao;

import com.bkash.springbatch.model.CompanyFundingRecordEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompanyFundingRecordRepository extends CrudRepository<CompanyFundingRecordEntity, Long> {


}
