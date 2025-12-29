# 评审模板管理后端实现 - 任务清单

## 阶段概览 (Phase Overview)

- **Phase 1**: 数据模型层（1天）
- **Phase 2**: 数据访问层 (Mapper)（1天）
- **Phase 3**: 业务逻辑层 (Service)（2-3天）
- **Phase 4**: API 接口层 (Controller)（0.5-1天）
- **Phase 5**: 导出功能（1-1.5天）
- **Phase 6**: 集成测试（1天）

**总预估**: 6.5-8.5 个工作日

**注意**: 数据字典 `eval_template_score` 已实现，无需添加。

---

## Phase 1: 数据模型层（1 天）

### 1.1 创建实体类 (Entity/DO)

#### 1.1.1 创建 `EvaluationTemplateDO.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/entity/EvaluationTemplateDO.java`

- [ ] 创建类并继承 `BaseDO`
- [ ] 添加类注解：`@Data`, `@TableName("prj_evaluation_template")`
- [ ] 添加字段：
  - [ ] `private String code;` - 模板编码
  - [ ] `private String name;` - 模板名称
  - [ ] `private String description;` - 模板说明
  - [ ] `private BigDecimal totalScore;` - 总分
  - [ ] `private Integer itemCount;` - 评分项数量
  - [ ] `private Integer categoryCount;` - 大类数量
  - [ ] `private DisEnableStatusEnum status;` - 状态
  - [ ] `private Integer sort;` - 排序
  - [ ] `private String remark;` - 备注
- [ ] 添加 `serialVersionUID`
- [ ] 添加中文注释（每个字段必须有注释）

#### 1.1.2 创建 `EvaluationTemplateItemDO.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/entity/EvaluationTemplateItemDO.java`

- [ ] 创建类并继承 `BaseDO`
- [ ] 添加类注解：`@Data`, `@TableName("prj_evaluation_template_item")`
- [ ] 添加字段：
  - [ ] `private Long templateId;` - 模板ID (外键)
  - [ ] `private String categoryName;` - 大类名称
  - [ ] `private Integer categorySort;` - 大类排序
  - [ ] `private String itemName;` - 评分项名称
  - [ ] `private Integer itemSort;` - 评分项排序
  - [ ] `private BigDecimal maxScore;` - 满分值
  - [ ] `private String description;` - 评分说明
- [ ] 添加 `serialVersionUID`
- [ ] 添加中文注释

### 1.2 创建请求参数 (Req)

#### 1.2.1 创建 `EvaluationTemplateItemReq.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/req/EvaluationTemplateItemReq.java`

- [ ] 创建类并实现 `Serializable`
- [ ] 添加类注解：`@Data`, `@Schema(description = "评审模板评分项请求参数")`
- [ ] 添加字段和校验注解：
  - [ ] `@NotBlank(message = "大类名称不能为空")` `private String categoryName;`
  - [ ] `@NotNull(message = "大类排序不能为空")` `private Integer categorySort;`
  - [ ] `@NotBlank(message = "评分项名称不能为空")` `private String itemName;`
  - [ ] `@NotNull(message = "评分项排序不能为空")` `private Integer itemSort;`
  - [ ] `@NotNull(message = "满分值不能为空")` `@DecimalMin(value = "0.01", message = "满分值必须大于0")` `private BigDecimal maxScore;`
  - [ ] `private String description;` - 评分说明（可选）
- [ ] 添加 `@Schema` 注解描述每个字段

#### 1.2.2 创建 `EvaluationTemplateReq.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/req/EvaluationTemplateReq.java`

- [ ] 创建类并实现 `Serializable`
- [ ] 添加类注解：`@Data`, `@Schema(description = "评审模板创建或修改请求参数")`
- [ ] 添加字段和校验注解：
  - [ ] `@NotBlank` `@Pattern(regexp = "^TPL_[A-Z0-9]+$", message = "编码格式不正确")` `private String code;`
  - [ ] `@NotBlank` `@Length(max = 50, message = "名称长度不能超过50个字符")` `private String name;`
  - [ ] `@Length(max = 500, message = "说明长度不能超过500个字符")` `private String description;`
  - [ ] `@NotNull` `@DecimalMin("50.00")` `@DecimalMax("500.00")` `private BigDecimal totalScore;`
  - [ ] `@NotNull` `private DisEnableStatusEnum status;`
  - [ ] `@Min(value = 0, message = "排序最小值为0")` `private Integer sort;`
  - [ ] `@Length(max = 255, message = "备注长度不能超过255个字符")` `private String remark;`
  - [ ] `@NotEmpty(message = "评分项不能为空")` `@Valid` `private List<EvaluationTemplateItemReq> items;`
