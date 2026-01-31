package top.continew.admin.review.form.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.review.form.mapper.FormFieldMapper;
import top.continew.admin.review.form.mapper.FormTemplateFileMapper;
import top.continew.admin.review.form.mapper.FormTemplateMapper;
import top.continew.admin.review.form.model.entity.FormTemplateDO;
import top.continew.admin.review.form.model.query.FormTemplateQuery;
import top.continew.admin.review.form.model.req.FormTemplateReq;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.form.service.FormTemplateService;
import top.continew.admin.system.service.FileService;
import top.continew.starter.data.service.impl.ServiceImpl;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.io.IOException;
import java.util.List;

/**
 * 表单模板业务实现
 *
 * @author zjx
 * @since 2026-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormTemplateServiceImpl extends ServiceImpl<FormTemplateMapper, FormTemplateDO>
    implements FormTemplateService {

    private final FormTemplateMapper formTemplateMapper;
    private final FormFieldMapper formFieldMapper;
    private final FormTemplateFileMapper formTemplateFileMapper;
    private final FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FormTemplateReq req) {
        // TODO: 1. 校验输入参数
        // TODO: 1.1 校验模板名称不能为空
        // TODO: 1.2 校验模板编码不能为空
        // TODO: 1.3 校验模板类型不能为空
        // TODO: 1.4 校验管理阶段不能为空(立项KICKOFF/执行EXECUTION/验收ACCEPTANCE)
        // TODO: 1.5 校验字段列表不能为空
        // TODO: 1.6 使用 FieldConfigValidator.validateFieldCodeUnique(req.getFields()) 校验字段编码唯一性
        // TODO: 1.7 使用 FieldConfigValidator.validateFieldOrder(req.getFields()) 校验字段排序连续性
        // TODO: 1.8 使用 FieldConfigValidator.validateFieldSpan(req.getFields()) 校验字段span布局合理性

        // TODO: 2. 校验业务唯一性
        // TODO: 2.1 根据模板编码查询是否已存在: FormTemplateDO existByCode = getByCode(req.getTemplateCode())
        // TODO: 2.2 如果存在,抛出 BusinessException("模板编码已存在")
        // TODO: 2.3 根据模板名称查询是否已存在: QueryWrapper<FormTemplateDO> queryByName = new QueryWrapper<>();
        // TODO: 2.4 queryByName.eq("template_name", req.getTemplateName())
        // TODO: 2.5 如果存在,抛出 BusinessException("模板名称已存在")

        // TODO: 3. 构建主表实体并保存
        // TODO: 3.1 创建 FormTemplateDO entity = new FormTemplateDO()
        // TODO: 3.2 设置基本属性: templateCode, templateName, description, templateType, stageType
        // TODO: 3.3 设置状态: status = 0 (默认禁用)
        // TODO: 3.4 设置创建信息: createTime, createUser (从 SecurityContextHolder 获取当前用户)
        // TODO: 3.5 调用 baseMapper.insert(entity) 保存主表
        // TODO: 3.6 获取自增主键: Long templateId = entity.getId()
        // TODO: 3.7 记录日志: log.info("创建表单模板成功, ID: {}, 编码: {}", templateId, req.getTemplateCode())

        // TODO: 4. 批量保存字段配置
        // TODO: 4.1 遍历 req.getFields(), 构建 List<FormFieldDO>
        // TODO: 4.2 对每个字段设置: templateId, fieldCode, fieldName, fieldType, isRequired, defaultValue, span, sort
        // TODO: 4.3 设置 configOptions (JSON字符串): 使用 JsonUtil.toJsonString() 转换 field.getOptions()
        // TODO: 4.4 设置 validationRules (JSON字符串): 使用 JsonUtil.toJsonString() 转换 field.getRules()
        // TODO: 4.5 设置创建信息: createTime, createUser
        // TODO: 4.6 调用 formFieldMapper 批量插入: 可使用 saveBatch() 或循环 insert()
        // TODO: 4.7 记录日志: log.info("保存表单字段配置成功, 模板ID: {}, 字段数: {}", templateId, req.getFields().size())

        // TODO: 5. 返回新建模板ID
        // TODO: 5.1 return templateId

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, FormTemplateReq req) {
        // TODO: 1. 校验模板是否存在
        // TODO: 1.1 根据ID查询模板: FormTemplateDO entity = baseMapper.selectById(id)
        // TODO: 1.2 如果不存在,抛出 BusinessException("模板不存在")
        // TODO: 1.3 记录日志: log.info("开始更新表单模板, ID: {}, 原编码: {}", id, entity.getTemplateCode())

        // TODO: 2. 校验输入参数(同create)
        // TODO: 2.1 校验模板名称不能为空
        // TODO: 2.2 校验字段列表不能为空
        // TODO: 2.3 使用 FieldConfigValidator.validateFieldCodeUnique(req.getFields()) 校验字段编码唯一性
        // TODO: 2.4 使用 FieldConfigValidator.validateFieldOrder(req.getFields()) 校验字段排序连续性
        // TODO: 2.5 使用 FieldConfigValidator.validateFieldSpan(req.getFields()) 校验字段span布局合理性

        // TODO: 3. 校验业务唯一性(排除自身)
        // TODO: 3.1 如果模板名称修改了,查询是否与其他模板重名
        // TODO: 3.2 QueryWrapper<FormTemplateDO> queryByName = new QueryWrapper<>();
        // TODO: 3.3 queryByName.eq("template_name", req.getTemplateName()).ne("id", id)
        // TODO: 3.4 如果存在,抛出 BusinessException("模板名称已存在")

        // TODO: 4. 更新主表信息
        // TODO: 4.1 更新基本属性: templateName, description, templateType, stageType
        // TODO: 4.2 注意: templateCode 不允许修改
        // TODO: 4.3 设置更新信息: updateTime, updateUser (从 SecurityContextHolder 获取当前用户)
        // TODO: 4.4 调用 baseMapper.updateById(entity)
        // TODO: 4.5 记录日志: log.info("更新表单模板主表成功, ID: {}", id)

        // TODO: 5. 删除原字段配置
        // TODO: 5.1 QueryWrapper<FormFieldDO> deleteQuery = new QueryWrapper<>();
        // TODO: 5.2 deleteQuery.eq("template_id", id)
        // TODO: 5.3 调用 formFieldMapper.delete(deleteQuery) 删除所有旧字段
        // TODO: 5.4 记录日志: log.info("删除原字段配置成功, 模板ID: {}", id)

        // TODO: 6. 重新保存字段配置(同create步骤4)
        // TODO: 6.1 遍历 req.getFields(), 构建 List<FormFieldDO>
        // TODO: 6.2 对每个字段设置: templateId, fieldCode, fieldName, fieldType, isRequired, defaultValue, span, sort
        // TODO: 6.3 设置 configOptions: JsonUtil.toJsonString(field.getOptions())
        // TODO: 6.4 设置 validationRules: JsonUtil.toJsonString(field.getRules())
        // TODO: 6.5 设置创建信息: createTime, createUser
        // TODO: 6.6 批量插入字段配置
        // TODO: 6.7 记录日志: log.info("重新保存字段配置成功, 模板ID: {}, 字段数: {}", id, req.getFields().size())

        // TODO: 7. 同步更新文件关联的字段ID(如果字段ID发生变化)
        // TODO: 7.1 查询该模板的所有附件文件: List<FormTemplateFileDO> files = formTemplateFileMapper.selectByTemplateId(id)
        // TODO: 7.2 遍历文件,根据 fieldCode 匹配新的 fieldId
        // TODO: 7.3 更新文件表的 fieldId 字段
        // TODO: 7.4 记录日志: log.info("同步更新文件字段关联成功, 模板ID: {}", id)

        // TODO: 8. 记录完成日志
        // TODO: 8.1 log.info("更新表单模板完成, ID: {}, 字段数: {}", id, req.getFields().size())
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // TODO: 1. 参数校验
        // TODO: 1.1 校验 ids 不能为空: if (CollUtil.isEmpty(ids))
        // TODO: 1.2 记录日志: log.info("开始批量删除表单模板, IDs: {}", ids)

        // TODO: 2. 校验模板是否可以删除
        // TODO: 2.1 遍历每个ID,查询模板是否存在
        // TODO: 2.2 校验模板是否被类型配置引用(查询 review_type 表的 form_template_config 字段)
        // TODO: 2.3 如果被引用,抛出 BusinessException("模板已被类型配置引用,无法删除")
        // TODO: 2.4 校验模板是否有正在进行的评审项目使用(如有项目管理模块)
        // TODO: 2.5 如果有项目使用,抛出 BusinessException("模板正在使用中,无法删除")

        // TODO: 3. 删除关联的附件文件
        // TODO: 3.1 遍历每个模板ID
        // TODO: 3.2 查询该模板的所有附件: List<FormTemplateFileDO> files = formTemplateFileMapper.selectByTemplateId(templateId)
        // TODO: 3.3 遍历附件,调用 fileService.delete(file.getFileId()) 删除物理文件
        // TODO: 3.4 删除文件记录: formTemplateFileMapper.deleteByTemplateId(templateId)
        // TODO: 3.5 记录日志: log.info("删除模板附件成功, 模板ID: {}, 文件数: {}", templateId, files.size())

        // TODO: 4. 删除字段配置
        // TODO: 4.1 遍历每个模板ID
        // TODO: 4.2 QueryWrapper<FormFieldDO> deleteFieldQuery = new QueryWrapper<>();
        // TODO: 4.3 deleteFieldQuery.eq("template_id", templateId)
        // TODO: 4.4 调用 formFieldMapper.delete(deleteFieldQuery)
        // TODO: 4.5 记录日志: log.info("删除字段配置成功, 模板ID: {}", templateId)

        // TODO: 5. 删除模板主表
        // TODO: 5.1 调用 baseMapper.deleteBatchIds(ids) 批量删除
        // TODO: 5.2 记录日志: log.info("批量删除表单模板成功, 删除数量: {}", ids.size())

        // TODO: 6. 记录完成日志
        // TODO: 6.1 log.info("批量删除表单模板完成, IDs: {}", ids)
    }

    @Override
    public FormTemplateResp getDetail(Long id) {
        // TODO: 1. 查询模板主表信息
        // TODO: 1.1 根据ID查询: FormTemplateDO entity = baseMapper.selectById(id)
        // TODO: 1.2 如果不存在,抛出 BusinessException("模板不存在")
        // TODO: 1.3 转换为响应对象: FormTemplateResp resp = BeanUtil.copyProperties(entity, FormTemplateResp.class)

        // TODO: 2. 查询字段配置列表
        // TODO: 2.1 QueryWrapper<FormFieldDO> fieldQuery = new QueryWrapper<>();
        // TODO: 2.2 fieldQuery.eq("template_id", id).orderByAsc("sort")
        // TODO: 2.3 查询字段列表: List<FormFieldDO> fieldList = formFieldMapper.selectList(fieldQuery)
        // TODO: 2.4 转换为响应对象: List<FormFieldResp>
        // TODO: 2.5 对每个字段,解析JSON: options = JsonUtil.parseJson(field.getConfigOptions())
        // TODO: 2.6 对每个字段,解析JSON: rules = JsonUtil.parseJson(field.getValidationRules())
        // TODO: 2.7 设置到响应对象: resp.setFields(fieldRespList)

        // TODO: 3. 查询附件文件列表
        // TODO: 3.1 QueryWrapper<FormTemplateFileDO> fileQuery = new QueryWrapper<>();
        // TODO: 3.2 fileQuery.eq("template_id", id).orderByDesc("upload_time")
        // TODO: 3.3 查询文件列表: List<FormTemplateFileDO> fileList = formTemplateFileMapper.selectList(fileQuery)
        // TODO: 3.4 转换为响应对象: List<FormTemplateFileResp>
        // TODO: 3.5 对每个文件,调用 fileService.getFileInfo(file.getFileId()) 获取文件详情
        // TODO: 3.6 设置文件访问URL: fileResp.setFileUrl(fileInfo.getUrl())
        // TODO: 3.7 设置到响应对象: resp.setFiles(fileRespList)

        // TODO: 4. 返回完整详情
        // TODO: 4.1 记录日志: log.info("查询表单模板详情成功, ID: {}, 字段数: {}", id, resp.getFields().size())
        // TODO: 4.2 return resp

        return null;
    }

    @Override
    public PageResp<FormTemplateResp> page(FormTemplateQuery query, PageQuery pageQuery) {
        // TODO: 1. 构建查询条件
        // TODO: 1.1 创建 QueryWrapper<FormTemplateDO>
        // TODO: 1.2 如果 query.getTemplateName() 不为空,添加 like 条件: .like("template_name", query.getTemplateName())
        // TODO: 1.3 如果 query.getTemplateCode() 不为空,添加 eq 条件: .eq("template_code", query.getTemplateCode())
        // TODO: 1.4 如果 query.getTemplateType() 不为空,添加 eq 条件: .eq("template_type", query.getTemplateType())
        // TODO: 1.5 如果 query.getStageType() 不为空,添加 eq 条件: .eq("stage_type", query.getStageType())
        // TODO: 1.6 如果 query.getStatus() 不为空,添加 eq 条件: .eq("status", query.getStatus())
        // TODO: 1.7 添加排序: .orderByDesc("create_time")

        // TODO: 2. 执行分页查询
        // TODO: 2.1 创建分页对象: Page<FormTemplateDO> page = new Page<>(pageQuery.getPage(), pageQuery.getSize())
        // TODO: 2.2 执行查询: Page<FormTemplateDO> pageResult = baseMapper.selectPage(page, queryWrapper)
        // TODO: 2.3 记录日志: log.info("分页查询表单模板, 当前页: {}, 每页数: {}, 总数: {}", pageQuery.getPage(), pageQuery.getSize(), pageResult.getTotal())

        // TODO: 3. 转换为响应对象
        // TODO: 3.1 获取记录列表: List<FormTemplateDO> records = pageResult.getRecords()
        // TODO: 3.2 转换为响应列表: List<FormTemplateResp> respList = BeanUtil.copyToList(records, FormTemplateResp.class)

        // TODO: 4. 补充字段数量统计(可选,提升用户体验)
        // TODO: 4.1 遍历每个模板响应对象
        // TODO: 4.2 查询字段数量: Integer fieldCount = formFieldMapper.countByTemplateId(template.getId())
        // TODO: 4.3 设置字段数量: template.setFieldCount(fieldCount)

        // TODO: 5. 构建分页响应
        // TODO: 5.1 创建 PageResp<FormTemplateResp>
        // TODO: 5.2 设置总数: pageResp.setTotal(pageResult.getTotal())
        // TODO: 5.3 设置记录: pageResp.setRecords(respList)
        // TODO: 5.4 return pageResp

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadTemplateFile(Long templateId, Long fieldId, MultipartFile file) throws IOException {
        // TODO: 1. 参数校验
        // TODO: 1.1 校验 templateId 不能为空
        // TODO: 1.2 校验 fieldId 不能为空
        // TODO: 1.3 校验 file 不能为空且不为空文件
        // TODO: 1.4 记录日志: log.info("开始上传模板文件, 模板ID: {}, 字段ID: {}, 文件名: {}", templateId, fieldId, file.getOriginalFilename())

        // TODO: 2. 校验模板和字段是否存在
        // TODO: 2.1 查询模板: FormTemplateDO template = baseMapper.selectById(templateId)
        // TODO: 2.2 如果不存在,抛出 BusinessException("模板不存在")
        // TODO: 2.3 查询字段: FormFieldDO field = formFieldMapper.selectById(fieldId)
        // TODO: 2.4 如果不存在,抛出 BusinessException("字段不存在")
        // TODO: 2.5 校验字段是否属于该模板: if (!field.getTemplateId().equals(templateId))
        // TODO: 2.6 如果不属于,抛出 BusinessException("字段不属于该模板")

        // TODO: 3. 校验文件类型和大小
        // TODO: 3.1 获取字段的文件类型配置: String allowedTypes = field.getFileTypes() (从 configOptions 中解析)
        // TODO: 3.2 获取文件扩展名: String ext = FileUtil.extName(file.getOriginalFilename())
        // TODO: 3.3 校验文件类型是否允许: if (!allowedTypes.contains(ext))
        // TODO: 3.4 如果不允许,抛出 BusinessException("不支持的文件类型")
        // TODO: 3.5 获取文件大小限制: Long maxSize = field.getMaxFileSize() (从 configOptions 中解析)
        // TODO: 3.6 校验文件大小: if (file.getSize() > maxSize)
        // TODO: 3.7 如果超限,抛出 BusinessException("文件大小超过限制")

        // TODO: 4. 上传文件到存储服务
        // TODO: 4.1 构建文件路径: String path = FileStorageConstants.REVIEW_FORM_TEMPLATE_PATH + "/" + templateId
        // TODO: 4.2 调用文件服务上传: FileInfo fileInfo = fileService.upload(file, path)
        // TODO: 4.3 获取文件ID: Long fileId = fileInfo.getId()
        // TODO: 4.4 记录日志: log.info("上传文件到存储服务成功, 文件ID: {}, 路径: {}", fileId, fileInfo.getUrl())

        // TODO: 5. 保存文件关联记录
        // TODO: 5.1 创建 FormTemplateFileDO fileEntity = new FormTemplateFileDO()
        // TODO: 5.2 设置属性: templateId, fieldId, fileId, fileName, fileType, fileSize
        // TODO: 5.3 设置上传信息: uploadTime, uploadUser (从 SecurityContextHolder 获取当前用户)
        // TODO: 5.4 调用 formTemplateFileMapper.insert(fileEntity)
        // TODO: 5.5 获取关联记录ID: Long recordId = fileEntity.getId()
        // TODO: 5.6 记录日志: log.info("保存文件关联记录成功, 记录ID: {}", recordId)

        // TODO: 6. 检查字段文件数量限制(可选)
        // TODO: 6.1 获取字段的文件数量限制: Integer maxCount = field.getMaxFileCount()
        // TODO: 6.2 查询当前字段已有文件数: Integer currentCount = formTemplateFileMapper.countByFieldId(fieldId)
        // TODO: 6.3 如果超过限制,删除最早上传的文件(或抛出异常)

        // TODO: 7. 返回文件关联记录ID
        // TODO: 7.1 return recordId

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplateFile(Long fileId) {
        // TODO: 1. 参数校验
        // TODO: 1.1 校验 fileId 不能为空
        // TODO: 1.2 记录日志: log.info("开始删除模板文件, 文件记录ID: {}", fileId)

        // TODO: 2. 查询文件关联记录
        // TODO: 2.1 查询: FormTemplateFileDO fileEntity = formTemplateFileMapper.selectById(fileId)
        // TODO: 2.2 如果不存在,抛出 BusinessException("文件记录不存在")
        // TODO: 2.3 获取存储文件ID: Long storageFileId = fileEntity.getFileId()

        // TODO: 3. 删除物理文件
        // TODO: 3.1 调用文件服务删除: fileService.delete(storageFileId)
        // TODO: 3.2 记录日志: log.info("删除物理文件成功, 存储文件ID: {}", storageFileId)
        // TODO: 3.3 处理异常: 如果文件服务删除失败,记录警告日志但继续删除关联记录

        // TODO: 4. 删除文件关联记录
        // TODO: 4.1 调用 formTemplateFileMapper.deleteById(fileId)
        // TODO: 4.2 记录日志: log.info("删除文件关联记录成功, 记录ID: {}", fileId)

        // TODO: 5. 记录完成日志
        // TODO: 5.1 log.info("删除模板文件完成, 文件记录ID: {}", fileId)
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // TODO: 1. 参数校验
        // TODO: 1.1 校验 id 不能为空
        // TODO: 1.2 校验 status 不能为空
        // TODO: 1.3 校验 status 取值范围: 0-禁用, 1-启用
        // TODO: 1.4 如果不在范围内,抛出 BusinessException("状态值非法")
        // TODO: 1.5 记录日志: log.info("开始更新模板状态, ID: {}, 目标状态: {}", id, status)

        // TODO: 2. 查询模板是否存在
        // TODO: 2.1 查询: FormTemplateDO entity = baseMapper.selectById(id)
        // TODO: 2.2 如果不存在,抛出 BusinessException("模板不存在")
        // TODO: 2.3 获取当前状态: Integer currentStatus = entity.getStatus()
        // TODO: 2.4 如果状态相同,直接返回: if (currentStatus.equals(status)) return;

        // TODO: 3. 业务校验(可选)
        // TODO: 3.1 如果要启用,校验模板配置是否完整(至少有一个字段)
        // TODO: 3.2 查询字段数量: Integer fieldCount = formFieldMapper.countByTemplateId(id)
        // TODO: 3.3 如果字段数为0,抛出 BusinessException("模板未配置字段,无法启用")

        // TODO: 4. 更新状态
        // TODO: 4.1 设置新状态: entity.setStatus(status)
        // TODO: 4.2 设置更新信息: updateTime, updateUser
        // TODO: 4.3 调用 baseMapper.updateById(entity)
        // TODO: 4.4 记录日志: log.info("更新模板状态成功, ID: {}, 状态: {} -> {}", id, currentStatus, status)

        // TODO: 5. 记录完成日志
        // TODO: 5.1 log.info("更新模板状态完成, ID: {}, 新状态: {}", id, status)
    }

    @Override
    public FormTemplateDO getByCode(String templateCode) {
        // TODO: 1. 参数校验
        // TODO: 1.1 校验 templateCode 不能为空: if (StrUtil.isBlank(templateCode))
        // TODO: 1.2 如果为空,抛出 BusinessException("模板编码不能为空")

        // TODO: 2. 根据编码查询模板
        // TODO: 2.1 创建 QueryWrapper<FormTemplateDO> query = new QueryWrapper<>();
        // TODO: 2.2 添加条件: query.eq("template_code", templateCode)
        // TODO: 2.3 查询: FormTemplateDO entity = baseMapper.selectOne(query)
        // TODO: 2.4 return entity (可能为null,由调用方判断)

        return null;
    }

    @Override
    public String generateCode() {
        // TODO: 1. 生成唯一编码(FORM_ + 时间戳)
        // TODO: 1.1 定义编码前缀: String prefix = "FORM_"
        // TODO: 1.2 获取当前时间戳: long timestamp = System.currentTimeMillis()
        // TODO: 1.3 拼接编码: String code = prefix + timestamp

        // TODO: 2. 校验编码唯一性(防止并发冲突)
        // TODO: 2.1 查询编码是否已存在: FormTemplateDO existing = getByCode(code)
        // TODO: 2.2 如果存在,重新生成: while (existing != null)
        // TODO: 2.3 重试时添加随机数: code = prefix + System.currentTimeMillis() + "_" + RandomUtil.randomNumbers(3)
        // TODO: 2.4 重新查询: existing = getByCode(code)
        // TODO: 2.5 最多重试3次,如果仍冲突,抛出异常

        // TODO: 3. 返回生成的编码
        // TODO: 3.1 记录日志: log.info("生成模板编码: {}", code)
        // TODO: 3.2 return code

        return null;
    }

    @Override
    public List<FormTemplateResp> listEnabled(Integer templateType) {
        // TODO: 1. 构建查询条件
        // TODO: 1.1 创建 QueryWrapper<FormTemplateDO> query = new QueryWrapper<>();
        // TODO: 1.2 添加状态条件: query.eq("status", 1) (1-启用)
        // TODO: 1.3 如果 templateType 不为空,添加类型条件: query.eq("template_type", templateType)
        // TODO: 1.4 添加排序: query.orderByDesc("create_time")

        // TODO: 2. 查询启用的模板列表
        // TODO: 2.1 查询: List<FormTemplateDO> list = baseMapper.selectList(query)
        // TODO: 2.2 记录日志: log.info("查询启用模板列表, 类型: {}, 数量: {}", templateType, list.size())

        // TODO: 3. 转换为响应对象
        // TODO: 3.1 转换: List<FormTemplateResp> respList = BeanUtil.copyToList(list, FormTemplateResp.class)
        // TODO: 3.2 对每个模板,可选择性填充字段数量统计
        // TODO: 3.3 遍历: for (FormTemplateResp resp : respList)
        // TODO: 3.4 查询字段数: Integer fieldCount = formFieldMapper.countByTemplateId(resp.getId())
        // TODO: 3.5 设置: resp.setFieldCount(fieldCount)

        // TODO: 4. 返回结果列表
        // TODO: 4.1 return respList

        return null;
    }
}
