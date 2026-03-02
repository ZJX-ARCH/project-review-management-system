package top.continew.admin.review.type.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.common.base.mapper.DataPermissionMapper;
import top.continew.admin.review.type.model.entity.TypeVisibilityConfigDO;

/**
 * 类型可见范围配置 Mapper
 *
 * @author zjx
 * @since 2026-03-02
 */
@Mapper
public interface TypeVisibilityConfigMapper extends DataPermissionMapper<TypeVisibilityConfigDO> {
}