- [ ] 添加 `@Schema` 注解描述每个字段

#### 1.2.3 创建 `EvaluationTemplateQuery.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/query/EvaluationTemplateQuery.java`

- [ ] 创建类并实现 `Serializable`
- [ ] 添加类注解：`@Data`, `@Schema(description = "评审模板查询条件")`
- [ ] 添加字段：
  - [ ] `private String code;` - 模板编码（模糊查询）
  - [ ] `private String name;` - 模板名称（模糊查询）
  - [ ] `private DisEnableStatusEnum status;` - 状态（精确查询）
- [ ] 添加 `@Schema` 注解描述每个字段

### 1.3 创建响应参数 (Resp)

#### 1.3.1 创建 `EvaluationTemplateItemResp.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/resp/EvaluationTemplateItemResp.java`

- [ ] 创建类并实现 `Serializable`
- [ ] 添加类注解：`@Data`, `@Schema(description = "评审模板评分项响应参数")`
- [ ] 添加字段（参考 DO，添加 `id` 和 `templateId`）
- [ ] 添加 `@Schema` 注解描述每个字段

#### 1.3.2 创建 `EvaluationTemplateResp.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/resp/EvaluationTemplateResp.java`

- [ ] 创建类并继承 `BaseDetailResp`
- [ ] 添加类注解：`@Data`, `@Schema(description = "评审模板响应参数")`
- [ ] 添加字段（参考 DO，不包含 `remark`）
- [ ] 添加 `@Schema` 注解描述每个字段

#### 1.3.3 创建 `EvaluationTemplateDetailResp.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/resp/EvaluationTemplateDetailResp.java`

- [ ] 创建类并继承 `EvaluationTemplateResp`
- [ ] 添加类注解：`@Data`, `@Schema(description = "评审模板详情响应参数")`
- [ ] 添加字段：
  - [ ] `private String remark;` - 备注
  - [ ] `private List<EvaluationTemplateItemResp> items;` - 评分项列表
- [ ] 添加 `@Schema` 注解描述每个字段

---

## Phase 2: 数据访问层 (Mapper)（1 天）

### 2.1 创建 Mapper 接口

#### 2.1.1 创建 `EvaluationTemplateMapper.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/mapper/EvaluationTemplateMapper.java`

- [ ] 创建接口并添加 `@Mapper` 注解
- [ ] 继承 `DataPermissionMapper<EvaluationTemplateDO>`
- [ ] 添加类注释（中文）
- [ ] 添加自定义方法（如果需要）：
  - [ ] `EvaluationTemplateDO selectByCode(@Param("code") String code);`
  - [ ] `EvaluationTemplateDO selectByName(@Param("name") String name);`

#### 2.1.2 创建 `EvaluationTemplateItemMapper.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/mapper/EvaluationTemplateItemMapper.java`

- [ ] 创建接口并添加 `@Mapper` 注解
- [ ] 继承 `DataPermissionMapper<EvaluationTemplateItemDO>`
- [ ] 添加类注释（中文）
- [ ] 添加自定义方法：
  - [ ] `List<EvaluationTemplateItemDO> selectByTemplateId(@Param("templateId") Long templateId);`
  - [ ] `int deleteByTemplateId(@Param("templateId") Long templateId);`
  - [ ] `int deleteByTemplateIds(@Param("templateIds") List<Long> templateIds);`
  - [ ] `List<EvaluationTemplateItemDO> selectByTemplateIds(@Param("templateIds") List<Long> templateIds);`

### 2.2 创建 Mapper XML（如果需要自定义 SQL）

#### 2.2.1 创建 `EvaluationTemplateMapper.xml`

路径: `continew-admin/continew-system/src/main/resources/mapper/EvaluationTemplateMapper.xml`

