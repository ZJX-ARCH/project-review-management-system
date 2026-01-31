package top.continew.admin.review.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 文件存储路径常量
 *
 * 集中管理项目评审系统的文件存储路径
 *
 * @author zjx
 * @since 2026-01-31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileStorageConstants {

    /**
     * 评审流程模板路径前缀
     */
    public static final String REVIEW_PROCESS_PATH = "review/process/";

    /**
     * 管理流程模板路径前缀
     */
    public static final String REVIEW_MANAGEMENT_PATH = "review/management/";

    /**
     * 表单模板文件路径前缀
     */
    public static final String REVIEW_FORM_TEMPLATE_PATH = "review/form/template/";

    /**
     * 表单示例文件路径前缀
     */
    public static final String REVIEW_FORM_EXAMPLE_PATH = "review/form/example/";

    /**
     * 项目附件路径前缀
     */
    public static final String REVIEW_PROJECT_ATTACHMENT_PATH = "review/project/attachment/";

    /**
     * 项目申请文件路径前缀
     */
    public static final String REVIEW_PROJECT_APPLICATION_PATH = "review/project/application/";

    /**
     * 项目审核文件路径前缀
     */
    public static final String REVIEW_PROJECT_AUDIT_PATH = "review/project/audit/";

    /**
     * 项目评审文件路径前缀
     */
    public static final String REVIEW_PROJECT_REVIEW_PATH = "review/project/review/";

    /**
     * 项目决策文件路径前缀
     */
    public static final String REVIEW_PROJECT_DECISION_PATH = "review/project/decision/";

    /**
     * 项目执行文件路径前缀
     */
    public static final String REVIEW_PROJECT_EXECUTION_PATH = "review/project/execution/";

    /**
     * 项目验收文件路径前缀
     */
    public static final String REVIEW_PROJECT_ACCEPTANCE_PATH = "review/project/acceptance/";
}
