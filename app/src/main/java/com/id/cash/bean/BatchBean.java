package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/6/13.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchBean {
  private String batchId;
  private String id;
  private String infoStatus;
  private String infoType;

  public String getBatchId() {
    return batchId;
  }

  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getInfoStatus() {
    return infoStatus;
  }

  public void setInfoStatus(String infoStatus) {
    this.infoStatus = infoStatus;
  }

  public String getInfoType() {
    return infoType;
  }

  public void setInfoType(String infoType) {
    this.infoType = infoType;
  }

  @Override
  public String toString() {
    return "BatchBean{" +
      "batchId='" + batchId + '\'' +
      ", id='" + id + '\'' +
      ", infoStatus='" + infoStatus + '\'' +
      ", infoType='" + infoType + '\'' +
      '}';
  }

  public interface InfoBatchBeanType {
    String SMS = "SMS";
    String CONTACTS = "CONTACTS";
  }

  public interface InfoBatchStatus {
    String CREATE = "CREATE";
    String COMPLETED = "COMPLETED";
    String UPLOADING = "UPLOADING";
  }
}