- [ ] 创建 XML 文件，指定 namespace
- [ ] 实现自定义查询（如果有）

#### 2.2.2 创建 `EvaluationTemplateItemMapper.xml`

路径: `continew-admin/continew-system/src/main/resources/mapper/EvaluationTemplateItemMapper.xml`

- [ ] 创建 XML 文件，指定 namespace
- [ ] 实现 `selectByTemplateId`
  ```xml
  <select id="selectByTemplateId" resultType="...EvaluationTemplateItemDO">
    SELECT * FROM prj_evaluation_template_item
    WHERE template_id = #{templateId} AND deleted = 0
    ORDER BY category_sort, item_sort
  </select>
  ```
- [ ] 实现 `deleteByTemplateId`
  ```xml
  <update id="deleteByTemplateId">
    UPDATE prj_evaluation_template_item
    SET deleted = id
    WHERE template_id = #{templateId} AND deleted = 0
  </update>
  ```
- [ ] 实现 `deleteByTemplateIds`
  ```xml
  <update id="deleteByTemplateIds">
    UPDATE prj_evaluation_template_item
    SET deleted = id
    WHERE template_id IN
    <foreach collection="templateIds" item="id" open="(" separator="," close=")">
      #{id}
    </foreach>
    AND deleted = 0
  </update>
  ```
- [ ] 实现 `selectByTemplateIds`
  ```xml
  <select id="selectByTemplateIds" resultType="...EvaluationTemplateItemDO">
    SELECT * FROM prj_evaluation_template_item
    WHERE template_id IN
    <foreach collection="templateIds" item="id" open="(" separator="," close=")">
      #{id}
    </foreach>
    AND deleted = 0
    ORDER BY template_id, category_sort, item_sort
  </select>
  ```

---

## Phase 3: 业务逻辑层 (Service)（2-3 天）

### 3.1 创建 Service 接口

#### 3.1.1 创建 `EvaluationTemplateItemService.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/service/EvaluationTemplateItemService.java`

- [ ] 创建接口
- [ ] 继承 `IService<EvaluationTemplateItemDO>`
- [ ] 添加类注释（中文）
- [ ] 添加自定义方法（如果需要）

#### 3.1.2 创建 `EvaluationTemplateService.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/service/EvaluationTemplateService.java`

- [ ] 创建接口
- [ ] 继承 `BaseService<EvaluationTemplateResp, EvaluationTemplateDetailResp, EvaluationTemplateQuery, EvaluationTemplateReq>` 和 `IService<EvaluationTemplateDO>`
- [ ] 添加类注释（中文）
- [ ] 添加自定义方法：
  - [ ] `void create(EvaluationTemplateReq req);` - 创建模板
  - [ ] `void update(EvaluationTemplateReq req, Long id);` - 修改模板
  - [ ] `void delete(List<Long> ids);` - 批量删除
  - [ ] `EvaluationTemplateDetailResp getDetail(Long id);` - 查询详情
  - [ ] `void export(EvaluationTemplateQuery query, List<Long> ids, HttpServletResponse response) throws IOException;` - 导出
  - [ ] `String generateCode();` - 生成编码

### 3.2 创建 Service 实现类

#### 3.2.1 创建 `EvaluationTemplateItemServiceImpl.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/service/impl/EvaluationTemplateItemServiceImpl.java`

- [ ] 创建类并添加 `@Service` 注解
- [ ] 继承 `ServiceImpl<EvaluationTemplateItemMapper, EvaluationTemplateItemDO>` 并实现 `EvaluationTemplateItemService`
- [ ] 添加类注释（中文）
- [ ] 实现自定义方法（如果需要）

#### 3.2.2 创建 `EvaluationTemplateServiceImpl.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/service/impl/EvaluationTemplateServiceImpl.java`

- [ ] 创建类并添加 `@Slf4j`, `@Service`, `@RequiredArgsConstructor` 注解
- [ ] 继承 `BaseServiceImpl<EvaluationTemplateMapper, EvaluationTemplateDO, EvaluationTemplateResp, EvaluationTemplateDetailResp, EvaluationTemplateQuery, EvaluationTemplateReq>` 并实现 `EvaluationTemplateService`
- [ ] 添加类注释（中文）
- [ ] 注入依赖：
  - [ ] `private final EvaluationTemplateItemService itemService;`
  - [ ] `private final EvaluationTemplateItemMapper itemMapper;`

