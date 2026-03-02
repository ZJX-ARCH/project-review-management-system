package top.continew.admin.review.type.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.continew.admin.common.base.mapper.DataPermissionMapper;
import top.continew.admin.review.type.model.entity.TypePersonnelConfigDO;

/**
 * 类型人员配置 Mapper
 *
 * @author zjx
 * @since 2026-03-02
 */
@Mapper
public interface TypePersonnelConfigMapper extends DataPermissionMapper<TypePersonnelConfigDO> {
}
