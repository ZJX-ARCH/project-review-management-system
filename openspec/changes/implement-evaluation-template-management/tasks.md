# Implementation Tasks

## Sprint 1: Frontend UI with Mock Data (3-4 days)

### 1.1 Directory Restructuring
- [ ] Create `pages/` directory
- [ ] Create `components/` directory
- [ ] Create `hooks/` directory
- [ ] Rename `index.vue` to `index.ts` (module exports)
- [ ] Delete `AddModal.vue` and `DetailDrawer.vue` (replaced by new structure)

### 1.2 API Layer Updates
- [ ] Update `api/type.ts` with new interfaces:
  - `EvaluationTemplateReq` (with items array)
  - `EvaluationTemplateItemReq`
  - `EvaluationTemplateDetailResp` (with items)
  - `EvaluationTemplateItemResp`
- [ ] Create `api/mock.ts` with sample data (3-5 templates, 3-5 categories each, 2-4 items per category)
- [ ] Update `api/index.ts` to use mock data initially

### 1.3 Main List Page (List.vue)
- [ ] Create `pages/List.vue` with GiPageLayout
- [ ] Add GiForm for search (code, name, status filters)
- [ ] Add GiTable with columns: code, name, categoryCount, itemCount, status, createTime
- [ ] Implement row selection (checkbox column) for multi-select
- [ ] Add toolbar buttons: 新增, 导出选中, 导出全部
- [ ] Configure column ellipsis + tooltip for long text
- [ ] Add action column: 详情, 编辑, 删除
- [ ] Wire up navigation to Create/Edit/Detail pages
- [ ] Implement export handlers (with mock data)

### 1.4 Create Page (Create.vue)
- [ ] Create `pages/Create.vue` with a-steps component (2 steps)
- [ ] Add step navigation (上一步, 下一步, 保存)
- [ ] Integrate BasicInfoStep for step 1
- [ ] Integrate ScoringItemsStep for step 2
- [ ] Add form validation before step transition
- [ ] Add save handler (POST to API)
- [ ] Add success/error feedback with Message

### 1.5 Edit Page (Edit.vue)
- [ ] Create `pages/Edit.vue` (similar to Create.vue)
- [ ] Load existing template data by ID
- [ ] Populate form with existing values
- [ ] Reuse Create page components
- [ ] Update save handler (PUT to API)

### 1.6 Detail Page (Detail.vue)
- [ ] Create `pages/Detail.vue` for read-only view
- [ ] Display basic info section (a-descriptions)
- [ ] Display nested table of categories and items
- [ ] Show category subtotals
- [ ] Add "返回列表" and "编辑" buttons
- [ ] Ensure all fields are non-editable

### 1.7 BasicInfoStep Component
- [ ] Create `components/BasicInfoStep.vue`
- [ ] Add GiForm with fields:
  - Template code (input + auto-generate button)
  - Template name (input with uniqueness validation)
  - Total score (select from dictionary)
  - Description (textarea)
  - Status (radio group: 启用/停用)
  - Sort (input-number, optional)
  - Remark (textarea, optional)
- [ ] Add field validations (required, maxLength, pattern)
- [ ] Wire up auto-generate code button
- [ ] Load total score options from dictionary store

### 1.8 ScoringItemsStep Component
- [ ] Create `components/ScoringItemsStep.vue` as wrapper
- [ ] Add layout: left panel (40%) + right panel (60%)
- [ ] Integrate CategoryPanel on left
- [ ] Integrate ItemPanel on right
- [ ] Add ScoreSummary at bottom
- [ ] Handle category selection state
- [ ] Pass selected category to ItemPanel

### 1.9 CategoryPanel Component
- [ ] Create `components/CategoryPanel.vue`
- [ ] Display category list with: name, subtotal score, item count
- [ ] Add category actions: 添加大类, 编辑, 删除, 上移/下移
- [ ] Handle category selection (highlight selected)
- [ ] Emit category-select event
- [ ] Add category form (inline or modal)
- [ ] Validate category name (required, unique within template)

### 1.10 ItemPanel Component
- [ ] Create `components/ItemPanel.vue`
- [ ] Display scoring items for selected category
- [ ] Show table with columns: itemName, maxScore, description, actions
- [ ] Add item actions: 添加评分项, 编辑, 删除, 上移/下移
- [ ] Add item form (inline or modal) with fields:
  - Item name (required)
  - Max score (required, number, decimal(5,2))
  - Description (optional, textarea)
- [ ] Validate max score (> 0, reasonable range)
- [ ] Emit score-change event for real-time calculation

### 1.11 ScoreSummary Component
- [ ] Create `components/ScoreSummary.vue`
- [ ] Display current total score (from template)
- [ ] Display calculated sum (from all items)
- [ ] Show validation status (green if equal, red if not)
- [ ] Add visual indicator (icon, color, progress bar)
- [ ] Display breakdown by category (expandable)

