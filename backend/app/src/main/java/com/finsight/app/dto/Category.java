package com.finsight.app.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
  private String id;
  private String name;
  private String color;
  private String icon;
  private boolean isDefault;
  private String userId;
}
