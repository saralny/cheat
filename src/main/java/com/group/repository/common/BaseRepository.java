package com.group.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

// 基础Repository
@NoRepositoryBean
public interface BaseRepository<E,ID extends Serializable>  extends JpaRepository<E,ID>{


}
