package top.continew.admin.review.type.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.review.type.mapper.ProjectTypeMapper;
import top.continew.admin.review.type.model.entity.ProjectTypeDO;
import top.continew.admin.review.type.service.ProjectTypeService;

/**
 * 项目类型 Service 实现
 *
 * @author zjx
 * @since 2026-03-02
 */
@Service
@RequiredArgsConstructor
public class ProjectTypeServiceImpl extends ServiceImpl<ProjectTypeMapper, ProjectTypeDO>
        implements ProjectTypeService {
}