### 1.12 Form State Hook
- [ ] Create `hooks/useTemplateForm.ts`
- [ ] Manage form state (basic info + items)
- [ ] Provide methods: addCategory, updateCategory, deleteCategory, moveCategory
- [ ] Provide methods: addItem, updateItem, deleteItem, moveItem
- [ ] Calculate total score in real-time
- [ ] Validate form before submit
- [ ] Normalize data for API submission

### 1.13 UI Enhancement with frontend-design
- [ ] Apply frontend-design skill for visual improvements
- [ ] Ensure consistent spacing, colors, typography
- [ ] Add smooth transitions and animations
- [ ] Optimize mobile responsiveness
- [ ] Add loading states and skeletons

## Sprint 2: Backend Implementation (3-4 days)

### 2.1 Database Dictionary
- [ ] Create Liquibase changeset `002_dict_evaluation_template_total_score.xml`
- [ ] Add dictionary: `evaluation_template_total_score`
- [ ] Add dictionary items: 100分, 120分, 150分
- [ ] Run migration and verify

### 2.2 Request Models
- [ ] Create `EvaluationTemplateReq.java`:
  - Fields: code, name, description, totalScore, status, sort, remark
  - Nested field: `List<EvaluationTemplateItemReq> items`
  - Validation annotations (@NotBlank, @NotNull, @Valid, @Size)
- [ ] Create `EvaluationTemplateItemReq.java`:
  - Fields: categoryName, categorySort, itemName, itemSort, maxScore, description
  - Validation annotations
- [ ] Create `EvaluationTemplateExportReq.java`:
  - Fields: query, ids
  - For batch export (selected or all)

### 2.3 Response Models
- [ ] Create `EvaluationTemplateDetailResp.java` extends `EvaluationTemplateResp`:
  - Add field: `List<EvaluationTemplateItemResp> items`
  - Group items by category
- [ ] Create `EvaluationTemplateItemResp.java`:
  - Fields: id, categoryName, categorySort, itemName, itemSort, maxScore, description
- [ ] Create `EvaluationTemplateExportResp.java`:
  - Fields for Excel: code, name, categoryName, itemName, maxScore, description
  - Special fields: isCategorySubtotal, isGrandTotal

### 2.4 Service Interface
- [ ] Update `EvaluationTemplateService.java`:
  - Add method: `void create(EvaluationTemplateReq req)`
  - Add method: `void update(EvaluationTemplateReq req, Long id)`
  - Add method: `void delete(List<Long> ids)`
  - Add method: `EvaluationTemplateDetailResp getDetail(Long id)`
  - Add method: `void export(EvaluationTemplateQuery query, List<Long> ids, HttpServletResponse response)`
  - Add method: `String generateCode()`

### 2.5 Service Implementation - Create
- [ ] Implement `create()` in `EvaluationTemplateServiceImpl`:
  - Validate code uniqueness
  - Validate name uniqueness
  - Calculate and validate total score sum
  - Insert template record
  - Batch insert items (loop or batchInsert)
  - Calculate itemCount and categoryCount
  - Update template with counts
  - Add transaction annotation

### 2.6 Service Implementation - Update
- [ ] Implement `update()`:
  - Validate code uniqueness (excluding current ID)
  - Validate name uniqueness (excluding current ID)
  - Validate total score sum
  - Update template record
  - Delete existing items for template
  - Batch insert new items
  - Update counts
  - Add transaction annotation

### 2.7 Service Implementation - Delete
- [ ] Implement `delete()`:
  - Delete items for all template IDs (IN clause)
  - Delete templates by IDs
  - Add transaction annotation

### 2.8 Service Implementation - GetDetail
- [ ] Implement `getDetail()`:
  - Get template by ID
  - Get items for template (ordered by categorySort, itemSort)
  - Group items by category name
  - Build detailed response with nested structure
  - Return DetailResp

### 2.9 Service Implementation - CodeGeneration
- [ ] Implement `generateCode()`:
  - Query max existing code (e.g., TPL_005)
  - Parse number part
  - Increment and format (TPL_006)
  - Handle edge cases (empty table, non-standard codes)
  - Return generated code

### 2.10 Service Implementation - Validation
- [ ] Create private method `validateTemplate()`:
  - Check code uniqueness (with optional excludeId)
  - Check name uniqueness (with optional excludeId)
  - Validate total score range (50-500)
  - Calculate sum of item max scores
  - Compare sum with template total score
  - Throw ValidationException with clear messages

### 2.11 Mapper Enhancement
- [ ] Add custom query in `EvaluationTemplateItemMapper`:
  - `List<EvaluationTemplateItemDO> selectByTemplateId(Long templateId)`
  - `int deleteByTemplateId(Long templateId)`
  - `int deleteByTemplateIds(List<Long> templateIds)`
