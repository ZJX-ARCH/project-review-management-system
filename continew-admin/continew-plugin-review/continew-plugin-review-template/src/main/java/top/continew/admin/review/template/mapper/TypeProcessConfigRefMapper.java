package top.continew.admin.review.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目类型流程配置只读引用查询 Mapper
 * <p>
 * 仅用于 template 模块检查模板是否被 type 模块引用，避免循环依赖。
 * 不做任何写操作，也不关联 type 模块的 Service。
 * </p>
 *
 * @author zjx
 * @since 2026-03-02
 */
@Mapper
public interface TypeProcessConfigRefMapper {

    /**
     * 查询引用了指定评审流程模板的项目类型数量（仅统计未删除的记录）
     *
     * @param templateId 评审流程模板ID
     * @return 引用数量
     */
    @Select("SELECT COUNT(*) FROM review_type_process_config "
            + "WHERE template_id = #{templateId} AND process_type = 'REVIEW' AND deleted = 0")
    long countByProcessTemplateId(@Param("templateId") Long templateId);

    /**
     * 查询引用了指定管理流程模板的项目类型数量（仅统计未删除的记录）
     *
     * @param templateId 管理流程模板ID
     * @return 引用数量
     */
    @Select("SELECT COUNT(*) FROM review_type_process_config "
            + "WHERE template_id = #{templateId} AND process_type = 'MANAGE' AND deleted = 0")
    long countByManageTemplateId(@Param("templateId") Long templateId);

    /**
     * 批量查询引用了指定评审流程模板列表的项目类型ID（仅统计未删除的记录）
     *
     * @param templateIds 评审流程模板ID列表
     * @return 被引用的模板ID列表
     */
    @Select("<script>"
            + "SELECT DISTINCT template_id FROM review_type_process_config "
            + "WHERE process_type = 'REVIEW' AND deleted = 0 "
            + "AND template_id IN "
            + "<foreach item='id' collection='templateIds' open='(' separator=',' close=')'>"
            + "#{id}"
            + "</foreach>"
            + "</script>")
    List<Long> findReferencedProcessTemplateIds(@Param("templateIds") List<Long> templateIds);

    /**
     * 批量查询引用了指定管理流程模板列表的项目类型ID（仅统计未删除的记录）
     *
     * @param templateIds 管理流程模板ID列表
     * @return 被引用的模板ID列表
     */
    @Select("<script>"
            + "SELECT DISTINCT template_id FROM review_type_process_config "
            + "WHERE process_type = 'MANAGE' AND deleted = 0 "
            + "AND template_id IN "
            + "<foreach item='id' collection='templateIds' open='(' separator=',' close=')'>"
            + "#{id}"
            + "</foreach>"
            + "</script>")
    List<Long> findReferencedManageTemplateIds(@Param("templateIds") List<Long> templateIds);
}