### 3.3 实现创建方法

- [ ] 实现 `create(EvaluationTemplateReq req)` 方法
  - [ ] 添加 `@Override` 和 `@Transactional(rollbackFor = Exception.class)` 注解
  - [ ] 调用 `validateTemplate(req, null)` 进行业务校验
  - [ ] 创建 `EvaluationTemplateDO` 对象并复制属性
  - [ ] 设置 `itemCount = 0`, `categoryCount = 0`
  - [ ] 调用 `save(template)` 保存模板主表
  - [ ] 调用 `saveItems(template.getId(), req.getItems())` 批量保存评分项
  - [ ] 调用 `updateCounts(template.getId())` 更新统计字段
  - [ ] 添加日志：`log.info("创建评审模板成功，模板ID: {}, 编码: {}", id, code);`

### 3.4 实现修改方法

- [ ] 实现 `update(EvaluationTemplateReq req, Long id)` 方法
  - [ ] 添加 `@Override` 和 `@Transactional(rollbackFor = Exception.class)` 注解
  - [ ] 检查模板是否存在
  - [ ] 调用 `validateTemplate(req, id)` 进行业务校验
  - [ ] 调用 `itemMapper.deleteByTemplateId(id)` 删除旧评分项
  - [ ] 创建 `EvaluationTemplateDO` 对象并复制属性
  - [ ] 设置 `id` 字段
  - [ ] 调用 `updateById(template)` 更新模板主表
  - [ ] 调用 `saveItems(id, req.getItems())` 批量保存新评分项
  - [ ] 调用 `updateCounts(id)` 更新统计字段
  - [ ] 添加日志：`log.info("修改评审模板成功，模板ID: {}, 编码: {}", id, code);`

### 3.5 实现删除方法

- [ ] 实现 `delete(List<Long> ids)` 方法
  - [ ] 添加 `@Override` 和 `@Transactional(rollbackFor = Exception.class)` 注解
  - [ ] 调用 `itemMapper.deleteByTemplateIds(ids)` 删除关联评分项
  - [ ] 调用 `removeByIds(ids)` 软删除模板主表
  - [ ] 添加日志：`log.info("删除评审模板成功，模板IDs: {}", ids);`

### 3.6 实现详情查询方法

- [ ] 实现 `getDetail(Long id)` 方法
  - [ ] 添加 `@Override` 注解
  - [ ] 调用 `getById(id)` 查询模板主表
  - [ ] 检查模板是否存在，不存在抛异常
  - [ ] 调用 `itemMapper.selectByTemplateId(id)` 查询评分项列表
  - [ ] 创建 `EvaluationTemplateDetailResp` 对象并复制属性
  - [ ] 转换评分项 DO → Resp
  - [ ] 设置 `items` 字段
  - [ ] 返回详情响应

### 3.7 实现生成编码方法

- [ ] 实现 `generateCode()` 方法
  - [ ] 添加 `@Override` 注解
  - [ ] 查询最大编码：`SELECT code FROM prj_evaluation_template WHERE code LIKE 'TPL_%' ORDER BY code DESC LIMIT 1`
  - [ ] 如果不存在，返回 `"TPL_001"`
  - [ ] 如果存在，解析数字部分并 +1
  - [ ] 格式化为 `"TPL_" + String.format("%03d", number)`
  - [ ] 返回生成的编码

### 3.8 实现数据校验方法

