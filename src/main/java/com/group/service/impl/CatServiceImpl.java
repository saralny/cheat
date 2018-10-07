package com.group.service.impl;

import com.group.repository.CatRepository;
import com.group.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatServiceImpl implements CatService{


    @Autowired
    private CatRepository catRepository;

}
