# Project Context

## Purpose

**Project Review Management System (PRMS)** - A comprehensive review and evaluation management platform built on the ContiNew Admin framework. The system provides:

-   Project review workflow management
-   Expert assignment and evaluation tracking
-   Template-based evaluation forms
-   Review progress monitoring
-   Multi-level approval processes
-   Statistical analysis and reporting

Built on enterprise-grade frameworks to provide a robust, scalable solution for managing review processes in organizations.

## Tech Stack

### Frontend

-   **Framework**: Vue 3.5.4 (Progressive JavaScript framework)
-   **UI Library**: Arco Design 2.57.0 (ByteDance's modern component library)
-   **Language**: TypeScript 5.0.4 (Type-safe JavaScript)
-   **Build Tool**: Vite 5.1.5 (Next-generation frontend tooling)
-   **State Management**: Pinia 2.0.16 with persistence
-   **Routing**: Vue Router 4.3.3 (Dynamic routing with permission control)
-   **HTTP Client**: Axios 0.27.2
-   **Charts**: ECharts 5.4.2 + vue-echarts 6.5.5
-   **Rich Text**: AIEditor 1.0.13
-   **File Preview**: @vue-office (docx, excel, pdf support)
-   **Code Editor**: CodeMirror 6.0.1
-   **Package Manager**: pnpm

### Backend

-   **Framework**: Spring Boot 3.3.12 (Java 17 required)
-   **Base Framework**: ContiNew Starter 2.14.0 (Enterprise starter with auto-configuration)
-   **Web Server**: Undertow 2.3.18.Final (High-performance NIO server)
-   **Authentication**: SaToken 1.44.0 + JWT (Lightweight auth framework)
-   **Database**: MySQL 8.0.42
-   **ORM**: MyBatis Plus 3.5.12 (Enhanced MyBatis)
-   **Connection Pool**: Hikari 5.1.0 (Ultra-fast JDBC pool)
-   **Multi-Datasource**: dynamic-datasource 4.3.1
-   **Database Migration**: Liquibase 4.27.0
-   **Cache**: JetCache 2.7.8 (Multi-level caching) + Redisson 3.49.0
-   **NoSQL**: Redis 7.2.8
-   **Task Scheduling**: Snail Job 1.5.0
-   **File Storage**: X File Storage 2.2.1 (Multi-storage support)
-   **SMS**: SMS4J 3.3.4 (SMS aggregation)
-   **Third-party Auth**: JustAuth 1.16.7
-   **Excel**: Fast Excel 1.2.0
-   **Captcha**: AJ-Captcha 1.3.0 + Easy Captcha 1.6.2
-   **Data Filling**: Crane4j 2.9.0
-   **Validation**: SpEL Validator 0.5.2-beta
-   **ID Generator**: CosID 2.13.0
-   **Response Wrapper**: Graceful Response 5.0.4-boot3
-   **API Docs**: Knife4j 4.5.0 (OpenAPI 3)
-   **HTTP Client**: OpenFeign 13.5
-   **Utilities**: Hutool 5.8.38, Lombok 1.18.36

## Project Conventions

### Code Style

-   **Java**: Follow Alibaba Java Coding Guidelines
    -   Use Lombok to reduce boilerplate (@Data, @Getter, @Setter, @Slf4j)
    -   Package structure: controller → service → mapper → model (entity/query/req/resp)
        -   Constants in separate constant classes
    -   Enums for type-safe constants
-   **Frontend**: ESLint with @antfu/eslint-config
    -   Use TypeScript for type safety
    -   Composition API for Vue components
    -   Script setup syntax preferred
    -   Use `<script setup lang="ts">` for components

### Architecture Patterns

#### Backend Architecture

-   **Modular Design**: Function-based module separation
    
    -   `continew-server`: Deployment module (contains main application)
    -   `continew-system`: System management (user, role, menu, dept, etc.)
    -   `continew-plugin`: Plugin modules (open API, tenant, schedule, generator)
    -   `continew-common`: Common utilities and configurations
    -   `continew-extension`: Extension modules
    -   `prms-continew-config`: Custom PRMS configuration module
-   **Layered Architecture**:
    
    -   Controller (API layer) → Service (Business logic) → Mapper (Data access)
    -   Request/Response DTOs separate from entities
    -   Query objects for search conditions
-   **Permission System**:
    
    -   Function permissions via SaToken annotations
    -   Data permissions via MyBatis Plus interceptors
    -   Button-level permission control with `v-permission` directive

#### Frontend Architecture

-   **Dynamic Routing**: Menu-driven route generation from backend
-   **Permission Control**: Role-based and permission-based access control
    -   Directive: `v-permission="['system:user:add']"`
    -   API: `hasPerm()`, `hasRole()`, `hasPermOr()`, `hasPermAnd()`
-   **State Management**: Pinia stores with persistence
    -   `useUserStore`: User info and auth
    -   `useRouteStore`: Dynamic routes
    -   Module-specific stores for features
-   **Component Structure**:
    -   `/src/views`: Page components (organized by module)
    -   `/src/components`: Shared business components
    -   `/src/layout`: Layout components

### Testing Strategy

-   **Backend**: Unit tests skipped by default (configure in maven-surefire-plugin)
-   **Frontend**: Vue TSC type checking before build
-   **Integration**: Test environments configured (dev, test, prod)

### Git Workflow

-   **Branching**: Feature branch workflow
    -   `master`: Main development branch
    -   Feature branches for new capabilities
-   **Commit Messages**: Clear, descriptive messages
    -   Use OpenSpec workflow for significant changes
    -   Commit format: `type: description` (e.g., `feat: add user management`)

## Domain Context

### Core Domains

1.  **Review Management**: Project review workflows and processes
2.  **Template Management**: Configurable evaluation templates
3.  **Expert Management**: Expert assignment and tracking
4.  **Evaluation Tracking**: Review progress and status monitoring
5.  **Statistical Analysis**: Reports and data visualization

### Key Business Entities

-   Projects: Items under review
-   Reviews: Individual review instances
-   Evaluations: Expert assessments
-   Templates: Evaluation criteria definitions
-   Experts: Review participants
-   Applications: Review requests

### User Roles

-   System Admin: Full system access
-   Department Manager: Department-level management
-   Expert: Evaluation and review
-   Applicant: Submit review applications

## Important Constraints

### Technical Constraints

-   **Java Version**: Minimum Java 17 (Spring Boot 3.x requirement)
-   **Database**: MySQL 8.0+ (uses newer SQL features)
-   **Browser Support**: Modern browsers (ES6+ support required)
-   **Node Version**: Compatible with pnpm package manager

### Business Constraints

-   Multi-tenant support via tenant plugin
-   Data permission isolation by department/role
-   Audit logging for all operations
-   Password policies configurable via system settings

### Security Requirements

-   JWT token-based authentication
-   Password encryption (configurable encoders)
-   XSS protection enabled
-   CSRF protection for state-changing operations
-   Rate limiting for API endpoints
-   Captcha for sensitive operations

## External Dependencies

### Infrastructure Services

-   **MySQL Database**: Primary data store
-   **Redis**: Cache and session storage
-   **File Storage**: Local or S3-compatible object storage
-   **Email Service**: SMTP for notifications (configurable)
-   **SMS Service**: SMS4J for OTP and alerts (configurable)

### Third-party Integrations

-   **WeChat Login**: OAuth integration via JustAuth
-   **Snail Job Server**: Distributed task scheduling (requires separate deployment)
-   **Knife4j**: API documentation (dev environment)

### Development Tools

-   **Maven**: Backend dependency management (repositories: HuaweiCloud, AliYun mirrors)
-   **pnpm**: Frontend package management (registry: npmmirror.com)
-   **Liquibase**: Database version control and migration

## Development Patterns

### Backend Development Patterns

#### Controller Layer Standards

**Pattern**: Controllers extend `BaseController` with 5 generic parameters

```java
@Tag(name = "XXX管理 API")@Validated@RestController@RequiredArgsConstructor@CrudRequestMapping(value = "/api/path", api = {Api.PAGE, Api.LIST, Api.GET,    Api.CREATE, Api.UPDATE, Api.BATCH_DELETE, Api.EXPORT, Api.DICT})public class XxxController extends BaseController<XxxService, XxxResp, XxxDetailResp, XxxQuery, XxxReq> {    // Custom methods only - basic CRUD provided by BaseController}
```

**Key Annotations**:

-   `@Tag(name)` - Swagger/OpenAPI documentation tag (required)
-   `@Validated` - Enable parameter validation
-   `@RequiredArgsConstructor` - Lombok constructor injection
-   `@CrudRequestMapping` - Replaces `@RequestMapping`, specifies available APIs
    -   `Api.PAGE` - Paginated query
    -   `Api.LIST` - List query
    -   `Api.TREE` - Tree query
    -   `Api.GET` - Detail query
    -   `Api.CREATE` - Create
    -   `Api.UPDATE` - Update
    -   `Api.BATCH_DELETE` - Batch delete
    -   `Api.EXPORT` - Export
    -   `Api.DICT` - Dictionary data
    -   `Api.TREE_DICT` - Tree dictionary
-   `@SaCheckPermission("permission:code")` - Permission check for custom methods
-   `@Operation(summary, description)` - API documentation for custom methods

**BaseController Generics**:

1.  Service interface type
2.  List response type
3.  Detail response type
4.  Query condition type
5.  Request parameter type

**Example - Simple Controller** (No code needed):

```java
@Tag(name = "部门管理 API")@RestController@CrudRequestMapping(value = "/system/dept", api = {Api.TREE, Api.GET, Api.CREATE, Api.UPDATE, Api.BATCH_DELETE, Api.EXPORT, Api.TREE_DICT})public class DeptController extends BaseController<DeptService, DeptResp, DeptResp, DeptQuery, DeptReq> {    // No methods needed - BaseController provides all CRUD}
```

**Hook Method** (Optional - for pre/post processing):

```java
@Overridepublic void preHandle(CrudApi crudApi, Object[] args, Method targetMethod, Class<?> targetClass) throws Exception {    // Custom validation or processing before CRUD operation}
```

#### Service Layer Standards

**Pattern**: Service must be an interface, implementation extends `BaseServiceImpl`

```java
// Service Interfacepublic interface XxxService extends BaseService<XxxResp, XxxDetailResp, XxxQuery, XxxReq>, IService<XxxDO> {    // Custom business methods with JavaDoc}// Service Implementation@Slf4j@Service@RequiredArgsConstructorpublic class XxxServiceImpl extends BaseServiceImpl<XxxMapper, XxxDO, XxxResp, XxxDetailResp, XxxQuery, XxxReq>    implements XxxService {    // Dependencies injected via constructor (RequiredArgsConstructor)    private final OtherService otherService;    // Circular dependencies use @Resource    @Resource    private SelfReferencingService selfService;    // Configuration values    @Value("${config.key}")    private String configValue;}
```

**BaseServiceImpl Generics**:

1.  Mapper interface type
2.  Entity type (DO)
3.  List response type
4.  Detail response type
5.  Query condition type
6.  Request parameter type

**Service Hook Methods**:

```java
@Overridepublic void beforeCreate(XxxReq req) {    // Validation, uniqueness check before create}@Override@Transactional(rollbackFor = Exception.class)public void afterCreate(XxxReq req, XxxDO entity) {    // Associated data creation, cache update after create}@Overridepublic void beforeUpdate(XxxReq req, Long id) {    // Validation, permission check before update}@Override@Transactional(rollbackFor = Exception.class)public void afterUpdate(XxxReq req, XxxDO newEntity, XxxDO oldEntity) {    // Associated data update, cache update after update}@Overridepublic void beforeDelete(Long id) {    // Association check, permission check before delete}@Override@Transactional(rollbackFor = Exception.class)public void afterDelete(Long id, XxxDO entity) {    // Associated data deletion, cache cleanup after delete}
```

**Transaction and Cache Annotations**:

```java
@Transactional(rollbackFor = Exception.class)  // All write operations@CacheInvalidate(name = "prefix:", key = "#id") // Delete cache@CacheUpdate(name = "prefix:", key = "#id", value = "#result") // Update cache@Cached(name = "prefix:", key = "#id", expire = 3600) // Query cache
```

#### Mapper Layer Standards

```java
@Mapperpublic interface XxxMapper extends DataPermissionMapper<XxxDO> {    @DataPermission(tableAlias = "t1")  // Apply data permission    IPage<XxxResp> selectXxxPage(@Param("page") IPage<XxxDO> page,                                  @Param(Constants.WRAPPER) QueryWrapper<XxxDO> queryWrapper);    @Select("SELECT * FROM table WHERE field = #{value}")    XxxDO selectByField(@FieldEncrypt @Param("value") String value);  // Encrypted field}
```

**Mapper XML** (complex queries):

```xml
<mapper namespace="package.XxxMapper">    <select id="selectXxxPage" resultType="package.XxxResp">        SELECT t1.*, t2.name AS relatedName        FROM xxx_table t1        LEFT JOIN related_table t2 ON t1.related_id = t2.id        ${ew.customSqlSegment}    </select></mapper>
```

#### Model Layer Standards

**Entity (DO)** - Database entity:

```java
@Data@TableName("table_name")@DictModel(labelKey = "name", extraKeys = {"code"})  // For dictionarypublic class XxxDO extends BaseDO {    @Serial    private static final long serialVersionUID = 1L;    private String field;    @FieldEncrypt  // Encrypt sensitive field    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY)    private String sensitiveField;    @FieldEncrypt(encryptor = PasswordEncoderEncryptor.class)    private String password;    private StatusEnum status;  // Use enum directly    private Boolean isSystem;}
```

**Request (Req)** - API input:

```java
@Data@Schema(description = "XXX创建或修改请求")public class XxxReq implements Serializable {    @Serial    private static final long serialVersionUID = 1L;    @Schema(description = "字段", example = "示例值")    @NotBlank(message = "字段不能为空")    @Pattern(regexp = RegexConstants.PATTERN, message = "格式不正确")    private String field;    @NotNull(message = "枚举字段无效")    private StatusEnum status;    @NotEmpty(message = "列表不能为空")    private List<Long> ids;    // Group validation for different operations    @NotBlank(message = "密码不能为空", groups = CrudValidationGroup.Create.class)    private String password;}
```

**Response (Resp)** - API output:

```java
@Data@Schema(description = "XXX响应")@Assemble(key = "id", props = @Mapping(src = "roleId", ref = "roleIds"),          container = "CONTAINER", handlerType = OneToManyAssembleOperationHandler.class)public class XxxResp extends BaseDetailResp {    @Serial    private static final long serialVersionUID = 1L;    @Schema(description = "字段", example = "示例")    private String field;    @JsonMask(MaskType.EMAIL)  // Data masking    private String email;    @JsonMask(MaskType.MOBILE_PHONE)    private String phone;    @Assemble(props = @Mapping(src = "name", ref = "names"),              container = "CONTAINER", handlerType = ManyToManyAssembleOperationHandler.class)    private List<Long> relatedIds;    private List<String> names;  // Auto-filled by Crane4j    @Override    public Boolean getDisabled() {        return this.getIsSystem() || Objects.equals(this.getId(), UserContextHolder.getUserId());    }}
```

**Query** - Search conditions:

```java
@Data@Schema(description = "XXX查询条件")public class XxxQuery implements Serializable {    @Serial    private static final long serialVersionUID = 1L;    @Schema(description = "关键词", example = "搜索内容")    private String keyword;    @Schema(description = "状态", example = "1")    private StatusEnum status;    @Schema(description = "创建时间")    private List<LocalDateTime> createTime;  // Date range}
```

**Common Validation Annotations**:

-   `@NotBlank` - String not empty
-   `@NotNull` - Object not null
-   `@NotEmpty` - Collection/Array not empty
-   `@Pattern(regexp)` - Regex validation
-   `@Email` - Email format
-   `@Length(min, max)` - Length validation
-   `@Mobile` - Phone number (custom)
-   `groups = CrudValidationGroup.Create.class` - Group validation

**MyBatis Plus Annotations**:

-   `@TableName("table")` - Map table
-   `@TableId(type = IdType.AUTO)` - Primary key
-   `@TableField(value = "column")` - Map column
-   `@TableField(insertStrategy = FieldStrategy.NOT_EMPTY)` - Insert strategy
-   `@TableLogic` - Logical delete

### Frontend Development Patterns

#### Standard Page Structure (Recommended)

```
page-name/├── api/                    # Page-specific API calls│   └── xxx.ts             # Export all API functions├── hooks/                  # Business logic hooks (optional)│   └── useXxx.ts          # Export business logic hook├── components/             # Page-specific components (optional)│   ├── XxxForm.vue        # Create/Edit form component│   └── XxxDetail.vue      # Detail component└── index.vue              # Main page component
```

**Alternative Pattern** (Current ContiNew Admin style):

```
page-name/├── AddDrawer.vue          # Create/Edit drawer├── DetailDrawer.vue       # Detail drawer├── ImportDrawer.vue       # Import drawer (optional)├── XxxModal.vue           # Other modals (optional)├── subfolder/             # Related sub-components└── index.vue              # Main page component
```

#### Main Page Component Pattern (index.vue)

```vue
<template>  <GiPageLayout>    <!-- Optional left sidebar (e.g., tree) -->    <template #left>      <DeptTree @node-click="handleSelectDept" />    </template>    <!-- Main table -->    <GiTable      row-key="id"      :data="dataList"      :columns="columns"      :loading="loading"      :scroll="{ x: '100%', y: '100%', minWidth: 1500 }"      :pagination="pagination"      @refresh="search"    >      <!-- Query form -->      <template #top>        <GiForm          v-model="queryForm"          search          :columns="queryFormColumns"          @search="search"          @reset="reset"        />      </template>      <!-- Toolbar buttons -->      <template #toolbar-left>        <a-button v-permission="['module:create']" type="primary" @click="onAdd">          <template #icon><icon-plus /></template>          <template #default>新增</template>        </a-button>      </template>      <template #toolbar-right>        <a-button v-permission="['module:export']" @click="onExport">          <template #icon><icon-download /></template>          <template #default>导出</template>        </a-button>      </template>      <!-- Custom column slots -->      <template #columnName="{ record }">        <GiCellStatus :status="record.status" />      </template>      <!-- Action column -->      <template #action="{ record }">        <a-space>          <a-link v-permission="['module:update']" @click="onUpdate(record)">修改</a-link>          <a-link v-permission="['module:delete']" status="danger" @click="onDelete(record)">删除</a-link>        </a-space>      </template>    </GiTable>    <!-- Child components -->    <AddDrawer ref="AddDrawerRef" @save-success="search" />  </GiPageLayout></template><script setup lang="ts">import { listXxx, deleteXxx, exportXxx } from '@/apis/module/xxx'import { useTable, useDownload, useResetReactive } from '@/hooks'defineOptions({ name: 'ModuleXxx' })// Query form with resetconst [queryForm, resetForm] = useResetReactive({  sort: ['id,desc'],})// Query form configurationconst queryFormColumns: ColumnItem[] = reactive([  {    type: 'input',    label: '关键词',    field: 'keyword',    span: { xs: 24, sm: 8 },    props: { placeholder: '请输入关键词' },  },  {    type: 'select',    label: '状态',    field: 'status',    span: { xs: 24, sm: 8 },    props: { options: StatusList, placeholder: '请选择状态' },  },])// Table data and operationsconst {  tableData: dataList,  loading,  pagination,  search,  handleDelete,} = useTable((page) => listXxx({ ...queryForm, ...page }))// Table columnsconst columns: TableInstance['columns'] = [  { title: '名称', dataIndex: 'name', minWidth: 140 },  { title: '状态', dataIndex: 'status', slotName: 'status', align: 'center' },  { title: '创建时间', dataIndex: 'createTime', width: 180 },  {    title: '操作',    dataIndex: 'action',    slotName: 'action',    width: 160,    align: 'center',    fixed: !isMobile() ? 'right' : undefined,    show: has.hasPermOr(['module:update', 'module:delete']),  },]// Operationsconst onDelete = (record: XxxResp) => {  return handleDelete(() => deleteXxx(record.id), {    content: `是否确定删除「${record.name}」？`,  })}const onExport = () => {  useDownload(() => exportXxx(queryForm))}const AddDrawerRef = ref()const onAdd = () => AddDrawerRef.value?.onOpen()const onUpdate = (record: XxxResp) => AddDrawerRef.value?.onOpen(record.id)</script>
```

#### Form Drawer Component Pattern

```vue
<template>  <a-drawer    v-model:visible="visible"    :title="title"    :mask-closable="false"    :width="width >= 500 ? 500 : '100%'"    @before-ok="save"    @close="reset"  >    <GiForm ref="formRef" v-model="form" :columns="columns" />  </a-drawer></template><script setup lang="ts">import { addXxx, getXxx, updateXxx } from '@/apis/module/xxx'import { useResetReactive } from '@/hooks'const emit = defineEmits<{ (e: 'save-success'): void }>()const dataId = ref('')const visible = ref(false)const isUpdate = computed(() => !!dataId.value)const title = computed(() => (isUpdate.value ? '修改XXX' : '新增XXX'))const formRef = ref<InstanceType<typeof GiForm>>()// Form data with default valuesconst [form, resetForm] = useResetReactive({  status: 1,})// Form configurationconst columns: ColumnItem[] = reactive([  {    label: '名称',    field: 'name',    type: 'input',    span: 24,    required: true,    props: { maxLength: 30 },  },  {    label: '状态',    field: 'status',    type: 'switch',    span: 24,    props: { checkedValue: 1, uncheckedValue: 2 },  },])// Open drawerconst onOpen = async (id = '') => {  visible.value = true  dataId.value = id  // Load edit data  if (id) {    const res = await getXxx(id)    Object.assign(form, res.data)  }}// Saveconst save = async () => {  const valid = await formRef.value?.validateForm()  if (!valid) return false  try {    const data = { ...form }    if (isUpdate.value) {      await updateXxx(data, dataId.value)      Message.success('修改成功')    } else {      await addXxx(data)      Message.success('新增成功')    }    emit('save-success')    return true  } catch {    return false  }}// Resetconst reset = () => {  formRef.value?.resetForm()  resetForm()}defineExpose({ onOpen })</script>
```

#### API File Pattern

```typescript
// src/apis/module/xxx.tsimport type * as T from './type'import http from '@/utils/http'const BASE_URL = '/api/module/xxx'/** @desc 查询XXX分页列表 */export function listXxx(query: T.XxxPageQuery) {  return http.get<PageRes<T.XxxResp[]>>(`${BASE_URL}`, query)}/** @desc 查询XXX详情 */export function getXxx(id: string) {  return http.get<T.XxxDetailResp>(`${BASE_URL}/${id}`)}/** @desc 新增XXX */export function addXxx(data: T.XxxReq) {  return http.post(`${BASE_URL}`, data)}/** @desc 修改XXX */export function updateXxx(data: T.XxxReq, id: string) {  return http.put(`${BASE_URL}/${id}`, data)}/** @desc 删除XXX */export function deleteXxx(id: string) {  return http.del(`${BASE_URL}`, { ids: [id] })}/** @desc 导出XXX */export function exportXxx(query: T.XxxQuery) {  return http.download(`${BASE_URL}/export`, query)}/** @desc 查询XXX字典 */export function listXxxDict() {  return http.get<LabelValueState[]>(`${BASE_URL}/dict`)}
```

#### Type Definition Pattern

```typescript
// src/apis/module/type.tsimport type { PageQuery } from '@/types/global'/** XXX查询条件 */export interface XxxQuery {  keyword?: string  status?: number  createTime?: string[]}/** XXX分页查询 */export interface XxxPageQuery extends XxxQuery, PageQuery {}/** XXX请求 */export interface XxxReq {  name: string  status: number  description?: string}/** XXX响应 */export interface XxxResp {  id: string  name: string  status: number  description: string  createTime: string}/** XXX详情响应 */export interface XxxDetailResp extends XxxResp {  // Additional detail fields}
```

#### Common Hooks

**useTable** - Table data management:

```typescript
const {  tableData,  loading,  pagination,  search,  handleDelete,} = useTable((page) => listApi({ ...queryForm, ...page }), { immediate: true })
```

**useResetReactive** - Form state with reset:

```typescript
const [form, resetForm] = useResetReactive({ field: defaultValue })
```

**useDownload** - File download:

```typescript
const onExport = () => useDownload(() => exportApi(queryForm))
```

**useDept / useRole** - Load dictionaries:

```typescript
const { deptList, getDeptList } = useDept()const { roleList, getRoleList } = useRole()
```

#### Common Components

**GiTable** - Enhanced table:

-   `row-key` - Unique identifier
-   `:data` - Table data
-   `:columns` - Column definitions
-   `:loading` - Loading state
-   `:pagination` - Pagination config
-   `@refresh` - Refresh handler

**GiForm** - Dynamic form:

-   `v-model` - Form data
-   `:columns` - Form item config
-   `search` - Search form mode
-   `@search` - Search handler
-   `@reset` - Reset handler

**GiCell Components**:

-   `<GiCellAvatar :avatar :name />` - Avatar display
-   `<GiCellGender :gender />` - Gender display
-   `<GiCellStatus :status />` - Status badge
-   `<GiCellTags :data />` - Tag list

#### Permission Control

```vue
<!-- Directive --><a-button v-permission="['module:create']">新增</a-button><!-- API in script --><script setup lang="ts">import has from '@/utils/has'const showColumn = has.hasPermOr(['module:update', 'module:delete'])const canEdit = has.hasPerm('module:update')const isAdmin = has.hasRole('admin')</script>
```

## Utility Classes and Helpers

### Backend Utilities (Hutool)

```java
// String utilitiesStrUtil.isBlank(str)StrUtil.format("模板{}", param)StrUtil.removePrefix(str, prefix)StrUtil.prependIfMissing(str, prefix)// Object utilitiesObjectUtil.isNull(obj)ObjectUtil.defaultIfNull(obj, default)ObjectUtil.equal(obj1, obj2)// Collection utilitiesCollUtil.isEmpty(list)CollUtil.newArrayList(1, 2, 3)// Bean utilitiesBeanUtil.copyProperties(source, target)BeanUtil.copyToList(sourceList, TargetClass.class)// Date utilitiesDateUtil.parse("2023-01-01")DateUtil.format(date, "yyyy-MM-dd")
```

### Project Utilities

```java
// ValidationValidationUtils.throwIf(condition, "错误消息")ValidationUtils.throwIfNull(obj, "对象不能为空")// SecuritySecureUtils.decryptPasswordByRsaPrivateKey(password, "解密失败", true)// User contextUserContextHolder.getUserId()UserContextHolder.getUsername()// RedisRedisUtils.set(key, value)RedisUtils.get(key)RedisUtils.delete(key)
```

### Common Constants

```java
// Regular expressionsRegexConstants.USERNAME = "^[a-zA-Z][a-zA-Z0-9_]{3,63}$"RegexConstants.GENERAL_NAME = "^[u4e00-u9fa5a-zA-Z0-9_-]{2,30}$"// EnumsGenderEnum.MALE(1, "男"), FEMALE(2, "女"), UNKNOWN(0, "未知")DisEnableStatusEnum.ENABLE(1, "启用"), DISABLE(2, "禁用")// Cache keysCacheConstants.USER_KEY_PREFIX = "user:"CacheConstants.ROLE_KEY_PREFIX = "role:"
```

## Project-Specific Notes

### Module Dependencies

-   `prms-continew-config`: Custom configuration for PRMS features (extends continew-admin)
-   All custom modules should depend on `continew-common` for shared utilities
-   Plugin modules are optional and can be enabled/disabled as needed

### Configuration Files

-   Backend: `application.yml`, `application-dev.yml`, `application-prod.yml`
-   Frontend: `.env.development`, `.env.production`, `.env.test`
-   Database: Liquibase scripts in `db/changelog/mysql/`

### Development Workflow

1.  Backend runs on port 8000 (default)
2.  Frontend dev server runs with `pnpm dev --host`
3.  API documentation available at `/doc.html` (Knife4j)
4.  Mock data available in frontend for offline development

### Naming Conventions

#### Backend

-   Controller: `XxxController.java`
-   Service: `XxxService.java` (interface)
-   ServiceImpl: `XxxServiceImpl.java`
-   Mapper: `XxxMapper.java`
-   Entity: `XxxDO.java`
-   Request: `XxxReq.java`
-   Response: `XxxResp.java`
-   Query: `XxxQuery.java`
-   Enum: `XxxEnum.java`
-   Constants: `XxxConstants.java`

#### Frontend

-   Page: `index.vue` (lowercase + hyphen in folder)
-   Component: `AddDrawer.vue`, `DetailDrawer.vue` (PascalCase)
-   API: `xxx.ts` (lowercase)
-   Type: `type.ts`
-   Hook: `useXxx.ts`