- [ ] 实现私有方法 `validateTemplate(EvaluationTemplateReq req, Long excludeId)`
  - [ ] 编码唯一性校验
    ```java
    Long codeCount = lambdaQuery()
        .eq(EvaluationTemplateDO::getCode, req.getCode())
        .ne(excludeId != null, EvaluationTemplateDO::getId, excludeId)
        .count();
    ValidationUtils.throwIf(codeCount > 0, "模板编码 [{}] 已存在", req.getCode());
    ```
  - [ ] 名称唯一性校验
    ```java
    Long nameCount = lambdaQuery()
        .eq(EvaluationTemplateDO::getName, req.getName())
        .ne(excludeId != null, EvaluationTemplateDO::getId, excludeId)
        .count();
    ValidationUtils.throwIf(nameCount > 0, "模板名称 [{}] 已存在", req.getName());
    ```
  - [ ] 总分一致性校验
    ```java
    BigDecimal itemsSum = req.getItems().stream()
        .map(EvaluationTemplateItemReq::getMaxScore)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    boolean isMatch = itemsSum.compareTo(req.getTotalScore()) == 0;
    ValidationUtils.throwIf(!isMatch,
        "评分项总分 [{}] 与模板总分 [{}] 不一致", itemsSum, req.getTotalScore());
    ```

### 3.9 实现辅助方法

- [ ] 实现私有方法 `saveItems(Long templateId, List<EvaluationTemplateItemReq> items)`
  - [ ] 将 `EvaluationTemplateItemReq` 转换为 `EvaluationTemplateItemDO`
  - [ ] 设置 `templateId` 字段
  - [ ] 设置 `createUser`, `createTime`
  - [ ] 调用 `itemService.saveBatch(itemDOs, 1000)` 批量保存

- [ ] 实现私有方法 `updateCounts(Long templateId)`
  - [ ] 查询评分项列表
  - [ ] 计算 `itemCount = items.size()`
  - [ ] 计算 `categoryCount = items.stream().map(i -> i.getCategoryName() + "_" + i.getCategorySort()).distinct().count()`
  - [ ] 更新模板主表的统计字段

### 3.10 重写 BaseService 钩子方法（如果需要）

- [ ] 重写 `buildQueryWrapper(EvaluationTemplateQuery query)` 方法
  - [ ] 添加 `code` 的模糊查询
  - [ ] 添加 `name` 的模糊查询
  - [ ] 添加 `status` 的精确查询

---

## Phase 4: API 接口层 (Controller)（0.5-1 天）

### 4.1 创建 Controller

#### 4.1.1 创建 `EvaluationTemplateController.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/controller/EvaluationTemplateController.java`

- [ ] 创建类并添加注解：
  - [ ] `@Tag(name = "评审模板管理 API")`
  - [ ] `@Validated`
  - [ ] `@RestController`
  - [ ] `@RequiredArgsConstructor`
  - [ ] `@CrudRequestMapping(value = "/prj/template/evaluation-template", api = {Api.PAGE, Api.GET, Api.BATCH_DELETE, Api.DICT})`
    - **注意**: 不包含 `Api.CREATE` 和 `Api.UPDATE`，因为需要自定义实现
- [ ] 继承 `BaseController<EvaluationTemplateService, EvaluationTemplateResp, EvaluationTemplateDetailResp, EvaluationTemplateQuery, EvaluationTemplateReq>`
- [ ] 添加类注释（中文）

### 4.2 实现自定义 API

#### 4.2.1 创建模板 API

- [ ] 添加方法 `create(@RequestBody @Valid EvaluationTemplateReq req)`
  - [ ] 添加注解：
    - [ ] `@Operation(summary = "创建评审模板", description = "创建评审模板，包含基本信息和评分项")`
    - [ ] `@SaCheckPermission("prj:template:evaluation-template:create")`
    - [ ] `@PostMapping`
  - [ ] 调用 `baseService.create(req)`
  - [ ] 返回 `void`

#### 4.2.2 修改模板 API

- [ ] 添加方法 `update(@RequestBody @Valid EvaluationTemplateReq req, @PathVariable Long id)`
  - [ ] 添加注解：
    - [ ] `@Operation(summary = "修改评审模板", description = "修改评审模板，包含基本信息和评分项")`
    - [ ] `@Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)`
    - [ ] `@SaCheckPermission("prj:template:evaluation-template:update")`
    - [ ] `@PutMapping("/{id}")`
  - [ ] 调用 `baseService.update(req, id)`
  - [ ] 返回 `void`

#### 4.2.3 导出模板 API

