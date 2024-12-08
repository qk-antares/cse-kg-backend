## 计算机科学与技术领域知识图谱及问答系统后端

### 1. 简介

#### 1.1 如何运行

1. 配置MySQL连接（见第3点，你可以使用已经搭建好的MySQL环境），导入sql目录下的建表语句（使用搭建好的环境则不需要）
2. 将仓库中`application-example.yml`拷贝一份为`application.yml`（不要将`application.yml`直接上传到github仓库，因为其中有密码等敏感数据，我已经在.gitignore中设置了忽略`application.yml`）
3. 配置`application.yml`中的`spring.datasource`和`minio`，用户名和密码在微信群中（minio和mysql的用户名密码是一样的）
4. 点开`pom.xml`下载依赖
5. `Debug`或者`Run`主类`CseKgApplication`

#### 1.2 如何测试

测试相关的代码放在`test`中。如果想测试RESTful API，可通过`test`下`request`目录中的Http Request

#### 1.3 项目任务书

##### 1.3.1 背景

计算机科学与技术领域知识体系庞大且复杂，涵盖了编程语言、算法、数据结构、数据库、操作系统、人工智能等众多分支。随着技术的不断发展和创新，新的概念、技术和应用不断涌现，使得该领域的知识更新迅速。传统的知识获取方式往往效率低下，难以满足人们快速准确获取所需信息的需求。知识图谱作为一种强大的知识表示和管理工具，可以有效地整合计算机科学与技术领域的知识，建立知识之间的关联，为用户提供更加智能化的知识服务。

##### 1.3.2 目标

1. 构建全面、准确、动态的计算机科学与技术领域知识图谱。
    - 涵盖该领域的主要概念、技术、人物、机构、文献等实体，以及它们之间的关系。
    - 能够及时反映领域内的最新发展和变化。
2. 实现基于知识图谱的问答功能。
    - 准确理解用户提出的问题，将其转化为知识图谱中的查询语句。
    - 从知识图谱中检索出相关的知识，并以自然语言的形式回答用户的问题。
    - 提供高质量、准确、有用的答案，满足用户在学习、研究和工作中的知识需求。

##### 1.3.3 具体步骤

1. 知识收集与整理
    - 选择合适的知识来源，如学术文献、技术博客、在线课程、专业书籍等。­
    - 对收集到的知识进行清洗和预处理，去除噪声和错误信息。
    - 提取关键信息，如实体、属性和关系，为构建知识图谱做准备。
2. 知识图谱构建
    - 确定知识图谱的实体类型和关系类型。
    - 采用实体识别和关系抽取技术，从整理好的知识中提取实体和关系。
    - 将提取出的实体和关系存储在知识图谱数据库中。
    - 使用可视化工具展示知识图谱，以便更好地理解和分析。
3. 问答功能实现
    - 对用户提出的问题进行自然语言处理，包括分词、词性标注、命名实体识别等。
    - 根据检索结果生成自然语言回答，并对回答进行优化和润色。
4. 评估与优化
    - 设计评估指标，如准确率、召回率、回答的完整性和有用性等。
    - 使用评估指标对知识图谱的构建质量和问答功能的性能进行评估。
    - 根据评估结果，对知识图谱的构建和问答功能的实现进行优化和改进。

##### 1.3.4 要求

1. 基础要求
    - 构建一个具有一定规模和准确性的计算机科学与技术领域知识图谱。
    - 实现基本的问答功能，能够回答基础的计算机科学与技术领域问题。
2. 扩展要求（可选）
    - 实现多轮对话功能，能够根据用户的追问和反馈进行进一步的回答解释。
    - 结合大模型和检索增强技术，形成领域智能的问答智能体。
    - 探究科学研究的潜在未来发展方向、研究学科间的交叉研究主题。

---

### 2. 知识收集与整理

#### 2.1 知识来源

知识来源为百度百科和维基百科词条，实现一个词条爬取系统来获取计算机领域名词的解释，大致的实现思路如下：

1. 爬取任务

   爬取任务的关键属性是根词条和最大爬取深度。根词条是一个具体的计算机领域名词，从根词条开始，获取HTML页面上指定元素里的文本，拼接成该名词的解释，并将完整的内容保存至txt文档中（上传至Minio）。同时，如果遇到了超链接，判断该超链接是否指向另一个百度百科词条且在当前任务中没有爬取过，如果是的话将其添加到待爬取的数据库表中。使用广度优先的方式爬取，直至达到目标深度或用户发出停止指令。

   系统可以同时执行多个爬取任务，暂停或继续特定的爬取任务，并通过线程池调度。

2. 爬取可视化（选作）

   爬取任务的创建、暂停、状态都有前端界面展示。

#### 2.2 知识清洗

1. 词条筛选

   即使是一个计算机领域名词的词条，它也会引用一些和计算机相关性不是很高的词条，且随着爬取深度越深，这些不相关的词条就会越多。因此在爬取的时候我们就做好词条筛选，一个大致的思路是：在爬取某个词条时，我们先把其总结（Summary）部分拿出来，丢给大模型让它给我们一个与计算机科学领域的相关性分数，如果未达到我们设定的阈值，就把其状态设置为EXIT，也就是不会从这个词条再往下爬取了。这样的话我们最终得到的词条都是和计算机科学领域比较相关的词条。

   上述过程的一个难点是**提示词的设计**，怎么设计提示词让大模型给我们更精准、更符合需求的打分。

2. 多个任务爬取词条的合并（选作）

   所有词条之间的引用关系是一张大图，而每个爬取任务是以根词条为起始、最大爬取深度为范围（带相关性分数剪枝）的一个子图，不同爬取任务之间可能会有重合的部分，所以**在所有的爬取任务完毕之后**，还要做爬取词条的合并。

   上述过程主要涉及对**MySQL数据库表的处理**。

3. 词条引用图谱的可视化（选作）

   在完成2之后，可以将爬取到的词条之间的引用关系用图的形式展示出来。点击某个节点，还可以即时地获取这个词条的解释展示出来。

### 3. 知识图谱构建

#### 3.1 实体和关系的抽取

在**准备好所有的词条之后**，利用GraphRAG来提取关键信息，例如实体、关系、社区等，并保存到Neo4J。

这里主要有两个问题：首先是**中文提示词的设计**（未解决），其次是**算力/时间开销**的问题（只保留100个和计算机科学最相关的词条，提供整体的解决方案，系统做简单的演示）

#### 3.2 知识图谱的可视化

我想到的一些问题：

- Java操作Neo4J？（我没有经验）
- 接口和数据的格式，前端需要哪些接口，什么格式的数据

- 渲染性能，前端用的图可视化库最多支持多少节点和边？因为100个词条涉及到的实体和关系也是非常多了

---

### 4. 问答系统







---

### 5. 评估优化