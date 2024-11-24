package com.antares.kg.mapper;

import com.antares.kg.model.entity.Lemma;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Antares
* @description 针对表【lemma(百度百科词条)】的数据库操作Mapper
* @createDate 2024-11-23 17:28:25
* @Entity com.antares.kg.model.entity.Lemma
*/
public interface LemmaMapper extends BaseMapper<Lemma> {
    Lemma getNextLemma(@Param("taskId") Long taskId);
}