- [ ] 添加方法 `export(@RequestBody EvaluationTemplateQuery query, @RequestParam(required = false) List<Long> ids, HttpServletResponse response) throws IOException`
  - [ ] 添加注解：
    - [ ] `@Operation(summary = "导出评审模板", description = "导出评审模板，支持按IDs或查询条件导出")`
    - [ ] `@SaCheckPermission("prj:template:evaluation-template:export")`
    - [ ] `@PostMapping("/export")`
  - [ ] 调用 `baseService.export(query, ids, response)`
  - [ ] 返回 `void`

#### 4.2.4 生成编码 API

- [ ] 添加方法 `generateCode()`
  - [ ] 添加注解：
    - [ ] `@Operation(summary = "生成模板编码", description = "自动生成唯一的模板编码")`
    - [ ] `@SaCheckPermission("prj:template:evaluation-template:create")`
    - [ ] `@GetMapping("/generate-code")`
  - [ ] 调用 `baseService.generateCode()`
  - [ ] 返回 `String` (编码)

---

## Phase 5: 导出功能（1-1.5 天）

### 5.1 定义导出数据模型

#### 5.1.1 创建 `EvaluationTemplateExportResp.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/resp/EvaluationTemplateExportResp.java`

- [ ] 创建类并实现 `Serializable`
- [ ] 添加注解：`@Data`, `@Schema(description = "评审模板导出响应参数")`
- [ ] 添加字段：
  - [ ] `@ExcelProperty(value = "模板编码", index = 0)` `private String code;`
  - [ ] `@ExcelProperty(value = "模板名称", index = 1)` `private String name;`
  - [ ] `@ExcelProperty(value = "评分大类", index = 2)` `private String categoryName;`
  - [ ] `@ExcelProperty(value = "评分项", index = 3)` `private String itemName;`
  - [ ] `@ExcelProperty(value = "满分", index = 4)` `private String maxScore;`
- [ ] 添加标记字段（用于应用样式）：
  - [ ] `private Boolean isSubtotalRow;` - 是否为小计行
  - [ ] `private Boolean isTotalRow;` - 是否为总计行

### 5.2 实现导出逻辑

#### 5.2.1 在 `EvaluationTemplateServiceImpl` 中实现 `export` 方法

- [ ] 添加 `@Override` 注解
- [ ] **步骤 1**: 查询模板列表
  - [ ] 如果 `ids` 不为空，按 IDs 查询：`listByIds(ids)`
  - [ ] 否则，按查询条件查询：`list(buildQueryWrapper(query))`
  - [ ] 如果列表为空，抛异常："没有可导出的数据"
- [ ] **步骤 2**: 查询所有模板的评分项
  - [ ] 提取模板 IDs
  - [ ] 调用 `itemMapper.selectByTemplateIds(templateIds)` 查询所有评分项
  - [ ] 按 `templateId` 分组：`Map<Long, List<EvaluationTemplateItemDO>> itemsMap`
- [ ] **步骤 3**: 构建导出行数据
  - [ ] 遍历每个模板
  - [ ] 获取该模板的评分项列表
  - [ ] 按 `categoryName + categorySort` 分组
  - [ ] 对每个大类：
    - [ ] 遍历该大类的评分项，添加普通行
    - [ ] 计算小计值：`sum(items.maxScore)`
    - [ ] 添加小计行（设置 `isSubtotalRow = true`）
  - [ ] 添加总计行（设置 `isTotalRow = true`）
  - [ ] 添加空行（用于分隔不同模板，如果有多个模板）
- [ ] **步骤 4**: 调用 ExcelUtils 导出
  - [ ] 设置文件名：`"评审模板_" + DateUtil.format(new Date(), "yyyyMMdd_HHmmss")`
  - [ ] 注册自定义样式策略：`EvaluationTemplateExportStyleStrategy`
  - [ ] 调用导出方法：
    ```java
    ExcelUtils.export(response, fileName, "评审模板", EvaluationTemplateExportResp.class, exportData,
        new EvaluationTemplateExportStyleStrategy());
    ```

### 5.3 实现自定义导出样式

#### 5.3.1 创建 `EvaluationTemplateExportStyleStrategy.java`

路径: `continew-admin/continew-system/src/main/java/top/continew/admin/system/export/EvaluationTemplateExportStyleStrategy.java`

