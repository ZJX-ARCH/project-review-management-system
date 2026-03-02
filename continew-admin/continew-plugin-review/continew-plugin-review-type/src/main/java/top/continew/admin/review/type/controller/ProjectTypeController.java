package top.continew.admin.review.type.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.review.type.service.ProjectTypeService;

/**
 * 项目类型管理 Controller
 *
 * @author zjx
 * @since 2026-03-02
 */
@Tag(name = "项目类型管理")
@RestController
@RequestMapping("/review/type")
@RequiredArgsConstructor
public class ProjectTypeController {

    private final ProjectTypeService projectTypeService;
}
