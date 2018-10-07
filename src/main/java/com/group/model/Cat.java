package com.group.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "t_cat")
public class Cat {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @NotNull(message = "猫咪名字不能为空")
  private String name;
  @NotNull(message = "猫咪年龄不能为空")
  private long age;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public long getAge() {
    return age;
  }

  public void setAge(long age) {
    this.age = age;
  }

}