- [ ] 创建类并实现 `CellWriteHandler`
- [ ] 添加类注释（中文）
- [ ] 重写 `afterCellDispose(CellWriteHandlerContext context)` 方法
  - [ ] 判断是否为表头行，如果是则跳过
  - [ ] 获取当前行数据对象：`EvaluationTemplateExportResp`
  - [ ] 判断是否为小计行（`isSubtotalRow == true`）
    - [ ] 应用样式：灰色背景（`#F0F0F0`）、加粗字体
  - [ ] 判断是否为总计行（`isTotalRow == true`）
    - [ ] 应用样式：蓝色背景（`#4472C4`）、白色字体、加粗
  - [ ] 应用样式到所有单元格（使用 `cell.setCellStyle(cellStyle)`）

---

## Phase 6: 集成测试（1 天）

### 6.1 单元测试

- [ ] 创建 `EvaluationTemplateServiceTest.java`
  - [ ] 测试 `create` 方法
    - [ ] 正常创建
    - [ ] 编码重复（预期抛异常）
    - [ ] 名称重复（预期抛异常）
    - [ ] 总分不一致（预期抛异常）
  - [ ] 测试 `update` 方法
    - [ ] 正常修改
    - [ ] 修改时编码重复（预期抛异常）
    - [ ] 修改时名称重复（预期抛异常）
  - [ ] 测试 `delete` 方法
    - [ ] 正常删除
    - [ ] 批量删除
  - [ ] 测试 `getDetail` 方法
    - [ ] 正常查询
    - [ ] 查询不存在的ID（预期抛异常）
  - [ ] 测试 `generateCode` 方法
    - [ ] 第一次生成（返回 TPL_001）
    - [ ] 已有数据时生成（返回递增编码）

### 6.2 前后端联调

#### 6.2.1 配置前端 API 调用

- [ ] 修改前端 `api/index.ts`
  - [ ] 将 `USE_MOCK` 改为 `false`
  - [ ] 取消注释真实 API 调用代码
  - [ ] 确认 BASE_URL 正确：`/prj/template/evaluation-template`

#### 6.2.2 测试创建流程

- [ ] 打开创建页面
- [ ] 填写基本信息
  - [ ] 点击"自动生成"按钮，验证编码生成
  - [ ] 选择模板总分（从字典加载）
- [ ] 点击"下一步"
- [ ] 添加评分大类和评分项
  - [ ] 验证实时总分计算
  - [ ] 验证总分校验（通过/不通过）
- [ ] 点击"保存"
  - [ ] 验证成功提示
  - [ ] 验证跳转到列表页
  - [ ] 验证新模板出现在列表中

#### 6.2.3 测试编辑流程

- [ ] 从列表页点击"编辑"
- [ ] 验证数据正确加载
  - [ ] 基本信息字段正确
  - [ ] 评分项列表正确（分组展示）
- [ ] 修改基本信息
- [ ] 修改评分项（增删改）
- [ ] 点击"保存"
  - [ ] 验证成功提示
  - [ ] 验证数据更新成功

#### 6.2.4 测试详情页

- [ ] 从列表页点击"详情"
- [ ] 验证所有字段正确显示
- [ ] 验证评分项按大类分组显示
- [ ] 验证小计和总计显示正确

#### 6.2.5 测试删除功能

- [ ] 从列表页点击"删除"
- [ ] 验证确认弹窗
- [ ] 确认删除
  - [ ] 验证成功提示
  - [ ] 验证列表刷新

#### 6.2.6 测试导出功能

- [ ] 测试"导出选中"
  - [ ] 选中多个模板
  - [ ] 点击"导出选中"
  - [ ] 验证 Excel 文件下载
  - [ ] 打开 Excel，验证格式正确（包含分组、小计、总计）
- [ ] 测试"导出全部"
  - [ ] 点击"导出全部"
  - [ ] 验证 Excel 文件下载
  - [ ] 验证包含所有模板

#### 6.2.7 测试列表筛选

- [ ] 测试按编码筛选
- [ ] 测试按名称筛选
- [ ] 测试按状态筛选
- [ ] 测试组合筛选

### 6.3 边界 Case 测试

