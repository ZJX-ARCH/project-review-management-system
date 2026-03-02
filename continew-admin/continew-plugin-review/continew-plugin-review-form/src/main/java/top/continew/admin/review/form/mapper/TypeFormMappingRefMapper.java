package top.continew.admin.review.form.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目类型表单映射只读引用查询 Mapper
 * <p>
 * 仅用于 form 模块检查表单模板是否被 type 模块引用，避免循环依赖。
 * 不做任何写操作，也不关联 type 模块的 Service。
 * </p>
 *
 * @author zjx
 * @since 2026-03-02
 */
@Mapper
public interface TypeFormMappingRefMapper {

    /**
     * 查询引用了指定表单模板的项目类型数量（仅统计未删除的记录）
     *
     * @param formTemplateId 表单模板ID
     * @return 引用数量
     */
    @Select("SELECT COUNT(*) FROM review_type_form_mapping "
            + "WHERE form_template_id = #{formTemplateId} AND deleted = 0")
    long countByFormTemplateId(@Param("formTemplateId") Long formTemplateId);

    /**
     * 批量查询被引用的表单模板ID（仅统计未删除的记录）
     *
     * @param formTemplateIds 表单模板ID列表
     * @return 被引用的表单模板ID列表
     */
    @Select("<script>"
            + "SELECT DISTINCT form_template_id FROM review_type_form_mapping "
            + "WHERE deleted = 0 "
            + "AND form_template_id IN "
            + "<foreach item='id' collection='formTemplateIds' open='(' separator=',' close=')'>"
            + "#{id}"
            + "</foreach>"
            + "</script>")
    List<Long> findReferencedFormTemplateIds(@Param("formTemplateIds") List<Long> formTemplateIds);
}
