package com.bkash.springbatch.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class CompanyFundingRecordEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "permalink")
    private String permalink;

    @Column(name = "company")
    private String company;

    @Column(name = "num_emps")
    private String numEmps;

    @Column(name = "category")
    private String category;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "fundedDate")
    private String fundedDate;

    @NotNull
    private BigDecimal raisedAmount;

    @Column(name = "raisedCurrency")
    private String raisedCurrency;

    @Column(name = "round")
    private String round;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNumEmps() {
        return numEmps;
    }

    public void setNumEmps(String numEmps) {
        this.numEmps = numEmps;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFundedDate() {
        return fundedDate;
    }

    public void setFundedDate(String fundedDate) {
        this.fundedDate = fundedDate;
    }

    public BigDecimal getRaisedAmount() {
        return raisedAmount;
    }

    public void setRaisedAmount(BigDecimal raisedAmount) {
        this.raisedAmount = raisedAmount;
    }

    public String getRaisedCurrency() {
        return raisedCurrency;
    }

    public void setRaisedCurrency(String raisedCurrency) {
        this.raisedCurrency = raisedCurrency;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    @Override
    public String toString() {
        return "CompanyFundingRecordEntity{" +
                "id=" + id +
                ", permalink='" + permalink + '\'' +
                ", company='" + company + '\'' +
                ", numEmps='" + numEmps + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", fundedDate='" + fundedDate + '\'' +
                ", raisedAmount=" + raisedAmount +
                ", raisedCurrency='" + raisedCurrency + '\'' +
                ", round='" + round + '\'' +
                '}';
    }
}