- [ ] 测试评分项数量很多的情况（如 100+ 个评分项）
- [ ] 测试大类数量很多的情况（如 20+ 个大类）
- [ ] 测试极端分数值（如 0.01、500.00）
- [ ] 测试总分精度（如 99.99 vs 100.00）
- [ ] 测试并发创建相同编码（预期一个成功，一个失败）

### 6.4 性能测试

- [ ] 测试创建包含 50 个评分项的模板（响应时间应 < 500ms）
- [ ] 测试修改包含 50 个评分项的模板（响应时间应 < 500ms）
- [ ] 测试导出 10 个模板（响应时间应 < 2s）
- [ ] 测试分页查询 1000 条记录（响应时间应 < 200ms）

### 6.5 代码审查

- [ ] 检查所有方法是否有中文注释
- [ ] 检查是否遵循 `开发标准规范.md`
- [ ] 检查是否添加了事务注解 `@Transactional`
- [ ] 检查是否添加了日志记录
- [ ] 检查是否有未使用的导入和变量
- [ ] 检查异常处理是否完善
- [ ] 检查 Swagger 文档是否完整
- [ ] 使用 SonarLint 或 Alibaba Java 代码规约插件检查

---

## 验收清单 (Acceptance Checklist)

### 功能验收

- [ ] 所有 API 接口可正常调用
- [ ] 创建模板成功，数据正确保存到数据库
- [ ] 修改模板成功，旧数据正确更新
- [ ] 删除模板成功，关联评分项同步删除
- [ ] 详情查询返回完整数据（包含评分项）
- [ ] 导出 Excel 格式正确（包含分组、小计、总计）
- [ ] 编码自动生成格式正确且唯一
- [ ] 编码唯一性校验生效
- [ ] 名称唯一性校验生效
- [ ] 总分一致性校验生效（误差 < 0.01）

### 性能验收

- [ ] 创建/修改模板响应时间 < 500ms
- [ ] 列表查询响应时间 < 200ms
- [ ] 详情查询响应时间 < 200ms
- [ ] 导出响应时间 < 2s（10 个模板）

### 代码质量验收

- [ ] 所有类、方法、字段都有中文注释
- [ ] 遵循 `开发标准规范.md`
- [ ] 遵循 Alibaba Java 编码规范
- [ ] 无 SonarLint 警告
- [ ] 单元测试覆盖率 > 80%
- [ ] 事务注解正确添加
- [ ] 异常处理完善

### 文档验收

- [ ] Swagger API 文档完整
- [ ] 所有 API 有 `@Operation` 注解
- [ ] 所有参数有 `@Schema` 注解
- [ ] 所有枚举有中文描述

---

## 注意事项 (Notes)

1. **严格遵循开发规范**
   - 参考 `docs/project/开发标准规范.md`
   - 参考现有模块（如 `UserService`, `DeptService`）

2. **数据库操作**
   - 所有写操作必须添加 `@Transactional` 注解
   - 使用 MyBatis Plus 的批量操作提高性能
   - 注意逻辑删除（`deleted = id`）

3. **BigDecimal 处理**
   - 分数字段统一使用 `BigDecimal` 类型
   - 比较时使用 `compareTo` 而不是 `equals`
   - 前后端传输时使用字符串格式

4. **错误处理**
   - 使用 `ValidationUtils.throwIf` 抛异常
   - 异常信息必须清晰易懂（中文）
   - 使用 `log.error` 记录错误日志

5. **性能优化**
   - 批量插入使用 `saveBatch(items, 1000)`
   - 避免 N+1 查询问题
   - 使用索引加速查询

6. **测试策略**
   - 先写单元测试，再写集成测试
   - 使用 H2 内存数据库进行单元测试
   - 前后端联调使用 Postman 或 Knife4j

7. **Git 提交规范**
   - 每完成一个小任务就提交一次
   - 提交信息格式：`feat: 添加评审模板创建功能`
   - 大功能完成后创建 feature 分支合并

8. **前端对接**
   - 确保接口路径和前端一致：`/prj/template/evaluation-template`
   - 确保请求/响应格式和前端定义一致
   - 测试时使用浏览器开发者工具查看网络请求

---

**预估工作量**: 6.5-8.5 个工作日
**开始日期**: [待填写]
**完成日期**: [待填写]
**负责人**: [待填写]
