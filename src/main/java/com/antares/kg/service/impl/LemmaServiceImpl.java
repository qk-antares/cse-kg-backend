package com.antares.kg.service.impl;

import com.antares.kg.model.entity.Lemma;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.antares.kg.service.LemmaService;
import com.antares.kg.mapper.LemmaMapper;
import org.springframework.stereotype.Service;

/**
* @author Antares
* @description 针对表【lemma(百度百科词条)】的数据库操作Service实现
* @createDate 2024-11-23 16:37:28
*/
@Service
public class LemmaServiceImpl extends ServiceImpl<LemmaMapper, Lemma>
    implements LemmaService{

}