- [ ] Add XML mappings if needed

### 2.12 Controller Updates
- [ ] Update `EvaluationTemplateController.java`:
  - Remove `Api.CREATE`, `Api.UPDATE`, `Api.DELETE` from @CrudRequestMapping
  - Add custom `@PostMapping` for create
  - Add custom `@PutMapping("/{id}")` for update
  - Add custom `@DeleteMapping` for delete
  - Add `@PostMapping("/export")` for batch export
  - Add `@GetMapping("/generateCode")` for code generation
  - Add @SaCheckPermission annotations
  - Add @Operation annotations for API docs

## Sprint 3: Export & Integration (2-3 days)

### 3.1 Export Service Logic
- [ ] Implement `export()` in service:
  - Query templates by query OR by IDs
  - For each template, get items
  - Group items by category name (LinkedHashMap to preserve order)
  - Build export rows:
    - Add rows for each item (with template code, name, category, item details)
    - Add subtotal row after each category (with calculated sum)
    - Add grand total row (with template total score)
    - Add blank row between templates
  - Call ExcelUtils.export with custom style strategy

### 3.2 Custom Export Style Strategy
- [ ] Create `CustomGroupedExportStyleStrategy.java` implements `CellWriteHandler`:
  - Override `afterCellDispose()` method
  - Check if row is category subtotal (gray background, bold)
  - Check if row is grand total (blue background, white bold text)
  - Apply CellStyle with colors and fonts
  - Handle all columns in the row

### 3.3 Frontend-Backend Integration
- [ ] Remove mock data from `api/mock.ts`
- [ ] Update `api/index.ts` to call real backend endpoints
- [ ] Update request/response interfaces to match backend models
- [ ] Handle BigDecimal as string in TypeScript
- [ ] Add error handling for API failures
- [ ] Add loading states during API calls

### 3.4 Export Frontend Integration
- [ ] Update "导出选中" button handler:
  - Get selected row IDs from table
  - Show error if no selection
  - Call POST /export with IDs
  - Use useDownload hook
- [ ] Update "导出全部" button handler:
  - Call POST /export with query params (no IDs)
  - Use useDownload hook
- [ ] Add export loading state
- [ ] Add success/error messages

### 3.5 Code Generation Integration
- [ ] Wire up "自动生成" button in BasicInfoStep:
  - Call GET /generateCode
  - Populate code field with result
  - Disable code input temporarily
  - Allow manual edit if needed

### 3.6 End-to-End Testing
- [ ] Test create flow (both steps, save)
- [ ] Test edit flow (load, modify, save)
- [ ] Test delete (single and batch)
- [ ] Test detail view (read-only)
- [ ] Test export (selected and all)
- [ ] Test code generation
- [ ] Test all validations (uniqueness, score sum)

### 3.7 Edge Case Handling
- [ ] Handle empty categories (no items)
- [ ] Handle decimal precision (5,2)
- [ ] Handle large datasets (pagination, performance)
- [ ] Handle concurrent edits (optimistic locking)
- [ ] Handle network failures (retry, timeout)

## Sprint 4: Polish & Testing (1-2 days)

### 4.1 UI/UX Refinements
- [ ] Review and improve visual design
- [ ] Add smooth transitions between steps
- [ ] Improve form layout and spacing
- [ ] Add helpful tooltips and hints
- [ ] Improve error message clarity

### 4.2 Error Handling
- [ ] Add user-friendly error messages for all failures
- [ ] Add validation feedback (inline errors)
- [ ] Add global error handler
- [ ] Log errors for debugging

### 4.3 Performance Optimization
- [ ] Optimize table rendering (virtual scroll if needed)
- [ ] Debounce score calculation
- [ ] Lazy load dictionary data
- [ ] Optimize export for large datasets

### 4.4 User Acceptance Testing
- [ ] Test with real user scenarios
- [ ] Gather feedback on UX
- [ ] Fix reported issues
- [ ] Verify all success criteria met

### 4.5 Documentation
- [ ] Update API documentation (Knife4j)
- [ ] Document validation rules
- [ ] Document export format
- [ ] Add inline code comments

## Validation Checklist

- [ ] Template code uniqueness enforced
- [ ] Template name uniqueness enforced
- [ ] Total score validation (sum of items must equal template total)
- [ ] Total score comes from dictionary
- [ ] Status uses DisEnableStatusEnum
- [ ] Multi-select export works (selected only)
- [ ] Export all works (when no selection)
- [ ] Export format has category grouping with subtotals
- [ ] Step wizard navigation works
- [ ] Category/item CRUD works in nested UI
- [ ] Real-time score calculation displays correctly
- [ ] Detail page is read-only and separate from edit
- [ ] Long text truncates with ellipsis + tooltip
- [ ] Mobile responsiveness maintained
- [ ] All permissions check correctly
