package com.antares.kg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.antares.kg.mapper.CrawlTaskMapper;
import com.antares.kg.mapper.LemmaLinkMapper;
import com.antares.kg.model.dto.crawl.CrawlRes;
import com.antares.kg.model.entity.CrawlTask;
import com.antares.kg.model.entity.Lemma;
import com.antares.kg.model.entity.LemmaLink;
import com.antares.kg.model.enums.LemmaStatusEnum;
import com.antares.kg.strategy.CrawlStrategy;
import com.antares.kg.strategy.CrawlStrategyFactory;
import com.antares.kg.utils.MinioUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.antares.kg.service.LemmaService;
import com.antares.kg.mapper.LemmaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Antares
 * @description 针对表【lemma(百度百科词条)】的数据库操作Service实现
 * @createDate 2024-11-23 16:37:28
 */
@Service
@Slf4j
public class LemmaServiceImpl extends ServiceImpl<LemmaMapper, Lemma>
        implements LemmaService {
    @Resource
    private LemmaLinkMapper lemmaLinkMapper;
    @Resource
    private CrawlStrategyFactory crawlStrategyFactory;
    @Resource
    private MinioUtil minioUtil;
    @Resource
    private CrawlTaskMapper crawlTaskMapper;

    /**
     * Todo: 做防止多个任务重复爬取同一个词条的处理（不太好设计）
     *
     * @param crawlTask
     * @param lemma
     */
    @Transactional
    public boolean crawlOneLemma(CrawlTask crawlTask, Lemma lemma) {
        try {
            // 1. 使用特定的策略爬取词条
            CrawlStrategy strategy = crawlStrategyFactory.getStrategy(crawlTask.getType());
            CrawlRes crawlRes = strategy.crawl(lemma.getUrl(), crawlTask.getScoreThreshold());

            Lemma mainLemma = crawlRes.getMainLemma();
            List<Lemma> referenceLemmas = crawlRes.getReferenceLemmas();

            // 2. 更新主词条
            if (mainLemma.getStatus() == LemmaStatusEnum.SUCCESS.getCode()) {
                String directory = String.format("%s/%d-RootName=%s/", crawlTask.getType(), crawlTask.getId(), crawlTask.getRootName());
                String fileName = String.format("%d-%s-Score=%d.txt", lemma.getId(), mainLemma.getTitle(), mainLemma.getScore());
                minioUtil.uploadStringAsTxt("cse-kg", directory, fileName, mainLemma.getContent());
                lemma.setContent(directory + fileName);
                log.info("保存路径: {}", lemma.getContent());

                // 3. 添加待爬词条
                Integer depth = lemma.getDepth();
                List<Lemma> pendingLemmas = new ArrayList<>();
                List<Lemma> addedLemmas = new ArrayList<>();
                for (Lemma referenceLemma : referenceLemmas) {
                    // 查询引用的词条是否已经在任务中了
                    Lemma addedLemma = this.getOne(new LambdaQueryWrapper<Lemma>()
                            .eq(Lemma::getUrl, referenceLemma.getUrl())
                            .eq(Lemma::getTaskId, crawlTask.getId()));
                    if (addedLemma != null) {
                        addedLemmas.add(addedLemma);
                    } else {
                        Lemma pendingLemma = BeanUtil.copyProperties(referenceLemma, Lemma.class);
                        pendingLemma.setDepth(depth + 1);
                        pendingLemma.setTaskId(crawlTask.getId());
                        pendingLemmas.add(pendingLemma);
                    }
                }

                // 4. 添加引用关系
                List<LemmaLink> lemmaLinks = new ArrayList<>();
                for (Lemma addLemma : addedLemmas) {
                    LemmaLink lemmaLink = new LemmaLink();
                    lemmaLink.setFromLemmaId(lemma.getId());
                    lemmaLink.setToLemmaId(addLemma.getId());
                    lemmaLinks.add(lemmaLink);
                }

                // 5. 未达到目标爬取深度
                if(depth < crawlTask.getMaxDepth()) {
                    this.saveBatch(pendingLemmas);

                    for (Lemma pendingLemma : pendingLemmas) {
                        LemmaLink lemmaLink = new LemmaLink();
                        lemmaLink.setFromLemmaId(lemma.getId());
                        lemmaLink.setToLemmaId(pendingLemma.getId());
                        lemmaLinks.add(lemmaLink);
                    }
                }

                lemmaLinkMapper.insert(lemmaLinks);
            }
            lemma.setStatus(mainLemma.getStatus());
            lemma.setTitle(mainLemma.getTitle());
            lemma.setScore(mainLemma.getScore());
            lemma.setLog(mainLemma.getLog());
            this.updateById(lemma);
            return true;
        } catch (Exception e) {
            log.error("爬取 {} 的过程出现未知错误：\n{}", lemma.getUrl(), e.getMessage());
            lemma.setStatus(LemmaStatusEnum.ERROR.getCode());
            lemma.setLog("出现未知错误:\n" + e.getMessage());
            this.updateById(lemma);
            crawlTask.setLog(crawlTask.getLog() + String.format("爬取 %s 的过程出现未知错误：\n%s", lemma.getUrl(), e.getMessage()));
            crawlTaskMapper.updateById(crawlTask);

            return false;
        }
    }
}




