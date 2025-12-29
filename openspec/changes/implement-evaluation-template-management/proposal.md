# Proposal: Implement Evaluation Template Management

## Summary

Implement a comprehensive Evaluation Template Management module for the Project Review Management System (PRMS) with master-detail table structure, step-wizard UI, grouped export functionality, and dynamic total score configuration via dictionary.

## Problem Statement

The current auto-generated CRUD implementation for evaluation templates is incomplete and doesn't meet business requirements:

1. **Multi-table complexity**: Two related tables (`prj_evaluation_template` and `prj_evaluation_template_item`) but only single-table CRUD generated
2. **Poor UX**: Modal-based editing insufficient for complex nested data (categories containing multiple scoring items)
3. **Hardcoded constraints**: Total score hardcoded to 100, no flexibility for different scoring schemes
4. **Missing functionality**: No grouped export, no template code generation, no real-time score validation
5. **Incomplete forms**: Frontend AddModal has TODO placeholder with no form fields implemented

## Proposed Solution

### High-Level Design

Implement a **complete rewrite** with:

1. **Step Wizard UI** (Frontend-First Approach)
   - Step 1: Basic information (code, name, total score from dict, status)
   - Step 2: Scoring items configuration (left panel: categories, right panel: items)
   - Real-time score validation (sum must equal total score)

2. **Custom Backend Logic** (Cannot use BaseServiceImpl)
   - Multi-table transactional operations
   - Cascade create/update/delete for template + items
   - Template code auto-generation
   - Uniqueness validation (code, name)
   - Score sum validation

3. **Grouped Export** (FastExcel with custom styling)
   - Single sheet with category grouping
   - Subtotal rows after each category
   - Grand total row with styling
   - Multi-select support (export selected or all)

4. **Dictionary-Based Configuration**
   - Total score values in `sys_dict` (100, 120, 150)
   - Allows future flexibility without code changes

### Key Features

- **Template Management**: Create, edit, view (read-only page), delete, export
- **Nested Data Editing**: Intuitive left-right panel for categories and scoring items
- **Validation**: Code/name uniqueness, score sum equals total score, decimal precision
- **Export**: Grouped format with category subtotals and custom Excel styling
- **Code Generation**: Auto-generate unique template codes (TPL_XXX pattern)

## Technical Approach

### Frontend Architecture

```
evaluationTemplate/
├── pages/
│   ├── List.vue              # Main list with multi-select
│   ├── Create.vue            # Step wizard (create mode)
│   ├── Edit.vue              # Step wizard (edit mode)
│   └── Detail.vue            # Read-only detail page
├── components/
│   ├── BasicInfoStep.vue     # Step 1 component
│   ├── ScoringItemsStep.vue  # Step 2 wrapper
│   ├── CategoryPanel.vue     # Left: category management
│   ├── ItemPanel.vue         # Right: item management
│   └── ScoreSummary.vue      # Score validation display
├── hooks/
│   └── useTemplateForm.ts    # Form state management
└── api/
    ├── index.ts              # API client
    ├── type.ts               # TypeScript interfaces
    └── mock.ts               # Mock data (temporary)
```

**UI Components**:
- GiTable with row selection for multi-select export
- GiForm for search filters and basic info
- a-steps for step wizard navigation
- Custom panels for category/item management
- Real-time score calculator with visual feedback

### Backend Architecture

**Cannot use standard CRUD** - requires custom implementation:

```java
// Service layer
@Service
public class EvaluationTemplateServiceImpl implements EvaluationTemplateService {

    @Transactional(rollbackFor = Exception.class)
    public void create(EvaluationTemplateReq req) {
        // 1. Validate code/name uniqueness
        // 2. Validate score sum
        // 3. Insert template
        // 4. Batch insert items (grouped by category)
        // 5. Update counts (itemCount, categoryCount)
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(EvaluationTemplateReq req, Long id) {
        // 1. Validate (exclude current record)
        // 2. Update template
        // 3. Delete existing items
        // 4. Batch insert new items
        // 5. Update counts
    }

    public void export(Query query, List<Long> ids, HttpServletResponse response) {
        // Custom grouped export with FastExcel
        // Category subtotals + grand total
        // Custom cell styling
    }
}
```

**Models**:
- `EvaluationTemplateReq` - Nested request with items array
- `EvaluationTemplateItemReq` - Item data (category + scoring details)
- `EvaluationTemplateDetailResp` - Response with nested items
- `EvaluationTemplateExportResp` - Flat structure for Excel export

### Database Schema

**Existing tables** (already created):
- `prj_evaluation_template` - Master table
- `prj_evaluation_template_item` - Detail table (categories + items)

**New dictionary** (to create):
- `evaluation_template_total_score` - Allowed total score values (100, 120, 150)

## Implementation Plan

### Phase 1: Frontend UI with Mock Data (Sprint 1)
1. Restructure directory (create pages/, components/, hooks/)
2. Implement List.vue with GiTable and multi-select
3. Implement Create.vue step wizard
4. Implement Edit.vue (reuse Create components)
5. Implement Detail.vue read-only page
6. Add mock data for testing
7. Apply frontend-design skill for UI enhancement

### Phase 2: Backend Implementation (Sprint 2)
1. Create dictionary for total_score via Liquibase
2. Create request/response models
3. Implement custom service methods (create, update, delete, getDetail)
4. Add validation logic (uniqueness, score sum)
5. Implement code generation
6. Override controller methods

### Phase 3: Export & Integration (Sprint 3)
1. Implement custom grouped export with FastExcel
2. Create custom cell style strategy (subtotals, grand total)
3. Connect frontend to backend APIs
4. Remove mock data
5. Test end-to-end flows

### Phase 4: Polish & Testing (Sprint 4)
1. UI/UX refinements
2. Error handling and user feedback
3. Performance optimization
4. User acceptance testing

## Dependencies

- **Frontend**: Vue 3, Arco Design, GiTable/GiForm components, Pinia
- **Backend**: Spring Boot 3, MyBatis Plus, FastExcel, SaToken, Liquibase
- **Database**: MySQL 8.0+, existing dictionary system

## Success Criteria

- [ ] Template creation with step wizard works smoothly
- [ ] Category and item management is intuitive (left-right panel)
- [ ] Score validation prevents invalid configurations
- [ ] Export produces correctly formatted Excel with grouping
- [ ] Code generation creates unique template codes
- [ ] All validations work (uniqueness, score sum, decimal precision)
- [ ] UI meets user aesthetic expectations
- [ ] Performance is acceptable (< 2s for operations)

## Risks & Mitigation

1. **Complex nested state management**
   - Mitigation: Use dedicated hook (useTemplateForm), normalize state

2. **Decimal precision issues (Java BigDecimal vs JS number)**
   - Mitigation: Use string in TypeScript, parse carefully

3. **Export styling complexity**
   - Mitigation: Write comprehensive unit tests, test with large datasets

4. **Score validation race conditions**
   - Mitigation: Debounce calculation, validate on save

## Alternatives Considered

1. **Modal-based editing** - Rejected: insufficient for complex nested data
2. **Multi-sheet export** - Rejected: poor user experience per user feedback
3. **Hardcoded 100 score** - Rejected: no flexibility for future requirements
4. **Using BaseServiceImpl** - Not feasible: requires custom multi-table logic

## Open Questions

None - all design decisions confirmed with user through interactive Q&A.
